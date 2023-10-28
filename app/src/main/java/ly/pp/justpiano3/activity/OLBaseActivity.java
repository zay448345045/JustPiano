package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.handler.android.OLBaseActivityHandler;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class OLBaseActivity extends ComponentActivity {
    private boolean online = true;
    public JPProgressBar jpprogressBar;
    public OLBaseActivityHandler olBaseActivityHandler = new OLBaseActivityHandler(this);
    private final Map<Byte, User> roomPlayerMap = new HashMap<>();

    public Map<Byte, User> getRoomPlayerMap() {
        return roomPlayerMap;
    }

    public static void returnMainMode(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public final void addDialog(String title, String buttonName, String message) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle(title);
        jpDialogBuilder.setMessage(message);
        jpDialogBuilder.setFirstButton(buttonName, (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.buildAndShowDialog();
    }

    public final void outLine() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle("提示");
        jpDialogBuilder.setMessage("非常抱歉,可能由于网络质量不稳定，服务器未能响应，点击确定回到到联网主界面重新登录");
        jpDialogBuilder.setCancelableFalse();
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
            dialog.dismiss();
            SongPlay.INSTANCE.stopPlay();
            OLBaseActivity.returnMainMode(OLBaseActivity.this);
        });
        jpDialogBuilder.buildAndShowDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.push(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JPStack.pop(this);
    }

    @Override
    public Resources getResources() {
        // 禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }
}
