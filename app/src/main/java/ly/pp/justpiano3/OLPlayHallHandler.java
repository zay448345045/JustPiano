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

import org.json.JSONException;
import org.json.JSONObject;

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
    public final void handleMessage(Message message) {
        OLPlayHall olPlayHall = (OLPlayHall) weakReference.get();
        switch (message.what) {
            case 1:
                post(() -> {
                    if (olPlayHall.msgList.size() > 100) {
                        olPlayHall.msgList.remove(0);
                    }
                    olPlayHall.msgList.add(message.getData());
                    File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Chats");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    SharedPreferences ds = PreferenceManager.getDefaultSharedPreferences(olPlayHall);
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
                                olPlayHall.mo2828a(olPlayHall.msgListView, olPlayHall.msgList);
                                return;
                            } else if (message.getData().getInt("T") == 2) {
                                writer.write(("[私]" + message.getData().getString("U") + ":" + (message.getData().getString("M"))));
                            } else {
                                writer.write("[公]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + "\n");
                            }
                            writer.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    olPlayHall.mo2828a(olPlayHall.msgListView, olPlayHall.msgList);
                });
                return;
            case 2:
                Intent intent = new Intent(olPlayHall, OLPlayRoom.class);
                Bundle data = message.getData();
                data.putBundle("bundle", olPlayHall.f4381N);
                intent.putExtras(data);
                olPlayHall.jpapplication.setNowSongsName("");
                olPlayHall.startActivity(intent);
                olPlayHall.finish();
                return;
            case 3:
                post(() -> {
                    olPlayHall.f4373F.clear();
                    Bundle data1 = message.getData();
                    int size = data1.size();
                    for (int i = 0; i < size; i++) {
                        olPlayHall.f4373F.add(data1.getBundle(String.valueOf(i)));
                    }
                    olPlayHall.mo2831b(olPlayHall.f4371D, olPlayHall.f4373F);
                });
                return;
            case 4:
                post(() -> Toast.makeText(olPlayHall, message.getData().getString("result"), Toast.LENGTH_SHORT).show());
                return;
            case 5:
                post(() -> {
                    olPlayHall.f4399o.clear();
                    Bundle data16 = message.getData();
                    int size = data16.size();
                    if (size >= 0) {
                        for (int i = 0; i < size; i++) {
                            olPlayHall.f4399o.add(data16.getBundle(String.valueOf(i)));
                        }
                        olPlayHall.mo2829a(olPlayHall.f4382O, olPlayHall.f4399o, 1, true);
                    }
                    olPlayHall.f4388U = size < 20;
                });
                return;
            case 6:
                post(() -> {
                    olPlayHall.tabHost.setCurrentTab(1);
                    String string = message.getData().getString("U");
                    if (!string.equals(JPApplication.kitiName)) {
                        olPlayHall.f4379L = "@" + string + ":";
                        olPlayHall.f4408x.setText(olPlayHall.f4379L);
                        CharSequence text = olPlayHall.f4408x.getText();
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
                    if (size < 20) {
                        olPlayHall.f4402r = true;
                    }
                    if (size >= 0) {
                        for (int i = 0; i < size; i++) {
                            olPlayHall.userInHallList.add(data13.getBundle(String.valueOf(i)));
                        }
                        olPlayHall.mo2829a(olPlayHall.f4395k, olPlayHall.userInHallList, 3, true);
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
                                jpdialog.setFirstButton("同意", new AddFriendsClick4(string, olPlayHall));
                                jpdialog.setSecondButton("拒绝", new RefuseFriendsClick2(string, olPlayHall));
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
                                    JSONObject jSONObject = new JSONObject();
                                    try {
                                        jSONObject.put("I", b);
                                        jSONObject.put("P", data14.getString("P"));
                                        olPlayHall.sendMsg((byte) 7, b, olPlayHall.hallID, jSONObject.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
                        jpdialog.showDialog();
                        olPlayHall.jpapplication.setIsShowDialog(true);
                    }
                });
                return;
            case 10:
                post(() -> {
                    Bundle data12 = message.getData();
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("F", data12.getString("F"));
                        jSONObject.put("T", 2);
                        olPlayHall.f4399o.remove(message.arg1);
                        olPlayHall.sendMsg((byte) 31, (byte) 0, (byte) 0, jSONObject.toString());
                        olPlayHall.mo2829a(olPlayHall.f4382O, olPlayHall.f4399o, 1, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                        radioButton.setHeight(90);
                        jpdialog.addRadioButton(radioButton);
                    }
                    jpdialog.setFirstButton(str2, (dialog, which) -> {
                        int checkedId = jpdialog.getRadioGroupCheckedId();
                        if (checkedId == -1 && msg.length > 1) {
                            Toast.makeText(olPlayHall, "请选择一首考级曲", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            if (i == 1) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("T", 1);
                                    jsonObject.put("S", checkedId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                olPlayHall.jpprogressBar.show();
                                olPlayHall.sendMsg((byte) 40, (byte) 0, (byte) 0, jsonObject.toString());
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
                    intent12.putExtra("bundle", olPlayHall.f4381N);
                    intent12.putExtra("bundleHall", olPlayHall.f4381N);
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
