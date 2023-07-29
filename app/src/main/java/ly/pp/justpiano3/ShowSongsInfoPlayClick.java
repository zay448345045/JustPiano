package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

final class ShowSongsInfoPlayClick implements OnClickListener {

    private final ShowSongsInfoAdapter showSongsInfoAdapter;
    private final String f5892b;
    private final String songID;
    private final int f5894d;
    private final double f5895e;

    ShowSongsInfoPlayClick(ShowSongsInfoAdapter showSongsInfoAdapter, String str, String l, int i, double d) {
        this.showSongsInfoAdapter = showSongsInfoAdapter;
        f5892b = str;
        songID = l;
        f5894d = i;
        f5895e = d;
    }

    @Override
    public void onClick(View view) {
        showSongsInfoAdapter.showSongsInfo.songName = f5892b;
        showSongsInfoAdapter.showSongsInfo.songID = songID;
        showSongsInfoAdapter.showSongsInfo.score = f5894d;
        showSongsInfoAdapter.showSongsInfo.nandu = f5895e;
        new ShowSongsInfoPlayTask(showSongsInfoAdapter.showSongsInfo).execute();
    }
}
