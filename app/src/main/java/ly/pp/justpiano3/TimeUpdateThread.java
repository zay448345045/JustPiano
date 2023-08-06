package ly.pp.justpiano3;

import android.os.Message;

public final class TimeUpdateThread extends Thread {
    private final OLPlayRoomInterface olPlayRoomInterface;

    TimeUpdateThread(OLPlayRoomInterface olPlayRoomInterface) {
        this.olPlayRoomInterface = olPlayRoomInterface;
    }

    @Override
    public void run() {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
            do {
                try {
                    Message message = Message.obtain(olPlayRoom.handler);
                    message.what = 3;
                    olPlayRoom.handler.sendMessage(message);
                    Thread.sleep(60000);
                } catch (Exception e) {
                    olPlayRoom.timeUpdateRunning = false;
                }
            } while (olPlayRoom.timeUpdateRunning);
        } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
            do {
                try {
                    Message message = Message.obtain(olPlayKeyboardRoom.handler);
                    message.what = 3;
                    olPlayKeyboardRoom.handler.sendMessage(message);
                    Thread.sleep(60000);
                } catch (Exception e) {
                    olPlayKeyboardRoom.timeUpdateRunning = false;
                }
            } while (olPlayKeyboardRoom.timeUpdateRunning);
        }
    }
}
