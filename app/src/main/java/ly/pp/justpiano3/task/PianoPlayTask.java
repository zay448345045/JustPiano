package ly.pp.justpiano3.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.view.JPProgressBar;

public final class PianoPlayTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<PianoPlay> pianoPlay;

    public PianoPlayTask(PianoPlay pianoPlay) {
        this.pianoPlay = new WeakReference<>(pianoPlay);
    }

    @Override
    protected Void doInBackground(Void... objArr) {
        pianoPlay.get().m3802m();
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (pianoPlay.get().jpprogressbar != null) {
            pianoPlay.get().jpprogressbar.cancel();
        } else {
            pianoPlay.get().jpprogressbar = new JPProgressBar(pianoPlay.get());
        }
        pianoPlay.get().loadSong();
    }

    @Override
    protected void onPreExecute() {
        pianoPlay.get().jpprogressbar.setCancelable(false);
        pianoPlay.get().jpprogressbar.show();
    }
}
