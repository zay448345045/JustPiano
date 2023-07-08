package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

public class BaseActivity extends Activity {
    boolean isOutLine = false;
    int activityNum = 0;
    JPProgressBar jpprogressBar;
    String versionStr;
    String packageName;
    int versionNum;
    BaseActivityHandler baseActivityHandler = new BaseActivityHandler(this);

    static void returnMainMode(BaseActivity baseActivity) {
        Intent intent = new Intent();
        intent.setClass(baseActivity, OLMainMode.class);
        baseActivity.startActivity(intent);
        baseActivity.finish();
    }

    final void setPackageAndVersion() {
        versionStr = "4.3";
        versionNum = 41;
        packageName = getPackageName();
    }

    final void addDialog(String str, String str2, String str3) {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle(str);
        jpdialog.setMessage(str3);
        jpdialog.setFirstButton(str2, new DialogDismissClick());
        jpdialog.showDialog();
    }

    final void outLine() {
        String str;
        String str2;
        if (activityNum == 0) {
            str = "提示";
            str2 = "非常抱歉,可能由于网络质量不稳定,服务器未能响应.请再试一次.";
        } else {
            str = "离线提示";
            str2 = "非常抱歉,可能由于网络质量不稳定,您目前已经掉线,点击确定回到到联网主界面重新登录.";
        }
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle(str);
        jpdialog.setMessage(str2);
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
}
