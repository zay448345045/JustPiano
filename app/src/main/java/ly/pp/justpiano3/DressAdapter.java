package ly.pp.justpiano3;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public final class DressAdapter extends BaseAdapter {
    private List<Bitmap> list;
    private LayoutInflater layoutInflater;

    DressAdapter(List<Bitmap> arrayList, OLPlayDressRoom oLPlayDressRoom) {
        layoutInflater = oLPlayDressRoom.getLayoutInflater();
        list = arrayList;
    }

    @Override
    public final int getCount() {
        return list.size();
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_dres_view, null);
        }
        view.setKeepScreenOn(true);
        ((ImageView) view.findViewById(R.id.ol_dress_img)).setImageBitmap(list.get(i));
        return view;
    }
}
