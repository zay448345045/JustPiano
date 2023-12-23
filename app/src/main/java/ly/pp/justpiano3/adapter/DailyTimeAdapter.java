package ly.pp.justpiano3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHallRoom;

public final class DailyTimeAdapter extends BaseAdapter {
    OLPlayHallRoom olPlayHallRoom;
    private final List<Map<String, String>> list;
    private final LayoutInflater layoutInflater;

    public DailyTimeAdapter(List<Map<String, String>> list, LayoutInflater layoutInflater, OLPlayHallRoom olPlayHallRoom) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        this.olPlayHallRoom = olPlayHallRoom;
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
            view = layoutInflater.inflate(R.layout.ol_c_dailytime_view, null);
        }
        if (list.size() == 0) {
            return view;
        }
        String name = list.get(i).get("N");
        if (name == null) {
            return view;
        }
        String bonus = list.get(i).get("B");
        String bonusGet = list.get(i).get("G");
        String onlineTime = list.get(i).get("T");
        TextView onlineTimeText = view.findViewById(R.id.ol_online_time);
        onlineTimeText.setText(onlineTime + "分钟");
        TextView nameText = view.findViewById(R.id.ol_user_name);
        nameText.setText(name);
        TextView dailyTimeBonusText = view.findViewById(R.id.ol_dailytime_bonus);
        dailyTimeBonusText.setText(bonus);
        TextView bonusGetText = view.findViewById(R.id.ol_bonus_get);
        if (bonusGet.equals("0")) {
            bonusGetText.setText("未领");
            bonusGetText.setBackgroundColor(ContextCompat.getColor(olPlayHallRoom, R.color.exit));
        } else {
            bonusGetText.setText("已领");
            bonusGetText.setBackgroundColor(ContextCompat.getColor(olPlayHallRoom, R.color.online));
        }
        if (JPApplication.kitiName.equals(name)) {
            view.findViewById(R.id.ol_dailytime_layout).setBackgroundResource(R.drawable.selector_list_a);
        } else {
            view.findViewById(R.id.ol_dailytime_layout).setBackgroundResource(R.drawable.selector_list_c);
        }
        switch (i) {
            case 0 -> {
                nameText.setTextColor(0xFFFFD700);
                onlineTimeText.setTextColor(0xFFFFD700);
                dailyTimeBonusText.setTextColor(0xFFFFD700);
            }
            case 1 -> {
                nameText.setTextColor(0xFFC0C0C0);
                onlineTimeText.setTextColor(0xFFC0C0C0);
                dailyTimeBonusText.setTextColor(0xFFC0C0C0);
            }
            case 2 -> {
                nameText.setTextColor(0xFFD2B48C);
                onlineTimeText.setTextColor(0xFFD2B48C);
                dailyTimeBonusText.setTextColor(0xFFD2B48C);
            }
            default -> {
                nameText.setTextColor(0xFFFFFFFF);
                onlineTimeText.setTextColor(0xFFFFFFFF);
                dailyTimeBonusText.setTextColor(0xFFFFFFFF);
            }
        }
        return view;
    }
}
