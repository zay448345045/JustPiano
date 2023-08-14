package ly.pp.justpiano3.adapter;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import protobuf.dto.OnlineChangeRoomDoorDTO;
import protobuf.dto.OnlineCoupleDTO;
import protobuf.dto.OnlineKickedQuitRoomDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class KeyboardPlayerImageAdapter extends BaseAdapter {
    byte roomID;
    ConnectionService connectionService;
    private final OLPlayKeyboardRoom olPlayKeyboardRoom;
    private final List<Bundle> playerList;
    private final LayoutInflater layoutInflater;

    public KeyboardPlayerImageAdapter(List<Bundle> list, OLPlayKeyboardRoom olPlayKeyboardRoom) {
        layoutInflater = olPlayKeyboardRoom.getLayoutInflater();
        roomID = olPlayKeyboardRoom.roomID0;
        playerList = list;
        this.olPlayKeyboardRoom = olPlayKeyboardRoom;
        connectionService = olPlayKeyboardRoom.jpapplication.getConnectionService();
    }

    private PopupWindow m4034a(User user) {
        PopupWindow popupWindow = new PopupWindow(olPlayKeyboardRoom);
        View inflate = LayoutInflater.from(olPlayKeyboardRoom).inflate(R.layout.ol_buttonlist_view, null);
        Button showUserInfoDialogButton = inflate.findViewById(R.id.ol_showinfo_b);
        Button privateChatButton = inflate.findViewById(R.id.ol_chat_b);
        Button kickOutButton = inflate.findViewById(R.id.ol_kickout_b);
        Button closePositionButton = inflate.findViewById(R.id.ol_closepos_b);
        Button showCoupleDialogButton = inflate.findViewById(R.id.ol_couple_b);
        Button chatBlackButton = inflate.findViewById(R.id.ol_chat_black);
        Button chatBlackCancelButton = inflate.findViewById(R.id.ol_chat_black_cancel);
        Button soundMuteButton = inflate.findViewById(R.id.ol_sound_b);
        soundMuteButton.setVisibility(View.VISIBLE);
        if (olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].isMuted()) {
            soundMuteButton.setText("取消静音");
        } else {
            soundMuteButton.setText("静音");
        }
        soundMuteButton.setOnClickListener(v -> {
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
                showCoupleDialogButton.setVisibility(View.GONE);
            } else {
                showCoupleDialogButton.setText(Consts.coupleType[user.getCpKind() - 1]);
                showCoupleDialogButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (connectionService != null) {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            builder.setType(4);
                            connectionService.writeData(OnlineProtocolType.COUPLE, builder.build());
                        }
                    }
                });
            }
            if (user.getIsHost().equals("C") || user.getIsHost().equals("O")) {
                privateChatButton.setVisibility(View.GONE);
            } else {
                privateChatButton.setOnClickListener(v -> {
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
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setVisibility(View.GONE);
            } else if (user.getIsHost().equals("C")) {
                closePositionButton.setText("打开空位");
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayKeyboardRoom.playerKind.equals("H") && connectionService != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            connectionService.writeData(OnlineProtocolType.CHANGE_ROOM_DOOR, builder.build());
                        }
                    }
                });
            } else if (user.getIsHost().equals("O")) {
                closePositionButton.setText("关闭空位");
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayKeyboardRoom.playerKind.equals("H") && connectionService != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            connectionService.writeData(OnlineProtocolType.CHANGE_ROOM_DOOR, builder.build());
                        }
                    }
                });
            } else {
                closePositionButton.setVisibility(View.GONE);
                kickOutButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayKeyboardRoom.playerKind.equals("H") && connectionService != null) {
                            if (!user.getStatus().equals("N") && !user.getStatus().equals("F") && !user.getStatus().equals("B")) {
                                Toast.makeText(olPlayKeyboardRoom, "用户当前状态不能被移出!", Toast.LENGTH_SHORT).show();
                            } else {
                                OnlineKickedQuitRoomDTO.Builder builder = OnlineKickedQuitRoomDTO.newBuilder();
                                builder.setRoomPosition(user.getPosition());
                                connectionService.writeData(OnlineProtocolType.KICKED_QUIT_ROOM, builder.build());
                                olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].setMidiKeyboardOn(false);
                            }
                        }
                    }
                });
            }

            // 屏蔽聊天按钮处理
            ChatBlackUserUtil.chatBlackButtonHandle(user, olPlayKeyboardRoom.jpapplication,
                    chatBlackButton, chatBlackCancelButton, popupWindow);
        } else {
            if (user.getCpKind() > 0 && user.getCpKind() <= 3) {
                showCoupleDialogButton.setText(Consts.coupleType[user.getCpKind() - 1]);
                showCoupleDialogButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (connectionService != null) {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            builder.setType(4);
                            connectionService.writeData(OnlineProtocolType.COUPLE, builder.build());
                        }
                    }
                });
            } else {
                showCoupleDialogButton.setVisibility(View.GONE);
            }
            privateChatButton.setVisibility(View.GONE);
            kickOutButton.setVisibility(View.GONE);
            closePositionButton.setVisibility(View.GONE);
            chatBlackButton.setVisibility(View.GONE);
            chatBlackCancelButton.setVisibility(View.GONE);
        }
        if (user.getIsHost().equals("C") || user.getIsHost().equals("O")) {
            showUserInfoDialogButton.setVisibility(View.GONE);
            chatBlackButton.setVisibility(View.GONE);
            chatBlackCancelButton.setVisibility(View.GONE);
        } else {
            showUserInfoDialogButton.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    if (connectionService != null) {
                        OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                        builder.setName(user.getPlayerName());
                        connectionService.writeData(OnlineProtocolType.USER_INFO_DIALOG, builder.build());
                    }
                }
            });
        }
        return popupWindow;
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
        try {
            return doGetView(i, view, viewGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return view;
        }
    }

    private View doGetView(int i, View view, ViewGroup viewGroup) {
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
        }
        int i5 = playerList.get(i).getInt("CP");
        if (i5 >= 0 && i5 <= 3) {
            imageView6.setImageResource(Consts.couples[i5]);
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
