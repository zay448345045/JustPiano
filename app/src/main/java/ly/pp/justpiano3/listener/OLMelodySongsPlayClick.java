package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.adapter.OLMelodySelectAdapter;
import ly.pp.justpiano3.task.OLMelodySongsPlayTask;

public final class OLMelodySongsPlayClick implements OnClickListener {
    private final OLMelodySelectAdapter olMelodySelectAdapter;
    private final Intent intent;
    private String songName;
    private final String songId;
    private int topScore;
    private double degree;

    public OLMelodySongsPlayClick(OLMelodySelectAdapter olMelodySelectAdapter, String songName, String songId, int topScore, double degree) {
        this.olMelodySelectAdapter = olMelodySelectAdapter;
        this.songName = songName;
        this.songId = songId;
        this.topScore = topScore;
        this.degree = degree;
        intent = new Intent(this.olMelodySelectAdapter.olMelodySelect, PianoPlay.class);
    }

    public OLMelodySongsPlayClick(OLMelodySelectAdapter olMelodySelectAdapter, String songId, Intent intent) {
        this.olMelodySelectAdapter = olMelodySelectAdapter;
        this.songId = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        olMelodySelectAdapter.olMelodySelect.songName = songName;
        OLMelodySelect.songId = songId;
        olMelodySelectAdapter.olMelodySelect.topScore = topScore;
        olMelodySelectAdapter.olMelodySelect.degree = degree;
        new OLMelodySongsPlayTask(olMelodySelectAdapter.olMelodySelect, intent).execute();
    }
}
