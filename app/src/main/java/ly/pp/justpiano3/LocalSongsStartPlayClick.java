package ly.pp.justpiano3;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;

final class LocalSongsStartPlayClick implements OnClickListener {
    private final LocalSongsAdapter localSongsAdapter;
    private final String path;
    private final String name;
    private final double nandu;
    private final double leftnandu;
    private final int songstime;
    private final int score;

    LocalSongsStartPlayClick(LocalSongsAdapter LocalSongsAdapter, Cursor cursor) {
        localSongsAdapter = LocalSongsAdapter;
        path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
        name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        nandu = cursor.getFloat(cursor.getColumnIndexOrThrow("diff"));
        leftnandu = cursor.getFloat(cursor.getColumnIndexOrThrow("Ldiff"));
        songstime = cursor.getInt(cursor.getColumnIndexOrThrow("length"));
        score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
    }

    @Override
    public void onClick(View view) {
        int i = 0;
        if (localSongsAdapter.melodyselect.playSongs != null) {
            localSongsAdapter.melodyselect.playSongs.isPlayingSongs = false;
            localSongsAdapter.melodyselect.playSongs = null;
        }
        Intent intent = new Intent();
        intent.putExtra("head", 0);
        intent.putExtra("path", path);
        intent.putExtra("name", name);
        intent.putExtra("nandu", nandu);
        intent.putExtra("leftnandu", leftnandu);
        intent.putExtra("songstime", songstime);
        intent.putExtra("score", score);
        intent.putExtra("isrecord", localSongsAdapter.melodyselect.isRecord.isChecked());
        String str = "hand";
        if (localSongsAdapter.melodyselect.isLeftHand.isChecked()) {
            i = 1;
        }
        intent.putExtra(str, i);
        intent.setClass(localSongsAdapter.melodyselect, PianoPlay.class);
        localSongsAdapter.melodyselect.startActivity(intent);
    }
}
