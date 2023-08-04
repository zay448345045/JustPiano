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
import android.widget.RadioButton;
import android.widget.Toast;
import ly.pp.justpiano3.utils.DialogUtil;
import protobuf.dto.OnlineClTestDTO;
import protobuf.dto.OnlineEnterRoomDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class OLPlayHallHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    OLPlayHallHandler(OLPlayHall ol) {
        weakReference = new WeakReference<>(ol);
    }

    @Override
    public void handleMessage(Message message) {
        OLPlayHall olPlayHall = (OLPlayHall) weakReference.get();
        switch (message.what) {
            case 1:
                post(() -> {
                    if (olPlayHall.msgList.size() > 100) {
                        olPlayHall.msgList.remove(0);
                    }
                    File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(olPlayHall);
                    boolean showTime = ds.getBoolean("chats_time_show", false);
                    String time = "";
                    if (showTime) {
                        time = new SimpleDateFormat("HH:mm", Locale.CHINESE).format(new Date(olPlayHall.jpapplication.getServerTime()));
                    }
                    message.getData().putString("TIME", time);
                    olPlayHall.msgList.add(message.getData());
                    if (olPlayHall.jpapplication.isChatSound() && !message.getData().getString("U").equals(olPlayHall.jpapplication.getKitiName())) {
                        olPlayHall.jpapplication.playChatSound();
                    }
                    if (ds.getBoolean("save_chats", false)) {
                        try {
                            String date = new SimpleDateFormat("yyyy-MM-dd聊天记录", Locale.CHINESE).format(new Date(System.currentTimeMillis()));
                            file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats/" + date + ".txt");
                            if (!file.exists()) {
                                file.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write((date + ":\n").getBytes());
                                fileOutputStream.close();
                            }
                            FileWriter writer = new FileWriter(file, true);
                            if (message.getData().getString("M").startsWith("//")) {
                                writer.close();
                                olPlayHall.mo2828a(olPlayHall.msgListView, olPlayHall.msgList, showTime);
                                return;
                            } else if (message.getData().getInt("T") == 2) {
                                writer.write((time + "[私]" + message.getData().getString("U") + ":" + (message.getData().getString("M"))));
                            } else {
                                writer.write(time + "[公]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n");
                            }
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    olPlayHall.mo2828a(olPlayHall.msgListView, olPlayHall.msgList, showTime);
                });
                return;
            case 2:
                Bundle data = message.getData();
                olPlayHall.jpapplication.setNowSongsName("");
                data.putBundle("bundle", olPlayHall.hallInfoBundle);
                int mode = data.getInt("mode");
                Intent intent;
                if (mode == 3) {
                    intent = new Intent(olPlayHall, OLPlayKeyboardRoom.class);
                } else {
                    intent = new Intent(olPlayHall, OLPlayRoom.class);
                }
                intent.putExtras(data);
                olPlayHall.startActivity(intent);
                olPlayHall.finish();
                return;
            case 3:
                post(() -> {
                    olPlayHall.roomList.clear();
                    Bundle data1 = message.getData();
                    int size = data1.size();
                    for (int i = 0; i < size; i++) {
                        olPlayHall.roomList.add(data1.getBundle(String.valueOf(i)));
                    }
                    olPlayHall.mo2831b(olPlayHall.roomListView, olPlayHall.roomList);
                });
                return;
            case 4:
                post(() -> Toast.makeText(olPlayHall, message.getData().getString("result"), Toast.LENGTH_SHORT).show());
                return;
            case 5:
                post(() -> {
                    olPlayHall.friendList.clear();
                    Bundle data16 = message.getData();
                    int size = data16.size();
                    if (size >= 0) {
                        for (int i = 0; i < size; i++) {
                            olPlayHall.friendList.add(data16.getBundle(String.valueOf(i)));
                        }
                        olPlayHall.mo2829a(olPlayHall.friendListView, olPlayHall.friendList, 1, true);
                    }
                    olPlayHall.pageIsEnd = size < 20;
                });
                return;
            case 6:
                post(() -> {
                    olPlayHall.tabHost.setCurrentTab(1);
                    String string = message.getData().getString("U");
                    if (!string.equals(JPApplication.kitiName)) {
                        olPlayHall.sendTo = "@" + string + ":";
                        olPlayHall.sendTextView.setText(olPlayHall.sendTo);
                        CharSequence text = olPlayHall.sendTextView.getText();
                        if (text instanceof Spannable) {
                            Selection.setSelection((Spannable) text, text.length());
                        }
                    }
                });
                return;
            case 7:
                post(() -> {
                    olPlayHall.userInHallList.clear();
                    Bundle data13 = message.getData();
                    int size = data13.size();
                    if (size >= 0) {
                        for (int i = 0; i < size; i++) {
                            olPlayHall.userInHallList.add(data13.getBundle(String.valueOf(i)));
                        }
                        olPlayHall.mo2829a(olPlayHall.userInHallListView, olPlayHall.userInHallList, 3, true);
                    }
                });
                return;
            case 8:
                post(() -> {
                    String string = message.getData().getString("F");
                    switch (message.getData().getInt("T")) {
                        case 0:
                            if (!string.isEmpty()) {
                                JPDialog jpdialog = new JPDialog(olPlayHall);
                                jpdialog.setTitle("好友请求");
                                jpdialog.setMessage("[" + string + "]请求加您为好友,同意吗?");
                                String finalString = string;
                                jpdialog.setFirstButton("同意", (dialog, which) -> {
                                    dialog.dismiss();
                                    OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                    builder.setType(1);
                                    builder.setName(finalString);
                                    builder.setReject(false);
                                    olPlayHall.sendMsg(31, builder.build());
                                });
                                jpdialog.setSecondButton("拒绝", (dialog, which) -> {
                                    dialog.dismiss();
                                    OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                                    builder.setType(1);
                                    builder.setName(finalString);
                                    builder.setReject(true);
                                    olPlayHall.sendMsg(31, builder.build());
                                });
                                jpdialog.showDialog();
                            }
                            return;
                        case 1:
                            olPlayHall.jpapplication.setIsShowDialog(false);
                            string = message.getData().getString("F");
                            int i = message.getData().getInt("I");
                            JPDialog jpdialog2 = new JPDialog(olPlayHall);
                            jpdialog2.setTitle("请求结果");
                            switch (i) {
                                case 0:
                                    jpdialog2.setMessage("[" + string + "]同意添加您为好友!");
                                    break;
                                case 1:
                                    jpdialog2.setMessage("对方拒绝了你的好友请求!");
                                    break;
                                case 2:
                                    jpdialog2.setMessage("对方已经是你的好友!");
                                    break;
                                case 3:
                                    jpdialog2.setTitle(message.getData().getString("title"));
                                    jpdialog2.setMessage(message.getData().getString("Message"));
                                    break;
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
            case 9:
                post(() -> {
                    if (!olPlayHall.jpapplication.getIsShowDialog()) {
                        Bundle data14 = message.getData();
                        int i = data14.getInt("T");
                        byte b = (byte) data14.getInt("R");
                        byte b2 = (byte) data14.getInt("H");
                        int i2 = data14.getInt("C");
                        String string = data14.getString("I");
                        String string2 = data14.getString("Ti");
                        JPDialog jpdialog = new JPDialog(olPlayHall);
                        jpdialog.setTitle(string2);
                        string2 = "确定";
                        if (i2 == 1 && b2 == olPlayHall.hallID && b2 > (byte) 0) {
                            jpdialog.setMessage(string);
                            jpdialog.setFirstButton("进入房间", (dialog, which) -> {
                                dialog.dismiss();
                                olPlayHall.jpapplication.setIsShowDialog(false);
                                if (i == 0) {
                                    olPlayHall.mo2826a(data14.getInt("P"), b);
                                } else if (i == 1) {
                                    OnlineEnterRoomDTO.Builder builder = OnlineEnterRoomDTO.newBuilder();
                                    builder.setRoomId(b);
                                    builder.setPassword(data14.getString("P"));
                                    olPlayHall.sendMsg(7, builder.build());
                                }
                            });
                            jpdialog.setSecondButton("取消", (dialog, which) -> {
                                dialog.dismiss();
                                olPlayHall.jpapplication.setIsShowDialog(false);
                            });
                        } else {
                            jpdialog.setMessage(string);
                            jpdialog.setFirstButton(string2, (dialog, which) -> {
                                dialog.dismiss();
                                olPlayHall.jpapplication.setIsShowDialog(false);
                            });
                        }
                        DialogUtil.handleGoldSend(olPlayHall, olPlayHall.jpapplication, jpdialog, i, data14.getString("N"), string);
                        jpdialog.showDialog();
                        olPlayHall.jpapplication.setIsShowDialog(true);
                    }
                });
                return;
            case 10:
                post(() -> {
                    Bundle data12 = message.getData();
                    OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                    builder.setType(2);
                    builder.setName(data12.getString("F"));
                    olPlayHall.friendList.remove(message.arg1);
                    olPlayHall.sendMsg(31, builder.build());
                    olPlayHall.mo2829a(olPlayHall.friendListView, olPlayHall.friendList, 1, true);
                });
                return;
            case 11:
                post(() -> {
                    Bundle data15 = message.getData();
                    int i = data15.getInt("result");
                    String[] msg = data15.getString("info").split("\n");
                    String str = "提示";
                    String str2 = null;
                    switch (i) {
                        case 0:
                            str2 = "确定";
                            break;
                        case 1:
                            str2 = "开始考级";
                            break;
                    }
                    JPDialog jpdialog = new JPDialog(olPlayHall);
                    if (msg.length > 1) {
                        jpdialog.setVisibleRadioGroup(true);
                    }
                    jpdialog.setTitle(str);
                    jpdialog.setMessage(msg[0]);
                    for (int j = 1; j < msg.length; j++) {
                        RadioButton radioButton = new RadioButton(olPlayHall);
                        radioButton.setText(msg[j]);
                        radioButton.setTextSize(13);
                        radioButton.setTag(j - 1);
                        radioButton.setHeight(100);
                        jpdialog.addRadioButton(radioButton);
                    }
                    jpdialog.setFirstButton(str2, (dialog, which) -> {
                        int checkedId = jpdialog.getRadioGroupCheckedId();
                        if (checkedId == -1 && msg.length > 1) {
                            Toast.makeText(olPlayHall, "请选择一首考级曲", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            if (i == 1) {
                                OnlineClTestDTO.Builder builder = OnlineClTestDTO.newBuilder();
                                builder.setType(1);
                                builder.setSongIndex(checkedId);
                                olPlayHall.jpprogressBar.show();
                                olPlayHall.sendMsg(40, builder.build());
                            }
                        }
                    });
                    if (i == 1) {
                        jpdialog.setSecondButton("取消", new DialogDismissClick());
                    }
                    try {
                        jpdialog.showDialog();
                    } catch (Exception ignored) {
                    }
                });
                return;
            case 12:
                post(() -> olPlayHall.mo2827a(message.getData()));
                return;
            case 13:
                post(() -> {
                    Bundle data15 = message.getData();
                    Intent intent12 = new Intent(olPlayHall, PianoPlay.class);
                    intent12.putExtra("head", 3);
                    intent12.putExtra("songBytes", data15.getString("songBytes"));
                    intent12.putExtra("times", data15.getInt("songsID"));
                    intent12.putExtra("hand", data15.getInt("hand"));
                    intent12.putExtra("name", "");
                    intent12.putExtra("bundle", olPlayHall.hallInfoBundle);
                    intent12.putExtra("bundleHall", olPlayHall.hallInfoBundle);
                    olPlayHall.startActivity(intent12);
                    olPlayHall.finish();
                });
                return;
            case 21:
                post(() -> {
                    Toast.makeText(olPlayHall, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent();
                    intent1.setClass(olPlayHall, OLMainMode.class);
                    olPlayHall.startActivity(intent1);
                    olPlayHall.finish();
                });
                return;
            case 23:
                post(() -> olPlayHall.showInfoDialog(message.getData()));
                return;
            default:
        }
    }
}
