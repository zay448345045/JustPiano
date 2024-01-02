package ly.pp.justpiano3.adapter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import ly.pp.justpiano3.activity.online.OLPlayRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.UnitConvertUtil;

public final class PlayerImageAdapter extends BaseAdapter {
    private final OLPlayRoom olPlayRoom;
    private final List<Bundle> playerList;
    private final LayoutInflater layoutInflater;

    public PlayerImageAdapter(List<Bundle> list, OLPlayRoom olPlayRoom) {
        layoutInflater = olPlayRoom.getLayoutInflater();
        playerList = list;
        this.olPlayRoom = olPlayRoom;
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
            view = layoutInflater.inflate(R.layout.ol_player_view, null);
        } else if (viewGroup.getChildCount() != i || i >= playerList.size()) {
            return view;
        }
        TextView statusTextView = view.findViewById(R.id.ol_ready_text);
        TextView levelTextView = view.findViewById(R.id.ol_player_level);
        TextView clTextView = view.findViewById(R.id.ol_player_class);
        TextView clNameTextView = view.findViewById(R.id.ol_player_clname);
        TextView autoConnectWaitView = view.findViewById(R.id.ol_player_wait);
        String userName = playerList.get(i).getString("N");
        String userStatus = playerList.get(i).getString("IR");
        String positionStatus = playerList.get(i).getString("IH");
        int color = playerList.get(i).getInt("IV");
        String familyID = playerList.get(i).getString("I");
        ImageView modView = view.findViewById(R.id.ol_player_mod);
        ImageView backgroundView = view.findViewById(R.id.ol_player_bg);
        ImageView trousersView = view.findViewById(R.id.ol_player_trousers);
        ImageView jacketView = view.findViewById(R.id.ol_player_jacket);
        ImageView shoesView = view.findViewById(R.id.ol_player_shoes);
        ImageView hairView = view.findViewById(R.id.ol_player_hair);
        ImageView eyeView = view.findViewById(R.id.ol_player_eye);
        ImageView coupleView = view.findViewById(R.id.ol_player_couple);
        ImageView familyView = view.findViewById(R.id.ol_player_family);
        int orientation = olPlayRoom.getResources().getConfiguration().orientation;
        DisplayMetrics displayMetrics = olPlayRoom.getResources().getDisplayMetrics();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (UnitConvertUtil.px2dp(olPlayRoom, displayMetrics.widthPixels) <= 360) {
                modView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                backgroundView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                trousersView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                jacketView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                shoesView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                hairView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                eyeView.getLayoutParams().width = displayMetrics.widthPixels / 3;
                modView.getLayoutParams().height = modView.getLayoutParams().width;
                backgroundView.getLayoutParams().height = backgroundView.getLayoutParams().width;
                trousersView.getLayoutParams().height = trousersView.getLayoutParams().width;
                jacketView.getLayoutParams().height = jacketView.getLayoutParams().width;
                shoesView.getLayoutParams().height = shoesView.getLayoutParams().width;
                hairView.getLayoutParams().height = hairView.getLayoutParams().width;
                eyeView.getLayoutParams().height = eyeView.getLayoutParams().width;
            } else {
                modView.getLayoutParams().height = displayMetrics.widthPixels / 6;
                backgroundView.getLayoutParams().height = displayMetrics.widthPixels / 6;
                trousersView.getLayoutParams().height = displayMetrics.widthPixels / 6;
                jacketView.getLayoutParams().height = displayMetrics.widthPixels / 6;
                shoesView.getLayoutParams().height = displayMetrics.widthPixels / 6;
                hairView.getLayoutParams().height = displayMetrics.widthPixels / 6;
                eyeView.getLayoutParams().height = displayMetrics.widthPixels / 6;
            }
        }
        if (positionStatus.equals("O")) {
            modView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_none.webp"));
            return view;
        } else if (positionStatus.equals("C")) {
            modView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_close.webp"));
            return view;
        }
        int lv = playerList.get(i).getInt("LV");
        int cl = playerList.get(i).getInt("CL");
        if (OLBaseActivity.kitiName.equals(userName)) {
            olPlayRoom.lv = lv;
            olPlayRoom.cl = cl;
            olPlayRoom.positionStatus = positionStatus;
            switch (RoomModeEnum.ofCode(olPlayRoom.getMode(), RoomModeEnum.NORMAL)) {
                case COUPLE -> olPlayRoom.currentHand = (playerList.get(i).getInt("GR") + 12) % 2;
                case TEAM -> olPlayRoom.currentHand = 0;
                case NORMAL -> olPlayRoom.currentHand = playerList.get(i).getInt("GR");
            }
            if ("H".equals(olPlayRoom.positionStatus)) {
                olPlayRoom.playButton.setText("开始");
            } else if ("R".equals(userStatus)) {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.ONCE);
                olPlayRoom.playButton.setText("取消");
            } else {
                SongPlay.setPlaySongsMode(PlaySongsModeEnum.ONCE);
                olPlayRoom.playButton.setText("准备");
            }
        }
        int coupleType = playerList.get(i).getInt("CP");
        if (coupleType >= 0 && coupleType < Consts.couples.length) {
            coupleView.setImageResource(Consts.couples[coupleType]);
        }
        ImageLoadUtil.setFamilyImageBitmap(olPlayRoom, familyID, familyView);
        if (!positionStatus.equals("H")) {
            if ("R".equals(userStatus)) {
                statusTextView.setText("准备");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            } else if ("N".equals(userStatus)) {
                statusTextView.setText("");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
        } else {
            statusTextView.setText("房主");
            statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.exit));
        }
        autoConnectWaitView.setVisibility("W".equals(userStatus) ? View.VISIBLE : View.INVISIBLE);
        switch (Objects.requireNonNull(userStatus)) {
            case "P" -> {
                statusTextView.setText("弹奏中");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
            case "F" -> {
                statusTextView.setText("查看成绩");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
            case "B" -> {
                statusTextView.setText("后台");
                statusTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.green_y));
            }
            default -> {
            }
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            levelTextView.setText(String.valueOf(lv));
            clTextView.setText(String.valueOf(cl));
        } else {
            levelTextView.setText("LV." + lv);
            clTextView.setText("CL." + cl);
        }
        clTextView.setTextColor(ContextCompat.getColor(olPlayRoom, Consts.colors[cl]));
        clNameTextView.setText(Consts.nameCL[cl]);
        clNameTextView.setTextColor(ContextCompat.getColor(olPlayRoom, Consts.colors[cl]));
        ImageLoadUtil.setUserDressImageBitmap(olPlayRoom, playerList.get(i).getString("S").equals("f") ? "f" : "m",
                playerList.get(i).getInt("TR"), playerList.get(i).getInt("JA"),
                playerList.get(i).getInt("HA"), playerList.get(i).getInt("EY"),
                playerList.get(i).getInt("SH"), modView, trousersView, jacketView, hairView, eyeView, shoesView);
        TextView userNameTextView = view.findViewById(R.id.ol_player_name);
        userNameTextView.setText(userName);
        TextView handTextView = view.findViewById(R.id.ol_player_hand);
        switch (RoomModeEnum.ofCode(olPlayRoom.getMode(), RoomModeEnum.NORMAL)) {
            case NORMAL -> {
                handTextView.setBackgroundResource(Consts.groupModeColor[0]);
                handTextView.setText(Consts.hand[(playerList.get(i).getInt("GR") + 12) % 2]);
            }
            case TEAM -> {
                handTextView.setText(Consts.groups[(playerList.get(i).getInt("GR")) - 1]);
                handTextView.setBackgroundResource(Consts.groupModeColor[(playerList.get(i).getInt("GR")) - 1]);
            }
            case COUPLE -> {
                handTextView.setText(Consts.hand[(playerList.get(i).getInt("GR") % 2)]);
                handTextView.setBackgroundResource(Consts.groupModeColor[(playerList.get(i).getInt("GR") - 1) / 2]);
            }
        }
        userNameTextView.setBackgroundResource(color == 0 ? R.drawable.back_puased : ColorUtil.userColor[color]);
        modView.setBackgroundResource(ColorUtil.filledUserColor[color]);
        return view;
    }
}
