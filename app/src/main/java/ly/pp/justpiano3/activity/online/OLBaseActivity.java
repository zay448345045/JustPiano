package ly.pp.justpiano3.activity.online;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.handler.android.OLBaseActivityHandler;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class OLBaseActivity extends BaseActivity {
    private boolean online = true;
    public JPProgressBar jpProgressBar;
    public OLBaseActivityHandler olBaseActivityHandler = new OLBaseActivityHandler(this);
    private static final Map<Byte, User> roomPlayerMap = new ConcurrentHashMap<>();

    public static String kitiName = "";
    public static SharedPreferences accountListSharedPreferences;

    public static String loginResultTitle = "";
    public static String loginResultMessage = "";
    private static String accountName = "";
    private static String password = "";

    public static Map<Byte, User> getRoomPlayerMap() {
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
        progressBarDismissAndReInit();
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle("提示");
        jpDialogBuilder.setMessage("非常抱歉，可能由于网络质量不稳定，服务器未能响应，点击确定回到到联网主界面重新登录");
        jpDialogBuilder.setCancelableFalse();
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
            dialog.dismiss();
            SongPlay.INSTANCE.stopPlay();
            OLBaseActivity.returnMainMode(OLBaseActivity.this);
        });
        jpDialogBuilder.buildAndShowDialog();
    }

    public final void progressBarDismissAndReInit() {
        if (jpProgressBar != null && jpProgressBar.isShowing()) {
            jpProgressBar.setText("");
            jpProgressBar.setCancelable(true);
            jpProgressBar.dismiss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.push(this);
        jpProgressBar = new JPProgressBar(this);
    }

    @Override
    protected void onDestroy() {
        JPStack.pop(this);
        super.onDestroy();
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

    public static String getAccountName() {
        if (accountName.isEmpty()) {
            accountName = accountListSharedPreferences.getString("name", "");
        }
        return accountName;
    }

    public static void setAccountName(String str) {
        accountName = str;
    }

    public static String getPassword() {
        if (password.isEmpty()) {
            password = accountListSharedPreferences.getString("password", "");
        }
        return password;
    }

    public static void setPassword(String str) {
        password = str;
    }

    public static String getKitiName() {
        if (TextUtils.isEmpty(kitiName)) {
            kitiName = accountListSharedPreferences.getString("userKitiName", "");
        }
        return kitiName;
    }

    public static void setKitiName(String str) {
        kitiName = str;
    }
}
