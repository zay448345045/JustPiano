package ly.pp.justpiano3.activity;

import android.app.Activity;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.listener.SoundDownloadClick;
import ly.pp.justpiano3.midi.MidiUtil;
import ly.pp.justpiano3.task.SoundDownloadTask;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class SoundDownload extends Activity implements Callback {
    public JPProgressBar jpProgressBar;
    public LayoutInflater layoutInflater;
    public GridView gridView;
    private Handler handler;
    private ProgressBar progressBar;
    private TextView downloadText;
    private OutputStream outputStream;
    private LinearLayout linearLayout;
    private int progress = 0;
    private int length = 0;
    private int intentFlag = 0;

    public static void downloadSound(SoundDownload soundDownload, String soundId, String soundName, String soundType) {
        Message message = Message.obtain(soundDownload.handler);
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + soundName + soundType);
        if (file.exists()) {
            file.delete();
        }
        message.what = 0;
        if (soundDownload.handler != null) {
            soundDownload.handler.sendMessage(message);
        }
        InputStream in = null;
        try {
            URL url = new URL("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/Sound" + soundId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/text"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 设置发送数据的格式
            connection.connect();
            if (connection.getResponseCode() == 200) {
                in = connection.getInputStream();
            }
            Message message2;
            try {
                soundDownload.outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                soundDownload.length = connection.getContentLength();
                int n;
                while (-1 != (n = in.read(buffer))) {
                    soundDownload.outputStream.write(buffer, 0, n);
                    soundDownload.progress += 4096;
                    message = Message.obtain(soundDownload.handler);
                    message.what = 1;
                    if (soundDownload.handler != null) {
                        soundDownload.handler.sendMessage(message);
                    }
                }
                in.close();
                soundDownload.outputStream.close();
                message2 = Message.obtain(soundDownload.handler);
                message2.what = 2;
                if (soundDownload.handler != null) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("name", soundName);
                    bundle2.putString("type", soundType);
                    message2.setData(bundle2);
                    soundDownload.handler.sendMessage(message2);
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                message2 = Message.obtain(soundDownload.handler);
                message2.what = 3;
                soundDownload.handler.sendMessage(message2);
                try {
                    soundDownload.outputStream.close();
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void handleSound(int eventType, String soundFileName, String soundId, int soundSize, String soundAuthor, String soundType) {
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

    public final void changeSound(String soundFileName) {
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

            SoundEngineUtil.unloadSf2Sound();
            if (soundFileName.endsWith(".ss")) {
                SoundEngineUtil.teardownAudioStreamNative();
                SoundEngineUtil.unloadWavAssetsNative();
                for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
                    SoundEngineUtil.preloadSounds(this, i);
                }
                SoundEngineUtil.afterLoadSounds();
            } else if (soundFileName.endsWith(".sf2")) {
                SoundEngineUtil.loadSf2Sound(this, new File(
                        Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + soundFileName));
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
                    progressBar.setProgress(progress * 45 / length);
                    downloadText.setText((progress * 45 / length) + "%");
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
        ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
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
