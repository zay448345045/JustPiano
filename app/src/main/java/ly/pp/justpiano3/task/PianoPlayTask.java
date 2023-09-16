package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import ly.pp.justpiano3.activity.PianoPlay;

import java.lang.ref.WeakReference;

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
        pianoPlay.get().jpprogressbar.cancel();
        pianoPlay.get().loadSong();
    }

    @Override
    protected void onPreExecute() {
        pianoPlay.get().jpprogressbar.setCancelable(false);
        pianoPlay.get().jpprogressbar.show();
    }
}
