package ly.pp.justpiano3.adapter;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLBaseActivity;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.utils.ColorUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        int ori = olPlayRoom.getResources().getConfiguration().orientation;
        DisplayMetrics dm = olPlayRoom.getResources().getDisplayMetrics();
        if (ori == Configuration.ORIENTATION_PORTRAIT) {
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

        try {
            if (string4.equals("O")) {
                imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_none.png")));
                return view;
            } else if (string4.equals("C")) {
                imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_close.png")));
                return view;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (i5 >= 0 && i5 <= 3) {
            imageView6.setImageResource(Consts.couples[i5]);
        }
        if (!familyID.equals("0")) {
            File file = new File(olPlayRoom.getFilesDir(), familyID + ".jpg");
            if (file.exists()) {
                try (InputStream inputStream = new FileInputStream(file)) {
                    int length = (int) file.length();
                    byte[] pic = new byte[length];
                    inputStream.read(pic);
                    imageView7.setImageBitmap(BitmapFactory.decodeByteArray(pic, 0, pic.length));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                imageView7.setImageResource(R.drawable.family);
            }
        }
        if (!string4.equals("H")) {
            if ("R".equals(string3)) {
                textView2.setText("准备");
                textView2.setBackgroundColor(olPlayRoom.getResources().getColor(R.color.online));
            } else if ("N".equals(string3)) {
                textView2.setText("");
                textView2.setBackgroundColor(olPlayRoom.getResources().getColor(R.color.online));
            }
        } else {
            textView2.setText("房主");
            textView2.setBackgroundColor(olPlayRoom.getResources().getColor(R.color.exit));
        }
        switch (string3) {
            case "P":
                textView2.setText("弹奏中");
                textView2.setBackgroundColor(olPlayRoom.getResources().getColor(R.color.online));
                break;
            case "F":
                textView2.setText("查看成绩");
                textView2.setBackgroundColor(olPlayRoom.getResources().getColor(R.color.online));
                break;
            case "B":
                textView2.setText("后台");
                textView2.setBackgroundColor(olPlayRoom.getResources().getColor(R.color.green_y));
                break;
        }
        try {
            int i6 = playerList.get(i).getInt("TR") - 1;
            int i7 = playerList.get(i).getInt("JA") - 1;
            int i8 = playerList.get(i).getInt("HA") - 1;
            int i8e = playerList.get(i).getInt("EY") - 1;
            int i9 = playerList.get(i).getInt("SH") - 1;
            if (ori == Configuration.ORIENTATION_PORTRAIT) {
                textView3.setText(String.valueOf(i3));
                textView4.setText(String.valueOf(i4));
            } else {
                textView3.setText("LV." + i3);
                textView4.setText("CL." + i4);
            }
            textView4.setTextColor(olPlayRoom.getResources().getColor(Consts.colors[i4]));
            textView5.setText(Consts.nameCL[i4]);
            textView5.setTextColor(olPlayRoom.getResources().getColor(Consts.colors[i4]));
            imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/" + str + "_m0.png")));

            if (i6 < 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/" + str + "_t" + i6 + ".png")));
            }
            if (i7 < 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/" + str + "_j" + i7 + ".png")));
            }
            if (i9 < 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/" + str + "_s" + i9 + ".png")));
            }
            if (i8 < 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/" + str + "_h" + i8 + ".png")));
            }
            if (i8e < 0) {
                imageView5e.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5e.setImageBitmap(BitmapFactory.decodeStream(olPlayRoom.getResources().getAssets().open("mod/" + str + "_e" + i8e + ".png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if (i2 == 0) {
            textView2.setBackgroundResource(R.drawable.back_puased);
        } else {
            textView2.setBackgroundResource(ColorUtil.kuang[i2]);
        }
        imageView.setBackgroundResource(ColorUtil.filledKuang[i2]);
        return view;
    }
}
