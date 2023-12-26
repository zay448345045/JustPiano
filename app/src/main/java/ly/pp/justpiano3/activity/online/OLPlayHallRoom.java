package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.MessageLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.FamilyAdapter;
import ly.pp.justpiano3.adapter.MainGameAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayHallRoomHandler;
import ly.pp.justpiano3.listener.AddFriendsClick;
import ly.pp.justpiano3.listener.ChangeBlessingClick;
import ly.pp.justpiano3.listener.tab.PlayHallRoomTabChange;
import ly.pp.justpiano3.task.OLPlayHallRoomTask;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.view.FamilyListView;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import protobuf.dto.OnlineDailyDTO;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineLoadUserDTO;
import protobuf.dto.OnlineLoadUserInfoDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

public final class OLPlayHallRoom extends OLBaseActivity implements OnClickListener {
    public int cl;
    public int lv = 1;
    public TabHost tabHost;
    public TextView userName;
    public TextView mailCountsView;
    public TextView userLevelView;
    public TextView userClassView;
    public TextView userClassNameView;
    public TextView userExpView;
    public JPProgressBar jpprogressBar;
    public SharedPreferences sharedPreferences;
    public ImageView coupleView;
    public ImageView familyView;
    public TextView coupleNameView;
    public TextView coupleLvView;
    public TextView couplePointsView;
    public TextView coupleClView;
    public TextView coupleClNameView;
    public TextView myFamilyPosition;
    public TextView myFamilyName;
    public TextView myFamilyContribution;
    public TextView myFamilyCount;
    public ImageView myFamilyPic;
    public byte[] myFamilyPicArray;
    public int familyListPosition;
    public String familyID = "0";
    public TextView coupleBlessView;
    public List<Bundle> friendList = new ArrayList<>();
    public ListView friendListView;
    public List<Map<String, Object>> familyList = new ArrayList<>();
    public FamilyListView familyListView;
    public int familyPageNum;
    public int cp;
    public boolean pageIsEnd;
    public OLPlayHallRoomHandler olPlayHallRoomHandler = new OLPlayHallRoomHandler(this);
    public int pageNum;
    public ListView hallListView;
    public List<Bundle> hallList = new ArrayList<>();
    public List<Bundle> mailList = new ArrayList<>();
    public ListView mailListView;
    public ImageView userModView;
    public ImageView userTrousersView;
    public ImageView userJacketsView;
    public ImageView userHairView;
    public ImageView userEyeView;
    public ImageView userShoesView;
    public int userTrousersIndex;
    public int userJacketIndex;
    public int userHairIndex;
    public int userEyeIndex;
    public int userShoesIndex;
    public String userSex = "f";
    private MainGameAdapter mailListAdapter;
    private MainGameAdapter hallListAdapter;
    private MainGameAdapter userListAdapter;
    public ImageView coupleModView;
    public ImageView coupleTrousersView;
    public ImageView coupleJacketView;
    public ImageView coupleHairView;
    public ImageView coupleEyeView;
    public ImageView coupleShoesView;
    public String coupleSex;
    private LayoutInflater layoutinflater;

    public void sendMsg(int type, MessageLite message) {
        if (OnlineUtil.getConnectionService() != null) {
            OnlineUtil.getConnectionService().writeData(type, message);
        } else {
            Toast.makeText(this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
        }
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
                    .setSecondButton("确定", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindAdapter(ListView listView, List<Map<String, Object>> list) {
        listView.setAdapter(new FamilyAdapter(list, layoutinflater, this));
    }

    public void updateFamilyListShow(FamilyAdapter familyAdapter, FamilyListView listView, List<Map<String, Object>> list) {
        familyAdapter.upDateList(list);
        familyAdapter.notifyDataSetChanged();
        listView.loadComplete();
    }

    public void updateMailListShow(int i) {
        mailList.remove(i);
        mailListAdapter.updateList(mailList);
        mailListAdapter.notifyDataSetChanged();
        saveMailToLocal();
    }

    public void mo2841a(int i, String str, String str2) {
        if (i == 3) {
            tabHost.setCurrentTab(1);
        }
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle(str);
        jpDialogBuilder.setMessage(str2);
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
        jpDialogBuilder.buildAndShowDialog();
    }

    public void setBroadcast(Bundle bundle) {
//        int i = bundle.getInt("C");
//        int i2 = bundle.getInt("D");
//        systemTextView.setText(bundle.getString("U"));
//        systemTextView.setTextColor(getResources().getColor(i));
//        broadCastTextView.setText(bundle.getString("M"));
//        systemTextView.setTextColor(getResources().getColor(i2));
    }

    public void sortListAndBindAdapter(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("I"), o2.getByte("I")));
        }
        if (hallListAdapter == null) {
            hallListAdapter = new MainGameAdapter(list, 0);
            listView.setAdapter(hallListAdapter);
            return;
        }
        hallListAdapter.updateList(list);
        hallListAdapter.notifyDataSetChanged();
    }

