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
        TextView readyTextView = view.findViewById(R.id.ol_ready_text);
        TextView levelTextView = view.findViewById(R.id.ol_player_level);
        TextView clTextView = view.findViewById(R.id.ol_player_class);
        TextView clNameTextView = view.findViewById(R.id.ol_player_clname);
        TextView autoConnectWaitView = view.findViewById(R.id.ol_player_wait);
        String userName = playerList.get(i).getString("N");
        String string2 = playerList.get(i).getString("S");
        String str = string2.equals("f") ? "f" : "m";
        String userStatus = playerList.get(i).getString("IR");
        String positionStatus = playerList.get(i).getString("IH");
        int i2 = playerList.get(i).getInt("IV");
        String familyID = playerList.get(i).getString("I");
        ImageView imageView = view.findViewById(R.id.ol_player_mod);
        ImageView imageView1 = view.findViewById(R.id.ol_player_bg);
        ImageView imageView2 = view.findViewById(R.id.ol_player_trousers);
        ImageView imageView3 = view.findViewById(R.id.ol_player_jacket);
        ImageView imageView4 = view.findViewById(R.id.ol_player_shoes);
        ImageView imageView5 = view.findViewById(R.id.ol_player_hair);
        ImageView imageView5e = view.findViewById(R.id.ol_player_eye);
        ImageView imageView6 = view.findViewById(R.id.ol_player_couple);
        ImageView imageView7 = view.findViewById(R.id.ol_player_family);
        int orientation = olPlayRoom.getResources().getConfiguration().orientation;
        DisplayMetrics dm = olPlayRoom.getResources().getDisplayMetrics();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (UnitConvertUtil.px2dp(olPlayRoom, dm.widthPixels) <= 360) {
                imageView.getLayoutParams().width = dm.widthPixels / 3;
                imageView1.getLayoutParams().width = dm.widthPixels / 3;
                imageView2.getLayoutParams().width = dm.widthPixels / 3;
                imageView3.getLayoutParams().width = dm.widthPixels / 3;
                imageView4.getLayoutParams().width = dm.widthPixels / 3;
                imageView5.getLayoutParams().width = dm.widthPixels / 3;
                imageView5e.getLayoutParams().width = dm.widthPixels / 3;
                imageView.getLayoutParams().height = imageView.getLayoutParams().width;
                imageView1.getLayoutParams().height = imageView1.getLayoutParams().width;
                imageView2.getLayoutParams().height = imageView2.getLayoutParams().width;
                imageView3.getLayoutParams().height = imageView3.getLayoutParams().width;
                imageView4.getLayoutParams().height = imageView4.getLayoutParams().width;
                imageView5.getLayoutParams().height = imageView5.getLayoutParams().width;
                imageView5e.getLayoutParams().height = imageView5e.getLayoutParams().width;
            } else {
                imageView.getLayoutParams().height = dm.widthPixels / 6;
                imageView1.getLayoutParams().height = dm.widthPixels / 6;
                imageView2.getLayoutParams().height = dm.widthPixels / 6;
                imageView3.getLayoutParams().height = dm.widthPixels / 6;
                imageView4.getLayoutParams().height = dm.widthPixels / 6;
                imageView5.getLayoutParams().height = dm.widthPixels / 6;
                imageView5e.getLayoutParams().height = dm.widthPixels / 6;
            }
        }
        if (positionStatus.equals("O")) {
            imageView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_none.webp"));
            return view;
        } else if (positionStatus.equals("C")) {
            imageView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_close.webp"));
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
                SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.ONCE);
                olPlayRoom.playButton.setText("取消");
            } else {
                SongPlay.INSTANCE.setPlaySongsMode(PlaySongsModeEnum.ONCE);
                olPlayRoom.playButton.setText("准备");
            }
        }
        int i5 = playerList.get(i).getInt("CP");
        if (i5 >= 0 && i5 < Consts.couples.length) {
            imageView6.setImageResource(Consts.couples[i5]);
        }
        ImageLoadUtil.setFamilyImageBitmap(olPlayRoom, familyID, imageView7);
        if (!positionStatus.equals("H")) {
            if ("R".equals(userStatus)) {
                readyTextView.setText("准备");
                readyTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            } else if ("N".equals(userStatus)) {
                readyTextView.setText("");
                readyTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
        } else {
            readyTextView.setText("房主");
            readyTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.exit));
        }
        autoConnectWaitView.setVisibility("W".equals(userStatus) ? View.VISIBLE : View.INVISIBLE);
        switch (Objects.requireNonNull(userStatus)) {
            case "P" -> {
                readyTextView.setText("弹奏中");
                readyTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
            case "F" -> {
                readyTextView.setText("查看成绩");
                readyTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
            case "B" -> {
                readyTextView.setText("后台");
                readyTextView.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.green_y));
            }
            default -> {
            }
        }
        int i6 = playerList.get(i).getInt("TR");
        int i7 = playerList.get(i).getInt("JA");
        int i8 = playerList.get(i).getInt("HA");
        int i8e = playerList.get(i).getInt("EY");
        int i9 = playerList.get(i).getInt("SH");
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
        ImageLoadUtil.setUserDressImageBitmap(olPlayRoom, str, i6, i7, i8, i8e, i9,
                imageView, imageView2, imageView3, imageView5, imageView5e, imageView4);
        readyTextView = view.findViewById(R.id.ol_player_name);
        readyTextView.setText(userName);
        TextView textView = view.findViewById(R.id.ol_player_hand);
        switch (RoomModeEnum.ofCode(olPlayRoom.getMode(), RoomModeEnum.NORMAL)) {
            case NORMAL -> {
                textView.setBackgroundResource(Consts.groupModeColor[0]);
                textView.setText(Consts.hand[(playerList.get(i).getInt("GR") + 12) % 2]);
            }
            case TEAM -> {
                textView.setText(Consts.groups[(playerList.get(i).getInt("GR")) - 1]);
                textView.setBackgroundResource(Consts.groupModeColor[(playerList.get(i).getInt("GR")) - 1]);
            }
            case COUPLE -> {
                textView.setText(Consts.hand[(playerList.get(i).getInt("GR") % 2)]);
                textView.setBackgroundResource(Consts.groupModeColor[(playerList.get(i).getInt("GR") - 1) / 2]);
            }
        }
        readyTextView.setBackgroundResource(i2 == 0 ? R.drawable.back_puased : ColorUtil.userColor[i2]);
        imageView.setBackgroundResource(ColorUtil.filledUserColor[i2]);
        return view;
    }
}
