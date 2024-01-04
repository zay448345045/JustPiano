package ly.pp.justpiano3.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLPlayKeyboardRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.entity.OLKeyboardState;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public final class KeyboardPlayerImageAdapter extends BaseAdapter {
    private final OLPlayKeyboardRoom olPlayKeyboardRoom;
    private final List<Bundle> playerList;
    private final LayoutInflater layoutInflater;

    public KeyboardPlayerImageAdapter(List<Bundle> list, OLPlayKeyboardRoom olPlayKeyboardRoom) {
        layoutInflater = olPlayKeyboardRoom.getLayoutInflater();
        playerList = list;
        this.olPlayKeyboardRoom = olPlayKeyboardRoom;
    }

    @Override
    public int getCount() {
        return playerList.size();
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
            view = layoutInflater.inflate(R.layout.ol_keyboard_player_view, null);
        } else if (viewGroup.getChildCount() != i || i >= playerList.size()) {
            return view;
        }
        String userName = playerList.get(i).getString("N");
        String userStatus = playerList.get(i).getString("IR");
        String positionStatus = playerList.get(i).getString("IH");
        int color = playerList.get(i).getInt("IV");
        String familyID = playerList.get(i).getString("I");
        ImageView modView = view.findViewById(R.id.ol_player_mod);
        ImageView soundView = view.findViewById(R.id.ol_player_sound);
        OLKeyboardState olKeyboardState = olPlayKeyboardRoom.olKeyboardStates.get(i);
        if (olKeyboardState != null) {
            soundView.setImageResource(olKeyboardState.getMuted() ? R.drawable.stop : R.drawable.null_pic);
        }
        if (Objects.equals(positionStatus, "O")) {
            modView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_none.webp"));
            return view;
        } else if (Objects.equals(positionStatus, "C")) {
            modView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_close.webp"));
            return view;
        }
        TextView statusTextView = view.findViewById(R.id.ol_ready_text);
        TextView levelTextView = view.findViewById(R.id.ol_player_level);
        TextView clTextView = view.findViewById(R.id.ol_player_class);
        TextView clNameTextView = view.findViewById(R.id.ol_player_clname);
        TextView autoConnectWaitView = view.findViewById(R.id.ol_player_wait);
        ImageView trousersView = view.findViewById(R.id.ol_player_trousers);
        ImageView jacketView = view.findViewById(R.id.ol_player_jacket);
        ImageView shoesView = view.findViewById(R.id.ol_player_shoes);
        ImageView hairView = view.findViewById(R.id.ol_player_hair);
        ImageView eyeView = view.findViewById(R.id.ol_player_eye);
        ImageView coupleView = view.findViewById(R.id.ol_player_couple);
        ImageView familyView = view.findViewById(R.id.ol_player_family);
        int lv = playerList.get(i).getInt("LV");
        int cl = playerList.get(i).getInt("CL");
        if (OLBaseActivity.kitiName.equals(userName)) {
            olPlayKeyboardRoom.lv = lv;
            olPlayKeyboardRoom.cl = cl;
            olPlayKeyboardRoom.positionStatus = positionStatus;
        }
        int coupleType = playerList.get(i).getInt("CP");
        if (coupleType >= 0 && coupleType <= 3) {
            coupleView.setImageResource(Consts.couples[coupleType]);
        }
        ImageLoadUtil.setFamilyImageBitmap(olPlayKeyboardRoom, familyID, familyView);
        autoConnectWaitView.setVisibility("W".equals(userStatus) ? View.VISIBLE : View.INVISIBLE);
        if (!"H".equals(positionStatus)) {
            if ("R".equals(userStatus)) {
                statusTextView.setText("准备");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.online));
            } else if ("N".equals(userStatus)) {
                statusTextView.setText("");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.online));
            }
        } else {
            statusTextView.setText("房主");
            statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.exit));
        }
        if ("B".equals(userStatus)) {
            statusTextView.setText("后台");
            statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.green_y));
        }
        levelTextView.setText("LV." + lv);
        clTextView.setText("CL." + cl);
        clTextView.setTextColor(ContextCompat.getColor(olPlayKeyboardRoom, Consts.colors[cl]));
        clNameTextView.setText(Consts.nameCL[cl]);
        clNameTextView.setTextColor(ContextCompat.getColor(olPlayKeyboardRoom, Consts.colors[cl]));
        ImageLoadUtil.setUserDressImageBitmap(olPlayKeyboardRoom, Objects.equals(playerList.get(i).getString("S"), "f") ? "f" : "m",
                playerList.get(i).getInt("TR"), playerList.get(i).getInt("JA"),
                playerList.get(i).getInt("HA"), playerList.get(i).getInt("EY"),
                playerList.get(i).getInt("SH"), modView, trousersView, jacketView, hairView, eyeView, shoesView);
        TextView userNameTextView = view.findViewById(R.id.ol_player_name);
        userNameTextView.setText(userName);
        ImageView midiView = view.findViewById(R.id.ol_player_midi);
        if (olKeyboardState != null) {
            midiView.setVisibility(olKeyboardState.getMidiKeyboardOn() ? View.VISIBLE : View.INVISIBLE);
        }
        userNameTextView.setBackgroundResource(color == 0 ? R.drawable.back_puased : ColorUtil.userColor[color]);
        modView.setBackgroundResource(ColorUtil.filledUserColor[color]);
        return view;
    }
}
