package ly.pp.justpiano3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public final class RoomMiniPeopleAdapter extends BaseAdapter {
    private Context context;
    private int[] f5805b;

    RoomMiniPeopleAdapter(Context context, int[] iArr) {
        this.context = context;
        f5805b = iArr;
    }

    @Override
    public final int getCount() {
        return f5805b.length;
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(Consts.sex[f5805b[i]]);
        return imageView;
    }
}
