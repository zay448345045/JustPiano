package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.*;
import android.widget.TabHost.TabSpec;
import androidx.core.content.res.ResourcesCompat;
import com.google.protobuf.MessageLite;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.ChattingAdapter;
import ly.pp.justpiano3.adapter.ExpressAdapter;
import ly.pp.justpiano3.adapter.MainGameAdapter;
import ly.pp.justpiano3.adapter.RoomTitleAdapter;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.Room;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayHallHandler;
import ly.pp.justpiano3.listener.AddFriendsClick;
import ly.pp.justpiano3.listener.CreateRoomClick;
import ly.pp.justpiano3.listener.RoomPasswordClick2;
import ly.pp.justpiano3.listener.SendMailClick;
import ly.pp.justpiano3.listener.tab.PlayHallTabChange;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.thread.ShowTimeThread;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import protobuf.dto.*;

import java.text.SimpleDateFormat;
import java.util.*;

public final class OLPlayHall extends OLBaseActivity implements Callback, OnClickListener, View.OnLongClickListener {
    public ConnectionService connectionService;
    public String hallName = "";
    public byte hallID = (byte) 0;
    public JPApplication jpapplication;
    public JPProgressBar jpprogressBar;
    public ListView msgListView;
    public ListView roomListView;
    public List<Bundle> roomList = new ArrayList<>();
    public TabHost tabHost;
    public String sendTo = "";
    public Bundle hallInfoBundle;
    public ListView friendListView;
    public boolean isTimeShowing;
    public boolean pageIsEnd = false;
    public Map<Byte, Room> roomTitleMap = new HashMap<>();
    public List<Bundle> msgList = new ArrayList<>();
    public ListView userInHallListView;
    public List<Bundle> userInHallList = new ArrayList<>();
    public List<Bundle> friendList = new ArrayList<>();
    public Handler showTimeHandler;
    public int pageNum = 0;
    public OLPlayHallHandler olPlayHallHandler = new OLPlayHallHandler(this);
    public TextView sendTextView;
    private ImageView imageView;
    private LayoutInflater layoutInflater1;
    private LayoutInflater layoutInflater2;
    private PopupWindow popupWindow = null;
    private ShowTimeThread showTimeThread;
    private TextView timeTextView;

    public void loadInRoomUserInfo(byte b) {
        OnlineLoadRoomUserListDTO.Builder builder = OnlineLoadRoomUserListDTO.newBuilder();
        builder.setRoomId(b);
        connectionService.writeData(OnlineProtocolType.LOAD_ROOM_USER_LIST, builder.build());
    }