    public void sendMail(String str, int i) {
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
        new JPDialogBuilder(this).setTitle(str3).loadInflate(inflate).
                setFirstButton(str2, new ChangeBlessingClick(this, textView, i, str))
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
    }

    public void updateUserListShow(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        if (userListAdapter == null) {
            userListAdapter = new MainGameAdapter(list, 1);
            listView.setAdapter(userListAdapter);
            return;
        }
        userListAdapter.updateList(list);
        userListAdapter.notifyDataSetChanged();
    }

    public void saveMailToLocal() {
        JSONArray jSONArray = new JSONArray();
        for (Bundle bundle : mailList) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("F", bundle.getString("F"));
                jSONObject.put("M", bundle.getString("M"));
                jSONObject.put("T", bundle.getString("T"));
                if (bundle.containsKey("type")) {
                    jSONObject.put("type", bundle.getInt("type"));
                }
                jSONArray.put(jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Editor editor = sharedPreferences.edit();
        editor.putString("mailsString", jSONArray.toString());
        editor.apply();
    }

    public void updateMailListShow(ListView listView, List<Bundle> list) {
        if (mailListAdapter == null) {
            mailListAdapter = new MainGameAdapter(list, 2);
            listView.setAdapter(mailListAdapter);
            return;
        }
        mailListAdapter.updateList(list);
        mailListAdapter.notifyDataSetChanged();
    }

    public void addFriends(String str) {
        if (!str.isEmpty()) {
            JPDialogBuilder buildAndShowDialog = new JPDialogBuilder(this);
            buildAndShowDialog.setTitle("好友请求");
            buildAndShowDialog.setMessage("[" + str + "]请求加您为好友，是否同意?");
            buildAndShowDialog.setFirstButton("同意", (dialog, which) -> {
                dialog.dismiss();
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("H", 1);
                    jSONObject.put("T", str);
                    jSONObject.put("F", OLBaseActivity.getAccountName());
                    new OLPlayHallRoomTask(this).execute(jSONObject.toString(), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            buildAndShowDialog.setSecondButton("拒绝", (dialog, which) -> dialog.dismiss());
            buildAndShowDialog.buildAndShowDialog();
        }
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            Bundle extras = intent.getExtras();
            userSex = extras.getString("S");
            userTrousersIndex = extras.getInt("T");
            userJacketIndex = extras.getInt("J");
            userHairIndex = extras.getInt("H");
            userEyeIndex = extras.getInt("E");
            userShoesIndex = extras.getInt("O");
            ImageLoadUtil.setUserDressImageBitmap(this, userSex, userTrousersIndex, userJacketIndex, userHairIndex, userEyeIndex, userShoesIndex,
                    userModView, userTrousersView, userJacketsView, userHairView, userEyeView, userShoesView);
        }
    }

    @Override
    public void onBackPressed() {
        try {
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
        int id = view.getId();
        if (id == R.id.ol_player_mod || id == R.id.ol_dress_button) {
            if (lv < 8) {
                Toast.makeText(this, "您的等级未达到8级，不能进入换衣间", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.setClass(this, OLPlayDressRoom.class);
            intent.putExtra("T", userTrousersIndex - 1);
            intent.putExtra("J", userJacketIndex - 1);
            intent.putExtra("H", userHairIndex - 1);
            intent.putExtra("E", userEyeIndex - 1);
            intent.putExtra("S", userSex);
            intent.putExtra("Lv", lv);
            intent.putExtra("O", userShoesIndex - 1);
            startActivityForResult(intent, 0);
        } else if (id == R.id.ol_bonus_button) {
            jpprogressBar.show();
            OnlineDailyDTO.Builder builder2 = OnlineDailyDTO.newBuilder();
            builder2.setType(1);
            sendMsg(OnlineProtocolType.DAILY, builder2.build());
        } else if (id == R.id.create_family) {
            OnlineFamilyDTO.Builder builder1 = OnlineFamilyDTO.newBuilder();
            builder1.setType(3);
            sendMsg(OnlineProtocolType.FAMILY, builder1.build());
        } else if (id == R.id.pre_button) {
            pageNum -= 20;
            if (pageNum < 0) {
                pageNum = 0;
                return;
            }
            OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
            builder.setType(1);
            builder.setPage(pageNum);
            sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
        } else if (id == R.id.online_button) {
            OnlineLoadUserInfoDTO.Builder builder;
            builder = OnlineLoadUserInfoDTO.newBuilder();
            builder.setType(1);
            builder.setPage(-1);
            sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
        } else if (id == R.id.next_button) {
            OnlineLoadUserInfoDTO.Builder builder;
            if (!pageIsEnd) {
                pageNum += 20;
                if (pageNum >= 0) {
                    builder = OnlineLoadUserInfoDTO.newBuilder();
                    builder.setType(1);
                    builder.setPage(pageNum);
                    sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                }
            }
        } else if (id == R.id.ol_breakup_button) {
            deleteCp(false);
        } else if (id == R.id.ol_setblessing_button) {
            if (cp > 0) {
                sendMail("", 1);
            }
        } else if (id == R.id.ol_myfamily_button) {
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
        }
    }

    public void deleteCp(boolean flag) {
        if (cp > 0) {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("警告");
            jpDialogBuilder.setMessage("确定要解除搭档关系吗?");
            jpDialogBuilder.setFirstButton("同意", (dialog, which) -> {
                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                builder.setName(String.valueOf(flag));
                builder.setType(3);
                sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                dialog.dismiss();
            }).setSecondButton("取消", (dialog, which) -> dialog.dismiss());
            jpDialogBuilder.buildAndShowDialog();
        }
    }

    public void letInFamily(String name) {
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(6);
        builder.setStatus(0);
        builder.setUserName(name);
        sendMsg(OnlineProtocolType.FAMILY, builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jpprogressBar = new JPProgressBar(this);
        layoutinflater = LayoutInflater.from(this);
        sharedPreferences = getSharedPreferences("mails_" + OLBaseActivity.getAccountName(), MODE_PRIVATE);
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.ol_hall_list);
        GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
        hallListView = findViewById(R.id.ol_hall_list);
        hallListView.setCacheColorHint(Color.TRANSPARENT);
        hallList.clear();
        friendListView = findViewById(R.id.ol_friend_list);
        friendListView.setCacheColorHint(Color.TRANSPARENT);
        familyListView = findViewById(R.id.ol_family_list);
        familyListView.setCacheColorHint(Color.TRANSPARENT);
        familyListView.setLoadListener(() -> ThreadPoolUtil.execute(() -> {
            familyPageNum++;
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(2);
            builder.setPage(familyPageNum);
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        }));
        mailListView = findViewById(R.id.ol_mail_list);
        mailListView.setCacheColorHint(Color.TRANSPARENT);
        friendList.clear();
        familyList.clear();
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
        findViewById(R.id.ol_dress_button).setOnClickListener(this);
        findViewById(R.id.ol_bonus_button).setOnClickListener(this);
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
        userEyeView = findViewById(R.id.ol_player_eye);
        userShoesView = findViewById(R.id.ol_player_shoes);
        myFamilyContribution = findViewById(R.id.ol_myfamily_contribution);
        myFamilyCount = findViewById(R.id.ol_myfamily_count);
        myFamilyName = findViewById(R.id.ol_myfamily_name);
        myFamilyPic = findViewById(R.id.ol_myfamily_pic);
        myFamilyPosition = findViewById(R.id.ol_myfamily_position);
        coupleView = findViewById(R.id.ol_player_couple);
        familyView = findViewById(R.id.ol_player_family);
        findViewById(R.id.pre_button).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);
        findViewById(R.id.online_button).setOnClickListener(this);
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
        coupleNameView.setText("岛村抱月");
        Button breakUpCpButton = findViewById(R.id.ol_breakup_button);
        breakUpCpButton.setText("解除搭档");
        breakUpCpButton.setOnClickListener(this);
        Button setBlessingButton = findViewById(R.id.ol_setblessing_button);
        setBlessingButton.setText("设置祝语");
        setBlessingButton.setHint(new SpannableString("输入新祝语"));
        setBlessingButton.setOnClickListener(this);
        findViewById(R.id.create_family).setOnClickListener(this);
        findViewById(R.id.ol_myfamily_button).setOnClickListener(this);
        coupleLvView = findViewById(R.id.ol_couple_level);
        coupleLvView.setText("LV.25");
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
        coupleEyeView = findViewById(R.id.ol_couple_eye);
        coupleShoesView = findViewById(R.id.ol_couple_shoes);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int childCount = tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            tabHost.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            TextView titleTextView = findViewById(R.id.ol_playhall_title);
            titleTextView.getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            TextView tv = tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(0xffffffff);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        tabHost.setOnTabChangedListener(new PlayHallRoomTabChange(this));
        tabHost.setCurrentTab(1);
        switch (getIntent().getIntExtra("HEAD", 0)) {
            case 5 -> Toast.makeText(this, "您的账号重复登录……", Toast.LENGTH_SHORT).show();
            case 16 -> {
                // 从家族中心返回，则去加载家族
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
                familyList = (List<Map<String, Object>>) getIntent().getSerializableExtra("familyList");
                tabHost.setCurrentTab(4);
                bindAdapter(familyListView, familyList);
                try {
                    Thread.sleep(320);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        sendMsg(OnlineProtocolType.LOAD_USER, OnlineLoadUserDTO.getDefaultInstance());
    }

    @Override
    protected void onDestroy() {
        hallList.clear();
        friendList.clear();
        familyList.clear();
        mailList.clear();
        super.onDestroy();
    }
}
