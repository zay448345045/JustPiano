package ly.pp.justpiano3.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.utils.ColorUtil;

public final class RoomTitleAdapter extends BaseAdapter {
    private List<Bundle> list;
    private final LayoutInflater layoutInflater;
    private final OLPlayHall olPlayHall;

    public RoomTitleAdapter(List<Bundle> list, LayoutInflater layoutInflater, OLPlayHall olPlayHall) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        this.olPlayHall = olPlayHall;
    }

    public void updateList(List<Bundle> list) {
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_room_view, null);
        }
        view.setKeepScreenOn(true);
        String name = list.get(i).getString("N");
        int i2 = list.get(i).getInt("V");
        byte b = list.get(i).getByte("I");
        ((TextView) view.findViewById(R.id.ol_room_id)).setText(String.valueOf(b));
        boolean isFull = list.get(i).getBoolean("IF");
        int i3 = list.get(i).getInt("IP");
        int i4 = list.get(i).getInt("D");
        int i5 = list.get(i).getInt("PA");
        ((GridView) view.findViewById(R.id.ol_player_grid)).setAdapter(new RoomMiniPeopleAdapter(olPlayHall, list.get(i).getIntArray("UA")));
        TextView textView = view.findViewById(R.id.ol_room_name);
        textView.setText(name);
        textView.setBackgroundResource(ColorUtil.userColor[i2]);
        textView.setOnClickListener(v -> olPlayHall.loadInRoomUserInfo(b));
        Button button = view.findViewById(R.id.ol_getin_button);
        if (i3 == 1) {
            button.setText("弹奏中");
            button.setOnClickListener(null);
        } else if (isFull) {
            button.setText("已满");
            button.setOnClickListener(null);
        } else if (i5 == 1) {
            button.setText("加密");
            button.setOnClickListener(v -> olPlayHall.enterRoomHandle(i5, b));
        } else if (i5 == 0) {
            button.setText("进入");
            button.setOnClickListener(v -> olPlayHall.enterRoomHandle(i5, b));
        }
        TextView roomModeTextView = view.findViewById(R.id.ol_room_mode);
        RoomModeEnum roomMode = RoomModeEnum.ofCode(i4, RoomModeEnum.NORMAL);
        roomModeTextView.setBackgroundResource(Consts.groupModeColor[roomMode.getCode()]);
        roomModeTextView.setText(roomMode.getSimpleName());
        return view;
    }
}
