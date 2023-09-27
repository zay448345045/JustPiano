package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.OLBaseActivity;

public final class OLBaseActivityHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLBaseActivityHandler(OLBaseActivity OLBaseActivity) {
        weakReference = new WeakReference<>(OLBaseActivity);
    }

    @Override
    public void handleMessage(Message message) {
        final OLBaseActivity olBaseActivity = (OLBaseActivity) weakReference.get();
        if (message.what == 0) {
            post(() -> {
                olBaseActivity.setOutline(true);
                olBaseActivity.outLine();
            });
        }
    }
}
