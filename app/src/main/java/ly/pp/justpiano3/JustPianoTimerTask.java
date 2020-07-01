package ly.pp.justpiano3;

import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

final class JustPianoTimerTask extends TimerTask {
    private final JustPiano f5113b;
    private final Timer f5114c;
    private int f5112a = 3;

    JustPianoTimerTask(JustPiano justPiano, Timer timer) {
        f5113b = justPiano;
        f5114c = timer;
    }

    @Override
    public final void run() {
        if (f5112a == 0) {
            cancel();
            f5114c.cancel();
            Message obtainMessage = f5113b.handler.obtainMessage();
            obtainMessage.what = 0;
            f5113b.handler.sendMessage(obtainMessage);
            new Thread(f5113b).start();
            return;
        }
        f5112a--;
    }
}
