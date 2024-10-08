package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import ly.pp.justpiano3.constant.Consts;

public final class RoomMiniPeopleAdapter extends BaseAdapter {
    private final Context context;
    private final int[] genderResource;

    RoomMiniPeopleAdapter(Context context, int[] genderResource) {
        this.context = context;
        this.genderResource = genderResource;
    }

    @Override
    public int getCount() {
        return genderResource.length;
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
        imageView.setImageResource(Consts.sex[genderResource[i]]);
        return imageView;
    }
}
