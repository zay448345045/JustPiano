package ly.pp.justpiano3;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

final class BaseActivityHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    BaseActivityHandler(BaseActivity baseActivity) {
        weakReference = new WeakReference<>(baseActivity);
    }

    @Override
    public final void handleMessage(Message message) {
        final BaseActivity baseActivity = (BaseActivity) weakReference.get();
        if (message.what == 0) {
            post(() -> {
                baseActivity.isOutLine = true;
                baseActivity.outLine();
            });
        }
    }
}
