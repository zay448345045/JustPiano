package ly.pp.justpiano3;

import android.content.Context;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public final class OLRoomSongsAdapter extends CursorAdapter {
    final OLPlayRoom olPlayRoom;

    OLRoomSongsAdapter(OLPlayRoom om, Context context, Cursor cursor) {
        super(context, cursor, false);
        olPlayRoom = om;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView songName = view.findViewById(R.id.song_name);
        songName.setText(cursor.getString(cursor.getColumnIndex("name")));
        songName.setMovementMethod(ScrollingMovementMethod.getInstance());
        songName.setHorizontallyScrolling(true);
        ImageView imageView = view.findViewById(R.id.song_favor);
        String string = cursor.getString(cursor.getColumnIndexOrThrow("path"));
        RatingBar ratingBar = view.findViewById(R.id.song_degree);
        RatingBar ratingBar2 = view.findViewById(R.id.song_degree2);
        TextView time = view.findViewById(R.id.ol_sound_time);
        TextView textView = view.findViewById(R.id.nandu_1);
        TextView textView2 = view.findViewById(R.id.nandu_2);
        float f = cursor.getFloat(cursor.getColumnIndexOrThrow("diff"));
        float g = cursor.getFloat(cursor.getColumnIndexOrThrow("Ldiff"));
        int i = cursor.getInt(cursor.getColumnIndexOrThrow("isfavo"));
        int timeNum = cursor.getInt(cursor.getColumnIndexOrThrow("length"));
        if (i == 0) {
            imageView.setImageResource(R.drawable.favor_1);
        } else {
            imageView.setImageResource(R.drawable.favor);
        }
        imageView.setOnClickListener(new OLAddFavorClick(this, i, imageView, string));
        textView.setText(" 右手:" + f);
        textView2.setText(" 左手:" + g);
        time.setText((timeNum / 60 < 10 ? "0" : "") + (timeNum / 60) + ":" + (timeNum % 60 < 10 ? "0" : "") + (timeNum % 60));
        ratingBar.setNumStars(5);
        ratingBar.setClickable(false);
        ratingBar.setRating(f / 2);
        ratingBar2.setNumStars(5);
        ratingBar2.setClickable(false);
        ratingBar2.setRating(g / 2);
        songName.setOnClickListener(new OLChooseSongClick(this, string));
        view.setOnClickListener(new OLChooseSongClick(this, string));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.song_view, null);
        inflate.setBackgroundResource(R.drawable.selector_list_c);
        return inflate;
    }
}
