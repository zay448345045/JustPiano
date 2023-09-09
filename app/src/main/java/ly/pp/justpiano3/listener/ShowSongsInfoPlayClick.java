package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.adapter.ShowSongsInfoAdapter;
import ly.pp.justpiano3.task.ShowSongsInfoPlayTask;

public final class ShowSongsInfoPlayClick implements OnClickListener {

    private final ShowSongsInfoAdapter showSongsInfoAdapter;
    private String f5892b;
    private String songID;
    private int f5894d;
    private double f5895e;
    private final Intent intent;

    public ShowSongsInfoPlayClick(ShowSongsInfoAdapter showSongsInfoAdapter, String str, String l, int i, double d) {
        this.showSongsInfoAdapter = showSongsInfoAdapter;
        f5892b = str;
        songID = l;
        f5894d = i;
        f5895e = d;
        this.intent = new Intent();
        intent.setClass(showSongsInfoAdapter.showSongsInfo, PianoPlay.class);
    }

    public ShowSongsInfoPlayClick(ShowSongsInfoAdapter showSongsInfoAdapter, String songId, Intent intent) {
        this.showSongsInfoAdapter = showSongsInfoAdapter;
        songID = songId;
        this.intent = intent;
    }

    @Override
    public void onClick(View view) {
        showSongsInfoAdapter.showSongsInfo.songName = f5892b;
        showSongsInfoAdapter.showSongsInfo.songID = songID;
        showSongsInfoAdapter.showSongsInfo.score = f5894d;
        showSongsInfoAdapter.showSongsInfo.nandu = f5895e;
        new ShowSongsInfoPlayTask(showSongsInfoAdapter.showSongsInfo, intent).execute();
    }
}
