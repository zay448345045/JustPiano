package ly.pp.justpiano3.adapter;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.utils.ColorUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        String gender = playerList.get(i).getString("S").equals("f") ? "f" : "m";
        byte b = playerList.get(i).getByte("PI");
        String string3 = playerList.get(i).getString("IR");
        String string4 = playerList.get(i).getString("IH");
        int i2 = playerList.get(i).getInt("IV");
        String familyID = playerList.get(i).getString("I");
        ImageView imageView = view.findViewById(R.id.ol_player_mod);
        ImageView isPlayingView = view.findViewById(R.id.ol_player_playing);
        isPlayingView.setVisibility(olPlayKeyboardRoom.olKeyboardStates[i].isPlaying() ? View.VISIBLE : View.INVISIBLE);
        ImageView imageView8 = view.findViewById(R.id.ol_player_sound);
        imageView8.setImageResource(olPlayKeyboardRoom.olKeyboardStates[i].isMuted() ? R.drawable.stop : R.drawable.null_pic);
        try {
            if (string4.equals("O")) {
                imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
                return view;
            } else if (string4.equals("C")) {
                imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_close.png")));
                return view;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView textView2 = view.findViewById(R.id.ol_ready_text);
        TextView textView3 = view.findViewById(R.id.ol_player_level);
        TextView textView4 = view.findViewById(R.id.ol_player_class);
        TextView textView5 = view.findViewById(R.id.ol_player_clname);
        ImageView imageView2 = view.findViewById(R.id.ol_player_trousers);
        ImageView imageView3 = view.findViewById(R.id.ol_player_jacket);
        ImageView imageView4 = view.findViewById(R.id.ol_player_shoes);
        ImageView imageView5 = view.findViewById(R.id.ol_player_hair);
        ImageView imageView5e = view.findViewById(R.id.ol_player_eye);
        ImageView imageView6 = view.findViewById(R.id.ol_player_couple);
        ImageView imageView7 = view.findViewById(R.id.ol_player_family);
        int lv = playerList.get(i).getInt("LV");
        int cl = playerList.get(i).getInt("CL");
        if (JPApplication.kitiName.equals(userName)) {
            olPlayKeyboardRoom.lv = lv;
            olPlayKeyboardRoom.cl = cl;
            olPlayKeyboardRoom.playerKind = string4;
        }
        int cpKind = playerList.get(i).getInt("CP");
        if (cpKind >= 0 && cpKind <= 3) {
            imageView6.setImageResource(Consts.couples[cpKind]);
        }
        if (!familyID.equals("0")) {
            File file = new File(olPlayKeyboardRoom.getFilesDir(), familyID + ".jpg");
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

        if (!"H".equals(string4)) {
            if ("R".equals(string3)) {
                textView2.setText("准备");
                textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.online));
            } else if ("N".equals(string3)) {
                textView2.setText("");
                textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.online));
            }
        } else {
            textView2.setText("房主");
            textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.exit));
        }
        if ("B".equals(string3)) {
            textView2.setText("后台");
            textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.green_y));
        }
        try {
            int i6 = playerList.get(i).getInt("TR") - 1;
            int i7 = playerList.get(i).getInt("JA") - 1;
            int i8 = playerList.get(i).getInt("HA") - 1;
            int i8e = playerList.get(i).getInt("EY") - 1;
            int i9 = playerList.get(i).getInt("SH") - 1;
            textView3.setText("LV." + lv);
            textView4.setText("CL." + cl);
            textView4.setTextColor(olPlayKeyboardRoom.getResources().getColor(Consts.colors[cl]));
            textView5.setText(Consts.nameCL[cl]);
            textView5.setTextColor(olPlayKeyboardRoom.getResources().getColor(Consts.colors[cl]));
            imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + gender + "_m0.png")));
            if (i6 < 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + gender + "_t" + i6 + ".png")));
            }
            if (i7 < 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + gender + "_j" + i7 + ".png")));
            }
            if (i9 < 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + gender + "_s" + i9 + ".png")));
            }
            if (i8 < 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + gender + "_h" + i8 + ".png")));
            }
            if (i8e < 0) {
                imageView5e.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5e.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + gender + "_e" + i8e + ".png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView2 = view.findViewById(R.id.ol_player_name);
        textView2.setText(userName);
        ImageView imageView1 = view.findViewById(R.id.ol_player_midi);
        imageView1.setVisibility(olPlayKeyboardRoom.olKeyboardStates[i].isMidiKeyboardOn() ? View.VISIBLE : View.INVISIBLE);
        textView2.setBackgroundResource(i2 == 0 ? R.drawable.back_puased : ColorUtil.kuang[i2]);
        imageView.setBackgroundResource(ColorUtil.filledKuang[i2]);
        return view;
    }
}
