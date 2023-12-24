package ly.pp.justpiano3.listener;

import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLBaseActivity;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.activity.OLRoomActivity;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.OLKeyboardState;
import ly.pp.justpiano3.entity.Room;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.utils.ChatUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPPopupWindow;
import protobuf.dto.OnlineChangeRoomDoorDTO;
import protobuf.dto.OnlineCoupleDTO;
import protobuf.dto.OnlineKickedQuitRoomDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

public final class PlayerImageItemClick implements OnItemClickListener {
    private final OLRoomActivity olRoomActivity;

    public PlayerImageItemClick(OLRoomActivity olRoomActivity) {
        this.olRoomActivity = olRoomActivity;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        User user = OLBaseActivity.getRoomPlayerMap().get((byte) (i + 1));
        if (user != null) {
            PopupWindow popupWindow = buildPopupWindow(user);
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            popupWindow.showAtLocation(view, Gravity.TOP | Gravity.START, iArr[0] + view.getWidth(), iArr[1]);
        }
    }

    private PopupWindow buildPopupWindow(User user) {
        PopupWindow popupWindow = new JPPopupWindow(olRoomActivity);
        View inflate = LayoutInflater.from(olRoomActivity).inflate(R.layout.ol_room_user_operation, null);
        Button showUserInfoDialogButton = inflate.findViewById(R.id.ol_showinfo_b);
        Button privateChatButton = inflate.findViewById(R.id.ol_chat_b);
        Button kickOutButton = inflate.findViewById(R.id.ol_kickout_b);
        Button closePositionButton = inflate.findViewById(R.id.ol_closepos_b);
        Button showCoupleDialogButton = inflate.findViewById(R.id.ol_couple_b);
        Button chatBlackButton = inflate.findViewById(R.id.ol_chat_black);
        Button chatBlackCancelButton = inflate.findViewById(R.id.ol_chat_black_cancel);
        if (olRoomActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
            Button soundMuteButton = inflate.findViewById(R.id.ol_sound_b);
            soundMuteButton.setVisibility(View.VISIBLE);
            OLKeyboardState olKeyboardState = olPlayKeyboardRoom.olKeyboardStates.get(user.getPosition() - 1);
            if (olKeyboardState != null) {
                soundMuteButton.setText(olKeyboardState.getMuted() ? "取消静音" : "静音");
                soundMuteButton.setOnClickListener(v -> {
                    olKeyboardState.setMuted(!olKeyboardState.getMuted());
                    if (olPlayKeyboardRoom.playerGrid.getAdapter() != null) {
                        ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                    }
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                });
            }
        }
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(olRoomActivity.getResources(), R.drawable._none, olRoomActivity.getTheme()));
        if (!user.getPlayerName().equals(JPApplication.kitiName)) {
            if (user.getCpKind() <= 0 || user.getCpKind() > 3) {
                showCoupleDialogButton.setVisibility(View.GONE);
            } else {
                showCoupleDialogButton.setText(Consts.coupleType[user.getCpKind() - 1]);
                showCoupleDialogButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (OnlineUtil.getConnectionService() != null) {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            builder.setType(4);
                            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.COUPLE, builder.build());
                        }
                    }
                });
            }
            if (user.getIshost().equals("C") || user.getIshost().equals("O")) {
                privateChatButton.setVisibility(View.GONE);
            } else {
                privateChatButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        Bundle bundle = new Bundle();
                        if (olRoomActivity instanceof OLPlayKeyboardRoom) {
                            Message obtainMessage = ((OLPlayKeyboardRoom) olRoomActivity).olPlayKeyboardRoomHandler.obtainMessage();
                            obtainMessage.what = 12;
                            obtainMessage.setData(bundle);
                            bundle.putString("U", user.getPlayerName());
                            ((OLPlayKeyboardRoom) olRoomActivity).olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                        } else if (olRoomActivity instanceof OLPlayRoom) {
                            Message obtainMessage = ((OLPlayRoom) olRoomActivity).olPlayRoomHandler.obtainMessage();
                            obtainMessage.what = 12;
                            obtainMessage.setData(bundle);
                            bundle.putString("U", user.getPlayerName());
                            ((OLPlayRoom) olRoomActivity).olPlayRoomHandler.handleMessage(obtainMessage);
                        }
                    }
                });
            }
            if (!olRoomActivity.playerKind.equals("H")) {
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setVisibility(View.GONE);
            } else if (user.getIshost().equals("C")) {
                closePositionButton.setText("打开空位");
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olRoomActivity.playerKind.equals("H") && OnlineUtil.getConnectionService() != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.CHANGE_ROOM_DOOR, builder.build());
                        }
                    }
                });
            } else if (user.getIshost().equals("O")) {
                closePositionButton.setText("关闭空位");
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olRoomActivity.playerKind.equals("H") && OnlineUtil.getConnectionService() != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.CHANGE_ROOM_DOOR, builder.build());
                        }
                    }
                });
            } else {
                closePositionButton.setVisibility(View.GONE);
                kickOutButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olRoomActivity.playerKind.equals("H") && OnlineUtil.getConnectionService() != null) {
                            if (!user.getStatus().equals("N") && !user.getStatus().equals("F") && !user.getStatus().equals("B")) {
                                Toast.makeText(olRoomActivity, "用户当前状态不能被移出!", Toast.LENGTH_SHORT).show();
                            } else {
                                OnlineKickedQuitRoomDTO.Builder builder = OnlineKickedQuitRoomDTO.newBuilder();
                                builder.setRoomPosition(user.getPosition());
                                OnlineUtil.getConnectionService().writeData(OnlineProtocolType.KICKED_QUIT_ROOM, builder.build());
                                if (olRoomActivity instanceof OLPlayKeyboardRoom && user.getPosition() > 0
                                        && user.getPosition() - 1 < Room.CAPACITY) {
                                    OLKeyboardState olKeyboardState = ((OLPlayKeyboardRoom) olRoomActivity).olKeyboardStates.get(user.getPosition() - 1);
                                    if (olKeyboardState != null) {
                                        olKeyboardState.setMidiKeyboardOn(false);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            // 屏蔽聊天按钮处理
            ChatUtil.chatBlackButtonHandle(olRoomActivity, user, chatBlackButton, chatBlackCancelButton, popupWindow);
        } else {
            if (user.getCpKind() > 0 && user.getCpKind() <= 3) {
                showCoupleDialogButton.setText(Consts.coupleType[user.getCpKind() - 1]);
                showCoupleDialogButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (OnlineUtil.getConnectionService() != null) {
                            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            builder.setType(4);
                            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.COUPLE, builder.build());
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
        if (user.getIshost().equals("C") || user.getIshost().equals("O")) {
            showUserInfoDialogButton.setVisibility(View.GONE);
            chatBlackButton.setVisibility(View.GONE);
            chatBlackCancelButton.setVisibility(View.GONE);
        } else {
            showUserInfoDialogButton.setOnClickListener(v -> {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    if (OnlineUtil.getConnectionService() != null) {
                        OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                        builder.setName(user.getPlayerName());
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.USER_INFO_DIALOG, builder.build());
                    }
                }
            });
        }
        return popupWindow;
    }
}
