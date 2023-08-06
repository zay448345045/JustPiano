package ly.pp.justpiano3;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public final class OLMelodySelectTypeAdapter extends BaseAdapter {
    private final OLMelodySelect olMelodySelect;

    OLMelodySelectTypeAdapter(OLMelodySelect oLMelodySelect) {
        olMelodySelect = oLMelodySelect;
    }

    @Override
    public int getCount() {
        return Consts.items.length - 1;
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
            view = olMelodySelect.layoutInflater1.inflate(R.layout.ol_f_view, null);
        }
        view.setKeepScreenOn(true);
        TextView textView = view.findViewById(R.id.ol_s_p);
        textView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        textView.setText(Consts.items[i + 1]);
        return view;
    }
}
