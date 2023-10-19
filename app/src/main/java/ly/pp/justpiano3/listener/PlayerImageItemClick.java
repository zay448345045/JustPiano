package ly.pp.justpiano3.listener;

import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.activity.OLPlayRoomActivity;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import protobuf.dto.OnlineChangeRoomDoorDTO;
import protobuf.dto.OnlineCoupleDTO;
import protobuf.dto.OnlineKickedQuitRoomDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

public final class PlayerImageItemClick implements OnItemClickListener {
    private final OLPlayRoomActivity olPlayRoomActivity;

    public PlayerImageItemClick(OLPlayRoomActivity olPlayRoomActivity) {
        this.olPlayRoomActivity = olPlayRoomActivity;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        User user = olPlayRoomActivity.getRoomPlayerMap().get((byte) (i + 1));
        if (user != null) {
            PopupWindow popupWindow = buildPopupWindow(user);
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            popupWindow.showAtLocation(view, Gravity.TOP | Gravity.START, iArr[0] + view.getWidth(), iArr[1]);
        }
    }

    private PopupWindow buildPopupWindow(User user) {
        ConnectionService connectionService = olPlayRoomActivity.jpapplication.getConnectionService();
        PopupWindow popupWindow = new PopupWindow(olPlayRoomActivity);
        View inflate = LayoutInflater.from(olPlayRoomActivity).inflate(R.layout.ol_room_user_operation, null);
        Button showUserInfoDialogButton = inflate.findViewById(R.id.ol_showinfo_b);
        Button privateChatButton = inflate.findViewById(R.id.ol_chat_b);
        Button kickOutButton = inflate.findViewById(R.id.ol_kickout_b);
        Button closePositionButton = inflate.findViewById(R.id.ol_closepos_b);
        Button showCoupleDialogButton = inflate.findViewById(R.id.ol_couple_b);
        Button chatBlackButton = inflate.findViewById(R.id.ol_chat_black);
        Button chatBlackCancelButton = inflate.findViewById(R.id.ol_chat_black_cancel);
        if (olPlayRoomActivity instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = ((OLPlayKeyboardRoom) olPlayRoomActivity);
            Button soundMuteButton = inflate.findViewById(R.id.ol_sound_b);
            soundMuteButton.setVisibility(View.VISIBLE);
            soundMuteButton.setText(olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].isMuted() ? "取消静音" : "静音");
            soundMuteButton.setOnClickListener(v -> {
                olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].setMuted(!olPlayKeyboardRoom.olKeyboardStates[user.getPosition() - 1].isMuted());
                if (olPlayKeyboardRoom.playerGrid.getAdapter() != null) {
                    ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                }
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            });
        }
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(olPlayRoomActivity.getResources(), R.drawable._none, olPlayRoomActivity.getTheme()));
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
            if (user.getIshost().equals("C") || user.getIshost().equals("O")) {
                privateChatButton.setVisibility(View.GONE);
            } else {
                privateChatButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        Bundle bundle = new Bundle();
                        if (olPlayRoomActivity instanceof OLPlayKeyboardRoom) {
                            Message obtainMessage = ((OLPlayKeyboardRoom) olPlayRoomActivity).olPlayKeyboardRoomHandler.obtainMessage();
                            obtainMessage.what = 12;
                            obtainMessage.setData(bundle);
                            bundle.putString("U", user.getPlayerName());
                            ((OLPlayKeyboardRoom) olPlayRoomActivity).olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                        } else if (olPlayRoomActivity instanceof OLPlayRoom) {
                            Message obtainMessage = ((OLPlayRoom) olPlayRoomActivity).olPlayRoomHandler.obtainMessage();
                            obtainMessage.what = 12;
                            obtainMessage.setData(bundle);
                            bundle.putString("U", user.getPlayerName());
                            ((OLPlayRoom) olPlayRoomActivity).olPlayRoomHandler.handleMessage(obtainMessage);
                        }
                    }
                });
            }
            if (!olPlayRoomActivity.playerKind.equals("H")) {
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setVisibility(View.GONE);
            } else if (user.getIshost().equals("C")) {
                closePositionButton.setText("打开空位");
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayRoomActivity.playerKind.equals("H") && connectionService != null) {
                            OnlineChangeRoomDoorDTO.Builder builder = OnlineChangeRoomDoorDTO.newBuilder();
                            builder.setRoomPosition(user.getPosition());
                            connectionService.writeData(OnlineProtocolType.CHANGE_ROOM_DOOR, builder.build());
                        }
                    }
                });
            } else if (user.getIshost().equals("O")) {
                closePositionButton.setText("关闭空位");
                kickOutButton.setVisibility(View.GONE);
                closePositionButton.setOnClickListener(v -> {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        if (olPlayRoomActivity.playerKind.equals("H") && connectionService != null) {
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
                        if (olPlayRoomActivity.playerKind.equals("H") && connectionService != null) {
                            if (!user.getStatus().equals("N") && !user.getStatus().equals("F") && !user.getStatus().equals("B")) {
                                Toast.makeText(olPlayRoomActivity, "用户当前状态不能被移出!", Toast.LENGTH_SHORT).show();
                            } else {
                                OnlineKickedQuitRoomDTO.Builder builder = OnlineKickedQuitRoomDTO.newBuilder();
                                builder.setRoomPosition(user.getPosition());
                                connectionService.writeData(OnlineProtocolType.KICKED_QUIT_ROOM, builder.build());
                                if (olPlayRoomActivity instanceof OLPlayKeyboardRoom) {
                                    ((OLPlayKeyboardRoom) olPlayRoomActivity).olKeyboardStates[user.getPosition() - 1].setMidiKeyboardOn(false);
                                }
                            }
                        }
                    }
                });
            }

            // 屏蔽聊天按钮处理
            ChatBlackUserUtil.chatBlackButtonHandle(olPlayRoomActivity, user, chatBlackButton, chatBlackCancelButton, popupWindow);
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
        if (user.getIshost().equals("C") || user.getIshost().equals("O")) {
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
}
