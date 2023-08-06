package ly.pp.justpiano3;

import android.os.Message;
import ly.pp.justpiano3.activity.OLPlayHall;

public final class ShowTimeThread extends Thread {
    private final OLPlayHall olPlayHall;

    public ShowTimeThread(OLPlayHall olPlayHall) {
        this.olPlayHall = olPlayHall;
    }

    @Override
    public void run() {
        do {
            try {
                Message message = Message.obtain(olPlayHall.showTimeHandler);
                message.what = 3;
                olPlayHall.showTimeHandler.sendMessage(message);
                Thread.sleep(60000);
            } catch (Exception e) {
                olPlayHall.isTimeShowing = false;
            }
        } while (olPlayHall.isTimeShowing);
    }
}
