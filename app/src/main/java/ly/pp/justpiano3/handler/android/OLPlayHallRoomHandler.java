package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayHallRoom;
import ly.pp.justpiano3.adapter.DailyTimeAdapter;
import ly.pp.justpiano3.adapter.FamilyAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.utils.DialogUtil;
import ly.pp.justpiano3.view.JPDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import protobuf.dto.OnlineDailyDTO;
import protobuf.dto.OnlineEnterHallDTO;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class OLPlayHallRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayHallRoomHandler(OLPlayHallRoom olPlayHallRoom) {
        weakReference = new WeakReference<>(olPlayHallRoom);
    }

    @Override
    public void handleMessage(Message message) {
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
                        olPlayHallRoom.userName.setText(data.getString("U"));
                        JPApplication.kitiName = data.getString("U");
                        int lv = data.getInt("LV");
                        int targetExp = (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
                        olPlayHallRoom.userExpView.setText("经验值:" + data.getInt("E") + "/" + targetExp);
                        olPlayHallRoom.userLevelView.setText("LV." + lv);
                        i = data.getInt("CL");
                        olPlayHallRoom.userClassView.setText("CL." + i);
                        olPlayHallRoom.userClassView.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.userClassNameView.setText(Consts.nameCL[i]);
                        olPlayHallRoom.userClassNameView.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.cp = data.getInt("CP");
                        olPlayHallRoom.familyID = data.getString("I");
                        if (olPlayHallRoom.cp >= 0 && olPlayHallRoom.cp <= 3) {
                            olPlayHallRoom.coupleView.setImageResource(Consts.couples[olPlayHallRoom.cp]);
                        }
                        if (!olPlayHallRoom.familyID.equals("0")) {
                            File file = new File(olPlayHallRoom.getFilesDir(), olPlayHallRoom.familyID + ".jpg");
                            if (file.exists()) {
                                try (InputStream inputStream = new FileInputStream(file)) {
                                    int length = (int) file.length();
                                    byte[] pic = new byte[length];
                                    inputStream.read(pic);
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
                        olPlayHallRoom.userSex = data.getString("S");
                        olPlayHallRoom.loadDress(data.getInt("DR_H"), data.getInt("DR_E"),
                                data.getInt("DR_J"), data.getInt("DR_T"), data.getInt("DR_S"));
                        i = data.getInt("M");
                        if (i > 0) {
                            olPlayHallRoom.mailCountsView.setText(String.valueOf(i));
                        }
                    });
                    return;
                case 1:
                    post(() -> {
                        Bundle data = message.getData();
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
                        olPlayHallRoom.pageIsEnd = size < 20;
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
                        String title = data.getString("Ti");
                        String messageStr = data.getString("I");
                        String str = "确定";
                        JPDialog jpdialog = new JPDialog(olPlayHallRoom);
                        jpdialog.setTitle(title);
                        if (b > 0) {
                            jpdialog.setMessage(messageStr);
                            jpdialog.setFirstButton("进入Ta所在大厅", (dialog, which) -> {
                                dialog.dismiss();
                                OnlineEnterHallDTO.Builder builder = OnlineEnterHallDTO.newBuilder();
                                builder.setHallId(b);
                                olPlayHallRoom.sendMsg(OnlineProtocolType.ENTER_HALL, builder.build());
                            });
                            jpdialog.setSecondButton("取消", new DialogDismissClick());
                        } else {
                            jpdialog.setMessage(messageStr);
                            jpdialog.setFirstButton(str, new DialogDismissClick());
                        }
                        DialogUtil.handleGoldSend(olPlayHallRoom.jpApplication, jpdialog, data.getInt("T"), data.getString("N"), data.getString("F"));
                        jpdialog.showDialog();
                    });
                    return;
                case 6:
                    post(() -> olPlayHallRoom.mo2842a(message.getData()));
                    return;
                case 7:
                    post(() -> {
                        Bundle data = message.getData();
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(2);
                        builder.setName(data.getString("F"));
                        olPlayHallRoom.friendList.remove(message.arg1);
                        olPlayHallRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        olPlayHallRoom.mo2846b(olPlayHallRoom.friendListView, olPlayHallRoom.friendList);
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
                            jpdialog.setVisibleEditText(true, "请填写家族名称");
                            jpdialog.setFirstButton("创建家族", (dialog, which) -> {
                                String name = jpdialog.getEditTextString();
                                if (name.isEmpty()) {
                                    Toast.makeText(olPlayHallRoom, "家族名称不能为空!", Toast.LENGTH_SHORT).show();
                                } else if (name.length() > 8) {
                                    Toast.makeText(olPlayHallRoom, "家族名称只能在八个字以下!", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                                    builder.setType(4);
                                    builder.setFamilyName(name);
                                    olPlayHallRoom.sendMsg(OnlineProtocolType.FAMILY, builder.build());
                                }
                            });
                            jpdialog.setSecondButton("取消", new DialogDismissClick());
                        } else {
                            jpdialog.setFirstButton("确定", new DialogDismissClick());
                        }
                        jpdialog.showDialog();
                    });
                    return;
                case 10:
                    post(() -> {
                        int result = message.getData().getInt("R");
                        switch (result) {
                            case 0:
                                Toast.makeText(olPlayHallRoom, "家族创建成功!", Toast.LENGTH_SHORT).show();
                                OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                                builder.setType(2);
                                builder.setPage(0);
                                olPlayHallRoom.familyPageNum = 0;
                                olPlayHallRoom.familyList.clear();
                                olPlayHallRoom.sendMsg(OnlineProtocolType.FAMILY, builder.build());
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
                case 11:
                    post(() -> {
                        List<HashMap> list = new ArrayList<>();
                        Bundle data = message.getData();
                        boolean disabled = true;
                        int size = data.size() - 2;
                        for (int i = 0; i < size; i++) {
                            HashMap hashMap = new HashMap();
                            String name = data.getBundle(String.valueOf(i)).getString("N");
                            String bonusGet = data.getBundle(String.valueOf(i)).getString("G");
                            hashMap.put("N", name);
                            hashMap.put("T", data.getBundle(String.valueOf(i)).getString("T"));
                            hashMap.put("B", data.getBundle(String.valueOf(i)).getString("B"));
                            hashMap.put("G", bonusGet);
                            if (JPApplication.kitiName.equals(name) && bonusGet.equals("0")) {
                                disabled = false;
                            }
                            list.add(hashMap);
                        }
                        String todayOnlineTime = data.getString("T");
                        String tomorrowExp = data.getString("M");
                        View inflate = olPlayHallRoom.getLayoutInflater().inflate(R.layout.account_list, olPlayHallRoom.findViewById(R.id.dialog));
                        ListView listView = inflate.findViewById(R.id.account_list);
                        listView.setBackgroundResource(R.color.translent);
                        listView.setCacheColorHint(0);
                        listView.setAlwaysDrawnWithCacheEnabled(true);
                        listView.setSelector(R.color.translent);
                        TextView textView = inflate.findViewById(R.id.account_msg);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("今日您已在线" + todayOnlineTime + "分钟，明日可获得" + tomorrowExp + "\n以下为昨日在线时长排名：");
                        JPDialog jpDialog = new JPDialog(olPlayHallRoom).setTitle("在线奖励").setSecondButton("取消", new DialogDismissClick())
                                .loadInflate(inflate).setFirstButtonDisabled(disabled).setFirstButton("领取奖励", (dialog, i) -> {
                                    dialog.dismiss();
                                    olPlayHallRoom.jpprogressBar.show();
                                    OnlineDailyDTO.Builder builder = OnlineDailyDTO.newBuilder();
                                    builder.setType(2);
                                    olPlayHallRoom.sendMsg(OnlineProtocolType.DAILY, builder.build());
                                });
                        listView.setAdapter(new DailyTimeAdapter(list, olPlayHallRoom.getLayoutInflater(), olPlayHallRoom));
                        jpDialog.showDialog();
                    });
                    return;
                case 12:
                    post(() -> {
                        String info = message.getData().getString("M");
                        Toast.makeText(olPlayHallRoom, info, Toast.LENGTH_SHORT).show();
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
                        olPlayHallRoom.coupleBlessView.setText(data.getString("IN"));
                        olPlayHallRoom.coupleNameView.setText(data.getString("U"));
                        olPlayHallRoom.coupleLvView.setText("LV." + data.getInt("LV"));
                        olPlayHallRoom.couplePointsView.setText("祝福点数:" + data.getInt("CP"));
                        int i = data.getInt("CL");
                        olPlayHallRoom.coupleClView.setText("CL." + i);
                        olPlayHallRoom.coupleClView.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.coupleClNameView.setText(Consts.nameCL[i]);
                        olPlayHallRoom.coupleClNameView.setTextColor(olPlayHallRoom.getResources().getColor(Consts.colors[i]));
                        olPlayHallRoom.coupleSex = data.getString("S");
                        olPlayHallRoom.loadClothes(data.getInt("DR_H"), data.getInt("DR_E"),
                                data.getInt("DR_J"), data.getInt("DR_T"), data.getInt("DR_S"));
                    });
                    return;
                case 23:
                    post(() -> olPlayHallRoom.showInfoDialog(message.getData()));
                    return;
                default:
            }
        }
    }
}
