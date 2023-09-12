package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.os.*;
import android.text.Selection;
import android.text.Spannable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.google.protobuf.MessageLite;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.ChattingAdapter;
import ly.pp.justpiano3.adapter.ExpressAdapter;
import ly.pp.justpiano3.adapter.MainGameAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.listener.*;
import ly.pp.justpiano3.listener.tab.PlayRoomTabChange;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.thread.TimeUpdateThread;
import ly.pp.justpiano3.utils.*;
import ly.pp.justpiano3.view.JPDialog;
import org.json.JSONObject;
import protobuf.dto.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 房间
 */
public class OLPlayRoomActivity extends OLBaseActivity implements Handler.Callback, View.OnClickListener {
    public int lv;
    public int cl;
    public Handler handler;
    public byte hallID0;
    public String hallName;
    public List<Bundle> friendPlayerList = new ArrayList<>();
    public boolean canNotNextPage;
    public TextView sendText;
    public List<Bundle> msgList = new ArrayList<>();
    public int maxListValue = 100;
    public GridView playerGrid;
    public List<Bundle> invitePlayerList = new ArrayList<>();
    public TabHost roomTabs;
    public boolean isOnStart = true;
    public String userTo = "";
    public ListView playerListView;
    public ListView friendsListView;
    public int page;
    public byte roomID0;
    public TextView roomNameView;
    public String roomName;
    public JPApplication jpapplication;
    public String playerKind = "";
    public ConnectionService connectionService;
    public Bundle bundle0;
    public Bundle bundle2;
    public boolean timeUpdateRunning;
    public ListView msgListView;
    public ImageView expressImageView;
    public LayoutInflater layoutInflater;
    public final List<Bundle> playerList = new ArrayList<>();
    public PopupWindow expressWindow;
    public PopupWindow changeColor;
    public TextView timeTextView;
    public int colorNum = 99;
    public TimeUpdateThread timeUpdateThread;
    public ImageView changeColorButton;

