package ly.pp.justpiano3.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.constant.Consts;

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
        if (Objects.equals(bonusGet, "0")) {
            bonusGetText.setText("未领");
            bonusGetText.setBackgroundColor(ContextCompat.getColor(olPlayHallRoom, R.color.exit));
        } else {
            bonusGetText.setText("已领");
            bonusGetText.setBackgroundColor(ContextCompat.getColor(olPlayHallRoom, R.color.online));
        }
        view.findViewById(R.id.ol_dailytime_layout).setBackgroundResource(OLBaseActivity.kitiName.equals(name)
                ? R.drawable.selector_list_a : R.drawable.selector_list_c);
        if (i >= 0 && i < Consts.challengePositionColor.length) {
            nameText.setTextColor(Consts.challengePositionColor[i]);
            onlineTimeText.setTextColor(Consts.challengePositionColor[i]);
            dailyTimeBonusText.setTextColor(Consts.challengePositionColor[i]);
        } else {
            nameText.setTextColor(Color.WHITE);
            onlineTimeText.setTextColor(Color.WHITE);
            dailyTimeBonusText.setTextColor(Color.WHITE);
        }
        return view;
    }
}
