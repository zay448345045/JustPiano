package ly.pp.justpiano3.task;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.database.dao.SongDao;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.PmSongData;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class SongSyncTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activity;
    private final String maxSongId;
    private int count = 0;

    public SongSyncTask(Activity activity, String maxSongId) {
        this.activity = new WeakReference<>(activity);
        this.maxSongId = maxSongId;
    }

    @Override
    protected String doInBackground(String... objects) {
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("maxSongId", maxSongId)
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + ((JPApplication) activity.get().getApplication()).getServer() + ":8910/JustPianoServer/server/SongSync")
                .post(formBody)
                .build();
        try {
            // 发送请求并获取响应
            Response response = OkHttpUtil.client().newCall(request).execute();
            if (response.isSuccessful()) {
                byte[] bytes = response.body().bytes();
                File zipFile = new File(activity.get().getFilesDir().getAbsolutePath() + "/Songs/" + System.currentTimeMillis());
                FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
                fileOutputStream.write(bytes, 0, bytes.length);
                fileOutputStream.close();
                List<File> files = GZIPUtil.ZIPFileTo(zipFile, zipFile.getParentFile().toString());
                zipFile.delete();
                List<Song> insertSongList = new ArrayList<>();
                for (File file : files) {
                    String item = file.getName().substring(0, 1);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] pmData = new byte[fileInputStream.available()];
                    fileInputStream.read(pmData);
                    PmSongData pmSongData = PmSongUtil.INSTANCE.parsePmDataByBytes(pmData);
                    if (pmSongData == null) {
                        continue;
                    }
                    ContentValues contentvalues = new ContentValues();
                    contentvalues.put("name", pmSongData.getSongName());
                    contentvalues.put("item", Consts.items[item.charAt(0) - 'a' + 1]);
                    contentvalues.put("path", "songs/" + item + '/' + file.getName());
                    contentvalues.put("isnew", 1);
                    contentvalues.put("ishot", 0);
                    contentvalues.put("isfavo", 0);
                    contentvalues.put("player", "");
                    contentvalues.put("score", 0);
                    contentvalues.put("date", 0);
                    contentvalues.put("count", 0);
                    contentvalues.put("diff", pmSongData.getRightHandDegree());
                    contentvalues.put("online", 1);
                    contentvalues.put("Ldiff", pmSongData.getLeftHandDegree());
                    contentvalues.put("length", pmSongData.getSongTime());
                    contentvalues.put("Lscore", 0);
                    insertSongList.add(new Song(null, pmSongData.getSongName(), Consts.items[item.charAt(0) - 'a' + 1],
                            "songs/" + item + '/' + file.getName(), 1, 0, 0, "",
                            0, 0, 0, pmSongData.getRightHandDegree(), 1,
                            pmSongData.getLeftHandDegree(), pmSongData.getSongTime(), 0));
                    count++;
                }
                JPApplication.getSongDatabase().songDao().insertSongs(insertSongList);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        if (activity.get() instanceof OLMainMode) {
            ((OLMainMode) activity.get()).loginOnline();
        } else if (activity.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) activity.get();
            melodySelect.jpprogressBar.dismiss();
            JPDialogBuilder jpdialog = new JPDialogBuilder(melodySelect);
            jpdialog.setTitle("在线曲库同步");
            jpdialog.setCancelableFalse();
            jpdialog.setMessage("在线曲库同步成功，本地新增曲谱" + count + "首");
            jpdialog.setSecondButton("确定", ((dialogInterface, i) -> {
                List<SongDao.TotalSongInfo> allSongsCountAndScore = JPApplication.getSongDatabase().songDao().getAllSongsCountAndScore();
                melodySelect.getTotalSongInfoMutableLiveData().setValue(allSongsCountAndScore.get(0));
                dialogInterface.dismiss();
            }));
            jpdialog.buildAndShowDialog();
        }
    }

    @Override
    protected void onPreExecute() {
        if (activity.get() instanceof OLMainMode) {
            OLMainMode olMainMode = (OLMainMode) activity.get();
            Toast.makeText(olMainMode, "曲库同步中，请不要离开...", Toast.LENGTH_SHORT).show();
            olMainMode.jpprogressBar.setCancelable(false);
            olMainMode.jpprogressBar.show();
        } else if (activity.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) activity.get();
            Toast.makeText(melodySelect, "曲库同步中，请不要离开...", Toast.LENGTH_SHORT).show();
            melodySelect.jpprogressBar.setCancelable(false);
            melodySelect.jpprogressBar.show();
        }
    }
}
