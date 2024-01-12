package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.activity.online.OLPlayRoom;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.enums.RoomModeEnum;
import ly.pp.justpiano3.thread.SongPlay;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import protobuf.dto.OnlineQuitRoomDTO;

public final class OLPlayRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayRoomHandler(OLPlayRoom olPlayRoom) {
        super(Looper.getMainLooper());
        weakReference = new WeakReference<>(olPlayRoom);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        OLPlayRoom olPlayRoom = (OLPlayRoom) weakReference.get();
        try {
            switch (message.what) {
                case 1 -> post(() -> {
                    olPlayRoom.updatePlayerList(olPlayRoom.playerGrid, message.getData());
                    String songFilePath = message.getData().getString("SI");
                    if (!TextUtils.isEmpty(songFilePath)) {
                        int tune = message.getData().getInt("diao");
                        olPlayRoom.setTune(tune);
                        songFilePath = "songs/" + songFilePath + ".pm";
                        olPlayRoom.currentPlaySongPath = songFilePath;
                        String[] simpleSongInfo = olPlayRoom.querySongNameAndDiffByPath(songFilePath);
                        String songName = simpleSongInfo[0];
                        String songDegree = simpleSongInfo[1];
                        if (songName != null) {
                            olPlayRoom.songNameScrollTextView.setText(songName + "[难度:" + songDegree + "]");
                            if (olPlayRoom.getMode() == RoomModeEnum.NORMAL.getCode()) {
                                if (tune > 0) {
                                    olPlayRoom.settingButton.setText(olPlayRoom.settingButton.getText().toString().charAt(0) + "+" + tune);
                                } else if (tune < 0) {
                                    olPlayRoom.settingButton.setText(olPlayRoom.settingButton.getText().toString().charAt(0) + String.valueOf(tune));
                                } else {
                                    olPlayRoom.settingButton.setText(olPlayRoom.settingButton.getText().toString().charAt(0) + "0" + tune);
                                }
                            }
                            if (!SongPlay.isPlaying()) {
                                SongPlay.startPlay(olPlayRoom, songFilePath, message.arg1, olPlayRoom.getTune());
                            }
                        }
                    }
                    int dialogType = message.getData().getBoolean("MSG_I") ? 1 : 0;
                    int coupleType = message.getData().getInt("MSG_CT");
                    byte couplePosition = (byte) message.getData().getInt("MSG_CI");
                    String dialogMessage = message.getData().getString("MSG_C");
                    if (!TextUtils.isEmpty(dialogMessage)) {
                        olPlayRoom.buildAndShowCpDialog(dialogType, dialogMessage, coupleType, couplePosition);
                    }
                });
                case 2, 4 -> post(() -> olPlayRoom.handleChat(message));
                case 3 -> post(() -> {
                    String songFilePath = message.getData().getString("song_path");
                    int tune = message.getData().getInt("diao");
                    if (!TextUtils.isEmpty(songFilePath)) {
                        songFilePath = "songs/" + songFilePath + ".pm";
                        olPlayRoom.currentPlaySongPath = songFilePath;
                        String[] simpleSongInfo = olPlayRoom.querySongNameAndDiffByPath(songFilePath);
                        String songName = simpleSongInfo[0];
                        String songRightHandDegree = simpleSongInfo[1];
                        if (songName != null) {
                            olPlayRoom.setTune(tune);
                            if (tune > 0) {
                                olPlayRoom.settingButton.setText(olPlayRoom.settingButton.getText().subSequence(0, 1) + "+" + tune);
                            } else if (tune < 0) {
                                olPlayRoom.settingButton.setText(olPlayRoom.settingButton.getText().subSequence(0, 1) + String.valueOf(tune));
                            } else {
                                olPlayRoom.settingButton.setText(olPlayRoom.settingButton.getText().subSequence(0, 1) + "0" + tune);
                            }
                            olPlayRoom.songNameScrollTextView.setText(songName + "[难度:" + songRightHandDegree + "]");
                            SongPlay.startPlay(olPlayRoom, songFilePath, tune);
                        }
                    }
                });
                case 5 -> post(() -> {
                    SongPlay.stopPlay();
                    String songFilePath = message.getData().getString("S");
                    if (!olPlayRoom.onStart) {
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
                        Intent intent = new Intent(olPlayRoom, OLPlayHall.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("hallName", olPlayRoom.hallName);
                        bundle.putByte("hallID", olPlayRoom.hallId);
                        intent.putExtras(bundle);
                        olPlayRoom.startActivity(intent);
                        olPlayRoom.finish();
                    } else if (!TextUtils.isEmpty(songFilePath)) {
                        if (DeviceUtil.isX86()) {
                            Toast.makeText(olPlayRoom, "您的设备不支持弹奏", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        olPlayRoom.setTune(message.getData().getInt("D"));
                        songFilePath = "songs/" + songFilePath + ".pm";
                        String songName = olPlayRoom.querySongNameAndDiffByPath(songFilePath)[0];
                        if (songName != null) {
                            olPlayRoom.onStart = false;
                            Intent intent = new Intent(olPlayRoom, PianoPlay.class);
                            intent.putExtra("head", 2);
                            intent.putExtra("path", songFilePath);
                            intent.putExtra("name", songName);
                            intent.putExtra("diao", olPlayRoom.getTune());
                            intent.putExtra("roomMode", olPlayRoom.roomMode);
                            intent.putExtra("hand", olPlayRoom.currentHand);
                            intent.putExtra("bundle", olPlayRoom.roomInfoBundle);
                            intent.putExtra("bundleHall", olPlayRoom.hallInfoBundle);
                            olPlayRoom.startActivity(intent);
                            olPlayRoom.finish();
                        }
                    }
                });
                case 6 -> post(() -> {
                    if (olPlayRoom.playButton != null) {
                        olPlayRoom.playButton.setText("取消准备");
                        olPlayRoom.playButton.setTextSize(14);
                    }
                });
                case 7 -> post(() -> olPlayRoom.updatePlayerList(olPlayRoom.playerGrid, message.getData()));
                case 8 -> post(olPlayRoom::handleKicked);
                case 9 -> post(() -> olPlayRoom.handleFriendRequest(message));
                case 10 -> post(() -> {
                    String name = message.getData().getString("R");
                    olPlayRoom.getIntent().putExtra("R", name);
                    olPlayRoom.roomInfoBundle.putString("R", name);
                    olPlayRoom.roomName = name;
                    olPlayRoom.roomNameView.setText("[" + olPlayRoom.roomId + "]" + olPlayRoom.roomName);
                });
                case 11 -> post(() -> olPlayRoom.handleRefreshFriendList(message));
                case 12 -> post(() -> olPlayRoom.handlePrivateChat(message));
                case 13 -> post(() -> olPlayRoom.handleRefreshFriendListWithoutPage(message));
                case 14 -> post(() -> olPlayRoom.handleDialog(message));
                case 15 -> post(() -> olPlayRoom.handleInvitePlayerList(message));
                case 16 -> post(() -> olPlayRoom.handleSetUserInfo(message));
                case 21 -> post(olPlayRoom::handleOffline);
                case 22 -> post(() -> {
                    int dialogType = message.getData().getInt("MSG_T");
                    int coupleType = message.getData().getInt("MSG_CT");
                    byte couplePosition = (byte) message.getData().getInt("MSG_CI");
                    String dialogMessage = message.getData().getString("MSG_C");
                    if (dialogType != 0) {
                        olPlayRoom.buildAndShowCpDialog(dialogType, dialogMessage, coupleType, couplePosition);
                    }
                });
                case 23 -> post(() -> olPlayRoom.showInfoDialog(message.getData()));
                default -> {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
