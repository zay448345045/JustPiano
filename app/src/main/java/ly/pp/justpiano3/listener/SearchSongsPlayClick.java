package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.adapter.SearchSongsAdapter;
import ly.pp.justpiano3.task.SearchSongsPlayTask;

public final class SearchSongsPlayClick implements OnClickListener {

    private final SearchSongsAdapter searchSongsAdapter;
    private String songName;
    private final String songId;
    private int topScore;
    private double degree;
    private final Intent intent;

    public SearchSongsPlayClick(SearchSongsAdapter searchSongsAdapter, String songName, String songId, int topScore, double degree) {
        this.searchSongsAdapter = searchSongsAdapter;
        this.songName = songName;
        this.songId = songId;
        this.topScore = topScore;
        this.degree = degree;
        intent = new Intent(searchSongsAdapter.searchSongs, PianoPlay.class);
    }

    public SearchSongsPlayClick(SearchSongsAdapter searchSongsAdapter, String songId, Intent intent) {
        this.searchSongsAdapter = searchSongsAdapter;
        this.songId = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        searchSongsAdapter.searchSongs.songName = songName;
        searchSongsAdapter.searchSongs.songId = songId;
        searchSongsAdapter.searchSongs.topScore = topScore;
        searchSongsAdapter.searchSongs.degree = degree;
        new SearchSongsPlayTask(searchSongsAdapter.searchSongs, intent).execute();
    }
}
