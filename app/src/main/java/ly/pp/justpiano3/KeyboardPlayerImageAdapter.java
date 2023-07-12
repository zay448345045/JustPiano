package ly.pp.justpiano3;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import protobuf.dto.OnlineChangeRoomDoorDTO;
import protobuf.dto.OnlineCoupleDTO;
import protobuf.dto.OnlineKickedQuitRoomDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

public final class KeyboardPlayerImageAdapter extends BaseAdapter {
    byte roomID;
    ConnectionService connectionService;
    private final OLPlayKeyboardRoom olPlayKeyboardRoom;
    private final List<Bundle> playerList;
    private final LayoutInflater layoutInflater;

    KeyboardPlayerImageAdapter(List<Bundle> list, OLPlayKeyboardRoom olPlayKeyboardRoom) {
        layoutInflater = olPlayKeyboardRoom.getLayoutInflater();
        roomID = olPlayKeyboardRoom.roomID0;
        playerList = list;
        this.olPlayKeyboardRoom = olPlayKeyboardRoom;
        connectionService = olPlayKeyboardRoom.jpapplication.getConnectionService();
    }

    private PopupWindow m4034a(User user) {
        PopupWindow popupWindow = new PopupWindow(olPlayKeyboardRoom);
        View inflate = LayoutInflater.from(olPlayKeyboardRoom).inflate(R.layout.ol_buttonlist_view, null);
        Button button = inflate.findViewById(R.id.ol_showinfo_b);
        Button button2 = inflate.findViewById(R.id.ol_chat_b);
        Button button3 = inflate.findViewById(R.id.ol_kickout_b);
        Button button4 = inflate.findViewById(R.id.ol_closepos_b);
        Button button5 = inflate.findViewById(R.id.ol_couple_b);
        Button button6 = inflate.findViewById(R.id.ol_sound_b);
        button6.setVisibility(View.VISIBLE);
        if (olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].isMuted()) {
            button6.setText("取消静音");
        } else {
            button6.setText("静音");
        }
        button6.setOnClickListener(v -> {
            olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].setMuted(!olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].isMuted());
            notifyDataSetChanged();
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(olPlayKeyboardRoom.getResources().getDrawable(R.drawable._none));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        if (!user.getPlayerName().equals(JPApplication.kitiName)) {
            if (user.getCpKind() <= 0 || user.getCpKind() > 3) {
                button5.setVisibility(View.GONE);
            } else {
                button5.setText(Consts.coupleType[user.getCpKind() - 1]);
                button5.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (connectionService != null) {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            builder.setType(4);
                            connectionService.writeData(45, builder.build());
                        }
                    }
                });
            }
            if (user.getIsHost().equals("C") || user.getIsHost().equals("O")) {
                button2.setVisibility(View.GONE);
            } else {
                button2.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        Bundle bundle = new Bundle();
                        Message obtainMessage = olPlayKeyboardRoom.olPlayKeyboardRoomHandler.obtainMessage();
                        obtainMessage.what = 12;
                        obtainMessage.setData(bundle);
                        bundle.putString("U", user.getPlayerName());
                        olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                    }
                });
            }
            if (!olPlayKeyboardRoom.playerKind.equals("H")) {
                button3.setVisibility(View.GONE);
                button4.setVisibility(View.GONE);
            } else if (user.getIsHost().equals("C")) {
                button4.setText("打开空位");
                button3.setVisibility(View.GONE);
                button4.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayKeyboardRoom.playerKind.equals("H") && connectionService != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            connectionService.writeData(42, builder.build());
                        }
                    }
                });
            } else if (user.getIsHost().equals("O")) {
                button4.setText("关闭空位");
                button3.setVisibility(View.GONE);
                button4.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayKeyboardRoom.playerKind.equals("H") && connectionService != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            connectionService.writeData(42, builder.build());
                        }
                    }
                });
            } else {
                button4.setVisibility(View.GONE);
                button3.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayKeyboardRoom.playerKind.equals("H") && connectionService != null) {
                            if (!user.getStatus().equals("N") && !user.getStatus().equals("F") && !user.getStatus().equals("B")) {
                                Toast.makeText(olPlayKeyboardRoom, "用户当前状态不能被移出!", Toast.LENGTH_SHORT).show();
                            } else {
                                OnlineKickedQuitRoomDTO.Builder builder = OnlineKickedQuitRoomDTO.newBuilder();
                                builder.setRoomPosition(user.getPosition());
                                connectionService.writeData(9, builder.build());
                                olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].setMidiKeyboardOn(false);
                            }
                        }
                    }
                });
            }
        } else {
            if (user.getCpKind() > 0 && user.getCpKind() <= 3) {
                button5.setText(Consts.coupleType[user.getCpKind() - 1]);
                button5.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (connectionService != null) {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            builder.setType(4);
                            connectionService.writeData(45, builder.build());
                        }
                    }
                });
            } else {
                button5.setVisibility(View.GONE);
            }
            button2.setVisibility(View.GONE);
            button3.setVisibility(View.GONE);
            button4.setVisibility(View.GONE);
        }
        if (user.getIsHost().equals("C") || user.getIsHost().equals("O")) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    if (connectionService != null) {
                        OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                        builder.setName(user.getPlayerName());
                        connectionService.writeData(2, builder.build());
                    }
                }
            });
        }
        return popupWindow;
    }

    @Override
    public final int getCount() {
        return playerList.size();
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
            view = layoutInflater.inflate(R.layout.ol_keyboard_player_view, null);
        } else if (viewGroup.getChildCount() != i || i >= playerList.size()) {
            return view;
        }
        String string = playerList.get(i).getString("N");
        String string2 = playerList.get(i).getString("S");
        String str = string2.equals("f") ? "f" : "m";
        byte b = playerList.get(i).getByte("PI");
        String string3 = playerList.get(i).getString("IR");
        String string4 = playerList.get(i).getString("IH");
        int i2 = playerList.get(i).getInt("IV");
        String familyID = playerList.get(i).getString("I");
        ImageView imageView = view.findViewById(R.id.ol_player_mod);
        ImageView isPlayingView = view.findViewById(R.id.ol_player_playing);
        if (olPlayKeyboardRoom.olKeyboardStates[i].isPlaying()) {
            isPlayingView.setVisibility(View.VISIBLE);
        } else {
            isPlayingView.setVisibility(View.INVISIBLE);
        }
        View.OnClickListener onClickListener = v -> {
            PopupWindow a = m4034a(olPlayKeyboardRoom.jpapplication.getHashmap().get(b));
            int[] iArr = new int[2];
            imageView.getLocationOnScreen(iArr);
            a.showAtLocation(imageView, 51, iArr[0] + imageView.getWidth(), iArr[1]);
        };
        imageView.setOnClickListener(onClickListener);
        isPlayingView.setOnClickListener(onClickListener);
        ImageView imageView8 = view.findViewById(R.id.ol_player_sound);
        if (olPlayKeyboardRoom.olKeyboardStates[i].isMuted()) {
            imageView8.setImageResource(R.drawable.stop);
        } else {
            imageView8.setImageResource(R.drawable.null_pic);
        }
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
        int i3 = playerList.get(i).getInt("LV");
        int i4 = playerList.get(i).getInt("CL");
        if (JPApplication.kitiName.equals(string)) {
            olPlayKeyboardRoom.lv = i3;
            olPlayKeyboardRoom.cl = i4;
            olPlayKeyboardRoom.playerKind = string4;
            textView3.setTextColor(olPlayKeyboardRoom.getResources().getColor(R.color.yellow));
        }
        int i5 = playerList.get(i).getInt("CP");
        if (i5 >= 0 && i5 <= 3) {
            imageView6.setImageResource(Consts.couples[i5]);
        }
        if (!familyID.equals("0")) {
            File file = new File(olPlayKeyboardRoom.getFilesDir(), familyID + ".jpg");
            if (file.exists()) {
                try (InputStream inputStream = new FileInputStream(file)){
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
                textView2.setText("🆗");
                textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.online));
            } else if ("N".equals(string3)) {
                textView2.setText("");
                textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.online));
            }
        } else {
            textView2.setText("⭐");
            textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.exit));
        }
        if ("B".equals(string3)) {
            textView2.setText("☕");
            textView2.setBackgroundColor(olPlayKeyboardRoom.getResources().getColor(R.color.green_y));
        }
        try {
            int i6 = playerList.get(i).getInt("TR") - 1;
            int i7 = playerList.get(i).getInt("JA") - 1;
            int i8 = playerList.get(i).getInt("HA") - 1;
            int i8e = playerList.get(i).getInt("EY") - 1;
            int i9 = playerList.get(i).getInt("SH") - 1;
            textView3.setText("LV." + i3);
            textView4.setText("CL." + i4);
            textView4.setTextColor(olPlayKeyboardRoom.getResources().getColor(Consts.colors[i4]));
            textView5.setText(Consts.nameCL[i4]);
            textView5.setTextColor(olPlayKeyboardRoom.getResources().getColor(Consts.colors[i4]));
            imageView.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + str + "_m0.png")));
            if (i6 < 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + str + "_t" + i6 + ".png")));
            }
            if (i7 < 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + str + "_j" + i7 + ".png")));
            }
            if (i9 < 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + str + "_s" + i9 + ".png")));
            }
            if (i8 < 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + str + "_h" + i8 + ".png")));
            }
            if (i8e < 0) {
                imageView5e.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5e.setImageBitmap(BitmapFactory.decodeStream(olPlayKeyboardRoom.getResources().getAssets().open("mod/" + str + "_e" + i8e + ".png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView2 = view.findViewById(R.id.ol_player_name);
        textView2.setText(string);
        ImageView imageView1 = view.findViewById(R.id.ol_player_midi);
        if (olPlayKeyboardRoom.olKeyboardStates[i].isMidiKeyboardOn()) {
            imageView1.setVisibility(View.VISIBLE);
        } else {
            imageView1.setVisibility(View.INVISIBLE);
        }
        if (i2 == 0) {
            textView2.setBackgroundResource(R.drawable.back_puased);
        } else {
            textView2.setBackgroundResource(Consts.kuang[i2]);
        }
        imageView.setBackgroundResource(Consts.filledKuang[i2]);
        return view;
    }
}
