package ly.pp.justpiano3.listener;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.WaterfallActivity;

public final class PlayWaterfallClick implements OnClickListener {
    private final Context context;
    private final String songsPath;

    public PlayWaterfallClick(Context context, Cursor cursor) {
        this.context = context;
        this.songsPath = cursor.getString(cursor.getColumnIndexOrThrow("path"));
    }

    public void onClick(View view) {
        ((JPApplication) (context.getApplicationContext())).stopPlaySong();
        Intent intent = new Intent();
        intent.putExtra("songPath", songsPath);
        intent.setClass(context, WaterfallActivity.class);
        context.startActivity(intent);
    }
}
