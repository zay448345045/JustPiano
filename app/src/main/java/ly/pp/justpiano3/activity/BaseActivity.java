package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import ly.pp.justpiano3.handler.android.BaseActivityHandler;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.listener.ReturnMainModeClick;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;

public class BaseActivity extends Activity {
    private boolean isOutLine = false;
    public JPProgressBar jpprogressBar;
    public BaseActivityHandler baseActivityHandler = new BaseActivityHandler(this);

    public static void returnMainMode(BaseActivity baseActivity) {
        Intent intent = new Intent();
        intent.setClass(baseActivity, LoginActivity.class);
        baseActivity.startActivity(intent);
        baseActivity.finish();
    }

    public final void addDialog(String str, String str2, String str3) {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle(str);
        jpdialog.setMessage(str3);
        jpdialog.setFirstButton(str2, new DialogDismissClick());
        jpdialog.showDialog();
    }

    public final void outLine() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle("提示");
        jpdialog.setMessage("非常抱歉,可能由于网络质量不稳定，服务器未能响应，点击确定回到到联网主界面重新登录");
        jpdialog.setCancelableFalse();
        jpdialog.setFirstButton("确定", new ReturnMainModeClick(this));
        try {
            jpdialog.showDialog();
        } catch (Exception ignored) {
        }
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dp2px(Context context, float dpValue) {
        // 获取当前手机的像素密度（1个dp对应几个px）
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 四舍五入取整
    }

    // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dp(Context context, float pxValue) {
        // 获取当前手机的像素密度（1个dp对应几个px）
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f); // 四舍五入取整
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
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
