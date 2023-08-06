package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.listener.AddSongsFavoClick;
import ly.pp.justpiano3.listener.LocalSongsStartPlayClick;
import ly.pp.justpiano3.listener.PlaySongsClick;
import ly.pp.justpiano3.view.ScrollText;

public final class LocalSongsAdapter extends CursorAdapter {
    public final MelodySelect melodyselect;

    public LocalSongsAdapter(MelodySelect melodySelect, Context context, Cursor cursor) {
        super(context, cursor, true);
        melodyselect = melodySelect;
    }

    public static void m3966a(LocalSongsAdapter LocalSongsAdapter, int i) {
        switch (i) {
            case 0:
                LocalSongsAdapter.melodyselect.mo2784a(LocalSongsAdapter.melodyselect.sqlitedatabase.query("jp_data", null, "isfavo = 1 AND " + LocalSongsAdapter.melodyselect.f4238O + " AND ishot=0 AND " + LocalSongsAdapter.melodyselect.f4238O, null, null, null, LocalSongsAdapter.melodyselect.sortStr));
                return;
            case 1:
            case 4:
            case 2:
            case 6:
            case 5:
            case 3:
                LocalSongsAdapter.melodyselect.mo2784a(LocalSongsAdapter.melodyselect.sqlitedatabase.query("jp_data", null, "ishot = 0 AND " + LocalSongsAdapter.melodyselect.f4238O + " AND item=?", new String[]{LocalSongsAdapter.melodyselect.f4263s}, null, null, LocalSongsAdapter.melodyselect.sortStr));
                return;
            case 7:
                LocalSongsAdapter.melodyselect.mo2784a(LocalSongsAdapter.melodyselect.sqlitedatabase.query("jp_data", null, "ishot = 1 AND " + LocalSongsAdapter.melodyselect.f4238O + " AND isfavo=0", new String[]{LocalSongsAdapter.melodyselect.f4263s}, null, null, LocalSongsAdapter.melodyselect.sortStr));
                return;
            default:
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view.setTag(cursor.getPosition());
        ImageView imageView = view.findViewById(R.id.play_image);
        ImageView imageView3 = view.findViewById(R.id.favor);
        view.findViewById(R.id.play).setOnClickListener(new LocalSongsStartPlayClick(this, cursor));
        imageView.setOnClickListener(new PlaySongsClick(this, cursor, view, context));
        imageView3.setOnClickListener(new AddSongsFavoClick(this, cursor, context));
        ScrollText scrollText = view.findViewById(R.id.s_n);
        scrollText.setText(cursor.getString(1));
        scrollText.setMovementMethod(ScrollingMovementMethod.getInstance());
        scrollText.setHorizontallyScrolling(true);
        TextView textView = view.findViewById(R.id.highscore);
        scrollText.setOnClickListener(new LocalSongsStartPlayClick(this, cursor));
        int i = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
        int i2 = cursor.getInt(cursor.getColumnIndexOrThrow("isnew"));
        textView.setText("最高分: " + (i <= 0 ? "0" : String.valueOf(i)));
        RatingBar ratingBar = view.findViewById(R.id.nandu);
        textView = view.findViewById(R.id.sound_time);
        int l = cursor.getInt(14);
        String str1 = l / 60 >= 10 ? "" + l / 60 : "0" + l / 60;
        String str2 = l % 60 >= 10 ? "" + l % 60 : "0" + l % 60;
        textView.setText(str1 + ":" + str2);
        textView = view.findViewById(R.id.nandu_1);
        float f = cursor.getFloat(11);
        if ((int) f == 10) {
            textView.setText(" 难度: 右手 10 ");
        } else {
            textView.setText(" 难度: 右手 " + f);
        }
        ratingBar.setNumStars(5);
        ratingBar.setClickable(false);
        ratingBar.setRating(f / 2);
        RatingBar ratingBar2 = view.findViewById(R.id.leftnandu);
        textView = view.findViewById(R.id.nandu_2);
        float j = cursor.getFloat(13);
        if ((int) j == 10) {
            textView.setText(" 左手 10");
        } else {
            textView.setText(" 左手 " + j);
        }
        ratingBar2.setNumStars(5);
        ratingBar2.setClickable(false);
        ratingBar2.setRating(j / 2);
        melodyselect.f4228E = view.findViewById(R.id.is_new);
        if (i2 == 1) {
            melodyselect.f4228E.setImageResource(R.drawable.s_new);
        } else {
            melodyselect.f4228E.setImageResource(R.drawable.null_pic);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View inflate = melodyselect.layoutInflater2.inflate(R.layout.c_view, null);
        inflate.setBackgroundResource(R.drawable.selector_list_c);
        return inflate;
    }
}
