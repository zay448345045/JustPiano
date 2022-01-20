package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class OLPlayHall extends BaseActivity implements Callback, OnClickListener {
    public ConnectionService cs;
    public String hallName = "";
    public byte hallID = (byte) 0;
    public JPApplication jpapplication;
    JPProgressBar jpprogressBar;
    ListView msgListView;
    ListView f4371D;
    List<Bundle> f4373F = new ArrayList<>();
    TabHost tabHost;
    String f4379L = "";
    Bundle f4381N;
    ListView f4382O;
    boolean f4385R;
    boolean f4388U = false;
    Map<Byte, Room> roomTitleMap = new HashMap<>();
    List<Bundle> msgList = new ArrayList<>();
    ListView f4395k;
    List<Bundle> userInHallList = new ArrayList<>();
    List<Bundle> f4399o = new ArrayList<>();
    Handler showTimeHandler;
    int pageNum = 0;
    boolean f4402r = false;
    OLPlayHallHandler olPlayHallHandler = new OLPlayHallHandler(this);
    TextView f4408x;
    private ImageView imageView;
    private LayoutInflater layoutImflater1;
    private LayoutInflater layoutImflater2;
    private PopupWindow popupWindow = null;
    private ShowTimeThread showTimeThread;
    private TextView f4409y;

    final void mo2823a(byte b) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("ID", b);
            cs.writeData((byte) 43, (byte) 0, (byte) 0, jSONObject.toString(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public final void sendMsg(byte b, byte b2, byte b3, String str) {
        if (cs != null) {
            cs.writeData(b, b2, b3, str, null);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    final void mo2825a(byte b, Room room) {
        roomTitleMap.put(b, room);
    }

    void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_info_dialog, findViewById(R.id.dialog));
        try {
            User User = new User(b.getString("U"), new JSONObject(b.getString("DR")), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView5 = inflate.findViewById(R.id.ol_user_shoes);
            TextView textView = inflate.findViewById(R.id.user_info);
            TextView textView2 = inflate.findViewById(R.id.user_psign);
            imageView.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_m0.png")));
            if (User.getTrousers() <= 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_t" + (User.getTrousers() - 1) + ".png")));
            }
            if (User.getJacket() <= 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_j" + (User.getJacket() - 1) + ".png")));
            }
            if (User.getHair() <= 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_h" + (User.getHair() - 1) + ".png")));
            }
            if (User.getShoes() <= 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_s" + (User.getShoes() - 1) + ".png")));
            }
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
            new JPDialog(this).setTitle("个人资料").loadInflate(inflate).setFirstButton("加为好友", new AddFriendsClick(this, User.getPlayerName())).setSecondButton("确定", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final void mo2826a(int i, byte b) {
        JSONObject jSONObject = new JSONObject();
        switch (i) {
            case 0:
                try {
                    jSONObject.put("I", b);
                    jSONObject.put("P", "");
                    sendMsg((byte) 7, b, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case 1:
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_2);
                TextView textView1 = inflate.findViewById(R.id.text_1);
                TextView textView2 = inflate.findViewById(R.id.title_1);
                textView1.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                textView.setSingleLine(true);
                new JPDialog(this).setTitle("输入密码").loadInflate(inflate).setFirstButton("确定", new RoomPasswordClick2(this, textView, b)).setSecondButton("取消", new DialogDismissClick()).showDialog();
                return;
            default:
        }
    }

    final void mo2827a(Bundle bundle) {
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
        listView.setBackgroundColor(-16777216);
        int i2 = bundle.getInt("R");
        new JPDialog(this).setTitle("房间用户信息").loadInflate(inflate).setFirstButton("进入房间", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mo2826a(bundle.getInt("P"), (byte) i2);
            }
        }).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    final void mo2828a(ListView listView, List<Bundle> list) {
        int posi = listView.getFirstVisiblePosition();
        listView.setAdapter(new ChattingAdapter(list, layoutImflater1));
        listView.setSelection(posi + 2);
    }

    final void mo2829a(ListView listView, List<Bundle> list, int i, boolean z) {
        if (z && list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        listView.setAdapter(new MainGameAdapter(list, (JPApplication) getApplicationContext(), i, this));
    }

    final void mo2830a(String str) {
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        TextView textView3 = inflate.findViewById(R.id.title_2);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView2.setText("内容:");
        new JPDialog(this).setTitle("发送私信给:" + str).loadInflate(inflate).setFirstButton("发送", new SendMessageClick2(this, textView, str)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    final void mo2831b(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("I"), o2.getByte("I")));
        }
        RoomTitleAdapter roomTitleAdapter = (RoomTitleAdapter) listView.getAdapter();
        if (roomTitleAdapter == null) {
            listView.setAdapter(new RoomTitleAdapter(list, layoutImflater2, this));
            return;
        }
        roomTitleAdapter.updateList(list);
        roomTitleAdapter.notifyDataSetChanged();
    }

    final void mo2832b(String str) {
        f4379L = "@" + str + ":";
        if (!str.isEmpty() && !str.equals(JPApplication.kitiName)) {
            f4408x.setText(f4379L);
        }
        CharSequence text = f4408x.getText();
        if (text instanceof Spannable) {
            Selection.setSelection((Spannable) text, text.length());
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.what == 3) {
            CharSequence format = SimpleDateFormat.getTimeInstance(3, Locale.CHINESE).format(new Date());
            if (f4409y != null) {
                f4409y.setText(format);
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        sendMsg((byte) 30, (byte) 0, hallID, "");
        startActivity(new Intent(this, OLPlayHallRoom.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        JSONObject jSONObject = new JSONObject();
        switch (view.getId()) {
            case R.id.ol_createroom_b:
                View inflate = getLayoutInflater().inflate(R.layout.create_room, findViewById(R.id.dialog));
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
                new JPDialog(this).setTitle("创建房间").loadInflate(inflate).setFirstButton("确定", new CreateRoomClick(this, textView, textView2, radioGroup)).setSecondButton("取消", new DialogDismissClick()).showDialog();
                return;
            case R.id.ol_testroom_b:
                try {
                    jSONObject.put("T", 0);
                    jpprogressBar.show();
                    sendMsg((byte) 40, (byte) 0, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
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
                try {
                    jSONObject.put("T", "L");
                    jSONObject.put("B", pageNum);
                    sendMsg((byte) 34, (byte) 0, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    return;
                }
            case R.id.online_button:
                try {
                    jSONObject.put("T", "L");
                    jSONObject.put("B", -1);
                    sendMsg((byte) 34, (byte) 0, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e22) {
                    e22.printStackTrace();
                    return;
                }
            case R.id.next_button:
                if (!f4388U) {
                    pageNum += 20;
                    if (pageNum >= 0) {
                        try {
                            jSONObject.put("T", "L");
                            jSONObject.put("B", pageNum);
                            sendMsg((byte) 34, (byte) 0, (byte) 0, jSONObject.toString());
                            return;
                        } catch (JSONException e222) {
                            e222.printStackTrace();
                            return;
                        }
                    }
                    return;
                }
                return;
            case R.id.ol_send_b:
                String valueOf = String.valueOf(f4408x.getText());
                JSONObject jSONObject2 = new JSONObject();
                try {
                    if (!valueOf.startsWith(f4379L) || valueOf.length() <= f4379L.length()) {
                        jSONObject2.put("@", "");
                        jSONObject2.put("M", valueOf);
                    } else {
                        jSONObject2.put("@", f4379L);
                        valueOf = valueOf.substring(f4379L.length());
                        jSONObject2.put("M", valueOf);
                    }
                    f4408x.setText("");
                    f4379L = "";
                    if (!valueOf.isEmpty()) {
                        sendMsg((byte) 12, (byte) 0, hallID, jSONObject2.toString());
                    }
                } catch (JSONException e2222) {
                    e2222.printStackTrace();
                }
                f4408x.setText("");
                f4379L = "";
                return;
            case R.id.ol_express_b:
                popupWindow.showAtLocation(imageView, Gravity.CENTER, 0, 0);
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 3;
        JPStack.create();
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        roomTitleMap.clear();
        f4381N = getIntent().getExtras();
        hallName = f4381N.getString("hallName");
        hallID = f4381N.getByte("hallID");
        layoutImflater1 = LayoutInflater.from(this);
        layoutImflater2 = LayoutInflater.from(this);
        jpapplication = (JPApplication) getApplication();
        jpapplication.loadSettings(1);
        setContentView(R.layout.olplayhall);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        JPApplication jPApplication = jpapplication;
        jPApplication.setGameMode(0);
        Button f4405u = findViewById(R.id.ol_send_b);
        f4405u.setOnClickListener(this);
        Button f4406v = findViewById(R.id.ol_createroom_b);
        f4406v.setOnClickListener(this);
        Button f4407w = findViewById(R.id.ol_testroom_b);
        f4407w.setOnClickListener(this);
        f4408x = findViewById(R.id.ol_send_text);
        TextView f4410z = findViewById(R.id.ol_playhall_tittle);
        f4410z.setText(hallName);
        showTimeHandler = new Handler(this);
        f4409y = findViewById(R.id.time_text);
        msgListView = findViewById(R.id.ol_msg_list);
        msgListView.setCacheColorHint(0);
        Button f4389V = findViewById(R.id.pre_button);
        Button f4390W = findViewById(R.id.next_button);
        Button f4391X = findViewById(R.id.online_button);
        Button challenge = findViewById(R.id.ol_challenge_b);
        challenge.setOnClickListener(this);
        f4389V.setOnClickListener(this);
        f4390W.setOnClickListener(this);
        f4391X.setOnClickListener(this);
        msgList.clear();
        f4371D = findViewById(R.id.ol_room_list);
        f4371D.setCacheColorHint(0);
        f4395k = findViewById(R.id.ol_player_list);
        f4395k.setCacheColorHint(0);
        imageView = findViewById(R.id.ol_express_b);
        imageView.setOnClickListener(this);
        f4382O = findViewById(R.id.ol_friend_list);
        f4382O.setCacheColorHint(0);
        ScrollText f4384Q = findViewById(R.id.broadCastText);
        f4384Q.setMovementMethod(ScrollingMovementMethod.getInstance());
        f4373F.clear();
        cs = jpapplication.getConnectionService();
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_express_list, null);
        popupWindow.setContentView(inflate);
        ((GridView) inflate.findViewById(R.id.ol_express_grid)).setAdapter(new ExpressAdapter(jpapplication, cs, Consts.expressions, popupWindow, (byte) 12, (byte) 0, hallID));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_bar));
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
            ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(0xffffffff);
        }
        tabHost.setOnTabChangedListener(new PlayHallTabChange(this));
        tabHost.setCurrentTab(1);
        sendMsg((byte) 19, (byte) 0, hallID, "");
        f4385R = true;
        showTimeThread = new ShowTimeThread(this);
        showTimeThread.start();
    }

    @Override
    protected void onDestroy() {
        f4385R = false;
        try {
            showTimeThread.interrupt();
        } catch (Exception ignored) {
        }
        JPStack.create();
        JPStack.pop(this);
        roomTitleMap.clear();
        msgList.clear();
        f4373F.clear();
        userInHallList.clear();
        super.onDestroy();
    }
}
