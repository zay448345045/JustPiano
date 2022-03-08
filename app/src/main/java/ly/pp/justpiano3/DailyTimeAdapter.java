package ly.pp.justpiano3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public final class DailyTimeAdapter extends BaseAdapter {
    OLPlayHallRoom olPlayHallRoom;
    private final List<HashMap> list;
    private final LayoutInflater li;

    DailyTimeAdapter(List<HashMap> list, LayoutInflater layoutInflater, OLPlayHallRoom olPlayHallRoom) {
        this.list = list;
        li = layoutInflater;
        this.olPlayHallRoom = olPlayHallRoom;
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
            view = li.inflate(R.layout.ol_c_dailytime_view, null);
        }
        if (list.size() == 0) {
            return view;
        }
        String name = (String) list.get(i).get("N");
        if (name == null) {
            return view;
        }
        String bonus = (String) list.get(i).get("B");
        String bonusGet = (String) list.get(i).get("G");
        String onlineTime = (String) list.get(i).get("T");
        TextView onlineTimeText = view.findViewById(R.id.ol_online_time);
        onlineTimeText.setText(onlineTime + "分钟");
        TextView nameText = view.findViewById(R.id.ol_user_name);
        nameText.setText(name);
        TextView dailyTimeBonusText = view.findViewById(R.id.ol_dailytime_bonus);
        dailyTimeBonusText.setText(bonus);
        TextView bonusGetText = view.findViewById(R.id.ol_bonus_get);
        if (bonusGet.equals("0")) {
            bonusGetText.setText("未领");
            bonusGetText.setBackgroundColor(olPlayHallRoom.getResources().getColor(R.color.exit));
        } else {
            bonusGetText.setText("已领");
            bonusGetText.setBackgroundColor(olPlayHallRoom.getResources().getColor(R.color.online));
        }
        if (JPApplication.kitiName.equals(name)) {
            view.findViewById(R.id.ol_dailytime_layout).setBackgroundResource(R.color.yellow_d);
        } else {
            view.findViewById(R.id.ol_dailytime_layout).setBackgroundResource(R.drawable.selector_list_c);
        }
        switch (i) {
            case 0:
                nameText.setTextColor(0xFFFFD700);
                onlineTimeText.setTextColor(0xFFFFD700);
                dailyTimeBonusText.setTextColor(0xFFFFD700);
                break;
            case 1:
                nameText.setTextColor(0xFFC0C0C0);
                onlineTimeText.setTextColor(0xFFC0C0C0);
                dailyTimeBonusText.setTextColor(0xFFC0C0C0);
                break;
            case 2:
                nameText.setTextColor(0xFFD2B48C);
                onlineTimeText.setTextColor(0xFFD2B48C);
                dailyTimeBonusText.setTextColor(0xFFD2B48C);
                break;
            default:
                nameText.setTextColor(0xFFFFFFFF);
                onlineTimeText.setTextColor(0xFFFFFFFF);
                dailyTimeBonusText.setTextColor(0xFFFFFFFF);
        }
        return view;
    }
}
