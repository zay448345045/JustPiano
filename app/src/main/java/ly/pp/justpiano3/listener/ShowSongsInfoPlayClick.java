package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.adapter.ShowSongsInfoAdapter;
import ly.pp.justpiano3.task.ShowSongsInfoPlayTask;

public final class ShowSongsInfoPlayClick implements OnClickListener {

    private final ShowSongsInfoAdapter showSongsInfoAdapter;
    private String songName;
    private final String songId;
    private int score;
    private double degree;
    private final Intent intent;

    public ShowSongsInfoPlayClick(ShowSongsInfoAdapter showSongsInfoAdapter, String songName, String songId, int score, double degree) {
        this.showSongsInfoAdapter = showSongsInfoAdapter;
        this.songName = songName;
        this.songId = songId;
        this.score = score;
        this.degree = degree;
        intent = new Intent(showSongsInfoAdapter.showSongsInfo, PianoPlay.class);
    }

    public ShowSongsInfoPlayClick(ShowSongsInfoAdapter showSongsInfoAdapter, String songId, Intent intent) {
        this.showSongsInfoAdapter = showSongsInfoAdapter;
        this.songId = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        showSongsInfoAdapter.showSongsInfo.songName = songName;
        showSongsInfoAdapter.showSongsInfo.songId = songId;
        showSongsInfoAdapter.showSongsInfo.score = score;
        showSongsInfoAdapter.showSongsInfo.degree = degree;
        new ShowSongsInfoPlayTask(showSongsInfoAdapter.showSongsInfo, intent).execute();
    }
}
