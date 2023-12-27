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
    private final String songID;
    private int topScore;
    private double degree;

    public OLMelodySongsPlayClick(OLMelodySelectAdapter olMelodySelectAdapter, String str, String l, int i, double d) {
        this.olMelodySelectAdapter = olMelodySelectAdapter;
        songName = str;
        songID = l;
        topScore = i;
        degree = d;
        this.intent = new Intent();
        intent.setClass(this.olMelodySelectAdapter.olMelodySelect, PianoPlay.class);
    }

    public OLMelodySongsPlayClick(OLMelodySelectAdapter olMelodySelectAdapter, String songId, Intent intent) {
        this.olMelodySelectAdapter = olMelodySelectAdapter;
        this.songID = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        olMelodySelectAdapter.olMelodySelect.songName = songName;
        OLMelodySelect.songID = songID;
        olMelodySelectAdapter.olMelodySelect.topScore = topScore;
        olMelodySelectAdapter.olMelodySelect.degree = degree;
        new OLMelodySongsPlayTask(olMelodySelectAdapter.olMelodySelect, intent).execute();
    }
}
