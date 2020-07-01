package ly.pp.justpiano3;

import android.os.Message;

public final class TimeUpdateThread extends Thread {
    private final OLPlayRoom olPlayRoom;

    TimeUpdateThread(OLPlayRoom olPlayRoom) {
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void run() {
        do {
            try {
                Message message = new Message();
                message.what = 3;
                olPlayRoom.handler.sendMessage(message);
                Thread.sleep(60000);
            } catch (Exception e) {
                olPlayRoom.timeUpdateRunning = false;
            }
        } while (olPlayRoom.timeUpdateRunning);
    }
}
