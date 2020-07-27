package ly.pp.justpiano3;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;

final class LocalSongsStartPlayClick implements OnClickListener {
    private final LocalSongsAdapter localSongsAdapter;
    private String f5165a;
    private String f5166b;
    private float f5167c;
    private float leftnandu;
    private int songstime;
    private int f5168d;

    LocalSongsStartPlayClick(LocalSongsAdapter LocalSongsAdapter, Cursor cursor) {
        localSongsAdapter = LocalSongsAdapter;
        f5165a = cursor.getString(cursor.getColumnIndexOrThrow("path"));
        f5166b = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        f5167c = cursor.getFloat(cursor.getColumnIndexOrThrow("diff"));
        leftnandu = cursor.getFloat(cursor.getColumnIndexOrThrow("Ldiff"));
        songstime = cursor.getInt(cursor.getColumnIndexOrThrow("length"));
        f5168d = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
    }

    @Override
    public final void onClick(View view) {
        int i = 0;
        if (localSongsAdapter.melodyselect.playSongs != null) {
            localSongsAdapter.melodyselect.playSongs.isPlayingSongs = false;
            localSongsAdapter.melodyselect.playSongs = null;
        }
        Intent intent = new Intent();
        intent.putExtra("head", 0);
        intent.putExtra("path", f5165a);
        intent.putExtra("name", f5166b);
        intent.putExtra("nandu", f5167c);
        intent.putExtra("leftnandu", leftnandu);
        intent.putExtra("songstime", songstime);
        intent.putExtra("score", f5168d);
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