    protected void showCpDialog(String str, String str2) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_couple_dialog, findViewById(R.id.dialog));
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONObject jSONObject2 = jSONObject.getJSONObject("P");
            User user = new User(jSONObject2.getString("N"), jSONObject2.getInt("D_H"),
                    jSONObject2.getInt("D_E"), jSONObject2.getInt("D_J"),
                    jSONObject2.getInt("D_T"), jSONObject2.getInt("D_S"),
                    jSONObject2.getString("S"), jSONObject2.getInt("L"), jSONObject2.getInt("C"));
            JSONObject jSONObject3 = jSONObject.getJSONObject("C");
            User user2 = new User(jSONObject3.getString("N"), jSONObject3.getInt("D_H"),
                    jSONObject3.getInt("D_E"), jSONObject3.getInt("D_J"),
                    jSONObject3.getInt("D_T"), jSONObject3.getInt("D_S"),
                    jSONObject3.getString("S"), jSONObject3.getInt("L"), jSONObject3.getInt("C"));
            JSONObject jSONObject4 = jSONObject.getJSONObject("I");
            TextView textView = inflate.findViewById(R.id.ol_player_level);
            TextView textView2 = inflate.findViewById(R.id.ol_player_class);
            TextView textView3 = inflate.findViewById(R.id.ol_player_clname);
            TextView textView4 = inflate.findViewById(R.id.ol_couple_name);
            TextView textView5 = inflate.findViewById(R.id.ol_couple_level);
            TextView textView6 = inflate.findViewById(R.id.ol_couple_class);
            TextView textView7 = inflate.findViewById(R.id.ol_couple_clname);
            ImageView imageView = inflate.findViewById(R.id.ol_player_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_player_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_player_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_player_hair);
            ImageView imageView4e = inflate.findViewById(R.id.ol_player_eye);
            ImageView imageView5 = inflate.findViewById(R.id.ol_player_shoes);
            ImageView imageView6 = inflate.findViewById(R.id.ol_couple_mod);
            ImageView imageView7 = inflate.findViewById(R.id.ol_couple_trousers);
            ImageView imageView8 = inflate.findViewById(R.id.ol_couple_jacket);
            ImageView imageView9 = inflate.findViewById(R.id.ol_couple_hair);
            ImageView imageView9e = inflate.findViewById(R.id.ol_couple_eye);
            ImageView imageView10 = inflate.findViewById(R.id.ol_couple_shoes);
            TextView textView8 = inflate.findViewById(R.id.couple_bless);
            ImageView imageView11 = inflate.findViewById(R.id.couple_type);
            ((TextView) inflate.findViewById(R.id.ol_player_name)).setText(user.getPlayerName());
            textView.setText("LV." + user.getLevel());
            textView2.setText("CL." + user.getClevel());
            textView3.setText(Consts.nameCL[user.getClevel()]);
            textView4.setText(user2.getPlayerName());
            textView5.setText("LV." + user2.getLevel());
            textView6.setText("CL." + user2.getClevel());
            textView7.setText(Consts.nameCL[user2.getClevel()]);
            textView8.setText(jSONObject4.getString("B"));
            imageView11.setImageResource(Consts.couples[jSONObject4.getInt("T")]);
            DialogUtil.setUserDressImageBitmap(this, user, imageView, imageView2, imageView3, imageView4, imageView4e, imageView5);
            DialogUtil.setUserDressImageBitmap(this, user2, imageView6, imageView7, imageView8, imageView9, imageView9e, imageView10);
            new JPDialog(this).setTitle(str).loadInflate(inflate).setFirstButton("祝福:" + jSONObject4.getInt("P"), (dialog, which) -> {
                try {
                    OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                    builder.setType(5);
                    builder.setRoomPosition(jSONObject4.getInt("I"));
                    sendMsg(OnlineProtocolType.COUPLE, builder.build());
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).setSecondButton("取消", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_info_dialog, findViewById(R.id.dialog));
        try {
            User user = new User(b.getString("U"), b.getInt("DR_H"), b.getInt("DR_E"), b.getInt("DR_J"),
                    b.getInt("DR_T"), b.getInt("DR_S"), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView4e = inflate.findViewById(R.id.ol_user_eye);
            ImageView imageView5 = inflate.findViewById(R.id.ol_user_shoes);
            TextView textView = inflate.findViewById(R.id.user_info);
            TextView textView2 = inflate.findViewById(R.id.user_psign);
            DialogUtil.setUserDressImageBitmap(this, user, imageView, imageView2, imageView3, imageView4, imageView4e, imageView5);
            int lv = b.getInt("LV");
            int targetExp = (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
            textView.setText("用户名称:" + b.getString("U")
                    + "\n用户等级:Lv." + lv
                    + "\n经验进度:" + b.getInt("E") + "/" + targetExp
                    + "\n考级进度:Cl." + b.getInt("CL")
                    + "\n所在家族:" + b.getString("F")
                    + "\n在线曲库冠军数:" + b.getInt("W")
                    + "\n在线曲库弹奏总分:" + b.getInt("SC"));
            textView2.setText("个性签名:\n" + (b.getString("P").isEmpty() ? "无" : b.getString("P")));
            new JPDialog(this).setTitle("个人资料").loadInflate(inflate).setFirstButton("加为好友", new AddFriendsClick(this, user.getPlayerName())).setSecondButton("确定", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(int type, MessageLite msg) {
        if (connectionService != null) {
            connectionService.writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public void putRoomPlayerMap(byte b, User User) {
        jpapplication.getRoomPlayerMap().put(b, User);
    }

    public void mo2863a(ListView listView, List<Bundle> list, int i) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        listView.setAdapter(new MainGameAdapter(list, (JPApplication) getApplicationContext(), i, this));
    }

    public void sendMail(String str) {
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        TextView textView3 = inflate.findViewById(R.id.title_2);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView2.setText("内容:");
        new JPDialog(this).setTitle("发送私信给:" + str).loadInflate(inflate)
                .setFirstButton("发送", new SendMailClick(this, textView, str))
                .setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public void setPrivateChatUserName(String str) {
        userTo = "@" + str + ":";
        if (!str.isEmpty() && !str.equals(JPApplication.kitiName)) {
            sendText.setText(userTo);
        }
        CharSequence text = sendText.getText();
        if (text instanceof Spannable) {
            Selection.setSelection((Spannable) text, text.length());
        }
    }

    @Override
    public void onBackPressed() {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle("提示");
        jpdialog.setMessage("退出房间并返回大厅?");
        jpdialog.setFirstButton("确定", new ReturnHallClick(this));
        jpdialog.setSecondButton("取消", new DialogDismissClick());
        jpdialog.showDialog();
    }

    protected void sendMailClick() {
        String str;
        OnlineRoomChatDTO.Builder builder2 = OnlineRoomChatDTO.newBuilder();
        str = String.valueOf(sendText.getText());
        if (!str.startsWith(userTo) || str.length() <= userTo.length()) {
            builder2.setUserName("");
            builder2.setMessage(str);
        } else {
            builder2.setUserName(userTo);
            str = str.substring(userTo.length());
            builder2.setMessage(str);
        }
        sendText.setText("");
        builder2.setColor(colorNum);
        if (!str.isEmpty()) {
            sendMsg(OnlineProtocolType.ROOM_CHAT, builder2.build());
        }
        userTo = "";
    }

    protected void changeChatColor(int lv, int colorNum, int color) {
        if (this.lv >= lv) {
            sendText.setTextColor(color);
            this.colorNum = colorNum;
        } else {
            Toast.makeText(this, "您的等级未达到" + lv + "级，不能使用该颜色!", Toast.LENGTH_SHORT).show();
        }
        if (changeColor != null) {
            changeColor.dismiss();
        }
    }

    protected void changeRoomTitleClick() {
        if (playerKind.equals("G")) {
            Toast.makeText(this, "只有房主才能修改房名!", Toast.LENGTH_SHORT).show();
        } else {
            View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
            EditText text1 = inflate.findViewById(R.id.text_1);
            EditText text2 = inflate.findViewById(R.id.text_2);
            new JPDialog(this).setTitle("修改房名").loadInflate(inflate).setFirstButton("修改", new ChangeRoomNameClick(this, text1, text2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
        }
    }

    protected void nextFriendPageClick() {
        if (!canNotNextPage) {
            page += 20;
            if (page >= 0) {
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(page);
                sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
            }
        }
    }

    protected void onlineFriendListClick() {
        OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
        builder.setType(1);
        builder.setPage(-1);
        sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
    }

    protected void preFriendPageClick() {
        page -= 20;
        if (page < 0) {
            page = 0;
            return;
        }
        OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
        builder.setType(1);
        builder.setPage(page);
        sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
    }

    protected void changeColorClick() {
        if (changeColor != null) {
            int[] iArr = new int[2];
            changeColorButton.getLocationOnScreen(iArr);
            changeColor.showAtLocation(changeColorButton, Gravity.TOP | Gravity.START, iArr[0] / 2 - 30, (int) (iArr[1] * 0.84f));
        }
    }

    public void bindMsgListView(boolean showChatTime) {
        int position = msgListView.getFirstVisiblePosition();
        msgListView.setAdapter(new ChattingAdapter(jpapplication, msgList, layoutInflater, showChatTime));
        if (position > 0) {
            msgListView.setSelection(position + 2);
        } else {
            msgListView.setSelection(msgListView.getBottom());
        }
    }

    public void handleKicked() {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setCancelableFalse();
        jpdialog.setTitle("提示").setMessage("您已被房主移出房间!").setFirstButton("确定", (dialog, which) -> {
            isOnStart = false;
            Intent intent = new Intent(this, OLPlayHall.class);
            Bundle bundle = new Bundle();
            bundle.putString("hallName", hallName);
            bundle.putByte("hallID", hallID0);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }).showDialog();
    }

    public void handleChat(Message message) {
        if (msgList.size() > maxListValue) {
            msgList.remove(0);
        }
        String time = "";
        if (GlobalSetting.INSTANCE.getShowChatTime()) {
            time = DateUtil.format(new Date(EncryptUtil.getServerTime()), "HH:mm");
        }
        message.getData().putString("TIME", time);
        // 如果聊天人没在屏蔽名单中，则将聊天消息加入list进行渲染展示
        if (!ChatBlackUserUtil.isUserInChatBlackList(jpapplication.getChatBlackList(), message.getData().getString("U"))) {
            msgList.add(message.getData());
        }

        // 聊天音效播放
        if (GlobalSetting.INSTANCE.getChatSound() && !message.getData().getString("U").equals(jpapplication.getKitiName())) {
            SoundEngineUtil.playChatSound();
        }

        // 聊天记录存储
        if (GlobalSetting.INSTANCE.getSaveChatRecord()) {
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
                    bindMsgListView(GlobalSetting.INSTANCE.getShowChatTime());
                    return;
                } else if (message.getData().getInt("T") == 2) {
                    writer.write((time + "[私]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + '\n'));
                    writer.close();
                } else if (message.getData().getInt("T") == 1) {
                    writer.write((time + "[公]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + '\n'));
                    writer.close();
                } else if (message.getData().getInt("T") == 18) {
                    writer.write((time + "[全服消息]" + message.getData().getString("U") + ":" + (message.getData().getString("M")) + '\n'));
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bindMsgListView(GlobalSetting.INSTANCE.getShowChatTime());
    }

    public void handleFriendRequest(Message message) {
        String string = message.getData().getString("F");
        switch (message.getData().getInt("T")) {
            case 0:
                if (!string.isEmpty()) {
                    JPDialog jpdialog = new JPDialog(this);
                    jpdialog.setTitle("好友请求");
                    jpdialog.setMessage("[" + string + "]请求加您为好友,同意吗?");
                    String finalString = string;
                    jpdialog.setFirstButton("同意", (dialog, which) -> {
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(1);
                        builder.setReject(false);
                        builder.setName(finalString);
                        sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        dialog.dismiss();
                    });
                    jpdialog.setSecondButton("拒绝", (dialog, which) -> {
                        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                        builder.setType(1);
                        builder.setReject(true);
                        builder.setName(finalString);
                        sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                        dialog.dismiss();
                    });
                    jpdialog.showDialog();
                }
                return;
            case 1:
                DialogUtil.setShowDialog(false);
                string = message.getData().getString("F");
                int i = message.getData().getInt("I");
                JPDialog jpdialog2 = new JPDialog(this);
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
                jpdialog2.showDialog();
                return;
            default:
        }
    }

    public void handleOffline() {
        Toast.makeText(this, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        startActivity(intent);
        finish();
    }

    public void handleSetUserInfo(Message message) {
        Bundle data = message.getData();
        OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
        builder.setType(2);
        builder.setName(data.getString("F"));
        friendPlayerList.remove(message.arg1);
        sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
        mo2863a(friendsListView, friendPlayerList, 1);
    }

    public void handleInvitePlayerList(Message message) {
        doHandlePlayerList(message, invitePlayerList, playerListView);
    }

    public void handlePrivateChat(Message message) {
        roomTabs.setCurrentTab(1);
        String string = message.getData().getString("U");
        if (string != null && !string.equals(JPApplication.kitiName)) {
            userTo = "@" + string + ":";
            sendText.setText(userTo);
            CharSequence text = sendText.getText();
            if (text instanceof Spannable) {
                Selection.setSelection((Spannable) text, text.length());
            }
        }
    }

    public void handleRefreshFriendList(Message message) {
        friendPlayerList.clear();
        Bundle data = message.getData();
        int size = data.size();
        if (size >= 0) {
            for (int i = 0; i < size; i++) {
                friendPlayerList.add(data.getBundle(String.valueOf(i)));
            }
            mo2863a(friendsListView, friendPlayerList, 1);
        }
        canNotNextPage = size < 20;
    }

    public void handleRefreshFriendListWithoutPage(Message message) {
        doHandlePlayerList(message, friendPlayerList, friendsListView);
    }

    private void doHandlePlayerList(Message message, List<Bundle> friendPlayerList, ListView friendsListView) {
        friendPlayerList.clear();
        Bundle data = message.getData();
        int size = data.size();
        if (size >= 0) {
            for (int i = 0; i < size; i++) {
                friendPlayerList.add(data.getBundle(String.valueOf(i)));
            }
            mo2863a(friendsListView, friendPlayerList, 3);
        }
    }

    public void handleDialog(Message message) {
        Bundle data = message.getData();
        String string = data.getString("Ti");
        String string2 = data.getString("I");
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle(string);
        jpdialog.setMessage(string2);
        jpdialog.setFirstButton("确定", new DialogDismissClick());
        DialogUtil.handleGoldSend(jpapplication, jpdialog, data.getInt("T"), data.getString("N"), data.getString("F"));
        jpdialog.showDialog();
    }

    protected void initRoomActivity() {
        layoutInflater = LayoutInflater.from(this);
        jpapplication = (JPApplication) getApplication();
        jpapplication.getRoomPlayerMap().clear();
        connectionService = jpapplication.getConnectionService();
        SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        roomNameView = findViewById(R.id.room_title);
        bundle0 = getIntent().getExtras();
        bundle2 = bundle0.getBundle("bundle");
        hallID0 = bundle2.getByte("hallID");
        hallName = bundle2.getString("hallName");
        roomID0 = bundle0.getByte("ID");
        roomName = bundle0.getString("R");
        playerKind = bundle0.getString("isHost");
        roomNameView.setText("[" + roomID0 + "]" + roomName);
        roomNameView.setOnClickListener(this);
        playerGrid = findViewById(R.id.ol_player_grid);
        playerGrid.setCacheColorHint(0);
        playerGrid.setOnItemClickListener(new PlayerImageItemClick(this));
        playerList.clear();
        msgListView = findViewById(R.id.ol_msg_list);
        msgListView.setCacheColorHint(0);
        Button olSendButton = findViewById(R.id.ol_send_b);
        olSendButton.setOnClickListener(this);
        timeTextView = findViewById(R.id.time_text);
        findViewById(R.id.pre_button).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);
        findViewById(R.id.online_button).setOnClickListener(this);
        friendsListView = findViewById(R.id.ol_friend_list);
        friendsListView.setCacheColorHint(0);
        sendText = findViewById(R.id.ol_send_text);
        expressImageView = findViewById(R.id.ol_express_b);
        changeColorButton = findViewById(R.id.ol_changecolor);
        playerListView = findViewById(R.id.ol_player_list);
        playerListView.setCacheColorHint(0);
        expressImageView.setOnClickListener(this);
        changeColorButton.setOnClickListener(this);
        handler = new Handler(this);
        sendMsg(OnlineProtocolType.LOAD_ROOM_POSITION, OnlineLoadRoomPositionDTO.getDefaultInstance());
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_express_list, null);
        popupWindow.setContentView(inflate);
        ((GridView) inflate.findViewById(R.id.ol_express_grid)).setAdapter(new ExpressAdapter(jpapplication, connectionService, Consts.expressions, popupWindow, 13));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable._none));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        expressWindow = popupWindow;
        PopupWindow popupWindow3 = new PopupWindow(this);
        View inflate3 = LayoutInflater.from(this).inflate(R.layout.ol_changecolor, null);
        popupWindow3.setContentView(inflate3);
        popupWindow3.setBackgroundDrawable(getResources().getDrawable(R.drawable._none));
        popupWindow3.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow3.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        inflate3.findViewById(R.id.white).setOnClickListener(this);
        inflate3.findViewById(R.id.yellow).setOnClickListener(this);
        inflate3.findViewById(R.id.blue).setOnClickListener(this);
        inflate3.findViewById(R.id.red).setOnClickListener(this);
        inflate3.findViewById(R.id.orange).setOnClickListener(this);
        inflate3.findViewById(R.id.purple).setOnClickListener(this);
        inflate3.findViewById(R.id.pink).setOnClickListener(this);
        inflate3.findViewById(R.id.gold).setOnClickListener(this);
        inflate3.findViewById(R.id.green).setOnClickListener(this);
        inflate3.findViewById(R.id.black).setOnClickListener(this);
        popupWindow3.setFocusable(true);
        popupWindow3.setTouchable(true);
        popupWindow3.setOutsideTouchable(true);
        changeColor = popupWindow3;
        roomTabs = findViewById(R.id.tabhost);
        roomTabs.setup();
        TabHost.TabSpec newTabSpec = roomTabs.newTabSpec("tab1");
        newTabSpec.setContent(R.id.friend_tab);
        newTabSpec.setIndicator("好友");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab2");
        newTabSpec.setContent(R.id.msg_tab);
        newTabSpec.setIndicator("聊天");
        roomTabs.addTab(newTabSpec);
        roomTabs.setOnTabChangedListener(new PlayRoomTabChange(this));
        timeUpdateRunning = true;
        timeUpdateThread = new TimeUpdateThread(this);
        timeUpdateThread.start();
    }

    protected void setTabTitleViewLayout(int i) {
        TextView textView = roomTabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        textView.setTextColor(0xffffffff);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == 3) {
            CharSequence format = SimpleDateFormat.getTimeInstance(3, Locale.CHINESE).format(new Date());
            if (timeTextView != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    timeTextView.setText(format + "\n电量:" + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%");
                } else {
                    timeTextView.setText(format);
                    timeTextView.setTextSize(20);
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pre_button:
                preFriendPageClick();
                return;
            case R.id.online_button:
                onlineFriendListClick();
                return;
            case R.id.next_button:
                nextFriendPageClick();
                return;
            case R.id.ol_send_b:
                sendMailClick();
                return;
            case R.id.ol_express_b:
                expressWindow.showAtLocation(expressImageView, Gravity.CENTER, 0, 0);
                return;
            case R.id.ol_changecolor:
                changeColorClick();
                return;
            case R.id.white:
                changeChatColor(0, 48, 0xffffffff);
                return;
            case R.id.yellow:
                changeChatColor(10, 1, 0xFFFFFACD);
                return;
            case R.id.blue:
                changeChatColor(14, 2, 0xFF00FFFF);
                return;
            case R.id.red:
                changeChatColor(18, 3, 0xFFFF6666);
                return;
            case R.id.orange:
                changeChatColor(22, 4, 0xFFFFA500);
                return;
            case R.id.purple:
                changeChatColor(25, 5, 0xFFBA55D3);
                return;
            case R.id.pink:
                changeChatColor(30, 6, 0xFFFA60EA);
                return;
            case R.id.gold:
                changeChatColor(35, 7, 0xFFFFD700);
                return;
            case R.id.green:
                changeChatColor(40, 8, 0xFFB7FF72);
                return;
            case R.id.black:
                changeChatColor(50, 9, 0xFF000000);
                return;
            case R.id.room_title:
                changeRoomTitleClick();
                return;
            default:
        }
    }
}
