package ly.pp.justpiano3.thread;

import android.os.Message;
import ly.pp.justpiano3.activity.PianoPlay;

import java.util.Timer;
import java.util.TimerTask;

public final class StartPlayTimer extends TimerTask {
    private final PianoPlay pianoPlay;
    private final Message msg;
    private final Timer timer0;
    private int seconds = 3;

    public StartPlayTimer(PianoPlay pianoPlay, Message message, Timer timer) {
        this.pianoPlay = pianoPlay;
        msg = message;
        timer0 = timer;
    }

    @Override
    public void run() {
        msg.arg1 = seconds;
        pianoPlay.pianoPlayHandler.handleMessage(msg);
        if (seconds == 0) {
            cancel();
            timer0.cancel();
            return;
        }
        seconds--;
    }
}
