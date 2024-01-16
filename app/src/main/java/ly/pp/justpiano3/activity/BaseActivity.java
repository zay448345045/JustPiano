package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.WindowUtil;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoadUtil.setBackground(this, GlobalSetting.getBackgroundPic());
        fullScreenHandle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotification();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fullScreenHandle();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        Context context = newBase.createConfigurationContext(override);
        super.attachBaseContext(context);
    }

    protected void fullScreenHandle() {
        if (GlobalSetting.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(getWindow());
        }
    }

    private void updateNotification() {
        Activity topActivity = JPStack.top();
        if (topActivity != null) {
            Intent targetIntent = new Intent(this, topActivity.getClass());
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, targetIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            Notification notification = new NotificationCompat.Builder(this, ConnectionService.CHANNEL_ID)
                    .setContentTitle(ConnectionService.NOTIFY_CONTENT_TITLE)
                    .setContentText(ConnectionService.NOTIFY_CONTENT_TEXT)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.icon)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(ConnectionService.NOTIFICATION_ID, notification);
        }
    }
}
