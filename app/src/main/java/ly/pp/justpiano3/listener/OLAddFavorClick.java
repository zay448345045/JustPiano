package ly.pp.justpiano3.listener;

import android.content.ContentValues;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import ly.pp.justpiano3.adapter.OLRoomSongsAdapter;
import ly.pp.justpiano3.R;

public final class OLAddFavorClick implements OnClickListener {
    private final OLRoomSongsAdapter olRoomSongsAdapter;
    private final int f5592b;
    private final ImageView f5593c;
    private final String f5594d;

    public OLAddFavorClick(OLRoomSongsAdapter olRoomSongsAdapter, int i, ImageView imageView, String str) {
        this.olRoomSongsAdapter = olRoomSongsAdapter;
        f5592b = i;
        f5593c = imageView;
        f5594d = str;
    }

    @Override
    public void onClick(View view) {
        ContentValues contentValues = new ContentValues();
        if (f5592b == 0) {
            contentValues.put("isfavo", 1);
            f5593c.setImageResource(R.drawable.favor);
        } else {
            contentValues.put("isfavo", 0);
            f5593c.setImageResource(R.drawable.favor_1);
        }
        olRoomSongsAdapter.olPlayRoom.sqlitedatabase.update("jp_data", contentValues, "path = '" + f5594d + "'", null);
        contentValues.clear();
        olRoomSongsAdapter.olPlayRoom.changeCursor();
    }
}
