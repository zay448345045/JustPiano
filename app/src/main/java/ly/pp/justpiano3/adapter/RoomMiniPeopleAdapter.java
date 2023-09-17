package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import ly.pp.justpiano3.constant.Consts;

public final class RoomMiniPeopleAdapter extends BaseAdapter {
    private final Context context;
    private final int[] f5805b;

    RoomMiniPeopleAdapter(Context context, int[] iArr) {
        this.context = context;
        f5805b = iArr;
    }

    @Override
    public int getCount() {
        return f5805b.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(Consts.sex[f5805b[i]]);
        return imageView;
    }
}
