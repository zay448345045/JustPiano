package ly.pp.justpiano3.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.netty.util.internal.StringUtil;
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
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public final class SongSyncTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<Activity> weakReference;
    private int count = 0;

    public SongSyncTask(Activity weakReference) {
        this.weakReference = new WeakReference<>(weakReference);
    }

    @Override
    protected Void doInBackground(Void... v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(weakReference.get());
        long lastSongModifiedTime = sharedPreferences.getLong("song_sync_time", 0);
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("lastSongModifiedTime", String.valueOf(lastSongModifiedTime))
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/SyncSong")
                .post(formBody)
                .build();
        try {
            // 发送请求并获取响应
            Response response = OkHttpUtil.client().newCall(request).execute();
            if (response.isSuccessful()) {
                // 1.解析响应为json
                JSONObject jSONObject;
                try {
                    jSONObject = new JSONObject(response.body().string());
                } catch (Exception e) {
                    jSONObject = new JSONObject();
                    e.printStackTrace();
                }

                // 2.取当前同步时间，存储到sharedPreference，下一次曲谱同步时进行获取
                long syncSongTime = jSONObject.getLong("T");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(weakReference.get()).edit();
                editor.putLong("song_sync_time", syncSongTime);
                editor.apply();

                // 3.取已删除的曲谱id列表，准备进行数据库曲谱同步的删除操作
                List<Song> deleteSongList = new ArrayList<>();
                String deleteSongIdList = jSONObject.getString("D");
                if (!StringUtil.isNullOrEmpty(deleteSongIdList)) {
                    for (String songId : deleteSongIdList.split(",")) {
                        deleteSongList.add(new Song(Integer.parseInt(songId), "", "", "",
                                0, 0, 0, "", 0, 0,
                                0, 0, 1, 0, 0, 0));
                    }
                }

                // 4.取新增/更新的曲谱文件，解压并准备进行数据库填充
                List<Song> insertSongList = new ArrayList<>();
                List<Song> updateSongList = new ArrayList<>();
                byte[] bytes = GZIPUtil.ZIPToArray(jSONObject.getString("S"));
                // 先解压zip文件，获取里面的所有pm文件
                File zipFile = new File(weakReference.get().getFilesDir().getAbsolutePath() + "/Songs/" + System.currentTimeMillis());
                FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
                fileOutputStream.write(bytes, 0, bytes.length);
                fileOutputStream.close();
                List<File> files = GZIPUtil.ZIPFileTo(zipFile, zipFile.getParentFile().toString());
                zipFile.delete();
                for (File file : files) {
                    String item = PmSongUtil.INSTANCE.getPmSongCategoryByFilePath(file.getName());
                    if (item == null) {
                        continue;
                    }
                    // 按曲谱id取原pm文件，若原pm文件存在，走更新逻辑
                    PmSongData oldPmSongData = PmSongUtil.INSTANCE.parsePmDataByFilePath(weakReference.get(),
                            "songs/" + item + "/" + file.getName());
                    if (oldPmSongData != null) {
                        Integer songId = PmSongUtil.INSTANCE.getPmSongIdByFilePath(file.getName());
                        if (songId != null) {
                            updateSongList.add(new Song(songId, oldPmSongData.getSongName(), Consts.items[item.charAt(0) - 'a' + 1],
                                    "songs/" + item + '/' + file.getName(), 1, 0, 0, "",
                                    0, 0, 0, oldPmSongData.getRightHandDegree(), 1,
                                    oldPmSongData.getLeftHandDegree(), oldPmSongData.getSongTime(), 0));
                        }
                    } else {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] pmData = new byte[fileInputStream.available()];
                        fileInputStream.read(pmData);
                        PmSongData pmSongData = PmSongUtil.INSTANCE.parsePmDataByBytes(pmData);
                        if (pmSongData == null) {
                            continue;
                        }
                        insertSongList.add(new Song(null, pmSongData.getSongName(), Consts.items[item.charAt(0) - 'a' + 1],
                                "songs/" + item + '/' + file.getName(), 1, 0, 0, "",
                                0, 0, 0, pmSongData.getRightHandDegree(), 1,
                                pmSongData.getLeftHandDegree(), pmSongData.getSongTime(), 0));
                    }
                    count++;
                }

                // 5.执行曲谱同步，数据库批量操作
                JPApplication.getSongDatabase().songDao().syncSongs(insertSongList, updateSongList, deleteSongList);
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (weakReference.get() instanceof OLMainMode) {
            Toast.makeText(weakReference.get(), "在线曲库同步成功，已处理曲谱" + count + "首", Toast.LENGTH_SHORT).show();
            ((OLMainMode) weakReference.get()).jpprogressBar.show();
            OnlineUtil.cancelAutoReconnect();
            OnlineUtil.onlineConnectionService(((OLMainMode) weakReference.get()).jpapplication);
        } else if (weakReference.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) weakReference.get();
            melodySelect.jpprogressBar.dismiss();
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(melodySelect);
            jpDialogBuilder.setTitle("在线曲库同步");
            jpDialogBuilder.setCancelableFalse();
            jpDialogBuilder.setMessage("在线曲库同步成功，已处理曲谱" + count + "首");
            jpDialogBuilder.setSecondButton("确定", ((dialogInterface, i) -> {
                List<SongDao.TotalSongInfo> allSongsCountAndScore = JPApplication.getSongDatabase().songDao().getAllSongsCountAndScore();
                melodySelect.getTotalSongInfoMutableLiveData().setValue(allSongsCountAndScore.get(0));
                dialogInterface.dismiss();
            }));
            jpDialogBuilder.buildAndShowDialog();
        }
    }

    @Override
    protected void onPreExecute() {
        if (weakReference.get() instanceof OLMainMode) {
            OLMainMode olMainMode = (OLMainMode) weakReference.get();
            Toast.makeText(olMainMode, "曲库同步中...", Toast.LENGTH_SHORT).show();
            olMainMode.jpprogressBar.setCancelable(false);
            olMainMode.jpprogressBar.show();
        } else if (weakReference.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) weakReference.get();
            Toast.makeText(melodySelect, "曲库同步中...", Toast.LENGTH_SHORT).show();
            melodySelect.jpprogressBar.setCancelable(false);
            melodySelect.jpprogressBar.show();
        }
    }
}
