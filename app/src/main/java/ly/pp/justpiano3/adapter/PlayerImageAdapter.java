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
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLBaseActivity;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;

import java.util.List;

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
        TextView textView;
        TextView textView2 = view.findViewById(R.id.ol_ready_text);
        TextView textView3 = view.findViewById(R.id.ol_player_level);
        TextView textView4 = view.findViewById(R.id.ol_player_class);
        TextView textView5 = view.findViewById(R.id.ol_player_clname);
        String string = playerList.get(i).getString("N");
        String string2 = playerList.get(i).getString("S");
        String str = string2.equals("f") ? "f" : "m";
        byte b = playerList.get(i).getByte("PI");
        String string3 = playerList.get(i).getString("IR");
        String string4 = playerList.get(i).getString("IH");
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
            if (OLBaseActivity.px2dp(olPlayRoom, dm.widthPixels) <= 360) {
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
        if (string4.equals("O")) {
            imageView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_none.png"));
            return view;
        } else if (string4.equals("C")) {
            imageView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_close.png"));
            return view;
        }
        int i3 = playerList.get(i).getInt("LV");
        int i4 = playerList.get(i).getInt("CL");
        if (JPApplication.kitiName.equals(string)) {
            olPlayRoom.lv = i3;
            olPlayRoom.cl = i4;
            olPlayRoom.playerKind = string4;
            switch (RoomModeEnum.ofCode(olPlayRoom.getMode(), RoomModeEnum.NORMAL)) {
                case COUPLE:
                    olPlayRoom.currentHand = (playerList.get(i).getInt("GR") + 12) % 2;
                    break;
                case TEAM:
                    olPlayRoom.currentHand = 0;
                    break;
                case NORMAL:
                    olPlayRoom.currentHand = playerList.get(i).getInt("GR");
                    break;
            }
            olPlayRoom.user = olPlayRoom.jpapplication.getRoomPlayerMap().get(b);
            if ("H".equals(olPlayRoom.playerKind)) {
                olPlayRoom.playButton.setText("开始");
                olPlayRoom.playButton.setTextSize(20);
            } else if ("R".equals(string3)) {
                olPlayRoom.playButton.setText("取消");
                olPlayRoom.playButton.setTextSize(20);
            } else {
                olPlayRoom.playButton.setText("准备");
                olPlayRoom.playButton.setTextSize(20);
            }
        }
        int i5 = playerList.get(i).getInt("CP");
        if (i5 >= 0 && i5 < Consts.couples.length) {
            imageView6.setImageResource(Consts.couples[i5]);
        }
        ImageLoadUtil.setFamilyImageBitmap(olPlayRoom, familyID, imageView7);
        if (!string4.equals("H")) {
            if ("R".equals(string3)) {
                textView2.setText("准备");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            } else if ("N".equals(string3)) {
                textView2.setText("");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
            }
        } else {
            textView2.setText("房主");
            textView2.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.exit));
        }
        switch (string3) {
            case "P":
                textView2.setText("弹奏中");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
                break;
            case "F":
                textView2.setText("查看成绩");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.online));
                break;
            case "B":
                textView2.setText("后台");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayRoom, R.color.green_y));
                break;
        }
        int i6 = playerList.get(i).getInt("TR");
        int i7 = playerList.get(i).getInt("JA");
        int i8 = playerList.get(i).getInt("HA");
        int i8e = playerList.get(i).getInt("EY");
        int i9 = playerList.get(i).getInt("SH");
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            textView3.setText(String.valueOf(i3));
            textView4.setText(String.valueOf(i4));
        } else {
            textView3.setText("LV." + i3);
            textView4.setText("CL." + i4);
        }
        textView4.setTextColor(ContextCompat.getColor(olPlayRoom, Consts.colors[i4]));
        textView5.setText(Consts.nameCL[i4]);
        textView5.setTextColor(ContextCompat.getColor(olPlayRoom, Consts.colors[i4]));
        ImageLoadUtil.setUserDressImageBitmap(olPlayRoom, str, i6, i7, i8, i8e, i9,
                imageView, imageView2, imageView3, imageView5, imageView5e, imageView4);
        textView2 = view.findViewById(R.id.ol_player_name);
        textView2.setText(string);
        textView = view.findViewById(R.id.ol_player_hand);
        switch (RoomModeEnum.ofCode(olPlayRoom.getMode(), RoomModeEnum.NORMAL)) {
            case NORMAL:
                textView.setBackgroundResource(Consts.groupModeColor[0]);
                textView.setText(Consts.hand[(playerList.get(i).getInt("GR") + 12) % 2]);
                break;
            case TEAM:
                textView.setText(Consts.groups[(playerList.get(i).getInt("GR")) - 1]);
                textView.setBackgroundResource(Consts.groupModeColor[(playerList.get(i).getInt("GR")) - 1]);
                break;
            case COUPLE:
                textView.setText(Consts.hand[(playerList.get(i).getInt("GR") % 2)]);
                textView.setBackgroundResource(Consts.groupModeColor[(playerList.get(i).getInt("GR") - 1) / 2]);
                break;
        }
        textView2.setBackgroundResource(i2 == 0 ? R.drawable.back_puased : ColorUtil.kuang[i2]);
        imageView.setBackgroundResource(ColorUtil.filledKuang[i2]);
        return view;
    }
}
