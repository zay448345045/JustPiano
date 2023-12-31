package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.local.MainMode;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.PmSongData;
import ly.pp.justpiano3.midi.MidiUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.view.JustPianoView;

public final class JustPiano extends BaseActivity implements Runnable {
    public static boolean updateSQL = false;
    private boolean isPause;
    private boolean loadFinish;
    private int songCount;
    private JustPianoView justpianoview;
    private String info;
    private int progress;
    private String loading;

    private void scanSongFileAndUpdateSongDatabase(List<Song> songList) {
        // 删除上个版本曲谱同步下来的所有文件
        deleteOldSyncSongFile();
        // 键为pm文件名，值为Song对象，更新和删除时候映射用
        Map<String, Song> pmPathMap = new HashMap<>();
        // 下面的字段是删除标记，更新和插入的曲谱会得到更新，然后把未更新也未插入的（就是新版没有这个pm）的曲子删掉，也就是在客户端曲子删库用
        int originalPmVersion = songList.isEmpty() ? 0 : songList.get(0).getFileVersion();
        for (Song song : songList) {
            if (song.getFilePath().length() > 8 && song.getFilePath().charAt(7) == '/') {
                pmPathMap.put(song.getFilePath().substring(9), song);
            }
        }
        // 创建pm解析器对象，用于解析pm
        // 遍历assets下的曲谱pm文件
        // 构造插入、更新和删除的曲谱list，扫描曲谱结束时进行事务批量操作
        List<Song> insertSongList = new ArrayList<>();
        List<Song> updateSongList = new ArrayList<>();
        List<Song> deleteSongList = new ArrayList<>();
        try {
            String[] pmCategoryList = getResources().getAssets().list("songs");
            if (pmCategoryList != null) {
                for (int i = 0; i < pmCategoryList.length; i++) {
                    String[] pmFileList = getResources().getAssets().list("songs/" + pmCategoryList[i]);
                    if (pmFileList != null) {
                        for (String pmFileName : pmFileList) {
                            String songPath = "songs/" + pmCategoryList[i] + "/" + pmFileName;
                            PmSongData pmSongData = PmSongUtil.INSTANCE.parsePmDataByFilePath(this, songPath);
                            if (pmSongData == null) {
                                continue;
                            }
                            String songName = pmSongData.getSongName();
                            float rightDegree = pmSongData.getRightHandDegree();
                            float leftDegree = pmSongData.getLeftHandDegree();
                            int songTime = pmSongData.getSongTime();
                            songCount++;
                            Song currentSong = pmPathMap.get(pmFileName.substring(1));
                            if (currentSong != null) {
                                info = "更新曲目:" + songName + "..." + songCount;
                                currentSong.setName(songName);
                                currentSong.setCategory(Consts.items[i + 1]);
                                currentSong.setFilePath(songPath);
                                currentSong.setRightHandDegree(rightDegree);
                                currentSong.setLeftHandDegree(leftDegree);
                                currentSong.setLength(songTime);
                                currentSong.setNew(0);
                                currentSong.setOnline(1);
                                currentSong.setFileVersion(originalPmVersion + 1);
                                updateSongList.add(currentSong);
                            } else {
                                info = "加入曲目" + songName + "..." + songCount;
                                insertSongList.add(new Song(null, songName, Consts.items[i + 1], songPath, 1,
                                        0, 0, "", 0, 0,
                                        originalPmVersion + 1, rightDegree, 1, leftDegree, songTime, 0));
                            }
                            runOnUiThread(() -> justpianoview.updateProgressAndInfo(progress, info, loading));
                        }
                    }
                }
                // 删除未检测到pm的曲谱
                for (Song song : songList) {
                    if (song.getFileVersion() != originalPmVersion + 1) {
                        deleteSongList.add(song);
                    }
                }
                // 执行数据库批量操作
                JPApplication.getSongDatabase().songDao().syncSongs(insertSongList, updateSongList, deleteSongList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteOldSyncSongFile() {
        File syncDir = new File(getFilesDir().getAbsolutePath(), "Songs");
        if (syncDir.exists()) {
            File[] files = syncDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        justpianoview = new JustPianoView(this);
        justpianoview.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(justpianoview);
        runOnUiThread(() -> justpianoview.updateProgressAndInfo(progress, info, loading));
        ThreadPoolUtil.execute(this);
    }

    @Override
    protected void onDestroy() {
        justpianoview.destroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!loadFinish) {
            isPause = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPause && loadFinish) {
            startActivity(new Intent(this, MainMode.class));
            finish();
        } else {
            isPause = false;
        }
    }

    @Override
    public void run() {
        File file = new File(getFilesDir().getAbsolutePath() + "/Sounds");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getFilesDir().getAbsolutePath() + "/Songs");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getFilesDir().getAbsolutePath() + "/Records");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            List<Song> songList = JPApplication.getSongDatabase().songDao().getAllSongs();
            if (songList.isEmpty() || updateSQL) {
                // 扫描包内的曲谱文件，更新至曲谱数据库
                scanSongFileAndUpdateSongDatabase(songList);
            }
        } catch (Exception e5) {
            e5.printStackTrace();
            runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                    "曲谱数据库初始化错误，请尝试卸载重装应用", Toast.LENGTH_SHORT).show());
            System.exit(-1);
        }
        SoundEngineUtil.setupAudioStreamNative();
        for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
            SoundEngineUtil.loadSoundAssetsNative(getApplicationContext(), i);
            progress++;
            loading = "正在载入声音资源..." + progress + "/88";
            runOnUiThread(() -> justpianoview.updateProgressAndInfo(progress, info, loading));
        }
        SoundEngineUtil.openFluidSynth();
        SoundEngineUtil.startAudioStreamNative();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String soundUri = sharedPreferences.getString("sound_select", "original");
        DocumentFile soundFile = FileUtil.uriToDocumentFile(this, Uri.parse(soundUri));
        if (soundFile != null && soundFile.getName() != null && soundFile.getName().endsWith(".sf2")) {
            loading = "正在载入sf2声音资源...";
            runOnUiThread(() -> justpianoview.updateProgressAndInfo(progress, info, loading));
            SoundEngineUtil.loadSf2(FileUtil.copyDocumentFileToAppFilesDir(this, soundFile));
        }
        runOnUiThread(() -> {
            loadFinish = true;
            if (!isPause) {
                startActivity(new Intent(this, MainMode.class));
                finish();
            }
        });
    }
}
