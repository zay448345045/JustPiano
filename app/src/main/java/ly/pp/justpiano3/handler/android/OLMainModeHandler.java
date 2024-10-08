package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.online.OLMainMode;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;

public final class OLMainModeHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLMainModeHandler(OLMainMode oLMainMode) {
        super(Looper.getMainLooper());
        weakReference = new WeakReference<>(oLMainMode);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        OLMainMode oLMainMode = (OLMainMode) weakReference.get();
        if (oLMainMode.jpProgressBar != null && oLMainMode.jpProgressBar.isShowing()) {
            oLMainMode.jpProgressBar.cancel();
        }
        switch (message.what) {
            case 1 -> {
                oLMainMode.startActivity(new Intent(oLMainMode, OLPlayHallRoom.class).putExtra("HEAD", 1));
                oLMainMode.finish();
            }
            case 2 -> post(() -> oLMainMode.addDialog("提示", "确定", "连接服务器失败"));
            case 3 -> post(() -> oLMainMode.addDialog("提示", "确定", "服务器未响应!"));
            case 4 ->
                    post(() -> oLMainMode.addDialog("提示", "确定", "登录过期，请尝试返回主界面重新登录"));
            case 5 -> post(() -> oLMainMode.addDialog("提示", "确定", "该账号已在别处登录!"));
            case 6 ->
                    post(() -> oLMainMode.addDialog("提示", "确定", "您的版本过低，请下载最新版本"));
            default -> {
            }
        }
    }
}
