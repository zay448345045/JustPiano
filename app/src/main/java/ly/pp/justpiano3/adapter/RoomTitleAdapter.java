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
        byte roomId = list.get(i).getByte("I");
        ((TextView) view.findViewById(R.id.ol_room_id)).setText(String.valueOf(roomId));
        int hasPassword = list.get(i).getInt("PA");
        ((GridView) view.findViewById(R.id.ol_player_grid)).setAdapter(
                new RoomMiniPeopleAdapter(olPlayHall, list.get(i).getIntArray("UA")));
        TextView textView = view.findViewById(R.id.ol_room_name);
        textView.setText(list.get(i).getString("N"));
        textView.setBackgroundResource(ColorUtil.userColor[list.get(i).getInt("V")]);
        textView.setOnClickListener(v -> olPlayHall.loadInRoomUserInfo(roomId));
        Button button = view.findViewById(R.id.ol_getin_button);
        if (list.get(i).getInt("IP") == 1) {
            button.setText("弹奏中");
            button.setOnClickListener(null);
        } else if (list.get(i).getBoolean("IF")) {
            button.setText("已满");
            button.setOnClickListener(null);
        } else if (hasPassword == 1) {
            button.setText("加密");
            button.setOnClickListener(v -> olPlayHall.enterRoomHandle(hasPassword, roomId));
        } else if (hasPassword == 0) {
            button.setText("进入");
            button.setOnClickListener(v -> olPlayHall.enterRoomHandle(hasPassword, roomId));
        }
        TextView roomModeTextView = view.findViewById(R.id.ol_room_mode);
        RoomModeEnum roomMode = RoomModeEnum.ofCode(list.get(i).getInt("D"), RoomModeEnum.NORMAL);
        roomModeTextView.setBackgroundResource(Consts.groupModeColor[roomMode.getCode()]);
        roomModeTextView.setText(roomMode.getSimpleName());
        return view;
    }
}
