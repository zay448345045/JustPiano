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

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.constant.Consts;
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
        String gender = playerList.get(i).getString("S").equals("f") ? "f" : "m";
        String string3 = playerList.get(i).getString("IR");
        String string4 = playerList.get(i).getString("IH");
        int i2 = playerList.get(i).getInt("IV");
        String familyID = playerList.get(i).getString("I");
        ImageView imageView = view.findViewById(R.id.ol_player_mod);
        ImageView imageView8 = view.findViewById(R.id.ol_player_sound);
        imageView8.setImageResource(olPlayKeyboardRoom.olKeyboardStates[i].isMuted() ? R.drawable.stop : R.drawable.null_pic);
        if (string4.equals("O")) {
            imageView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_none.png"));
            return view;
        } else if (string4.equals("C")) {
            imageView.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get("mod/_close.png"));
            return view;
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
        ImageLoadUtil.setFamilyImageBitmap(olPlayKeyboardRoom, familyID, imageView7);
        if (!"H".equals(string4)) {
            if ("R".equals(string3)) {
                textView2.setText("准备");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.online));
            } else if ("N".equals(string3)) {
                textView2.setText("");
                textView2.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.online));
            }
        } else {
            textView2.setText("房主");
            textView2.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.exit));
        }
        if ("B".equals(string3)) {
            textView2.setText("后台");
            textView2.setBackgroundColor(ContextCompat.getColor(olPlayKeyboardRoom, R.color.green_y));
        }
        int i6 = playerList.get(i).getInt("TR");
        int i7 = playerList.get(i).getInt("JA");
        int i8 = playerList.get(i).getInt("HA");
        int i8e = playerList.get(i).getInt("EY");
        int i9 = playerList.get(i).getInt("SH");
        textView3.setText("LV." + lv);
        textView4.setText("CL." + cl);
        textView4.setTextColor(ContextCompat.getColor(olPlayKeyboardRoom, Consts.colors[cl]));
        textView5.setText(Consts.nameCL[cl]);
        textView5.setTextColor(ContextCompat.getColor(olPlayKeyboardRoom, Consts.colors[cl]));
        ImageLoadUtil.setUserDressImageBitmap(olPlayKeyboardRoom, gender, i6, i7, i8, i8e, i9,
                imageView, imageView2, imageView3, imageView5, imageView5e, imageView4);
        textView2 = view.findViewById(R.id.ol_player_name);
        textView2.setText(userName);
        ImageView imageView1 = view.findViewById(R.id.ol_player_midi);
        imageView1.setVisibility(olPlayKeyboardRoom.olKeyboardStates[i].isMidiKeyboardOn() ? View.VISIBLE : View.INVISIBLE);
        textView2.setBackgroundResource(i2 == 0 ? R.drawable.back_puased : ColorUtil.userColor[i2]);
        imageView.setBackgroundResource(ColorUtil.filledUserColor[i2]);
        return view;
    }
}
