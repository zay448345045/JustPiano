package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.adapter.SearchSongsAdapter;
import ly.pp.justpiano3.task.SearchSongsPlayTask;

public final class SearchSongsPlayClick implements OnClickListener {

    private final SearchSongsAdapter searchSongsAdapter;
    private String f5865b;
    private String songID;
    private int f5867d;
    private double f5868e;
    private final Intent intent;

    public SearchSongsPlayClick(SearchSongsAdapter searchSongsAdapter, String str, String l, int i, double d) {
        this.searchSongsAdapter = searchSongsAdapter;
        f5865b = str;
        songID = l;
        f5867d = i;
        f5868e = d;
        this.intent = new Intent();
        intent.setClass(searchSongsAdapter.searchSongs, PianoPlay.class);
    }

    public SearchSongsPlayClick(SearchSongsAdapter searchSongsAdapter, String songId, Intent intent) {
        this.searchSongsAdapter = searchSongsAdapter;
        this.songID = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        searchSongsAdapter.searchSongs.f4949d = f5865b;
        searchSongsAdapter.searchSongs.songID = songID;
        searchSongsAdapter.searchSongs.f4954i = f5867d;
        searchSongsAdapter.searchSongs.f4953h = f5868e;
        new SearchSongsPlayTask(searchSongsAdapter.searchSongs, intent).execute();
    }
}
