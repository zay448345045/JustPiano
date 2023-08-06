package ly.pp.justpiano3;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public final class LocalSongsItemAdapter extends BaseAdapter {
    private final MelodySelect melodySelect;

    LocalSongsItemAdapter(MelodySelect melodySelect) {
        this.melodySelect = melodySelect;
    }

    @Override
    public int getCount() {
        return Consts.items.length;
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
        if (view == null) {
            view = melodySelect.layoutInflater1.inflate(R.layout.f_view, null);
        }
        view.setKeepScreenOn(true);
        ((TextView) view.findViewById(R.id.ol_s_p)).setText(Consts.items[i]);
        return view;
    }
}
