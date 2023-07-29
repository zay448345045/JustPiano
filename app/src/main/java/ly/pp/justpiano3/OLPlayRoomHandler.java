package ly.pp.justpiano3;

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
import protobuf.dto.OnlineQuitRoomDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class OLPlayRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    OLPlayRoomHandler(OLPlayRoom olPlayRoom) {
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
                        String str1;
                        str1 = message.getData().getString("SI");
                        if (!str1.isEmpty()) {
                            olPlayRoom.jpapplication.setNowSongsName(str1);
                            int diao = message.getData().getInt("diao");
                            olPlayRoom.setdiao(diao);
                            str1 = "songs/" + str1 + ".pm";
                            String[] a = olPlayRoom.mo2864a(str1);
                            String string = a[0];
                            String str2 = a[1];
                            if (string != null) {
                                olPlayRoom.songNameText.setText(string + "[难度:" + str2 + "]");
                                try {
                                    if (olPlayRoom.getMode() == 0) {
                                        if (diao > 0) {
                                            olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "+" + diao);
                                        } else if (diao < 0) {
                                            olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "" + diao);
                                        } else {
                                            olPlayRoom.groupButton.setText(olPlayRoom.groupButton.getText().toString().charAt(0) + "0" + diao);
                                        }
                                    }
                                    olPlayRoom.playSongs = new PlaySongs(olPlayRoom.jpapplication, str1, null, olPlayRoom, 0, olPlayRoom.getdiao());
                                    olPlayRoom.nandu = str2;
                                } catch (Exception e) {
                                    return;
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
                    });
                    return;
                case 2:
                case 4:
                    post(() -> {
                        if (olPlayRoom.msgList.size() > olPlayRoom.maxListValue) {
                            olPlayRoom.msgList.remove(0);
                        }
                        SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(olPlayRoom);
                        boolean showTime = ds.getBoolean("chats_time_show", false);
                        String time = "";
                        if (showTime) {
                            time = new SimpleDateFormat("HH:mm", Locale.CHINESE).format(new Date(olPlayRoom.jpapplication.getServerTime()));
                        }
                        message.getData().putString("TIME", time);
                        olPlayRoom.msgList.add(message.getData());
                        if (olPlayRoom.jpapplication.isChatSound() && !message.getData().getString("U").equals(olPlayRoom.jpapplication.getKitiName())) {
                            olPlayRoom.jpapplication.playChatSound();
                        }
                        if (ds.getBoolean("save_chats", false)) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats");
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                String date = new SimpleDateFormat("yyyy-MM-dd聊天记录", Locale.CHINESE).format(new Date(System.currentTimeMillis()));
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
                                    olPlayRoom.mo2862a(showTime);
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
                        olPlayRoom.mo2862a(showTime);
                    });
                    return;
                case 3:
                    post(() -> {
                        String str1 = message.getData().getString("song_path");
                        int diao = message.getData().getInt("diao");
                        if (!str1.isEmpty()) {
                            olPlayRoom.jpapplication.setNowSongsName(str1);
                            str1 = "songs/" + str1 + ".pm";
                            String[] a = olPlayRoom.mo2864a(str1);
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
                                if (olPlayRoom.playSongs != null) {
                                    olPlayRoom.playSongs.isPlayingSongs = false;
                                    olPlayRoom.playSongs = null;
                                }
                                try {
                                    olPlayRoom.playSongs = new PlaySongs(olPlayRoom.jpapplication, str1, null, olPlayRoom, 0, diao);
                                    olPlayRoom.nandu = str1;
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    });
                    return;
                case 5:
                    post(() -> {
                        if (olPlayRoom.playSongs != null) {
                            olPlayRoom.playSongs.isPlayingSongs = false;
                            olPlayRoom.playSongs = null;
                        }
                        String str1 = message.getData().getString("S");
                        if (!olPlayRoom.isOnStart) {
                            olPlayRoom.jpapplication.getConnectionService().writeData(8, OnlineQuitRoomDTO.getDefaultInstance());
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
                            String str = olPlayRoom.mo2864a(str1)[0];
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
                    post(() -> {
                        if (olPlayRoom.playSongs != null) {
                            olPlayRoom.playSongs.isPlayingSongs = false;
                            olPlayRoom.playSongs = null;
                        }
                        JPDialog jpdialog = new JPDialog(olPlayRoom);
                        jpdialog.setCancelableFalse();
                        jpdialog.setTitle("提示").setMessage("您已被房主移出房间!").setFirstButton("确定", (dialog, which) -> {
                            olPlayRoom.isOnStart = false;
                            Intent intent = new Intent(olPlayRoom, OLPlayHall.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("hallName", olPlayRoom.hallName);
                            bundle.putByte("hallID", olPlayRoom.hallID0);
                            intent.putExtras(bundle);
                            olPlayRoom.startActivity(intent);
                            olPlayRoom.finish();
                        }).showDialog();
                    });
                    return;
                case 9:
                    post(() -> {
                        String string = message.getData().getString("F");
                        switch (message.getData().getInt("T")) {
                            case 0:
                                if (!string.isEmpty()) {
                                    JPDialog jpdialog = new JPDialog(olPlayRoom);
                                    jpdialog.setTitle("好友请求");
                                    jpdialog.setMessage("[" + string + "]请求加您为好友,同意吗?");
                                    String finalString = string;
                                    jpdialog.setFirstButton("同意", (dialog, which) -> {
                                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                        builder.setType(1);
                                        builder.setReject(false);
                                        builder.setName(finalString);
                                        olPlayRoom.sendMsg(31, builder.build());
                                        dialog.dismiss();
                                    });
                                    jpdialog.setSecondButton("拒绝", (dialog, which) -> {
                                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                        builder.setType(1);
                                        builder.setReject(true);
                                        builder.setName(finalString);
                                        olPlayRoom.sendMsg(31, builder.build());
                                        dialog.dismiss();
                                    });
                                    jpdialog.showDialog();
                                }
                                return;
                            case 1:
                                olPlayRoom.jpapplication.setIsShowDialog(false);
                                string = message.getData().getString("F");
                                int i = message.getData().getInt("I");
                                JPDialog jpdialog2 = new JPDialog(olPlayRoom);
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
                        olPlayRoom.bundle0.putString("R", name);
                        olPlayRoom.roomName = name;
                        olPlayRoom.roomNameView.setText("[" + olPlayRoom.roomID0 + "]" + olPlayRoom.roomName);
                    });
                    return;
                case 11:
                    post(() -> {
                        olPlayRoom.friendPlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayRoom.friendPlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayRoom.mo2863a(olPlayRoom.friendsListView, olPlayRoom.friendPlayerList, 1);
                        }
                        olPlayRoom.canNotNextPage = size < 20;
                    });
                    return;
                case 12:
                    post(() -> {
                        olPlayRoom.roomTabs.setCurrentTab(1);
                        String string = message.getData().getString("U");
                        if (string != null && !string.equals(JPApplication.kitiName)) {
                            olPlayRoom.userTo = "@" + string + ":";
                            olPlayRoom.sendText.setText(olPlayRoom.userTo);
                            CharSequence text = olPlayRoom.sendText.getText();
                            if (text instanceof Spannable) {
                                Selection.setSelection((Spannable) text, text.length());
                            }
                        }
                    });
                    return;
                case 13:
                    post(() -> {
                        olPlayRoom.friendPlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayRoom.friendPlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayRoom.mo2863a(olPlayRoom.friendsListView, olPlayRoom.friendPlayerList, 3);
                        }
                    });
                    return;
                case 14:
                    post(() -> {
                        Bundle data = message.getData();
                        String string = data.getString("Ti");
                        String string2 = data.getString("I");
                        JPDialog jpdialog = new JPDialog(olPlayRoom);
                        jpdialog.setTitle(string);
                        jpdialog.setMessage(string2);
                        jpdialog.setFirstButton("确定", new DialogDismissClick());
                        jpdialog.showDialog();
                    });
                    return;
                case 15:
                    post(() -> {
                        olPlayRoom.invitePlayerList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayRoom.invitePlayerList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayRoom.mo2863a(olPlayRoom.playerListView, olPlayRoom.invitePlayerList, 3);
                        }
                    });
                    return;
                case 16:
                    post(() -> {
                        Bundle data = message.getData();
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(2);
                        builder.setName(data.getString("F"));
                        olPlayRoom.friendPlayerList.remove(message.arg1);
                        olPlayRoom.sendMsg(31, builder.build());
                        olPlayRoom.mo2863a(olPlayRoom.friendsListView, olPlayRoom.friendPlayerList, 1);
                    });
                    return;
                case 21:
                    post(() -> {
                        Toast.makeText(olPlayRoom, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(olPlayRoom, OLMainMode.class);
                        olPlayRoom.startActivity(intent);
                        olPlayRoom.finish();
                    });
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
        } catch (Exception ignored) {
        }
    }
}
