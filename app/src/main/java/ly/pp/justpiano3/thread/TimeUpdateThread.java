package ly.pp.justpiano3.thread;

import android.os.Message;

import ly.pp.justpiano3.activity.OLPlayRoomActivity;

public final class TimeUpdateThread extends Thread {
    private final OLPlayRoomActivity olPlayRoomActivity;

    public TimeUpdateThread(OLPlayRoomActivity olPlayRoomActivity) {
        this.olPlayRoomActivity = olPlayRoomActivity;
    }

    @Override
    public void run() {
        do {
            try {
                Message message = Message.obtain(olPlayRoomActivity.handler);
                message.what = 3;
                olPlayRoomActivity.handler.sendMessage(message);
                Thread.sleep(60000);
            } catch (Exception e) {
                olPlayRoomActivity.timeUpdateRunning = false;
            }
        } while (olPlayRoomActivity.timeUpdateRunning);
    }
}
