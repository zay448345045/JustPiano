package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.activity.ComponentActivity;
import ly.pp.justpiano3.handler.android.OLBaseActivityHandler;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class OLBaseActivity extends ComponentActivity {
    private boolean isOutLine = false;
    public JPProgressBar jpprogressBar;
    public OLBaseActivityHandler olBaseActivityHandler = new OLBaseActivityHandler(this);

    public static void returnMainMode(OLBaseActivity olBaseActivity) {
        Intent intent = new Intent();
        intent.setClass(olBaseActivity, LoginActivity.class);
        olBaseActivity.startActivity(intent);
        olBaseActivity.finish();
    }

    public final void addDialog(String str, String str2, String str3) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle(str);
        jpDialogBuilder.setMessage(str3);
        jpDialogBuilder.setFirstButton(str2, ((dialog, which) -> dialog.dismiss()));
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
    public Resources getResources() {
        // 禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

    public void setOutline(boolean value) {
        isOutLine = value;
    }

    public boolean isOutLine() {
        return isOutLine;
    }
}
