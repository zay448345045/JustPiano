package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

final class OLPlayHallRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    OLPlayHallRoomHandler(OLPlayHallRoom olPlayHallRoom) {
        weakReference = new WeakReference<>(olPlayHallRoom);
    }

    @Override
    public final void handleMessage(Message message) {
        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) weakReference.get();
        if (olPlayHallRoom != null) {
            switch (message.what) {
                case 0:
                    post(() -> {
                        int i;
                        olPlayHallRoom.hallList.clear();
                        Bundle data = message.getData();
                        Bundle bundle = data.getBundle("L");
                        int size = bundle.size();
                        for (i = 0; i < size; i++) {
                            olPlayHallRoom.hallList.add(bundle.getBundle(String.valueOf(i)));
                        }
                        olPlayHallRoom.mo2843a(olPlayHallRoom.hallListView, olPlayHallRoom.hallList);
                        olPlayHallRoom.userName.setText(data.getString("U"));//载入昵称
                        JPApplication.kitiName = data.getString("U");
                        olPlayHallRoom.userExp.setText("经验值:" + data.getInt("E") + "/" + data.getInt("X"));
                        olPlayHallRoom.userLevel.setText("LV." + data.getInt("LV"));
                        i = data.getInt("CL");
                        olPlayHallRoom.f4415E.setText("CL." + i);
                        olPlayHallRoom.f4415E.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.f4416F.setText(Consts.nameCL[i]);
                        olPlayHallRoom.f4416F.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.cp = data.getInt("CP");
                        olPlayHallRoom.familyID = data.getString("I");
                        if (olPlayHallRoom.cp >= 0 && olPlayHallRoom.cp <= 3) {
                            olPlayHallRoom.coupleView.setImageResource(Consts.couples[olPlayHallRoom.cp]);
                        }
                        if (!olPlayHallRoom.familyID.equals("0")) {
                            File file = new File(olPlayHallRoom.getFilesDir(), olPlayHallRoom.familyID + ".jpg");
                            if (file.exists()) {
                                try {
                                    int length = (int) file.length();
                                    byte[] pic = new byte[length];
                                    new FileInputStream(file).read(pic);
                                    olPlayHallRoom.familyView.setImageBitmap(BitmapFactory.decodeByteArray(pic, 0, pic.length));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                olPlayHallRoom.familyView.setImageResource(R.drawable.family);
                            }
                        }
                        olPlayHallRoom.lv = data.getInt("LV");
                        olPlayHallRoom.cl = data.getInt("CL");
                        olPlayHallRoom.mo2844a(data.getString("DR"));
                        i = data.getInt("M");
                        if (i > 0) {
                            olPlayHallRoom.mailCounts.setText(String.valueOf(i));
                        }
                    });
                    return;
                case 1:
                    post(() -> {
                        Bundle data = message.getData();
                        Integer.valueOf(data.getInt("T"));
                        if (data.getInt("T") == 0) {
                            Intent intent = new Intent(olPlayHallRoom, OLPlayHall.class);
                            intent.putExtras(data);
                            olPlayHallRoom.startActivity(intent);
                            olPlayHallRoom.finish();
                            return;
                        }
                        olPlayHallRoom.mo2841a(data.getInt("T"), data.getString("N"), data.getString("I"));
                    });
                    return;
                case 2:
                    post(() -> {
                        Bundle data = message.getData();
                        int size = data.size() - 7;
                        if (size == 0) {
                            olPlayHallRoom.familyPageNum--;
                        }
                        int preSize = olPlayHallRoom.familyList.size();
                        for (int i = 0; i < size; i++) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("C", data.getBundle(String.valueOf(i)).getString("C"));
                            hashMap.put("N", data.getBundle(String.valueOf(i)).getString("N"));
                            hashMap.put("T", data.getBundle(String.valueOf(i)).getString("T"));
                            hashMap.put("U", data.getBundle(String.valueOf(i)).getString("U"));
                            hashMap.put("I", data.getBundle(String.valueOf(i)).getString("I"));
                            hashMap.put("J", data.getBundle(String.valueOf(i)).getByteArray("J"));
                            hashMap.put("P", String.valueOf(i + 1 + preSize));
                            olPlayHallRoom.familyList.add(hashMap);
                        }
                        FamilyAdapter fa = (FamilyAdapter) olPlayHallRoom.familyListView.getAdapter();
                        if (fa == null) {
                            olPlayHallRoom.mo2907b(olPlayHallRoom.familyListView, olPlayHallRoom.familyList);
                        } else {
                            olPlayHallRoom.mo2905a(fa, olPlayHallRoom.familyListView, olPlayHallRoom.familyList);
                        }
                        olPlayHallRoom.myFamilyPosition.setText(data.getString("P"));
                        olPlayHallRoom.myFamilyName.setText(data.getString("N"));
                        olPlayHallRoom.myFamilyContribution.setText(data.getString("C"));
                        olPlayHallRoom.myFamilyCount.setText(data.getString("U") + "/" + data.getString("T"));
                        olPlayHallRoom.familyID = data.getString("I");
                        olPlayHallRoom.myFamilyPicArray = data.getByteArray("J");
                        if (olPlayHallRoom.myFamilyPicArray == null || olPlayHallRoom.myFamilyPicArray.length <= 1) {
                            olPlayHallRoom.myFamilyPic.setImageResource(R.drawable.family);
                        } else {
                            olPlayHallRoom.myFamilyPic.setImageBitmap(
                                    BitmapFactory.decodeByteArray(olPlayHallRoom.myFamilyPicArray,
                                            0, olPlayHallRoom.myFamilyPicArray.length));
                            File file1 = new File(olPlayHallRoom.getFilesDir(), olPlayHallRoom.familyID + ".jpg");
                            try {
                                if (!file1.exists()) {
                                    file1.createNewFile();
                                }
                                OutputStream outputStream1 = new FileOutputStream(file1);
                                outputStream1.write(olPlayHallRoom.myFamilyPicArray);
                                outputStream1.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                case 3:
                    post(() -> {
                        olPlayHallRoom.friendList.clear();
                        olPlayHallRoom.jpprogressBar.dismiss();
                        Bundle data = message.getData();
                        int size = data.size();
                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                olPlayHallRoom.friendList.add(data.getBundle(String.valueOf(i)));
                            }
                            olPlayHallRoom.mo2846b(olPlayHallRoom.friendListView, olPlayHallRoom.friendList);
                        }
                        olPlayHallRoom.f4467r = size < 20;
                    });
                    return;
                case 4:
                    post(() -> {
                        int i;
                        int i2 = 0;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        olPlayHallRoom.mailList.clear();
                        Bundle data = message.getData();
                        int size = data.size();
                        for (i = 0; i < size; i++) {
                            olPlayHallRoom.mailList.add(data.getBundle(String.valueOf(i)));
                        }
                        try {
                            JSONArray jSONArray = new JSONArray(olPlayHallRoom.sharedPreferences.getString("mailsString", "[]"));
                            i = jSONArray.length();
                            while (i2 < i) {
                                Bundle bundle = new Bundle();
                                JSONObject jSONObject = jSONArray.getJSONObject(i2);
                                if (jSONObject.has("type")) {
                                    bundle.putInt("type", jSONObject.getInt("type"));
                                }
                                bundle.putString("F", jSONObject.getString("F"));
                                bundle.putString("M", jSONObject.getString("M"));
                                bundle.putString("T", jSONObject.getString("T"));
                                olPlayHallRoom.mailList.add(bundle);
                                i2++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (olPlayHallRoom.mailList.size() > 0) {
                            olPlayHallRoom.mo2848c();
                            olPlayHallRoom.mo2849c(olPlayHallRoom.mailListView, olPlayHallRoom.mailList);
                        }
                    });
                    return;
                case 5:
                    post(() -> {
                        olPlayHallRoom.jpprogressBar.dismiss();
                        Bundle data = message.getData();
                        byte b = (byte) data.getInt("H");
                        String string = data.getString("Ti");
                        String string2 = data.getString("I");
                        String str = "确定";
                        JPDialog jpdialog = new JPDialog(olPlayHallRoom);
                        jpdialog.setTitle(string);
                        if (b > 0) {
                            jpdialog.setMessage(string2);
                            jpdialog.setFirstButton("进入Ta所在大厅", new GoIntoItsHall(b, olPlayHallRoom));
                            jpdialog.setSecondButton("取消", new DialogDismissClick());
                        } else {
                            jpdialog.setMessage(string2);
                            jpdialog.setFirstButton(str, new DialogDismissClick());
                        }
                        try {
                            jpdialog.showDialog();
                        } catch (Exception ignored) {
                        }
                    });
                    return;
                case 6:
                    post(() -> olPlayHallRoom.mo2842a(message.getData()));
                    return;
                case 7:
                    post(() -> {
                        Bundle data = message.getData();
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("F", data.getString("F"));
                            jSONObject.put("T", 2);
                            olPlayHallRoom.friendList.remove(message.arg1);
                            olPlayHallRoom.sendMsg((byte) 31, (byte) 0, jSONObject.toString());
                            olPlayHallRoom.mo2846b(olPlayHallRoom.friendListView, olPlayHallRoom.friendList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                case 8:
                    post(() -> olPlayHallRoom.tabHost.setCurrentTab(4));
                    return;
                case 9:
                    post(() -> {
                        Bundle data = message.getData();
                        byte b = (byte) data.getInt("R");
                        String string2 = data.getString("I");
                        JPDialog jpdialog = new JPDialog(olPlayHallRoom);
                        jpdialog.setTitle("创建家族");
                        jpdialog.setMessage(string2);
                        if (b > 0) {
                            jpdialog.setVisibleEditText(true);
                            jpdialog.setFirstButton("创建家族", new CreateFamily(jpdialog, olPlayHallRoom));
                            jpdialog.setSecondButton("取消", new DialogDismissClick());
                        } else {
                            jpdialog.setFirstButton("确定", new DialogDismissClick());
                        }
                        try {
                            jpdialog.showDialog();
                        } catch (Exception ignored) {
                        }
                    });
                    return;
                case 10:
                    post(() -> {
                        int result = message.getData().getInt("R");
                        switch (result) {
                            case 0:
                                Toast.makeText(olPlayHallRoom, "家族创建成功!", Toast.LENGTH_SHORT).show();
                                JSONObject jSONObject = new JSONObject();
                                try {
                                    jSONObject.put("K", 2);
                                    jSONObject.put("B", 0);
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                                olPlayHallRoom.familyPageNum = 0;
                                olPlayHallRoom.familyList.clear();
                                olPlayHallRoom.sendMsg((byte) 18, (byte) 0, jSONObject.toString());
                                break;
                            case 1:
                                Toast.makeText(olPlayHallRoom, "家族创建失败!", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(olPlayHallRoom, "家族名称已存在，请重试!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });
                    return;
                case 21:
                    post(() -> {
                        Toast.makeText(olPlayHallRoom, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(olPlayHallRoom, OLMainMode.class);
                        olPlayHallRoom.startActivity(intent);
                        olPlayHallRoom.finish();
                    });
                    return;
                case 22:
                    post(() -> {
                        Bundle data = message.getData();
                        olPlayHallRoom.coupleBless.setText(data.getString("IN"));
                        olPlayHallRoom.f4438ab.setText(data.getString("U"));
                        olPlayHallRoom.f4441ae.setText("LV." + data.getInt("LV"));
                        olPlayHallRoom.f4442af.setText("祝福点数:" + data.getInt("CP"));
                        int i = data.getInt("CL");
                        olPlayHallRoom.f4443ag.setText("CL." + i);
                        olPlayHallRoom.f4443ag.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.f4444ah.setText(Consts.nameCL[i]);
                        olPlayHallRoom.f4444ah.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.mo2847b(data.getString("DR"));
                    });
                    return;
                default:
            }
        }
    }
}
