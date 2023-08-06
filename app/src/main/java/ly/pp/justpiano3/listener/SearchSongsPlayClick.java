package ly.pp.justpiano3.listener;

import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.adapter.SearchSongsAdapter;
import ly.pp.justpiano3.task.SearchSongsPlayTask;

public final class SearchSongsPlayClick implements OnClickListener {

    private final SearchSongsAdapter searchSongsAdapter;
    private final String f5865b;
    private final String songID;
    private final int f5867d;
    private final double f5868e;

    public SearchSongsPlayClick(SearchSongsAdapter searchSongsAdapter, String str, String l, int i, double d) {
        this.searchSongsAdapter = searchSongsAdapter;
        f5865b = str;
        songID = l;
        f5867d = i;
        f5868e = d;
    }

    @Override
    public void onClick(View view) {
        searchSongsAdapter.searchSongs.f4949d = f5865b;
        searchSongsAdapter.searchSongs.songID = songID;
        searchSongsAdapter.searchSongs.f4954i = f5867d;
        searchSongsAdapter.searchSongs.f4953h = f5868e;
        new SearchSongsPlayTask(searchSongsAdapter.searchSongs).execute();
    }
}
