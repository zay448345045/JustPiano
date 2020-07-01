package ly.pp.justpiano3;

import android.os.Message;

final class ShowTimeThread extends Thread {
    private final OLPlayHall olPlayHall;

    ShowTimeThread(OLPlayHall olPlayHall) {
        this.olPlayHall = olPlayHall;
    }

    @Override
    public final void run() {
        do {
            try {
                Message message = new Message();
                message.what = 3;
                olPlayHall.showTimeHandler.sendMessage(message);
                Thread.sleep(60000);
            } catch (Exception e) {
                olPlayHall.f4385R = false;
            }
        } while (olPlayHall.f4385R);
    }
}
