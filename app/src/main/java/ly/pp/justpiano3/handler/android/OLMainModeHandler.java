package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.activity.OLPlayHallRoom;

public final class OLMainModeHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLMainModeHandler(OLMainMode oLMainMode) {
        weakReference = new WeakReference<>(oLMainMode);
    }

    @Override
    public void handleMessage(Message message) {
        OLMainMode oLMainMode = (OLMainMode) weakReference.get();
        if (oLMainMode.jpProgressBar != null && oLMainMode.jpProgressBar.isShowing()) {
            oLMainMode.jpProgressBar.cancel();
        }
        switch (message.what) {
            case 1:
                Intent intent = new Intent(oLMainMode, OLPlayHallRoom.class);
                intent.putExtra("HEAD", 1);
                oLMainMode.startActivity(intent);
                oLMainMode.finish();
                break;
            case 2:
                post(() -> oLMainMode.addDialog("提示", "确定", "连接服务器失败"));
                break;
            case 3:
                post(() -> oLMainMode.addDialog("提示", "确定", "服务器未响应!"));
                break;
            case 4:
                post(() -> oLMainMode.addDialog("提示", "确定", "账号不存在，请尝试返回主界面重新登录"));
                break;
            case 5:
                post(() -> oLMainMode.addDialog("提示", "确定", "该账号已在别处登录!"));
                break;
            case 6:
                post(() -> oLMainMode.addDialog("提示", "确定", "您的版本过低，请下载最新版本"));
                break;
            default:
                break;
        }
    }
}
