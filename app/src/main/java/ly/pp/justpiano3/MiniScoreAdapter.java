package ly.pp.justpiano3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public final class MiniScoreAdapter extends BaseAdapter {
    private List<Bundle> list;
    private LayoutInflater layoutInfalter;
    private int f5671d;
    private int[] f5672e = new int[]{R.drawable.back_puased, R.drawable.v1_name, R.drawable.v6_name};
    private int[] f5673f = new int[]{R.drawable.back_puased, R.drawable.back_puased, R.drawable.v1_name, R.drawable.v1_name, R.drawable.v6_name, R.drawable.v6_name};

    MiniScoreAdapter(List<Bundle> list, LayoutInflater layoutInflater, int i) {
        this.list = list;
        layoutInfalter = layoutInflater;
        f5671d = i;
    }

    final void mo3332a(List<Bundle> list) {
        this.list = list;
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
        String str;
        int i2;
        String str2;
        View inflate = layoutInfalter.inflate(R.layout.ol_score_v, null);
        inflate.setKeepScreenOn(true);
        try {
            str2 = (String) list.get((byte) i).get("U");
            try {
                str = (String) list.get((byte) i).get("M");
            } catch (Exception e2) {
                e2.printStackTrace();
                inflate.setVisibility(View.GONE);
                return inflate;
            }
            try {
                i2 = Integer.parseInt((String) list.get((byte) i).get("G"));
            } catch (Exception e4) {
                e4.printStackTrace();
                inflate.setVisibility(View.GONE);
                return inflate;
            }
        } catch (Exception e5) {
            e5.printStackTrace();
            inflate.setVisibility(View.GONE);

            return inflate;
        }
        if (str2 == null || str2.isEmpty()) {
            inflate.setVisibility(View.GONE);
        } else {
            TextView textView = inflate.findViewById(R.id.ol_user_text);
            if (f5671d == 1 && i2 > 0) {
                textView.setBackgroundResource(f5672e[i2 - 1]);
            } else if (f5671d == 2 && i2 > 0) {
                textView.setBackgroundResource(f5673f[i2 - 1]);
            } else if (f5671d == 0) {
                if (i2 == 0) {
                    ((TextView) inflate.findViewById(R.id.ol_state_text)).setText("右     ");
                } else {
                    ((TextView) inflate.findViewById(R.id.ol_state_text)).setText("左     ");
                }
                textView.setBackgroundResource(f5672e[0]);
            }
            textView.setText(str2);
            ((TextView) inflate.findViewById(R.id.ol_score_text)).setText(str);
        }
        return inflate;
    }
}
