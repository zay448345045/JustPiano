package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Selection;
import android.text.Spannable;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.utils.ChatBlackUserUtil;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.DialogUtil;
import protobuf.dto.OnlineSetUserInfoDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.util.Date;

public final class OLPlayKeyboardRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayKeyboardRoomHandler(OLPlayKeyboardRoom olPlayKeyboardRoom) {
        weakReference = new WeakReference<>(olPlayKeyboardRoom);
    }

    @Override
    public void handleMessage(Message message) {
        OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                case 7:
                    post(() -> olPlayKeyboardRoom.mo2861a(olPlayKeyboardRoom.playerGrid, message.getData()));
                    return;
                case 2:
                case 4:
                    post(() -> {
                        if (olPlayKeyboardRoom.msgList.size() > olPlayKeyboardRoom.maxListValue) {
                            olPlayKeyboardRoom.msgList.remove(0);
                        }
                        SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(olPlayKeyboardRoom);
                        boolean showTime = ds.getBoolean("chats_time_show", false);
                        String time = "";
                        if (showTime) {
                            time = DateUtil.format(new Date(olPlayKeyboardRoom.jpapplication.getServerTime()), "HH:mm");
                        }
                        message.getData().putString("TIME", time);

                        // 如果聊天人没在屏蔽名单中，则将聊天消息加入list进行渲染展示
                        if (!ChatBlackUserUtil.isUserInChatBlackList(olPlayKeyboardRoom.jpapplication.getChatBlackList(), message.getData().getString("U"))) {
                            olPlayKeyboardRoom.msgList.add(message.getData());
                        }

                        // 聊天音效播放
                        if (olPlayKeyboardRoom.jpapplication.isChatSound() && !message.getData().getString("U").equals(olPlayKeyboardRoom.jpapplication.getKitiName())) {
                            olPlayKeyboardRoom.jpapplication.playChatSound();
                        }

                        // 聊天记录存储
                        if (ds.getBoolean("save_chats", false)) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats");
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                String date = DateUtil.format(DateUtil.now(), "yyyy-MM-dd聊天记录");
                                file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats/" + date + ".txt");
                                if (!file.exists()) {
                                    file.createNewFile();
                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    fileOutputStream.write((date + ":\n").getBytes());
                                    fileOutputStream.close();
                                }
                                FileWriter writer = new FileWriter(file, true);
                                String str = message.getData().getString("M");
                                if (str.startsWith("//")) {
                                    writer.close();
                                    olPlayKeyboardRoom.mo2862a(showTime);
                                    return;
                                } else if (message.getData().getInt("T") == 2) {
                                    writer.write((time + "[私]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n"));
                                    writer.close();
                                } else if (message.getData().getInt("T") == 1) {
                                    writer.write((time + "[公]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n"));
                                    writer.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        olPlayKeyboardRoom.mo2862a(showTime);
                    });
                    return;
                case 5:
                    post(() -> {
                        byte[] notes = message.getData().getByteArray("NOTES");
                        if (notes.length == 0) {
                            return;
                        }
                        int roomPositionSub1 = (byte) (notes[0] & 0xF);
                        User user = olPlayKeyboardRoom.jpapplication.getHashmap().get((byte) (roomPositionSub1 + 1));
                        if (user == null) {
                            return;
                        }
                        boolean midiKeyboardOn = (notes[0] >> 4) > 0;
                        boolean notify = false;
                        if (olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isMidiKeyboardOn() != midiKeyboardOn) {
                            olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(midiKeyboardOn);
                            notify = true;
                        }
                        if (!olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isPlaying()) {
                            olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setPlaying(true);
                            notify = true;
                        }
                        if (notify) {
                            if (olPlayKeyboardRoom.playerGrid.getAdapter() != null) {
                                ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                            } else {
                                olPlayKeyboardRoom.playerGrid.setAdapter(new KeyboardPlayerImageAdapter(olPlayKeyboardRoom.playerList, olPlayKeyboardRoom));
                            }
                        }
                        olPlayKeyboardRoom.receiveThreadPool.execute(() -> {
                            for (int i = 1; i < notes.length; i += 3) {
                                int intervalTime = notes[i];
                                if (intervalTime > 0) {
                                    try {
                                        Thread.sleep(intervalTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isMuted()) {
                                    olPlayKeyboardRoom.jpapplication.playSound(notes[i + 1], notes[i + 2]);
                                }
                                int finalI = i;
                                olPlayKeyboardRoom.runOnUiThread(() -> {
                                    // 此处fireKeyDown的最后一个参数必须为false，否则就会循环发送导致卡死
                                    if (notes[finalI + 2] > 0) {
                                        olPlayKeyboardRoom.keyboardView.fireKeyDown(notes[finalI + 1], notes[finalI + 2], user.getKuang(), false);
                                    } else {
                                        olPlayKeyboardRoom.keyboardView.fireKeyUp(notes[finalI + 1], false);
                                    }
                                });
                            }
                            olPlayKeyboardRoom.runOnUiThread(() -> {
                                if (olPlayKeyboardRoom.playerGrid.getAdapter() != null && olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].isPlaying()) {
                                    olPlayKeyboardRoom.olKeyboardStates[roomPositionSub1].setPlaying(false);
                                    ((KeyboardPlayerImageAdapter) (olPlayKeyboardRoom.playerGrid.getAdapter())).notifyDataSetChanged();
                                }
                            });
                        });
                    });
                    return;
                case 8:
                    post(() -> {
                        JPDialog jpdialog = new JPDialog(olPlayKeyboardRoom);
                        jpdialog.setCancelableFalse();
                        jpdialog.setTitle("提示").setMessage("您已被房主移出房间!").setFirstButton("确定", (dialog, which) -> {
                            olPlayKeyboardRoom.isOnStart = false;
                            Intent intent = new Intent(olPlayKeyboardRoom, OLPlayHall.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("hallName", olPlayKeyboardRoom.hallName);
                            bundle.putByte("hallID", olPlayKeyboardRoom.hallID0);
                            intent.putExtras(bundle);
                            olPlayKeyboardRoom.startActivity(intent);
                            olPlayKeyboardRoom.finish();
                        }).showDialog();
                    });
                    return;
                case 9:
                    post(() -> {
                        String string = message.getData().getString("F");
                        switch (message.getData().getInt("T")) {
                            case 0:
                                if (!string.isEmpty()) {
                                    JPDialog jpdialog = new JPDialog(olPlayKeyboardRoom);
                                    jpdialog.setTitle("好友请求");
                                    jpdialog.setMessage("[" + string + "]请求加您为好友,同意吗?");
                                    String finalString = string;
                                    jpdialog.setFirstButton("同意", (dialog, which) -> {
                                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                        builder.setType(1);
                                        builder.setReject(false);
                                        builder.setName(finalString);
                                        olPlayKeyboardRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                                        dialog.dismiss();
                                    });
                                    jpdialog.setSecondButton("拒绝", (dialog, which) -> {
                                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                        builder.setType(1);
                                        builder.setReject(true);
                                        builder.setName(finalString);
                                        olPlayKeyboardRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                                        dialog.dismiss();
                                    });
                                    jpdialog.showDialog();
                                }
                                return;
                            case 1:
                                olPlayKeyboardRoom.jpapplication.setIsShowDialog(false);
                                string = message.getData().getString("F");
                                int i = message.getData().getInt("I");
                                JPDialog jpdialog2 = new JPDialog(olPlayKeyboardRoom);
                                jpdialog2.setTitle("请求结果");
                                if (i == 0) {
                                    jpdialog2.setMessage("[" + string + "]同意添加您为好友!");
                                } else if (i == 1) {
                                    jpdialog2.setMessage("对方拒绝了你的好友请求!");
                                } else if (i == 2) {
                                    jpdialog2.setMessage("对方已经是你的好友!");
                                } else if (i == 3) {
                                    jpdialog2.setTitle(message.getData().getString("title"));
                                    jpdialog2.setMessage(message.getData().getString("Message"));
                                }
                                jpdialog2.setFirstButton("确定", new DialogDismissClick());
                                try {
                                    jpdialog2.showDialog();
                                    return;
                                } catch (Exception e2) {
                                    return;
                                }
                            default:
                        }
                    });
                    return;
                case 10:
                    post(() -> {
                        String name = message.getData().getString("R");
                        olPlayKeyboardRoom.roomTitle.setText("[" + olPlayKeyboardRoom.roomID0 + "]" + name);
                    });
                    return;
                case 11:
                    post(() -> {
                        olPlayKeyboardRoom.friendPlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayKeyboardRoom.friendPlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayKeyboardRoom.mo2863a(olPlayKeyboardRoom.friendsListView, olPlayKeyboardRoom.friendPlayerList, 1);
                        }
                        olPlayKeyboardRoom.canNotNextPage = size < 20;
                    });
                    return;
                case 12:
                    post(() -> {
                        olPlayKeyboardRoom.roomTabs.setCurrentTab(1);
                        String string = message.getData().getString("U");
                        if (string != null && !string.equals(JPApplication.kitiName)) {
                            olPlayKeyboardRoom.userTo = "@" + string + ":";
                            olPlayKeyboardRoom.sendText.setText(olPlayKeyboardRoom.userTo);
                            CharSequence text = olPlayKeyboardRoom.sendText.getText();
                            if (text instanceof Spannable) {
                                Selection.setSelection((Spannable) text, text.length());
                            }
                        }
                    });
                    return;
                case 13:
                    post(() -> {
                        olPlayKeyboardRoom.friendPlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayKeyboardRoom.friendPlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayKeyboardRoom.mo2863a(olPlayKeyboardRoom.friendsListView, olPlayKeyboardRoom.friendPlayerList, 3);
                        }
                    });
                    return;
                case 14:
                    post(() -> {
                        Bundle data = message.getData();
                        String string = data.getString("Ti");
                        String string2 = data.getString("I");
                        JPDialog jpdialog = new JPDialog(olPlayKeyboardRoom);
                        jpdialog.setTitle(string);
                        jpdialog.setMessage(string2);
                        jpdialog.setFirstButton("确定", new DialogDismissClick());
                        DialogUtil.handleGoldSend(olPlayKeyboardRoom.jpapplication, jpdialog, data.getInt("T"), data.getString("N"), data.getString("F"));
                        jpdialog.showDialog();
                    });
                    return;
                case 15:
                    post(() -> {
                        olPlayKeyboardRoom.invitePlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayKeyboardRoom.invitePlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayKeyboardRoom.mo2863a(olPlayKeyboardRoom.playerListView, olPlayKeyboardRoom.invitePlayerList, 3);
                        }
                    });
                    return;
                case 16:
                    post(() -> {
                        Bundle data = message.getData();
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(2);
                        builder.setName(data.getString("F"));
                        olPlayKeyboardRoom.friendPlayerList.remove(message.arg1);
                        olPlayKeyboardRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        olPlayKeyboardRoom.mo2863a(olPlayKeyboardRoom.friendsListView, olPlayKeyboardRoom.friendPlayerList, 1);
                    });
                    return;
                case 21:
                    post(() -> {
                        Toast.makeText(olPlayKeyboardRoom, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(olPlayKeyboardRoom, OLMainMode.class);
                        olPlayKeyboardRoom.startActivity(intent);
                        olPlayKeyboardRoom.finish();
                    });
                    return;
                case 22:
                    post(() -> {
                        int i = message.getData().getInt("MSG_T");
                        int i2 = message.getData().getInt("MSG_CT");
                        byte b = (byte) message.getData().getInt("MSG_CI");
                        String string = message.getData().getString("MSG_C");
                        if (i != 0) {
                            olPlayKeyboardRoom.mo2860a(i, string, i2, b);
                        }
                    });
                    return;
                case 23:
                    post(() -> olPlayKeyboardRoom.showInfoDialog(message.getData()));
                    return;
                default:
            }
        } catch (Exception ignored) {
        }
    }
}
