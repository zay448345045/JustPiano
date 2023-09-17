package ly.pp.justpiano3.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ly.pp.justpiano3.R;

public final class MiniScoreAdapter extends BaseAdapter {
    private List<Bundle> list;
    private final LayoutInflater layoutInfalter;
    private final int roomMode;
    private final int[] originalRoomModeResource = new int[]{R.drawable.back_puased, R.drawable.v1_name, R.drawable.v6_name};
    private final int[] teamRoomModeResource = new int[]{R.drawable.back_puased, R.drawable.back_puased, R.drawable.v1_name, R.drawable.v1_name, R.drawable.v6_name, R.drawable.v6_name};

    public MiniScoreAdapter(List<Bundle> list, LayoutInflater layoutInflater, int i) {
        this.list = list;
        layoutInfalter = layoutInflater;
        roomMode = i;
    }

    public void changeList(List<Bundle> list) {
        this.list = list;
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
        String str;
        int i2;
        String str2;
        view = layoutInfalter.inflate(R.layout.ol_score_v, null);
        view.setKeepScreenOn(true);
        try {
            str2 = (String) list.get((byte) i).get("U");
            try {
                str = (String) list.get((byte) i).get("M");
            } catch (Exception e2) {
                e2.printStackTrace();
                view.setVisibility(View.GONE);
                return view;
            }
            try {
                i2 = Integer.parseInt((String) list.get((byte) i).get("G"));
            } catch (Exception e4) {
                e4.printStackTrace();
                view.setVisibility(View.GONE);
                return view;
            }
        } catch (Exception e5) {
            e5.printStackTrace();
            view.setVisibility(View.GONE);
            return view;
        }
        if (str2 == null || str2.isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            TextView textView = view.findViewById(R.id.ol_user_text);
            if (roomMode == 1 && i2 > 0) {
                textView.setBackgroundResource(originalRoomModeResource[i2 - 1]);
            } else if (roomMode == 2 && i2 > 0) {
                textView.setBackgroundResource(teamRoomModeResource[i2 - 1]);
            } else if (roomMode == 0) {
                if (i2 == 0) {
                    ((TextView) view.findViewById(R.id.ol_state_text)).setText("右     ");
                } else {
                    ((TextView) view.findViewById(R.id.ol_state_text)).setText("左     ");
                }
                textView.setBackgroundResource(originalRoomModeResource[0]);
            }
            textView.setText(str2);
            ((TextView) view.findViewById(R.id.ol_score_text)).setText(str);
        }
        return view;
    }
}
