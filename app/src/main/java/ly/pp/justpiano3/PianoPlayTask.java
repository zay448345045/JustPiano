package ly.pp.justpiano3;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public final class PianoPlayTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<PianoPlay> pianoPlay;

    PianoPlayTask(PianoPlay pianoPlay) {
        this.pianoPlay = new WeakReference<>(pianoPlay);
    }

    @Override
    protected final Void doInBackground(Void... objArr) {
        pianoPlay.get().m3802m();
        return null;
    }

    @Override
    protected final void onPostExecute(Void v) {
        pianoPlay.get().jpprogressbar.cancel();
        pianoPlay.get().m3794f();
    }

    @Override
    protected final void onPreExecute() {
        pianoPlay.get().jpprogressbar.setCancelable(false);
        pianoPlay.get().jpprogressbar.show();
    }
}
