package ly.pp.justpiano3.listener;

import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.adapter.OLMelodySelectAdapter2;
import ly.pp.justpiano3.task.OLMelodySongsPlayTask;
import ly.pp.justpiano3.activity.OLMelodySelect;

public final class OLMelodySongsPlayClick implements OnClickListener {

    private final OLMelodySelectAdapter2 olMelodtsa2;
    private final String songName;
    private final String songID;
    private final int topScore;
    private final double degree;

    public OLMelodySongsPlayClick(OLMelodySelectAdapter2 olMelodySelectAdapter2, String str, String l, int i, double d) {
        olMelodtsa2 = olMelodySelectAdapter2;
        songName = str;
        songID = l;
        topScore = i;
        degree = d;
    }

    @Override
    public void onClick(View view) {
        olMelodtsa2.olMelodySelect.songName = songName;
        OLMelodySelect.songID = songID;
        olMelodtsa2.olMelodySelect.topScore = topScore;
        olMelodtsa2.olMelodySelect.degree = degree;
        new OLMelodySongsPlayTask(olMelodtsa2.olMelodySelect).execute();
    }
}
