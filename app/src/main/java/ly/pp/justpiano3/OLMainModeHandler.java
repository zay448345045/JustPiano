package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

final class OLMainModeHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    OLMainModeHandler(OLMainMode oLMainMode) {
        weakReference = new WeakReference<>(oLMainMode);
    }

    @Override
    public final void handleMessage(Message message) {
        OLMainMode oLMainMode = (OLMainMode) weakReference.get();
        Intent intent = new Intent(oLMainMode, OLPlayHallRoom.class);
        switch (message.what) {
            case 1:
                oLMainMode.jpprogressBar.cancel();
                intent.putExtra("HEAD", 1);
                oLMainMode.startActivity(intent);
                oLMainMode.finish();
                return;
            case 2:
                oLMainMode.jpprogressBar.cancel();
                Looper.prepare();
                oLMainMode.addDialog("提示", "确定", "连接服务器失败");
                Looper.loop();
                return;
            case 3:
                if (oLMainMode.jpprogressBar != null && oLMainMode.jpprogressBar.isShowing()) {
                    oLMainMode.jpprogressBar.cancel();
                }
                Looper.prepare();
                oLMainMode.addDialog("提示", "确定", "服务器未响应!");
                Looper.loop();
                return;
            case 4:
                oLMainMode.jpprogressBar.cancel();
                Looper.prepare();
                oLMainMode.addDialog("提示", "确定", "账号不存在!!");
                Looper.loop();
                return;
            case 5:
                oLMainMode.jpprogressBar.cancel();
                //intent.putExtra("HEAD", 5);
                Looper.prepare();
                oLMainMode.addDialog("提示", "确定", "该账号已在别处登录!");
                Looper.loop();
                //oLMainMode.startActivity(intent);
                //oLMainMode.finish();
                return;
            case 6:
                oLMainMode.jpprogressBar.cancel();
                Looper.prepare();
                oLMainMode.addDialog("提示", "确定", "您的版本过低，请下载最新版本");
                Looper.loop();
                return;
            default:
        }
    }
}
