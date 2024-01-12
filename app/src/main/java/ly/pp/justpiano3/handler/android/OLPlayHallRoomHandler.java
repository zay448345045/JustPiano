package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLMainMode;
import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.adapter.DailyTimeAdapter;
import ly.pp.justpiano3.adapter.FamilyAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.utils.BizUtil;
import ly.pp.justpiano3.utils.DialogUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineDailyDTO;
import protobuf.dto.OnlineEnterHallDTO;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

public final class OLPlayHallRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayHallRoomHandler(OLPlayHallRoom olPlayHallRoom) {
        super(Looper.getMainLooper());
        weakReference = new WeakReference<>(olPlayHallRoom);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) weakReference.get();
        switch (message.what) {
            case 0 -> post(() -> {
                olPlayHallRoom.hallList.clear();
                Bundle data = message.getData();
                Bundle bundle = data.getBundle("L");
                int size = bundle.size();
                for (int i = 0; i < size; i++) {
                    olPlayHallRoom.hallList.add(bundle.getBundle(String.valueOf(i)));
                }
                olPlayHallRoom.sortListAndBindAdapter(olPlayHallRoom.hallListView, olPlayHallRoom.hallList);
                olPlayHallRoom.userNameTextView.setText(data.getString("U"));
                OLBaseActivity.kitiName = data.getString("U");
                int lv = data.getInt("LV");
                olPlayHallRoom.userExpView.setText("经验值:" + data.getInt("E") + "/" + BizUtil.getTargetExp(lv));
                olPlayHallRoom.userLevelView.setText("LV." + lv);
                int cl = data.getInt("CL");
                olPlayHallRoom.userClassView.setText("CL." + cl);
                olPlayHallRoom.userClassView.setTextColor(ContextCompat.getColor(olPlayHallRoom, Consts.colors[cl]));
                olPlayHallRoom.userClassNameView.setText(Consts.nameCL[cl]);
                olPlayHallRoom.userClassNameView.setTextColor(ContextCompat.getColor(olPlayHallRoom, Consts.colors[cl]));
                olPlayHallRoom.coupleType = data.getInt("CP");
                olPlayHallRoom.familyID = data.getString("I");
                if (olPlayHallRoom.coupleType >= 0 && olPlayHallRoom.coupleType <= 3) {
                    olPlayHallRoom.coupleView.setImageResource(Consts.couples[olPlayHallRoom.coupleType]);
                }
                ImageLoadUtil.setFamilyImageBitmap(olPlayHallRoom, olPlayHallRoom.familyID, olPlayHallRoom.familyView);
                olPlayHallRoom.lv = data.getInt("LV");
                olPlayHallRoom.userSex = data.getString("S");
                olPlayHallRoom.userTrousersIndex = data.getInt("DR_T");
                olPlayHallRoom.userJacketIndex = data.getInt("DR_J");
                olPlayHallRoom.userHairIndex = data.getInt("DR_H");
                olPlayHallRoom.userEyeIndex = data.getInt("DR_E");
                olPlayHallRoom.userShoesIndex = data.getInt("DR_S");
                ImageLoadUtil.setUserDressImageBitmap(olPlayHallRoom, olPlayHallRoom.userSex, olPlayHallRoom.userTrousersIndex,
                        olPlayHallRoom.userJacketIndex, olPlayHallRoom.userHairIndex, olPlayHallRoom.userEyeIndex, olPlayHallRoom.userShoesIndex,
                        olPlayHallRoom.userModView, olPlayHallRoom.userTrousersView, olPlayHallRoom.userJacketsView,
                        olPlayHallRoom.userHairView, olPlayHallRoom.userEyeView, olPlayHallRoom.userShoesView);
                int mailCount = data.getInt("M");
                if (mailCount > 0) {
                    olPlayHallRoom.mailCountsView.setText(String.valueOf(mailCount));
                }
            });
            case 1 -> post(() -> {
                Bundle bundle = message.getData();
                if (bundle.getInt("T") == 0) {
                    Intent intent = new Intent(olPlayHallRoom, OLPlayHall.class);
                    intent.putExtras(bundle);
                    olPlayHallRoom.startActivity(intent);
                    olPlayHallRoom.finish();
                    return;
                }
                olPlayHallRoom.buildDialog(bundle.getInt("T"), bundle.getString("N"), bundle.getString("I"));
            });
            case 2 -> post(() -> {
                Bundle bundle = message.getData();
                int size = bundle.size() - 7;
                if (size == 0) {
                    olPlayHallRoom.familyPageNum--;
                }
                int preSize = olPlayHallRoom.familyList.size();
                for (int i = 0; i < size; i++) {
                    Map<String, Object> familyDataMap = new HashMap<>();
                    familyDataMap.put("C", bundle.getBundle(String.valueOf(i)).getString("C"));
                    familyDataMap.put("N", bundle.getBundle(String.valueOf(i)).getString("N"));
                    familyDataMap.put("T", bundle.getBundle(String.valueOf(i)).getString("T"));
                    familyDataMap.put("U", bundle.getBundle(String.valueOf(i)).getString("U"));
                    familyDataMap.put("I", bundle.getBundle(String.valueOf(i)).getString("I"));
                    familyDataMap.put("J", bundle.getBundle(String.valueOf(i)).getByteArray("J"));
                    familyDataMap.put("P", String.valueOf(i + 1 + preSize));
                    olPlayHallRoom.familyList.add(familyDataMap);
                }
                FamilyAdapter familyAdapter = (FamilyAdapter) olPlayHallRoom.pullUpdateListView.getAdapter();
                if (familyAdapter == null) {
                    olPlayHallRoom.bindAdapter(olPlayHallRoom.pullUpdateListView, olPlayHallRoom.familyList);
                } else {
                    olPlayHallRoom.updateFamilyListShow(familyAdapter, olPlayHallRoom.pullUpdateListView, olPlayHallRoom.familyList);
                }
                olPlayHallRoom.myFamilyPosition.setText(bundle.getString("P"));
                olPlayHallRoom.myFamilyName.setText(bundle.getString("N"));
                olPlayHallRoom.myFamilyContribution.setText(bundle.getString("C"));
                olPlayHallRoom.myFamilyCount.setText(bundle.getString("U") + "/" + bundle.getString("T"));
                olPlayHallRoom.familyID = bundle.getString("I");
                olPlayHallRoom.myFamilyPicArray = bundle.getByteArray("J");
                if (olPlayHallRoom.myFamilyPicArray == null || olPlayHallRoom.myFamilyPicArray.length <= 1) {
                    ImageLoadUtil.familyBitmapCacheMap.put(olPlayHallRoom.familyID, null);
                    olPlayHallRoom.myFamilyPic.setImageResource(R.drawable.family);
                } else {
                    Bitmap myFamilyBitmap = BitmapFactory.decodeByteArray(olPlayHallRoom.myFamilyPicArray,
                            0, olPlayHallRoom.myFamilyPicArray.length);
                    olPlayHallRoom.myFamilyPic.setImageBitmap(myFamilyBitmap);
                    ImageLoadUtil.familyBitmapCacheMap.put(olPlayHallRoom.familyID, myFamilyBitmap);
                    ThreadPoolUtil.execute(() -> {
                        File file = new File(olPlayHallRoom.getFilesDir(), olPlayHallRoom.familyID + ".webp");
                        try {
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            OutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(olPlayHallRoom.myFamilyPicArray);
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
            case 3 -> post(() -> {
                olPlayHallRoom.friendList.clear();
                olPlayHallRoom.jpprogressBar.dismiss();
                Bundle bundle = message.getData();
                int size = bundle.size();
                for (int i = 0; i < size; i++) {
                    olPlayHallRoom.friendList.add(bundle.getBundle(String.valueOf(i)));
                }
                olPlayHallRoom.updateUserListShow(olPlayHallRoom.friendListView, olPlayHallRoom.friendList);
                olPlayHallRoom.pageIsEnd = size < 20;
            });
            case 4 -> {
                post(() -> {
                    olPlayHallRoom.jpprogressBar.dismiss();
                    olPlayHallRoom.mailList.clear();
                    Bundle data = message.getData();
                    int size = data.size();
                    for (int i = 0; i < size; i++) {
                        olPlayHallRoom.mailList.add(data.getBundle(String.valueOf(i)));
                    }
                    try {
                        JSONArray jSONArray = new JSONArray(olPlayHallRoom.sharedPreferences.getString("mailsString", "[]"));
                        for (int i = 0; i < jSONArray.length(); i++) {
                            Bundle bundle = new Bundle();
                            JSONObject jSONObject = jSONArray.getJSONObject(i);
                            if (jSONObject.has("type")) {
                                bundle.putInt("type", jSONObject.getInt("type"));
                            }
                            bundle.putString("F", jSONObject.getString("F"));
                            bundle.putString("M", jSONObject.getString("M"));
                            bundle.putString("T", jSONObject.getString("T"));
                            olPlayHallRoom.mailList.add(bundle);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (olPlayHallRoom.mailList.size() > 0) {
                        olPlayHallRoom.saveMailToLocal();
                        olPlayHallRoom.updateMailListShow(olPlayHallRoom.mailListView, olPlayHallRoom.mailList);
                    }
                });
            }
            case 5 -> post(() -> {
                olPlayHallRoom.jpprogressBar.dismiss();
                Bundle bundle = message.getData();
                byte hallId = (byte) bundle.getInt("H");
                String title = bundle.getString("Ti");
                String messageStr = bundle.getString("I");
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHallRoom);
                jpDialogBuilder.setTitle(title);
                jpDialogBuilder.setMessage(messageStr);
                if (hallId > 0) {
                    jpDialogBuilder.setFirstButton("进入Ta所在大厅", (dialog, which) -> {
                        dialog.dismiss();
                        OnlineEnterHallDTO.Builder builder = OnlineEnterHallDTO.newBuilder();
                        builder.setHallId(hallId);
                        olPlayHallRoom.sendMsg(OnlineProtocolType.ENTER_HALL, builder.build());
                    });
                    jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                } else {
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                }
                DialogUtil.handleGoldSend(jpDialogBuilder, bundle.getInt("T"), bundle.getString("N"), bundle.getString("F"));
                jpDialogBuilder.buildAndShowDialog();
            });
            case 6 -> post(() -> olPlayHallRoom.setBroadcast(message.getData()));
            case 7 -> post(() -> {
                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                builder.setType(2);
                builder.setName(message.getData().getString("F"));
                olPlayHallRoom.friendList.remove(message.arg1);
                olPlayHallRoom.sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                olPlayHallRoom.updateUserListShow(olPlayHallRoom.friendListView, olPlayHallRoom.friendList);
            });
            case 8 -> post(() -> olPlayHallRoom.tabHost.setCurrentTab(4));
            case 9 -> post(() -> {
                Bundle data = message.getData();
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHallRoom);
                jpDialogBuilder.setTitle("创建家族");
                jpDialogBuilder.setMessage(data.getString("I"));
                if (data.getInt("R") > 0) {
                    jpDialogBuilder.setVisibleEditText(true, "请填写家族名称")
                            .setFirstButton("创建家族", (dialog, which) -> {
                                String name = jpDialogBuilder.getEditTextString();
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
                    jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                } else {
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                }
                jpDialogBuilder.buildAndShowDialog();
            });
            case 10 -> post(() -> {
                switch (message.getData().getInt("R")) {
                    case 0 -> {
                        Toast.makeText(olPlayHallRoom, "家族创建成功!", Toast.LENGTH_SHORT).show();
                        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                        builder.setType(2);
                        builder.setPage(0);
                        olPlayHallRoom.familyPageNum = 0;
                        olPlayHallRoom.familyList.clear();
                        olPlayHallRoom.sendMsg(OnlineProtocolType.FAMILY, builder.build());
                    }
                    case 1 ->
                            Toast.makeText(olPlayHallRoom, "家族创建失败!", Toast.LENGTH_SHORT).show();
                    case 4 ->
                            Toast.makeText(olPlayHallRoom, "家族名称已存在，请重试!", Toast.LENGTH_SHORT).show();
                }
            });
            case 11 -> post(() -> {
                List<Map<String, String>> dailyUserList = new ArrayList<>();
                Bundle data = message.getData();
                boolean disabled = true;
                int size = data.size() - 2;
                for (int i = 0; i < size; i++) {
                    Map<String, String> dailyUserMap = new HashMap<>();
                    String userName = data.getBundle(String.valueOf(i)).getString("N");
                    String bonusGet = data.getBundle(String.valueOf(i)).getString("G");
                    dailyUserMap.put("N", userName);
                    dailyUserMap.put("T", data.getBundle(String.valueOf(i)).getString("T"));
                    dailyUserMap.put("B", data.getBundle(String.valueOf(i)).getString("B"));
                    dailyUserMap.put("G", bonusGet);
                    if (OLBaseActivity.kitiName.equals(userName) && bonusGet.equals("0")) {
                        disabled = false;
                    }
                    dailyUserList.add(dailyUserMap);
                }
                String todayOnlineTime = data.getString("T");
                String tomorrowExp = data.getString("M");
                View accountListView = olPlayHallRoom.getLayoutInflater().inflate(R.layout.account_list, olPlayHallRoom.findViewById(R.id.dialog));
                ListView listView = accountListView.findViewById(R.id.account_list);
                listView.setBackgroundResource(R.color.translent);
                listView.setCacheColorHint(Color.TRANSPARENT);
                listView.setAlwaysDrawnWithCacheEnabled(true);
                listView.setSelector(R.color.translent);
                TextView textView = accountListView.findViewById(R.id.account_msg);
                textView.setVisibility(View.VISIBLE);
                textView.setText("今日您已在线" + todayOnlineTime + "分钟，明日可获得" + tomorrowExp + "\n以下为昨日在线时长排名：");
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayHallRoom).setWidth(375).setTitle("在线奖励")
                        .setSecondButton("取消", (dialog, which) -> dialog.dismiss())
                        .loadInflate(accountListView).setFirstButtonDisabled(disabled).setFirstButton("领取奖励", (dialog, i) -> {
                            dialog.dismiss();
                            olPlayHallRoom.jpprogressBar.show();
                            OnlineDailyDTO.Builder builder = OnlineDailyDTO.newBuilder();
                            builder.setType(2);
                            olPlayHallRoom.sendMsg(OnlineProtocolType.DAILY, builder.build());
                        });
                listView.setAdapter(new DailyTimeAdapter(dailyUserList, olPlayHallRoom.getLayoutInflater(), olPlayHallRoom));
                jpDialogBuilder.buildAndShowDialog();
            });
            case 12 ->
                    post(() -> Toast.makeText(olPlayHallRoom, message.getData().getString("M"), Toast.LENGTH_SHORT).show());
            case 21 -> post(() -> {
                Toast.makeText(olPlayHallRoom, "您已掉线，请检查您的网络再重新登录", Toast.LENGTH_SHORT).show();
                olPlayHallRoom.startActivity(new Intent(olPlayHallRoom, OLMainMode.class));
                olPlayHallRoom.finish();
            });
            case 22 -> post(() -> {
                Bundle bundle = message.getData();
                olPlayHallRoom.coupleBlessView.setText(bundle.getString("IN"));
                olPlayHallRoom.coupleNameView.setText(bundle.getString("U"));
                olPlayHallRoom.coupleLvView.setText("LV." + bundle.getInt("LV"));
                olPlayHallRoom.couplePointsView.setText("祝福点数:" + bundle.getInt("CP"));
                int cl = bundle.getInt("CL");
                olPlayHallRoom.coupleClView.setText("CL." + cl);
                olPlayHallRoom.coupleClView.setTextColor(ContextCompat.getColor(olPlayHallRoom, Consts.colors[cl]));
                olPlayHallRoom.coupleClNameView.setText(Consts.nameCL[cl]);
                olPlayHallRoom.coupleClNameView.setTextColor(ContextCompat.getColor(olPlayHallRoom, Consts.colors[cl]));
                olPlayHallRoom.coupleSex = bundle.getString("S");
                ImageLoadUtil.setUserDressImageBitmap(olPlayHallRoom, olPlayHallRoom.coupleSex, bundle.getInt("DR_T"),
                        bundle.getInt("DR_J"), bundle.getInt("DR_H"), bundle.getInt("DR_E"), bundle.getInt("DR_S"),
                        olPlayHallRoom.coupleModView, olPlayHallRoom.coupleTrousersView, olPlayHallRoom.coupleJacketView,
                        olPlayHallRoom.coupleHairView, olPlayHallRoom.coupleEyeView, olPlayHallRoom.coupleShoesView);
            });
            case 23 -> post(() -> olPlayHallRoom.showInfoDialog(message.getData()));
            default -> {
            }
        }
    }
}
