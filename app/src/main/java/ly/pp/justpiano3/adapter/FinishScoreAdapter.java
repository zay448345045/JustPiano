package ly.pp.justpiano3.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.Consts;

public final class FinishScoreAdapter extends BaseAdapter {
    private final List<Bundle> list;
    private final LayoutInflater layoutInflater;
    private final int type;

    public FinishScoreAdapter(List<Bundle> list, LayoutInflater layoutInflater, int i) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        type = i;
    }

    @Override
    public int getCount() {
        return list.size();
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
            view = layoutInflater.inflate(R.layout.ol_c_finish_view, null);
        }
        String userName = (String) list.get(i).get("N");
        if (userName == null) {
            return view;
        }
        int color = Integer.parseInt((String) Objects.requireNonNull(list.get(i).get("GR")));
        ((TextView) view.findViewById(R.id.ol_name)).setText(userName);
        ((TextView) view.findViewById(R.id.ol_perfect)).setText((String) list.get(i).get("P"));
        ((TextView) view.findViewById(R.id.ol_cool)).setText((String) list.get(i).get("C"));
        ((TextView) view.findViewById(R.id.ol_great)).setText((String) list.get(i).get("G"));
        ((TextView) view.findViewById(R.id.ol_bad)).setText((String) list.get(i).get("B"));
        ((TextView) view.findViewById(R.id.ol_miss)).setText((String) list.get(i).get("M"));
        ((TextView) view.findViewById(R.id.ol_combo)).setText((String) list.get(i).get("T"));
        ((TextView) view.findViewById(R.id.ol_total)).setText
                (Objects.equals(list.get(i).get("I"), "P") ? "弹奏中" : (String) list.get(i).get("SC"));
        ((TextView) view.findViewById(R.id.ol_exp)).setText((String) list.get(i).get("E"));
        if (type == 2 && color > 0) {
            view.setBackgroundResource(Consts.groupModeColor[(color - 1) / 2]);
        } else if (type == 1 && color > 0) {
            view.setBackgroundResource(Consts.groupModeColor[color - 1]);
        }
        return view;
    }
}
