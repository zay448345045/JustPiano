package ly.pp.justpiano3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class OLPlayHallRoom extends BaseActivity implements OnClickListener {
    public int cl = 0;
    public int lv = 1;
    TabHost tabHost;
    TextView userName;
    TextView mailCountsView;
    TextView userLevelView;
    TextView userClassView;
    TextView userClassNameView;
    TextView userExpView;
    JPProgressBar jpprogressBar;
    SharedPreferences sharedPreferences = null;
    ImageView coupleView;
    ImageView familyView;
    TextView coupleNameView;
    TextView coupleLvView;
    TextView couplePointsView;
    TextView coupleClView;
    TextView coupleClNameView;
    TextView myFamilyPosition;
    TextView myFamilyName;
    TextView myFamilyContribution;
    TextView myFamilyCount;
    ImageView myFamilyPic;
    byte[] myFamilyPicArray;
    int familyListPosition;
    String familyID = "0";
    TextView coupleBlessView;
    List<Bundle> friendList = new ArrayList<>();
    ListView friendListView;
    List<HashMap> familyList = new ArrayList<>();
    FamilyListView familyListView;
    int familyPageNum;
    int cp;
    boolean f4467r = false;
    OLPlayHallRoomHandler olPlayHallRoomHandler = new OLPlayHallRoomHandler(this);
    int pageNum = 0;
    JPApplication jpApplication;
    ListView hallListView;
    List<Bundle> hallList = new ArrayList<>();
    List<Bundle> mailList = new ArrayList<>();
    ListView mailListView;
    ConnectionService cs;
    private ImageView userModView;
    private ImageView userTrousersView;
    private ImageView userJacketsView;
    private ImageView userHairView;
    private ImageView userShoesView;
    private int f4427Q;
    private int f4428R;
    private int f4429S;
    private int f4430T;
    private String sex = "f";
    private Editor editor = null;
    private MainGameAdapter f4434X = null;
    private MainGameAdapter f4435Y = null;
    private MainGameAdapter f4436Z = null;
    private ImageView coupleModView;
    private ImageView coupleTrousersView;
    private ImageView coupleJacketView;
    private ImageView coupleHairView;
    private ImageView coupleShoesView;
    private String f4450an;
    private int f4451ao;
    private int f4452ap;
    private int f4453aq;
    private int f4454ar;
    private TextView systemTextView;
    private ScrollText broadCastTextView;
    private String f4462m = "";
    private String f4463n = "";
    private LayoutInflater layoutinflater;

    private static Bitmap m3706a(Context context, String str, String str2, int i) {
        int i2 = i - 1;
        if (i2 >= 0) {
            try {
                return BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + str + "_" + str2 + i2 + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png"));
        } catch (IOException e) {
            return null;
        }
    }

    final void sendMsg(byte b, byte b2, String str) {
        if (cs != null) {
            cs.writeData(b, (byte) 0, b2, str, null);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    final void mo2907b(ListView listView, List<HashMap> list) {
        listView.setAdapter(new FamilyAdapter(list, layoutinflater, this));
    }

    public final void mo2905a(FamilyAdapter fa, FamilyListView listView, List<HashMap> list) {
        fa.upDateList(list);
        fa.notifyDataSetChanged();
        listView.loadComplete();
    }

    final void mo2840a(int i) {
        mailList.remove(i);
        f4434X.updateList(mailList);
        f4434X.notifyDataSetChanged();
        mo2848c();
    }

    final void mo2841a(int i, String str, String str2) {
        if (i == 3) {
            tabHost.setCurrentTab(1);
        }
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle(str);
        jpdialog.setMessage(str2);
        jpdialog.setFirstButton("确定", new DialogDismissClick());
        try {
            jpdialog.showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final void mo2842a(Bundle bundle) {
        f4462m = bundle.getString("U");
        f4463n = bundle.getString("M");
        int i = bundle.getInt("C");
        int i2 = bundle.getInt("D");
        systemTextView.setText(f4462m);
        systemTextView.setTextColor(getResources().getColor(i));
        broadCastTextView.setText(f4463n);
        systemTextView.setTextColor(getResources().getColor(i2));
    }

    final void mo2843a(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("I"), o2.getByte("I")));
        }
        if (f4435Y == null) {
            f4435Y = new MainGameAdapter(list, (JPApplication) getApplicationContext(), 0, this);
            listView.setAdapter(f4435Y);
            return;
        }
        f4435Y.updateList(list);
        f4435Y.notifyDataSetChanged();
    }

    final void mo2844a(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            sex = jSONObject.getString("S");
            f4427Q = jSONObject.getInt("T");
            f4428R = jSONObject.getInt("J");
            f4429S = jSONObject.getInt("H");
            f4430T = jSONObject.getInt("O");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userModView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "m", 1));
        userTrousersView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "t", f4427Q));
        userJacketsView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "j", f4428R));
        userHairView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "h", f4429S));
        userShoesView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "s", f4430T));
    }

    final void sendMail(String str, int i) {
        String str2;
        String str3;
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        inflate.findViewById(R.id.title_2).setVisibility(View.GONE);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView2.setText("内容:");
        if (i == 0) {
            str2 = "发送";
            str3 = "发送私信给:" + str;
        } else if (i == 1) {
            str3 = "设置祝语";
            str2 = "修改";
            textView.setText(coupleBlessView.getText().toString().substring(4));
        } else {
            return;
        }
        new JPDialog(this).setTitle(str3).loadInflate(inflate).setFirstButton(str2, new ChangeZhuyuClick(this, textView, i, str)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    final void mo2846b(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        if (f4436Z == null) {
            f4436Z = new MainGameAdapter(list, (JPApplication) getApplicationContext(), 1, this);
            listView.setAdapter(f4436Z);
            return;
        }
        f4436Z.updateList(list);
        f4436Z.notifyDataSetChanged();
    }

    final void mo2847b(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            f4450an = jSONObject.getString("S");
            f4451ao = jSONObject.getInt("T");
            f4452ap = jSONObject.getInt("J");
            f4453aq = jSONObject.getInt("H");
            f4454ar = jSONObject.getInt("O");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        coupleModView.setImageBitmap(OLPlayHallRoom.m3706a(this, f4450an, "m", 1));
        coupleTrousersView.setImageBitmap(OLPlayHallRoom.m3706a(this, f4450an, "t", f4451ao));
        coupleJacketView.setImageBitmap(OLPlayHallRoom.m3706a(this, f4450an, "j", f4452ap));
        coupleHairView.setImageBitmap(OLPlayHallRoom.m3706a(this, f4450an, "h", f4453aq));
        coupleShoesView.setImageBitmap(OLPlayHallRoom.m3706a(this, f4450an, "s", f4454ar));
    }

    final void mo2848c() {
        JSONArray jSONArray = new JSONArray();
        for (Bundle aF4473x : mailList) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("F", aF4473x.getString("F"));
                jSONObject.put("M", aF4473x.getString("M"));
                jSONObject.put("T", aF4473x.getString("T"));
                if (aF4473x.containsKey("type")) {
                    jSONObject.put("type", aF4473x.getInt("type"));
                }
                jSONArray.put(jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString("mailsString", jSONArray.toString());
        editor.commit();
    }

    final void mo2849c(ListView listView, List<Bundle> list) {
        if (f4434X == null) {
            f4434X = new MainGameAdapter(list, (JPApplication) getApplicationContext(), 2, this);
            listView.setAdapter(f4434X);
            return;
        }
        f4434X.updateList(list);
        f4434X.notifyDataSetChanged();
    }

    final void addFriends(String str) {
        if (!str.isEmpty()) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle("好友请求");
            jpdialog.setMessage("[" + str + "]请求加您为好友,同意吗?");
            jpdialog.setFirstButton("同意", (dialog, which) -> {
                dialog.dismiss();
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("H", 1);
                    jSONObject.put("T", str);
                    jSONObject.put("F", OLPlayHallRoom.this.jpApplication.getAccountName());
                    new OLPlayHallRoomTask(OLPlayHallRoom.this).execute(jSONObject.toString(), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            jpdialog.setSecondButton("拒绝", new DialogDismissClick());
            try {
                jpdialog.showDialog();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            Bundle extras = intent.getExtras();
            sex = extras.getString("S");
            f4427Q = extras.getInt("T");
            f4428R = extras.getInt("J");
            f4429S = extras.getInt("H");
            f4430T = extras.getInt("O");
            userModView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "m", 1));
            userTrousersView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "t", f4427Q));
            userJacketsView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "j", f4428R));
            userHairView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "h", f4429S));
            userShoesView.setImageBitmap(OLPlayHallRoom.m3706a(this, sex, "s", f4430T));
        }
    }

    @Override
    public void onBackPressed() {
        try {
            JPStack.create();
            JPStack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, OLMainMode.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        JSONObject jSONObject;
        switch (view.getId()) {
            case R.id.ol_player_mod:
            case R.id.ol_dress_button:
                if (lv < 8) {
                    Toast.makeText(this, "您的等级未达到8级,不能进入换衣间!", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.setClass(this, OLPlayDressRoom.class);
                intent.putExtra("T", f4427Q - 1);
                intent.putExtra("J", f4428R - 1);
                intent.putExtra("H", f4429S - 1);
                intent.putExtra("S", sex);
                intent.putExtra("Lv", lv);
                intent.putExtra("O", f4430T - 1);
                startActivityForResult(intent, 0);
                return;
            case R.id.ol_bonus_button:
                try {
                    jSONObject = new JSONObject();
                    jSONObject.put("K", 1);
                    jpprogressBar.show();
                    sendMsg((byte) 38, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case R.id.create_family:
                try {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("K", 3);
                    sendMsg((byte) 18, (byte) 0, jSONObject2.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            case R.id.pre_button:
                pageNum -= 20;
                if (pageNum < 0) {
                    pageNum = 0;
                    return;
                }
                try {
                    jSONObject = new JSONObject();
                    jSONObject.put("T", "L");
                    jSONObject.put("B", pageNum);
                    sendMsg((byte) 34, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case R.id.online_button:
                try {
                    jSONObject = new JSONObject();
                    jSONObject.put("T", "L");
                    jSONObject.put("B", -1);
                    sendMsg((byte) 34, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    return;
                }
            case R.id.next_button:
                if (!f4467r) {
                    pageNum += 20;
                    if (pageNum >= 0) {
                        try {
                            jSONObject = new JSONObject();
                            jSONObject.put("T", "L");
                            jSONObject.put("B", pageNum);
                            sendMsg((byte) 34, (byte) 0, jSONObject.toString());
                            return;
                        } catch (JSONException e22) {
                            e22.printStackTrace();
                            return;
                        }
                    }
                    return;
                }
                return;
            case R.id.ol_breakup_button:
                deleteCp(false);
                return;
            case R.id.ol_setblessing_button:
                if (cp > 0) {
                    sendMail("", 1);
                    return;
                }
                return;
            case R.id.ol_myfamily_button:
                if (familyID != null && !familyID.equals("0")) {
                    intent = new Intent();
                    intent.setClass(this, OLFamily.class);
                    intent.putExtra("familyID", familyID);
                    intent.putExtra("pageNum", familyPageNum);
                    intent.putExtra("position", 0);
                    intent.putExtra("myFamilyPosition", myFamilyPosition.getText().toString());
                    intent.putExtra("myFamilyContribution", myFamilyContribution.getText().toString());
                    intent.putExtra("myFamilyCount", myFamilyCount.getText().toString());
                    intent.putExtra("myFamilyName", myFamilyName.getText().toString());
                    intent.putExtra("myFamilyPicArray", myFamilyPicArray);
                    intent.putExtra("familyList", (Serializable) familyList);
                    startActivity(intent);
                    finish();
                }
                return;
            default:
        }
    }

    void deleteCp(boolean flag) {
        if (cp > 0) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle("警告");
            jpdialog.setMessage("确定要解除搭档关系吗?");
            jpdialog.setFirstButton("同意", (dialog, which) -> {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("F", flag);
                    jSONObject.put("T", 3);
                    sendMsg((byte) 31, (byte) 0, jSONObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }).setSecondButton("取消", new DialogDismissClick());
            try {
                jpdialog.showDialog();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    void letInFamily(String name) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("K", 6);
            jSONObject.put("F", name);
            jSONObject.put("S", 0);  //S为0表示进入家族
            sendMsg((byte) 18, (byte) 0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 1;
        JPStack.create();
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        layoutinflater = LayoutInflater.from(this);
        jpApplication = (JPApplication) getApplication();
        sharedPreferences = getSharedPreferences("mails_" + jpApplication.getAccountName(), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        jpApplication.loadSettings(1);
        setContentView(R.layout.olplayhallroom);
        jpApplication.setBackGround(this, "ground", findViewById(R.id.layout));
        jpApplication.setGameMode(0);
        hallListView = findViewById(R.id.ol_hall_list);
        hallListView.setCacheColorHint(0);
        hallList.clear();
        friendListView = findViewById(R.id.ol_friend_list);
        friendListView.setCacheColorHint(0);
        familyListView = findViewById(R.id.ol_family_list);
        familyListView.setCacheColorHint(0);
        familyListView.setLoadListener(() -> ThreadPoolUtils.execute(() -> {
            familyPageNum++;
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("K", 2);
                jSONObject.put("B", familyPageNum);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            sendMsg((byte) 18, (byte) 0, jSONObject.toString());
        }));
        mailListView = findViewById(R.id.ol_mail_list);
        mailListView.setCacheColorHint(0);
        friendList.clear();
        familyList.clear();
        cs = jpApplication.getConnectionService();
        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        TabSpec newTabSpec = tabHost.newTabSpec("tab1");
        newTabSpec.setContent(R.id.friend_tab);
        newTabSpec.setIndicator("好友");
        tabHost.addTab(newTabSpec);
        newTabSpec = tabHost.newTabSpec("tab2");
        newTabSpec.setContent(R.id.infor_tab);
        newTabSpec.setIndicator("资料");
        tabHost.addTab(newTabSpec);
        userName = findViewById(R.id.ol_player_name);
        userName.setText("");
        Button dressChangeButton = findViewById(R.id.ol_dress_button);
        dressChangeButton.setOnClickListener(this);
        Button bonusButton = findViewById(R.id.ol_bonus_button);
        bonusButton.setOnClickListener(this);
        userLevelView = findViewById(R.id.ol_player_level);
        userExpView = findViewById(R.id.user_exp);
        userLevelView.setText("");
        userClassView = findViewById(R.id.ol_player_class);
        userClassView.setText("");
        userClassNameView = findViewById(R.id.user_classname);
        userClassNameView.setText("");
        userExpView.setText("");
        mailCountsView = findViewById(R.id.user_mailcount);
        mailCountsView.setText("");
        userModView = findViewById(R.id.ol_player_mod);
        userModView.setOnClickListener(this);
        userTrousersView = findViewById(R.id.ol_player_trousers);
        userJacketsView = findViewById(R.id.ol_player_jacket);
        userHairView = findViewById(R.id.ol_player_hair);
        userShoesView = findViewById(R.id.ol_player_shoes);
        broadCastTextView = findViewById(R.id.broadCastText);
        systemTextView = findViewById(R.id.systemText);
        myFamilyContribution = findViewById(R.id.ol_myfamily_contribution);
        myFamilyCount = findViewById(R.id.ol_myfamily_count);
        myFamilyName = findViewById(R.id.ol_myfamily_name);
        myFamilyPic = findViewById(R.id.ol_myfamily_pic);
        myFamilyPosition = findViewById(R.id.ol_myfamily_position);
        systemTextView.setText(f4462m);
        broadCastTextView.setText(f4463n);
        broadCastTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        coupleView = findViewById(R.id.ol_player_couple);
        familyView = findViewById(R.id.ol_player_family);
        Button preButton = findViewById(R.id.pre_button);
        Button nextButton = findViewById(R.id.next_button);
        Button onlineButton = findViewById(R.id.online_button);
        preButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        onlineButton.setOnClickListener(this);
        newTabSpec = tabHost.newTabSpec("tab3");
        newTabSpec.setContent(R.id.couple_tab);
        newTabSpec.setIndicator("搭档");
        tabHost.addTab(newTabSpec);
        newTabSpec = tabHost.newTabSpec("tab4");
        newTabSpec.setContent(R.id.msg_tab);
        newTabSpec.setIndicator("信箱");
        tabHost.addTab(newTabSpec);
        newTabSpec = tabHost.newTabSpec("tab5");
        newTabSpec.setContent(R.id.family_tab);
        newTabSpec.setIndicator("家族");
        tabHost.addTab(newTabSpec);
        coupleNameView = findViewById(R.id.ol_couple_name);
        coupleNameView.setText("");
        Button breakUpCpButton = findViewById(R.id.ol_breakup_button);
        breakUpCpButton.setText("解除搭档");
        Button setBlessingButton = findViewById(R.id.ol_setblessing_button);
        setBlessingButton.setText("设置祝语");
        setBlessingButton.setHint(new SpannableString("输入新祝语)"));
        breakUpCpButton.setOnClickListener(this);
        setBlessingButton.setOnClickListener(this);
        Button createFamily = findViewById(R.id.create_family);
        createFamily.setOnClickListener(this);
        LinearLayout myFamilyButton = findViewById(R.id.ol_myfamily_button);
        myFamilyButton.setOnClickListener(this);
        coupleLvView = findViewById(R.id.ol_couple_level);
        coupleLvView.setText("");
        couplePointsView = findViewById(R.id.couple_points);
        coupleClView = findViewById(R.id.ol_couple_class);
        coupleClView.setText("");
        coupleBlessView = findViewById(R.id.ol_couple_blessing);
        coupleClNameView = findViewById(R.id.couple_classname);
        coupleClNameView.setText("");
        coupleModView = findViewById(R.id.ol_couple_mod);
        coupleModView.setOnClickListener(this);
        coupleTrousersView = findViewById(R.id.ol_couple_trousers);
        coupleJacketView = findViewById(R.id.ol_couple_jacket);
        coupleHairView = findViewById(R.id.ol_couple_hair);
        coupleShoesView = findViewById(R.id.ol_couple_shoes);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int childCount = tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            tabHost.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(0xffffffff);
        }
        tabHost.setOnTabChangedListener(new PlayHallRoomTabChange(this));
        tabHost.setCurrentTab(1);
        switch (getIntent().getIntExtra("HEAD", 0)) {
            case 5:
                Toast.makeText(this, "您的账号重复登录……", Toast.LENGTH_SHORT).show();
                break;
            case 16:
                Bundle b = getIntent().getExtras();
                familyPageNum = b.getInt("pageNum");
                familyListPosition = b.getInt("position");
                myFamilyPicArray = b.getByteArray("myFamilyPicArray");
                myFamilyPosition.setText(b.getString("myFamilyPosition"));
                myFamilyName.setText(b.getString("myFamilyName"));
                myFamilyContribution.setText(b.getString("myFamilyContribution"));
                myFamilyCount.setText(b.getString("myFamilyCount"));
                if (myFamilyPicArray == null || myFamilyPicArray.length <= 1) {
                    myFamilyPic.setImageResource(R.drawable.family);
                } else {
                    myFamilyPic.setImageBitmap(BitmapFactory.decodeByteArray(myFamilyPicArray, 0, myFamilyPicArray.length));
                }
                familyList = (List<HashMap>) getIntent().getSerializableExtra("familyList");
                tabHost.setCurrentTab(4);
                mo2907b(familyListView, familyList);
                try {
                    Thread.sleep(320);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
        sendMsg((byte) 28, (byte) 0, "");
    }

    @Override
    protected void onDestroy() {
        hallList.clear();
        friendList.clear();
        familyList.clear();
        mailList.clear();
        JPStack.create();
        JPStack.pop(this);
        super.onDestroy();
    }
}
