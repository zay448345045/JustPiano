package ly.pp.justpiano3.thread;

import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import ly.pp.justpiano3.activity.PianoPlay;

public final class StartPlayTimerTask extends TimerTask {
    private final PianoPlay pianoPlay;
    private final Message message;
    private final Timer timer;
    private int seconds = 3;

    public StartPlayTimerTask(PianoPlay pianoPlay, Message message, Timer timer) {
        this.pianoPlay = pianoPlay;
        this.message = message;
        this.timer = timer;
    }

    @Override
    public void run() {
        message.arg1 = seconds;
        pianoPlay.pianoPlayHandler.handleMessage(message);
        if (seconds == 0) {
            cancel();
            timer.cancel();
            return;
        }
        seconds--;
    }
}
