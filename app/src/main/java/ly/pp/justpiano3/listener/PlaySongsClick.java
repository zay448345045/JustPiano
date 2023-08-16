package ly.pp.justpiano3.listener;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import ly.pp.justpiano3.adapter.LocalSongsAdapter;

public final class PlaySongsClick implements OnClickListener {
    private final LocalSongsAdapter localSongsAdapter;
    private final View view;
    private final Context context;
    private final String songsPath;
    private final String songsName;

    public PlaySongsClick(LocalSongsAdapter localSongsAdapter, Cursor cursor, View view, Context ct) {
        this.localSongsAdapter = localSongsAdapter;
        this.view = view;
        context = ct;
        songsPath = cursor.getString(cursor.getColumnIndexOrThrow("path"));
        songsName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
    }

    public void onClick(View view) {
        if (songsPath.equals(localSongsAdapter.melodyselect.songsPath)) {
            localSongsAdapter.melodyselect.songsPath = "";
            return;
        }
        localSongsAdapter.melodyselect.songsPath = songsPath;
        localSongsAdapter.melodyselect.jpapplication.startPlaySongLocal(songsPath, localSongsAdapter.melodyselect, (Integer) this.view.getTag());
        Toast.makeText(context, "正在播放:《" + songsName + "》", Toast.LENGTH_SHORT).show();
    }
}
