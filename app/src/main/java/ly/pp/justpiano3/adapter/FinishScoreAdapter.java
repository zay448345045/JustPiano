package ly.pp.justpiano3.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.Consts;

import java.util.List;

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
        view.setKeepScreenOn(true);
        String str = (String) list.get(i).get("N");
        if (str == null) {
            return view;
        }
        String str2 = (String) list.get(i).get("I");
        String str3 = (String) list.get(i).get("SC");
        String str4 = (String) list.get(i).get("P");
        String str5 = (String) list.get(i).get("C");
        String str6 = (String) list.get(i).get("G");
        String str7 = (String) list.get(i).get("B");
        String str8 = (String) list.get(i).get("M");
        String str9 = (String) list.get(i).get("T");
        String str10 = (String) list.get(i).get("E");
        int intValue = Integer.parseInt((String) list.get(i).get("GR"));
        ((TextView) view.findViewById(R.id.ol_name)).setText(str);
        ((TextView) view.findViewById(R.id.ol_perfect)).setText(str4);
        ((TextView) view.findViewById(R.id.ol_cool)).setText(str5);
        ((TextView) view.findViewById(R.id.ol_great)).setText(str6);
        ((TextView) view.findViewById(R.id.ol_bad)).setText(str7);
        ((TextView) view.findViewById(R.id.ol_miss)).setText(str8);
        ((TextView) view.findViewById(R.id.ol_combo)).setText(str9);
        TextView textView = view.findViewById(R.id.ol_total);
        if (str2.equals("P")) {
            textView.setText("弹奏中");
        } else {
            textView.setText(str3);
        }
        ((TextView) view.findViewById(R.id.ol_exp)).setText(str10);
        if (type == 2 && intValue > 0) {
            view.setBackgroundResource(Consts.groupModeColor[(intValue - 1) / 2]);
        } else if (type == 1 && intValue > 0) {
            view.setBackgroundResource(Consts.groupModeColor[intValue - 1]);
        }
        return view;
    }
}
