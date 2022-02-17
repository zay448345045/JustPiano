package ly.pp.justpiano3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

public final class RoomTitleAdapter extends BaseAdapter {
    private List<Bundle> list;
    private final LayoutInflater layoutInflater;
    private final OLPlayHall olPlayHall;

    RoomTitleAdapter(List<Bundle> list, LayoutInflater layoutInflater, OLPlayHall olPlayHall) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        this.olPlayHall = olPlayHall;
    }

    final void updateList(List<Bundle> list) {
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_room_view, null);
        }
        view.setKeepScreenOn(true);
        CharSequence string = list.get(i).getString("N");
        int i2 = list.get(i).getInt("V");
        byte b = list.get(i).getByte("I");
        ((TextView) view.findViewById(R.id.ol_room_id)).setText(String.valueOf(b));
        boolean valueOf = list.get(i).getBoolean("IF");
        int i3 = list.get(i).getInt("IP");
        int i4 = list.get(i).getInt("D");
        int i5 = list.get(i).getInt("PA");
        ((GridView) view.findViewById(R.id.ol_player_grid)).setAdapter(new RoomMiniPeopleAdapter(olPlayHall, list.get(i).getIntArray("UA")));
        TextView textView = view.findViewById(R.id.ol_room_name);
        textView.setText(string);
        textView.setBackgroundResource(Consts.kuang[i2]);
        textView.setOnClickListener(v -> olPlayHall.loadInRoomUserInfo(b));
        Button button = view.findViewById(R.id.ol_getin_button);
        if (i3 == 1) {
            button.setText("弹奏中");
        } else if (valueOf) {
            button.setText("已满");
        } else if (i5 == 1) {
            button.setText("加密");
            button.setOnClickListener(v -> olPlayHall.mo2826a(i5, b));
        } else if (i5 == 0) {
            button.setText("进入");
            button.setOnClickListener(v -> olPlayHall.mo2826a(i5, b));
        }
        textView = view.findViewById(R.id.ol_room_mode);
        string = "普通";
        switch (i4) {
            case 0:
                string = "普通";
                break;
            case 1:
                string = "组队";
                break;
            case 2:
                string = "双人";
                break;
            case 3:
                string = "键盘";
                break;
        }
        if (i4 >= 0 && i4 <= 3) {
            textView.setBackgroundResource(Consts.groupModeColor[i4]);
        }
        textView.setText(string);
        return view;
    }
}
