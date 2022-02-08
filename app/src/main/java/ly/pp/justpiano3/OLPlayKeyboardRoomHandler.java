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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class OLPlayKeyboardRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    OLPlayKeyboardRoomHandler(OLPlayKeyboardRoom olPlayKeyboardRoom) {
        weakReference = new WeakReference<>(olPlayKeyboardRoom);
    }

    @Override
    public final void handleMessage(Message message) {
        OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                case 7:
                    post(() -> {
                        olPlayKeyboardRoom.mo2861a(olPlayKeyboardRoom.playerGrid, message.getData());
                    });
                    return;
                case 2:
                case 4:
                    post(() -> {
                        if (olPlayKeyboardRoom.msgList.size() > olPlayKeyboardRoom.maxListValue) {
                            olPlayKeyboardRoom.msgList.remove(0);
                        }
                        olPlayKeyboardRoom.msgList.add(message.getData());
                        SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(olPlayKeyboardRoom);
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
                                    olPlayKeyboardRoom.mo2862a();
                                    return;
                                } else if (message.getData().getInt("T") == 2) {
                                    writer.write(("[私]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n"));
                                    writer.close();
                                } else if (message.getData().getInt("T") == 1) {
                                    writer.write(("[公]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n"));
                                    writer.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        olPlayKeyboardRoom.mo2862a();
                    });
                    return;
                case 5:
                    post(() -> {
                        byte[] notes = message.getData().getByteArray("NOTES");
                        byte roomPosition = (byte) (notes[0] & 0xF);
                        User user = olPlayKeyboardRoom.jpapplication.getHashmap().get(roomPosition);
                        if (user == null) {
                            return;
                        }
                        boolean midiKeyboardOn = (notes[0] >> 4) > 0;
                        int notesSpeed = ((notes.length - 1) << 8) / OLPlayKeyboardRoom.NOTES_SEND_INTERVAL;
                        olPlayKeyboardRoom.olKeyboardStates[roomPosition - 1].setMidiKeyboardOn(midiKeyboardOn);
                        olPlayKeyboardRoom.olKeyboardStates[roomPosition - 1].setSpeed(notesSpeed);
                        ((KeyboardPlayerStatusAdapter) (olPlayKeyboardRoom.keyboardStatusGrid.getAdapter())).notifyDataSetChanged();
                        ThreadPoolUtils.execute(() -> {
                            for (int i = 1; i < notes.length; i += 3) {
                                int intervalTime = (notes[i] << 2);
                                if (intervalTime > 0) {
                                    try {
                                        Thread.sleep(intervalTime);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                int finalI = i;
                                olPlayKeyboardRoom.runOnUiThread(() -> {
                                    // 此处fireKeyDown的最后一个参数必须为false，否则就会循环发送导致卡死
                                    if (notes[finalI + 2] > 0) {
                                        if (!olPlayKeyboardRoom.olKeyboardStates[roomPosition - 1].isMuted()) {
                                            olPlayKeyboardRoom.jpapplication.playSound(notes[finalI + 1], notes[finalI + 2]);
                                        }
                                        olPlayKeyboardRoom.keyboardView.fireKeyDown(notes[finalI + 1], notes[finalI + 2], user.getKuang(), false);
                                    } else {
                                        olPlayKeyboardRoom.keyboardView.fireKeyUp(notes[finalI + 1], false);
                                    }
                                });
                            }
                            olPlayKeyboardRoom.runOnUiThread(() -> {
                                olPlayKeyboardRoom.olKeyboardStates[roomPosition - 1].setSpeed(0);
                                ((KeyboardPlayerStatusAdapter) (olPlayKeyboardRoom.keyboardStatusGrid.getAdapter())).notifyDataSetChanged();
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
                                    jpdialog.setFirstButton("同意", new AddFriendsClick3(string, olPlayKeyboardRoom));
                                    jpdialog.setSecondButton("拒绝", new RefuseFriendsClick(string, olPlayKeyboardRoom));
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
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("F", data.getString("F"));
                            jSONObject.put("T", 2);
                            olPlayKeyboardRoom.friendPlayerList.remove(message.arg1);
                            olPlayKeyboardRoom.sendMsg((byte) 31, (byte) 0, (byte) 0, jSONObject.toString());
                            olPlayKeyboardRoom.mo2863a(olPlayKeyboardRoom.friendsListView, olPlayKeyboardRoom.friendPlayerList, 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                        try {
                            JSONObject jSONObject = new JSONObject(message.getData().getString("MSG"));
                            int i = jSONObject.getInt("T");
                            int i2 = jSONObject.getInt("CT");
                            String string = jSONObject.getString("C");
                            if (i != 0) {
                                olPlayKeyboardRoom.mo2860a(i, string, i2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
