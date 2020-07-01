package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

final class OLMelodySongsPlayClick implements OnClickListener {

    private final OLMelodySelectAdapter2 olMelodtsa2;
    private final String f5351b;
    private final String songID;
    private final int f5353d;
    private final double f5354e;

    OLMelodySongsPlayClick(OLMelodySelectAdapter2 olMelodySelectAdapter2, String str, String l, int i, double d) {
        olMelodtsa2 = olMelodySelectAdapter2;
        f5351b = str;
        songID = l;
        f5353d = i;
        f5354e = d;
    }

    @Override
    public final void onClick(View view) {
        olMelodtsa2.olMelodySelect.f4318f = f5351b;
        OLMelodySelect.songID = songID;
        olMelodtsa2.olMelodySelect.f4301F = f5353d;
        olMelodtsa2.olMelodySelect.f4300E = f5354e;
        new OLMelodySongsPlayTask(olMelodtsa2.olMelodySelect).execute();
    }
}
