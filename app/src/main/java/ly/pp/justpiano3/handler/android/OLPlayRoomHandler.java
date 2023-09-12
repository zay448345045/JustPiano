package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.thread.SongPlay;
import protobuf.dto.OnlineQuitRoomDTO;

import java.lang.ref.WeakReference;

public final class OLPlayRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayRoomHandler(OLPlayRoom olPlayRoom) {
        weakReference = new WeakReference<>(olPlayRoom);
    }

    @Override
    public void handleMessage(Message message) {
        OLPlayRoom olPlayRoom = (OLPlayRoom) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                    post(() -> {
                        olPlayRoom.mo2861a(olPlayRoom.playerGrid, message.getData());
                        String str1 = message.getData().getString("SI");
                        if (!str1.isEmpty()) {
                            int diao = message.getData().getInt("diao");
                            olPlayRoom.setdiao(diao);
                            str1 = "songs/" + str1 + ".pm";
                            olPlayRoom.currentPlaySongPath = str1;
                            String[] a = olPlayRoom.querySongNameAndDiffByPath(str1);
                            String string = a[0];
                            String str2 = a[1];
                            if (string != null) {
                                olPlayRoom.songNameText.setText(string + "[难度:" + str2 + "]");
                                if (olPlayRoom.getMode() == RoomModeEnum.NORMAL.getCode()) {
                                    if (diao > 0) {
                                        olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "+" + diao);
                                    } else if (diao < 0) {
                                        olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "" + diao);
                                    } else {
                                        olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "0" + diao);
                                    }
                                }
                                if (!olPlayRoom.isChangeScreen) {
                                    SongPlay.INSTANCE.startPlay(olPlayRoom, str1, olPlayRoom.getdiao());
                                }
                            }
                        }
                        int i = message.getData().getBoolean("MSG_I") ? 1 : 0;
                        int i2 = message.getData().getInt("MSG_CT");
                        byte b = (byte) message.getData().getInt("MSG_CI");
                        String string = message.getData().getString("MSG_C");
                        if (!string.isEmpty()) {
                            olPlayRoom.mo2860a(i, string, i2, b);
                        }
                        olPlayRoom.isChangeScreen = false;
                    });
                    return;
                case 2:
                case 4:
                    post(() -> olPlayRoom.handleChat(message));
                    return;
                case 3:
                    post(() -> {
                        String str1 = message.getData().getString("song_path");
                        int diao = message.getData().getInt("diao");
                        if (!str1.isEmpty()) {
                            str1 = "songs/" + str1 + ".pm";
                            olPlayRoom.currentPlaySongPath = str1;
                            String[] a = olPlayRoom.querySongNameAndDiffByPath(str1);
                            String string = a[0];
                            String str2 = a[1];
                            if (string != null) {
                                olPlayRoom.setdiao(diao);
                                if (diao > 0) {
                                    olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().subSequence(0, 1) + "+" + diao);
                                } else if (diao < 0) {
                                    olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().subSequence(0, 1) + "" + diao);
                                } else {
                                    olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().subSequence(0, 1) + "0" + diao);
                                }
                                olPlayRoom.songNameText.setText(string + "[难度:" + str2 + "]");
                                SongPlay.INSTANCE.startPlay(olPlayRoom, str1, diao);
                            }
                        }
                    });
                    return;
                case 5:
                    post(() -> {
                        SongPlay.INSTANCE.stopPlay();
                        String str1 = message.getData().getString("S");
                        if (!olPlayRoom.isOnStart) {
                            olPlayRoom.jpapplication.getConnectionService().writeData(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
                            Intent intent = new Intent(olPlayRoom, OLPlayHall.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("hallName", olPlayRoom.hallName);
                            bundle.putByte("hallID", olPlayRoom.hallID0);
                            intent.putExtras(bundle);
                            olPlayRoom.startActivity(intent);
                            olPlayRoom.finish();
                        } else if (!str1.isEmpty()) {
                            olPlayRoom.setdiao(message.getData().getInt("D"));
                            str1 = "songs/" + str1 + ".pm";
                            String str = olPlayRoom.querySongNameAndDiffByPath(str1)[0];
                            if (str != null) {
                                olPlayRoom.isOnStart = false;
                                Intent intent2 = new Intent(olPlayRoom, PianoPlay.class);
                                intent2.putExtra("head", 2);
                                intent2.putExtra("path", str1);
                                intent2.putExtra("name", str);
                                intent2.putExtra("diao", olPlayRoom.getdiao());
                                intent2.putExtra("roomMode", olPlayRoom.roomMode);
                                intent2.putExtra("hand", olPlayRoom.currentHand);
                                intent2.putExtra("bundle", olPlayRoom.bundle0);
                                intent2.putExtra("bundleHall", olPlayRoom.bundle2);
                                olPlayRoom.startActivity(intent2);
                                olPlayRoom.finish();
                            }
                        }
                    });
                    return;
                case 6:
                    post(() -> {
                        if (olPlayRoom.playButton != null) {
                            olPlayRoom.playButton.setText("取消准备");
                            olPlayRoom.playButton.setTextSize(14);
                        }
                    });
                    return;
                case 7:
                    post(() -> olPlayRoom.mo2861a(olPlayRoom.playerGrid, message.getData()));
                    return;
                case 8:
                    post(olPlayRoom::handleKicked);
                    return;
                case 9:
                    post(() -> olPlayRoom.handleFriendRequest(message));
                    return;
                case 10:
                    post(() -> {
                        String name = message.getData().getString("R");
                        olPlayRoom.getIntent().putExtra("R", name);
                        olPlayRoom.bundle0.putString("R", name);
                        olPlayRoom.roomName = name;
                        olPlayRoom.roomNameView.setText("[" + olPlayRoom.roomID0 + "]" + olPlayRoom.roomName);
                    });
                    return;
                case 11:
                    post(() -> olPlayRoom.handleRefreshFriendList(message));
                    return;
                case 12:
                    post(() -> olPlayRoom.handlePrivateChat(message));
                    return;
                case 13:
                    post(() -> olPlayRoom.handleRefreshFriendListWithoutPage(message));
                    return;
                case 14:
                    post(() -> olPlayRoom.handleDialog(message));
                    return;
                case 15:
                    post(() -> olPlayRoom.handleInvitePlayerList(message));
                    return;
                case 16:
                    post(() -> olPlayRoom.handleSetUserInfo(message));
                    return;
                case 21:
                    post(olPlayRoom::handleOffline);
                    return;
                case 22:
                    post(() -> {
                        int i = message.getData().getInt("MSG_T");
                        int i2 = message.getData().getInt("MSG_CT");
                        byte b = (byte) message.getData().getInt("MSG_CI");
                        String string = message.getData().getString("MSG_C");
                        if (i != 0) {
                            olPlayRoom.mo2860a(i, string, i2, b);
                        }
                    });
                    return;
                case 23:
                    post(() -> olPlayRoom.showInfoDialog(message.getData()));
                    return;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
