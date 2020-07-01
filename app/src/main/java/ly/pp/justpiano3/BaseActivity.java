package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;

public class BaseActivity extends Activity {
    boolean f3995a = false;
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
        packageName = "ly.pp.justpiano";
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
}
