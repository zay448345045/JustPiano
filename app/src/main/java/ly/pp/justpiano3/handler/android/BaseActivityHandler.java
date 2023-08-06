package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import ly.pp.justpiano3.activity.BaseActivity;

import java.lang.ref.WeakReference;

public final class BaseActivityHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public BaseActivityHandler(BaseActivity baseActivity) {
        weakReference = new WeakReference<>(baseActivity);
    }

    @Override
    public void handleMessage(Message message) {
        final BaseActivity baseActivity = (BaseActivity) weakReference.get();
        if (message.what == 0) {
            post(() -> {
                baseActivity.setOutline(true);
                baseActivity.outLine();
            });
        }
    }
}