    public void sendMsg(int type, MessageLite message) {
        if (connectionService != null) {
            connectionService.writeData(type, message);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public void putRoomToMap(byte b, Room room) {
        roomTitleMap.put(b, room);
    }

    public void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_user_info_dialog, findViewById(R.id.dialog));
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
            ImageLoadUtil.setUserDressImageBitmap(this, user, imageView, imageView2, imageView3, imageView4, imageView4e, imageView5);
            int lv = b.getInt("LV");
            int targetExp = (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
            textView.setText("用户名称:" + b.getString("U")
                    + "\n用户等级:LV." + lv
                    + "\n经验进度:" + b.getInt("E") + "/" + targetExp
                    + "\n考级进度:CL." + b.getInt("CL")
                    + "\n所在家族:" + b.getString("F")
                    + "\n在线曲库冠军数:" + b.getInt("W")
                    + "\n在线曲库弹奏总分:" + b.getInt("SC"));
            textView2.setText("个性签名:\n" + (b.getString("P").isEmpty() ? "无" : b.getString("P")));
            new JPDialogBuilder(this).setWidth(324).setTitle("个人资料").loadInflate(inflate)
                    .setFirstButton("加为好友", new AddFriendsClick(this, user.getPlayerName()))
                    .setSecondButton("确定", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enterRoomHandle(int i, byte b) {
        switch (i) {
            case 0:
                OnlineEnterRoomDTO.Builder builder = OnlineEnterRoomDTO.newBuilder();
                builder.setRoomId(b);
                builder.setPassword("");
                sendMsg(OnlineProtocolType.ENTER_ROOM, builder.build());
                return;
            case 1:
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_2);
                TextView textView1 = inflate.findViewById(R.id.text_1);
                TextView textView2 = inflate.findViewById(R.id.title_1);
                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                textView.setSingleLine(true);
                new JPDialogBuilder(this).setTitle("输入密码").loadInflate(inflate)
                        .setFirstButton("确定", new RoomPasswordClick2(this, textView, b))
                        .setSecondButton("取消", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
                return;
            default:
        }
    }

    public void mo2827a(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(R.layout.room_info, findViewById(R.id.dialog));
        ListView listView = inflate.findViewById(R.id.playerlist);
        Bundle bundle2 = bundle.getBundle("L");
        int size = bundle2.size();
        List<Bundle> arrayList = new ArrayList<>();
        if (size >= 0) {
            for (int i = 0; i < size; i++) {
                arrayList.add(bundle2.getBundle(String.valueOf(i)));
            }
            mo2829a(listView, arrayList, 3, false);
        }
        listView.setCacheColorHint(0);
        listView.setAlwaysDrawnWithCacheEnabled(true);
        int i2 = bundle.getInt("R");
        new JPDialogBuilder(this).setTitle(i2 + "房" + " 房间信息").loadInflate(inflate).setFirstButton("进入房间", (dialog, which) -> {
            dialog.dismiss();
            enterRoomHandle(bundle.getInt("P"), (byte) i2);
        }).setSecondButton("取消", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
    }

    public void mo2828a(ListView listView, List<Bundle> list) {
        int posi = listView.getFirstVisiblePosition();
        listView.setAdapter(new ChattingAdapter(jpapplication, list, layoutInflater1));
        if (posi > 0) {
            listView.setSelection(posi + 2);
        } else {
            msgListView.setSelection(msgListView.getBottom());
        }
    }

    public void mo2829a(ListView listView, List<Bundle> list, int i, boolean z) {
        if (z && list != null && !list.isEmpty()) {
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
        new JPDialogBuilder(this).setTitle("发送私信给:" + str).loadInflate(inflate)
                .setFirstButton("发送", new SendMailClick(this, textView, str))
                .setSecondButton("取消", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
    }

    public void mo2831b(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("I"), o2.getByte("I")));
        }
        RoomTitleAdapter roomTitleAdapter = (RoomTitleAdapter) listView.getAdapter();
        if (roomTitleAdapter == null) {
            listView.setAdapter(new RoomTitleAdapter(list, layoutInflater2, this));
            return;
        }
        roomTitleAdapter.updateList(list);
        roomTitleAdapter.notifyDataSetChanged();
    }

    public void setPrivateChatUserName(String userName) {
        sendTo = "@" + userName + ":";
        if (!userName.isEmpty() && !userName.equals(JPApplication.kitiName)) {
            sendTextView.setText(sendTo);
        }
        CharSequence text = sendTextView.getText();
        if (text instanceof Spannable) {
            Selection.setSelection((Spannable) text, text.length());
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == 3) {
            CharSequence format = SimpleDateFormat.getTimeInstance(3, Locale.CHINESE).format(new Date());
            if (timeTextView != null) {
                timeTextView.setText(format);
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        sendMsg(OnlineProtocolType.QUIT_HALL, OnlineQuitHallDTO.getDefaultInstance());
        startActivity(new Intent(this, OLPlayHallRoom.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ol_createroom_b:
                View inflate = getLayoutInflater().inflate(R.layout.ol_create_room, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.room_name);
                TextView textView2 = inflate.findViewById(R.id.room_password);
                RadioGroup radioGroup = inflate.findViewById(R.id.room_mode);
                CharSequence stringBuilder;
                if (String.valueOf(JPApplication.kitiName).length() == 8) {
                    stringBuilder = String.valueOf(JPApplication.kitiName);
                } else if (String.valueOf(JPApplication.kitiName).length() == 7) {
                    stringBuilder = JPApplication.kitiName + "房";
                } else if (String.valueOf(JPApplication.kitiName).length() == 6) {
                    stringBuilder = JPApplication.kitiName + "琴房";
                } else {
                    stringBuilder = JPApplication.kitiName + "的琴房";
                }
                textView.setText(stringBuilder);
                textView.setSingleLine(true);
                textView2.setSingleLine(true);
                new JPDialogBuilder(this).setTitle("创建房间").loadInflate(inflate)
                        .setFirstButton("确定", new CreateRoomClick(this, textView, textView2, radioGroup))
                        .setSecondButton("取消", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
                return;
            case R.id.ol_testroom_b:
                OnlineClTestDTO.Builder builder2 = OnlineClTestDTO.newBuilder();
                builder2.setType(0);
                jpprogressBar.show();
                sendMsg(OnlineProtocolType.CL_TEST, builder2.build());
                return;
            case R.id.ol_challenge_b:
                Intent intent = new Intent();
                intent.setClass(this, OLChallenge.class);
                intent.putExtra("hallID", hallID);
                intent.putExtra("hallName", hallName);
                startActivity(intent);
                finish();
                return;
            case R.id.pre_button:
                pageNum -= 20;
                if (pageNum < 0) {
                    pageNum = 0;
                    return;
                }
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(pageNum);
                sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                return;
            case R.id.online_button:
                builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(-1);
                sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                return;
            case R.id.next_button:
                if (!pageIsEnd) {
                    pageNum += 20;
                    if (pageNum >= 0) {
                        builder = OnlineLoadUserInfoDTO.newBuilder();
                        builder.setType(1);
                        builder.setPage(pageNum);
                        sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                        return;
                    }
                    return;
                }
                return;
            case R.id.ol_send_b:
                sendHallChat(false);
                return;
            case R.id.ol_express_b:
                popupWindow.showAtLocation(imageView, Gravity.CENTER, 0, 0);
                return;
            default:
        }
    }

    /**
     * 发送大厅消息
     *
     * @param isBroadcast 是否为全服广播类型
     */
    private void sendHallChat(boolean isBroadcast) {
        String str;
        OnlineHallChatDTO.Builder builder1 = OnlineHallChatDTO.newBuilder();
        builder1.setIsBroadcast(isBroadcast);
        str = String.valueOf(sendTextView.getText());
        if (!str.startsWith(sendTo) || str.length() <= sendTo.length()) {
            builder1.setUserName("");
            builder1.setMessage(str);
        } else {
            builder1.setUserName(sendTo);
            str = str.substring(sendTo.length());
            builder1.setMessage(str);
        }
        sendTextView.setText("");
        sendTo = "";
        if (!str.isEmpty()) {
            sendMsg(OnlineProtocolType.HALL_CHAT, builder1.build());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        roomTitleMap.clear();
        hallInfoBundle = getIntent().getExtras();
        hallName = hallInfoBundle.getString("hallName");
        hallID = hallInfoBundle.getByte("hallID");
        layoutInflater1 = LayoutInflater.from(this);
        layoutInflater2 = LayoutInflater.from(this);
        jpapplication = (JPApplication) getApplication();
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.ol_room_list);
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        JPApplication jPApplication = jpapplication;
        jPApplication.setGameMode(LocalPlayModeEnum.NORMAL);
        findViewById(R.id.ol_send_b).setOnClickListener(this);
        findViewById(R.id.ol_send_b).setOnLongClickListener(this);
        findViewById(R.id.ol_createroom_b).setOnClickListener(this);
        findViewById(R.id.ol_testroom_b).setOnClickListener(this);
        sendTextView = findViewById(R.id.ol_send_text);
        TextView playHallTitleView = findViewById(R.id.ol_playhall_title);
        playHallTitleView.setText(hallName);
        showTimeHandler = new Handler(this);
        timeTextView = findViewById(R.id.time_text);
        msgListView = findViewById(R.id.ol_msg_list);
        msgListView.setCacheColorHint(0);
        findViewById(R.id.ol_challenge_b).setOnClickListener(this);
        findViewById(R.id.pre_button).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);
        findViewById(R.id.online_button).setOnClickListener(this);
        msgList.clear();
        roomListView = findViewById(R.id.ol_room_list);
        roomListView.setCacheColorHint(0);
        userInHallListView = findViewById(R.id.ol_player_list);
        userInHallListView.setCacheColorHint(0);
        imageView = findViewById(R.id.ol_express_b);
        imageView.setOnClickListener(this);
        friendListView = findViewById(R.id.ol_friend_list);
        friendListView.setCacheColorHint(0);
        roomList.clear();
        connectionService = jpapplication.getConnectionService();
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_express_list, null);
        popupWindow.setContentView(inflate);
        ((GridView) inflate.findViewById(R.id.ol_express_grid)).setAdapter(
                new ExpressAdapter(jpapplication, connectionService, Consts.expressions, popupWindow, 12));
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        this.popupWindow = popupWindow;
        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        TabSpec newTabSpec = tabHost.newTabSpec("tab1");
        newTabSpec.setContent(R.id.friend_tab);
        newTabSpec.setIndicator("好友");
        tabHost.addTab(newTabSpec);
        newTabSpec = tabHost.newTabSpec("tab2");
        newTabSpec.setContent(R.id.msg_tab);
        newTabSpec.setIndicator("聊天");
        tabHost.addTab(newTabSpec);
        newTabSpec = tabHost.newTabSpec("tab3");
        newTabSpec.setContent(R.id.players_tab);
        newTabSpec.setIndicator("用户");
        tabHost.addTab(newTabSpec);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        for (int i = 0; i < 3; i++) {
            tabHost.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            TextView tv = tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(0xffffffff);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        tabHost.setOnTabChangedListener(new PlayHallTabChange(this));
        tabHost.setCurrentTab(1);
        OnlineLoadRoomListDTO.Builder builder = OnlineLoadRoomListDTO.newBuilder();
        sendMsg(OnlineProtocolType.LOAD_ROOM_LIST, builder.build());
        isTimeShowing = true;
        showTimeThread = new ShowTimeThread(this);
        showTimeThread.start();
    }

    @Override
    protected void onDestroy() {
        isTimeShowing = false;
        try {
            showTimeThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPStack.pop(this);
        roomTitleMap.clear();
        msgList.clear();
        roomList.clear();
        userInHallList.clear();
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(View v) {
        int vid = v.getId();
        if (vid == R.id.ol_send_b) {
            String text = sendTextView.getText().toString();
            if (!text.isEmpty()) {
                new JPDialogBuilder(this)
                        .setTitle("全服消息")
                        .setMessage("您是否需要使用\"全服广播\"进行消息发送？\n(如无\"全服广播\"商品，将为您自动扣费5音符购买)\n发送内容：\"" + sendTextView.getText() + '\"')
                        .setFirstButton("确定", (dialog, i) -> {
                            sendHallChat(true);
                            dialog.dismiss();
                        })
                        .setSecondButton("取消", ((dialog, which) -> dialog.dismiss()))
                        .buildAndShowDialog();
            }
            return true;
        }
        return false;
    }
}
