package ly.pp.justpiano3;

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
import android.widget.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SoundDownload extends Activity implements Callback {
    public JPApplication jpapplication;
    JPProgressBar jpProgressBar;
    LayoutInflater layoutInflater;
    GridView gridView;
    List<String> list = new ArrayList<>();
    private Handler handler;
    private ProgressBar progressBar;
    private TextView downloadText;
    private OutputStream outputStream;
    private LinearLayout linearLayout;
    private int progress = 0;
    private int length = 0;
    private int intentFlag = 0;

    static void downloadSS(SoundDownload soundDownload, String str, String str2) {
        Message message = Message.obtain(soundDownload.handler);
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + str2 + ".ss");
        if (file.exists()) {
            Bundle bundle = new Bundle();
            bundle.putString("name", str2);
            message.setData(bundle);
            message.what = 4;
            soundDownload.handler.sendMessage(message);
            return;
        }
        message.what = 0;
        if (soundDownload.handler != null) {
            soundDownload.handler.sendMessage(message);
        }
        InputStream in = null;
        try {
            URL url = new URL("http://" + soundDownload.jpapplication.getServer() + ":8910/JustPianoServer/server/Sound" + str);
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
                    bundle2.putString("name", str2);
                    message2.setData(bundle2);
                    soundDownload.handler.sendMessage(message2);
                }
                soundDownload.list.add(str2);
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

    public final void getLocalSoundList() {
        List<File> b = SkinAndSoundFileHandle.getLocalSoundList(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds");
        for (File aB : b) {
            String name = aB.getName();
            list.add(name.substring(0, name.lastIndexOf('.')));
        }
    }

    public final void mo3005a(int i, String str, String str2, int i2, String str3) {
        JPDialog jpdialog = new JPDialog(this);
        String str4 = "使用";
        jpdialog.setTitle("提示");
        if (i == 0) {
            jpdialog.setMessage("名称:" + str + "\n作者:" + str3 + "\n大小:" + i2 + "KB\n您要下载并使用吗?");
            str4 = "下载";
        } else if (i == 1) {
            jpdialog.setMessage("[" + str + "]音源已下载，是否使用?");
            str4 = "使用";
        } else if (i == 2) {
            jpdialog.setMessage("您要还原极品钢琴的默认音源吗?");
            str4 = "确定";
        }
        jpdialog.setFirstButton(str4, new SoundDownloadClick(this, i, str2, str));
        jpdialog.setSecondButton("取消", new DialogDismissClick());
        jpdialog.showDialog();
    }

    public final void mo3006a(String str) {
        Message message = Message.obtain(handler);
        message.what = 5;
        handler.sendMessage(message);
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("sound_list", Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + str);
        try {
            int i;
            File file = new File(getFilesDir(), "Sounds");
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File delete : listFiles) {
                        delete.delete();
                    }
                }
            }
            GZIP.ZIPFileTo(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + str), file.toString());
            edit.apply();
            JPApplication.teardownAudioStreamNative();
            JPApplication.unloadWavAssetsNative();
            for (i = 108; i >= 24; i--) {
                JPApplication.preloadSounds(i);
            }
            JPApplication.confirmLoadSounds();
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
                    mo3005a(1, message.getData().getString("name"), "", 0, "");
                    break;
                case 3:
                    linearLayout.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "很抱歉,连接出错!", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    linearLayout.setVisibility(View.GONE);
                    progressBar.setProgress(100);
                    downloadText.setText("100%");
                    Toast.makeText(getApplicationContext(), "已存在该音源!", Toast.LENGTH_LONG).show();
                    mo3005a(1, message.getData().getString("name"), "", 0, "");
                    break;
                case 5:
                    linearLayout.setVisibility(View.GONE);
                    jpProgressBar.show();
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        setContentView(R.layout.skin_list);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
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
