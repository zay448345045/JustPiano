package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.listener.SoundDownloadClick;
import ly.pp.justpiano3.midi.MidiUtil;
import ly.pp.justpiano3.task.SoundDownloadTask;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public final class SoundDownload extends BaseActivity implements Callback {
    public JPProgressBar jpProgressBar;
    public LayoutInflater layoutInflater;
    public GridView gridView;
    private Handler handler;
    private ProgressBar progressBar;
    private TextView downloadText;
    private LinearLayout linearLayout;
    private int progress;
    private int intentFlag;

    public static void downloadSound(SoundDownload soundDownload, String soundId, String soundName, String soundType) {
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + soundName + soundType);
        if (file.exists()) {
            file.delete();
        }
        Message message = Message.obtain(soundDownload.handler);
        message.what = 0;
        if (soundDownload.handler != null) {
            soundDownload.handler.sendMessage(message);
        }
        FileUtil.INSTANCE.downloadFile("https://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/sounds/" + soundId + soundType,
                file, progress -> {
                    soundDownload.progress = progress;
                    Message message1 = Message.obtain(soundDownload.handler);
                    message1.what = 1;
                    if (soundDownload.handler != null) {
                        soundDownload.handler.sendMessage(message1);
                    }
                }, () -> downloadSuccessHandle(soundDownload, soundName, soundType), () -> downloadFailHandle(soundDownload));
    }

    private static void downloadSuccessHandle(SoundDownload soundDownload, String soundName, String soundType) {
        Message successMessage = Message.obtain(soundDownload.handler);
        successMessage.what = 2;
        if (soundDownload.handler != null) {
            Bundle bundle = new Bundle();
            bundle.putString("name", soundName);
            bundle.putString("type", soundType);
            successMessage.setData(bundle);
            soundDownload.handler.sendMessage(successMessage);
        }
    }

    private static void downloadFailHandle(SoundDownload soundDownload) {
        Message failMessage = Message.obtain(soundDownload.handler);
        failMessage.what = 3;
        soundDownload.handler.sendMessage(failMessage);
    }

    public void handleSound(int eventType, String soundFileName, String soundId, int soundSize, String soundAuthor, String soundType) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        String buttonText = "使用";
        jpDialogBuilder.setTitle("提示");
        switch (eventType) {
            case 0:
                jpDialogBuilder.setMessage("名称:" + soundFileName + "\n作者:" + soundAuthor + "\n大小:" + soundSize + "KB\n您要下载并使用吗?");
                buttonText = "下载";
                break;
            case 1:
                jpDialogBuilder.setMessage("[" + soundFileName + "]音源已下载，是否使用?");
                buttonText = "使用";
                break;
            case 2:
                jpDialogBuilder.setMessage("您要还原极品钢琴的默认音源吗?");
                buttonText = "确定";
                break;
        }
        jpDialogBuilder.setFirstButton(buttonText, new SoundDownloadClick(this, eventType, soundId, soundFileName, soundType));
        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.buildAndShowDialog();
    }

    public void changeSound(String soundFileName) {
        Message message = Message.obtain(handler);
        message.what = 5;
        handler.sendMessage(message);
        try {
            File file = new File(getFilesDir(), "Sounds");
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    for (File delete : listFiles) {
                        delete.delete();
                    }
                }
            }

            if (soundFileName.endsWith(".ss")) {
                GZIPUtil.ZIPFileTo(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + soundFileName), file.toString());
            }

            Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString("sound_list", Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + soundFileName);
            edit.apply();
            if (soundFileName.endsWith(".ss")) {
                SoundEngineUtil.teardownAudioStreamNative();
                SoundEngineUtil.unloadSf2();
                SoundEngineUtil.unloadWavAssetsNative();
                SoundEngineUtil.setupAudioStreamNative();
                for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
                    SoundEngineUtil.loadSoundAssetsNative(this, i);
                }
                SoundEngineUtil.startAudioStreamNative();
            } else if (soundFileName.endsWith(".sf2")) {
                String newSf2Path = FileUtil.INSTANCE.copyFileToAppFilesDir(this, new File(
                        Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + soundFileName));
                SoundEngineUtil.teardownAudioStreamNative();
                SoundEngineUtil.unloadSf2();
                SoundEngineUtil.setupAudioStreamNative();
                SoundEngineUtil.loadSf2(newSf2Path);
                SoundEngineUtil.startAudioStreamNative();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        message = Message.obtain(handler);
        message.what = 6;
        handler.sendMessage(message);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (!Thread.currentThread().isInterrupted()) {
            switch (message.what) {
                case 0:
                    linearLayout.setVisibility(View.VISIBLE);
                    progressBar.setMax(100);
                    break;
                case 1:
                    progressBar.setProgress(progress);
                    downloadText.setText(progress + "%");
                    break;
                case 2:
                    linearLayout.setVisibility(View.GONE);
                    downloadText.setVisibility(View.GONE);
                    handleSound(1, message.getData().getString("name"), "",
                            0, "", message.getData().getString("type"));
                    break;
                case 3:
                    linearLayout.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "网络连接错误或您未授予文件读取权限!", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    linearLayout.setVisibility(View.GONE);
                    jpProgressBar.show();
                    jpProgressBar.setCancelable(false);
                    Toast.makeText(getApplicationContext(), "正在载入音源,请稍后。。。", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    linearLayout.setVisibility(View.GONE);
                    jpProgressBar.dismiss();
                    Toast.makeText(getApplicationContext(), "音源载入成功!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (jpProgressBar.isShowing()) {
            jpProgressBar.dismiss();
        }
        if (intentFlag == Intent.FILL_IN_ACTION) {
            Intent intent = new Intent();
            intent.setClass(this, MainMode.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin_list);
        intentFlag = getIntent().getFlags();
        layoutInflater = getLayoutInflater();
        jpProgressBar = new JPProgressBar(this);
        handler = new Handler(this);
        gridView = findViewById(R.id.skin_grid_list);
        progressBar = findViewById(R.id.downloadProgress);
        downloadText = findViewById(R.id.downloadText);
        linearLayout = findViewById(R.id.window);
        new SoundDownloadTask(this).execute();
    }
}
