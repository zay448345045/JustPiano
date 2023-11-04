package ly.pp.justpiano3.thread;

import android.os.Message;

import ly.pp.justpiano3.activity.OLRoomActivity;

public final class TimeUpdateThread extends Thread {
    private final OLRoomActivity olRoomActivity;

    public TimeUpdateThread(OLRoomActivity olRoomActivity) {
        this.olRoomActivity = olRoomActivity;
    }

    @Override
    public void run() {
        do {
            try {
                Message message = Message.obtain(olRoomActivity.handler);
                message.what = 3;
                olRoomActivity.handler.sendMessage(message);
                Thread.sleep(60000);
            } catch (Exception e) {
                olRoomActivity.timeUpdateRunning = false;
            }
        } while (olRoomActivity.timeUpdateRunning);
    }
}
