package ly.pp.justpiano3.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

public final class SongSyncTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<Activity> weakReference;
    private int count = 0;

    public SongSyncTask(Activity weakReference) {
        this.weakReference = new WeakReference<>(weakReference);
    }

    @Override
    protected String doInBackground(Void... v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(weakReference.get());
        long lastSongModifiedTime = sharedPreferences.getLong("song_sync_time", PmSongUtil.SONG_SYNC_DEFAULT_TIME);
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
                byte[] bytes = GZIPUtil.ZIPToArray(jSONObject.getString("S"));
                // 先解压zip文件，获取里面的所有pm文件
                File zipFile = new File(weakReference.get().getFilesDir().getAbsolutePath() + "/Songs/" + System.currentTimeMillis());
                FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
                fileOutputStream.write(bytes, 0, bytes.length);
                fileOutputStream.close();
                List<File> files = GZIPUtil.ZIPFileTo(zipFile, zipFile.getParentFile().toString());
                zipFile.delete();
                // 2.取已删除的曲谱id列表，准备进行数据库曲谱同步的删除操作
                List<String> songPathList = new ArrayList<>();
                String deleteSongIdList = jSONObject.getString("D");
                if (!TextUtils.isEmpty(deleteSongIdList)) {
                    for (String songPath : deleteSongIdList.split(",")) {
                        songPathList.add("songs/" + songPath + ".pm");
                    }
                }
                // 3.按曲谱文件path查询数据库，确认曲谱是否已存在
                for (File file : files) {
                    String item = PmSongUtil.INSTANCE.getPmSongCategoryByFilePath(file.getAbsolutePath());
                    if (item == null) {
                        continue;
                    }
                    songPathList.add("songs/" + item + '/' + file.getName());
                }
                List<Song> songList = JPApplication.getSongDatabase().songDao().getSongByFilePathList(songPathList);
                Map<String, Integer> songIdMap = new HashMap<>();
                for (Song song : songList) {
                    songIdMap.put(song.getFilePath(), song.getId());
                }
                List<Song> insertSongList = new ArrayList<>();
                List<Song> updateSongList = new ArrayList<>();
                List<Song> deleteSongList = new ArrayList<>();
                if (!TextUtils.isEmpty(deleteSongIdList)) {
                    for (String songPath : deleteSongIdList.split(",")) {
                        Integer songId = songIdMap.get("songs/" + songPath + ".pm");
                        if (songId != null && songId > 0) {
                            deleteSongList.add(new Song(songId, "", "", "",
                                    0, 0, 0, "", 0, 0,
                                    0, 0, 1, 0, 0, 0));
                            count++;
                        }
                    }
                }
                // 4.取新增/更新的曲谱文件，解压并准备进行数据库填充
                for (File file : files) {
                    String item = PmSongUtil.INSTANCE.getPmSongCategoryByFilePath(file.getAbsolutePath());
                    if (item == null) {
                        continue;
                    }
                    // 读取新的曲谱文件信息
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] pmData = new byte[fileInputStream.available()];
                    fileInputStream.read(pmData);
                    PmSongData pmSongData = PmSongUtil.INSTANCE.parsePmDataByBytes(pmData);
                    if (pmSongData == null) {
                        continue;
                    }
                    // 若数据库中存在此曲谱，走更新逻辑，否则走插入逻辑
                    String songPath = "songs/" + item + '/' + file.getName();
                    Integer songId = songIdMap.get(songPath);
                    if (songId != null && songId > 0) {
                        updateSongList.add(new Song(songId, pmSongData.getSongName(), Consts.items[item.charAt(0) - 'a' + 1],
                                songPath, 1, 0, 0, "", 0,
                                0, 0, pmSongData.getRightHandDegree(), 1,
                                pmSongData.getLeftHandDegree(), pmSongData.getSongTime(), 0));
                    } else {
                        insertSongList.add(new Song(null, pmSongData.getSongName(), Consts.items[item.charAt(0) - 'a' + 1],
                                songPath, 1, 0, 0, "", 0,
                                0, 0, pmSongData.getRightHandDegree(), 1,
                                pmSongData.getLeftHandDegree(), pmSongData.getSongTime(), 0));
                    }
                    count++;
                }
                // 4.执行曲谱同步，数据库批量操作
                JPApplication.getSongDatabase().songDao().syncSongs(insertSongList, updateSongList, deleteSongList);
                // 5.取当前同步时间，存储到sharedPreference，下一次曲谱同步时进行获取
                long syncSongTime = jSONObject.getLong("T");
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(weakReference.get()).edit();
                editor.putLong("song_sync_time", syncSongTime);
                editor.apply();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
            return "error";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (weakReference.get() instanceof OLMainMode) {
            if (Objects.equals("error", result)) {
                Toast.makeText(weakReference.get(), "曲库同步失败，请尝试卸载重装app或联系开发者", Toast.LENGTH_SHORT).show();
                ((OLMainMode) weakReference.get()).jpProgressBar.dismiss();
            } else {
                Toast.makeText(weakReference.get(), "曲库同步成功，已处理曲谱" + count + "首", Toast.LENGTH_SHORT).show();
                ((OLMainMode) weakReference.get()).jpProgressBar.show();
                OnlineUtil.cancelAutoReconnect();
                OnlineUtil.onlineConnectionService((JPApplication) weakReference.get().getApplication());
            }
        } else if (weakReference.get() instanceof MelodySelect) {
            if (Objects.equals("error", result)) {
                Toast.makeText(weakReference.get(), "曲库同步失败，请尝试卸载重装app或联系开发者", Toast.LENGTH_SHORT).show();
                ((MelodySelect) weakReference.get()).jpProgressBar.dismiss();
            } else {
                MelodySelect melodySelect = (MelodySelect) weakReference.get();
                melodySelect.jpProgressBar.dismiss();
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(melodySelect);
                jpDialogBuilder.setTitle("曲库同步");
                jpDialogBuilder.setCancelableFalse();
                jpDialogBuilder.setMessage("曲库同步成功，已处理曲谱" + count + "首");
                jpDialogBuilder.setSecondButton("确定", ((dialogInterface, i) -> {
                    List<SongDao.TotalSongInfo> allSongsCountAndScore = JPApplication.getSongDatabase().songDao().getAllSongsCountAndScore();
                    melodySelect.getTotalSongInfoMutableLiveData().setValue(allSongsCountAndScore.get(0));
                    dialogInterface.dismiss();
                }));
                jpDialogBuilder.buildAndShowDialog();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        if (weakReference.get() instanceof OLMainMode olMainMode) {
            Toast.makeText(olMainMode, "曲库同步中...", Toast.LENGTH_SHORT).show();
            olMainMode.jpProgressBar.setCancelable(false);
            olMainMode.jpProgressBar.show();
        } else if (weakReference.get() instanceof MelodySelect melodySelect) {
            Toast.makeText(melodySelect, "曲库同步中...", Toast.LENGTH_SHORT).show();
            melodySelect.jpProgressBar.setCancelable(false);
            melodySelect.jpProgressBar.show();
        }
    }
}
