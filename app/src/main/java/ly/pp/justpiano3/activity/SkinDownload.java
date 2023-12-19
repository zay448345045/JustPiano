package ly.pp.justpiano3.activity;

import android.content.Context;
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
import ly.pp.justpiano3.listener.SkinDownloadClick;
import ly.pp.justpiano3.task.SkinDownloadTask;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class SkinDownload extends BaseActivity implements Callback {
    public JPProgressBar jpProgressBar;
    public LayoutInflater layoutInflater;
    public GridView gridView;
    private Handler handler;
    private ProgressBar progressBar;
    private TextView downloadText;
    private LinearLayout linearLayout;
    private int progress = 0;
    private int intentFlag = 0;

    public static void downloadPS(SkinDownload skinDownload, String skinId, String skinName) {
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Skins/" + skinName + ".ps");
        if (file.exists()) {
            file.delete();
        }
        Message message = Message.obtain(skinDownload.handler);
        message.what = 0;
        if (skinDownload.handler != null) {
            skinDownload.handler.sendMessage(message);
        }
        FileUtil.INSTANCE.downloadFile("https://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/skins/" + skinId + ".ps",
                file, progress -> {
                    skinDownload.progress = progress;
                    Message message1 = Message.obtain(skinDownload.handler);
                    message1.what = 1;
                    if (skinDownload.handler != null) {
                        skinDownload.handler.sendMessage(message1);
                    }
                }, () -> downloadSuccessHandle(skinDownload, skinName), () -> downloadFailHandle(skinDownload));
    }

    private static void downloadSuccessHandle(SkinDownload skinDownload, String skinName) {
        Message successMessage = Message.obtain(skinDownload.handler);
        successMessage.what = 2;
        if (skinDownload.handler != null) {
            Bundle bundle = new Bundle();
            bundle.putString("name", skinName);
            successMessage.setData(bundle);
            skinDownload.handler.sendMessage(successMessage);
        }
    }

    private static void downloadFailHandle(SkinDownload skinDownload) {
        Message failMessage = Message.obtain(skinDownload.handler);
        failMessage.what = 3;
        skinDownload.handler.sendMessage(failMessage);
    }

    public final void handleSkin(int i, String name, String str2, int size, String author) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        String buttonName = "使用";
        jpDialogBuilder.setTitle("提示");
        if (i == 0) {
            jpDialogBuilder.setMessage("名称:" + name + "\n作者:" + author + "\n大小:" + size + "KB\n您要下载并使用吗?");
            buttonName = "下载";
        } else if (i == 1) {
            jpDialogBuilder.setMessage("[" + name + "]皮肤已下载，是否使用?");
            buttonName = "使用";
        } else if (i == 2) {
            jpDialogBuilder.setMessage("您要还原默认的皮肤吗?");
            buttonName = "使用";
        }
        jpDialogBuilder.setFirstButton(buttonName, new SkinDownloadClick(this, i, str2, name));
        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.buildAndShowDialog();
    }

    public final void changeSkin(String str) {
        Message message = Message.obtain(handler);
        message.what = 5;
        handler.sendMessage(message);
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("skin_list", Environment.getExternalStorageDirectory() + "/JustPiano/Skins/" + str);
        File dir = getDir("Skin", Context.MODE_PRIVATE);
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        GZIPUtil.ZIPFileTo(new File(Environment.getExternalStorageDirectory() + "/JustPiano/Skins/" + str), dir.toString());
        edit.apply();
        Message message2 = Message.obtain(handler);
        message2.what = 6;
        handler.sendMessage(message2);
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
                    handleSkin(1, message.getData().getString("name"), "", 0, "");
                    break;
                case 3:
                    linearLayout.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "网络连接错误或您未授予文件读取权限!", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    linearLayout.setVisibility(View.GONE);
                    jpProgressBar.show();
                    jpProgressBar.setCancelable(false);
                    break;
                case 6:
                    linearLayout.setVisibility(View.GONE);
                    jpProgressBar.dismiss();
                    Toast.makeText(getApplicationContext(), "皮肤设置成功!", Toast.LENGTH_SHORT).show();
                    ImageLoadUtil.setBackground(this);
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
        new SkinDownloadTask(this).execute();
    }
}
