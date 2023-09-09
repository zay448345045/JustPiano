package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.OLMelodySelect;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.adapter.OLMelodySelectAdapter2;
import ly.pp.justpiano3.task.OLMelodySongsPlayTask;

public final class OLMelodySongsPlayClick implements OnClickListener {

    private final OLMelodySelectAdapter2 olMelodtsa2;
    private Intent intent;
    private String songName;
    private String songID;
    private int topScore;
    private double degree;

    public OLMelodySongsPlayClick(OLMelodySelectAdapter2 olMelodySelectAdapter2, String str, String l, int i, double d) {
        olMelodtsa2 = olMelodySelectAdapter2;
        songName = str;
        songID = l;
        topScore = i;
        degree = d;
        this.intent = new Intent();
        intent.setClass(olMelodtsa2.olMelodySelect, PianoPlay.class);
    }

    public OLMelodySongsPlayClick(OLMelodySelectAdapter2 olMelodySelectAdapter2, String songId, Intent intent) {
        olMelodtsa2 = olMelodySelectAdapter2;
        this.songID = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        olMelodtsa2.olMelodySelect.songName = songName;
        OLMelodySelect.songID = songID;
        olMelodtsa2.olMelodySelect.topScore = topScore;
        olMelodtsa2.olMelodySelect.degree = degree;
        new OLMelodySongsPlayTask(olMelodtsa2.olMelodySelect, intent).execute();
    }
}
