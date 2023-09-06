package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.TabHost.TabSpec;
import com.google.protobuf.MessageLite;
import ly.pp.justpiano3.*;
import ly.pp.justpiano3.adapter.FamilyAdapter;
import ly.pp.justpiano3.adapter.MainGameAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.GameModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayHallRoomHandler;
import ly.pp.justpiano3.listener.AddFriendsClick;
import ly.pp.justpiano3.listener.ChangeBlessingClick;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.listener.tab.PlayHallRoomTabChange;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.task.OLPlayHallRoomTask;
import ly.pp.justpiano3.thread.ThreadPoolUtils;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;
import ly.pp.justpiano3.view.FamilyListView;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import protobuf.dto.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class OLPlayHallRoom extends BaseActivity implements OnClickListener {
    public int cl = 0;
    public int lv = 1;
    public TabHost tabHost;
    public TextView userName;
    public TextView mailCountsView;
    public TextView userLevelView;
    public TextView userClassView;
    public TextView userClassNameView;
    public TextView userExpView;
    public JPProgressBar jpprogressBar;
    public SharedPreferences sharedPreferences = null;
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
    public List<HashMap> familyList = new ArrayList<>();
    public FamilyListView familyListView;
    public int familyPageNum;
    public int cp;
    public boolean pageIsEnd = false;
    public OLPlayHallRoomHandler olPlayHallRoomHandler = new OLPlayHallRoomHandler(this);
    public int pageNum = 0;
    public JPApplication jpApplication;
    public ListView hallListView;
    public List<Bundle> hallList = new ArrayList<>();
    public List<Bundle> mailList = new ArrayList<>();
    public ListView mailListView;
    public ConnectionService connectionService;
    private ImageView userModView;
    private ImageView userTrousersView;
    private ImageView userJacketsView;
    private ImageView userHairView;
    private ImageView userEyeView;
    private ImageView userShoesView;
    private int userTrousersIndex;
    private int userJacketIndex;
    private int userHairIndex;
    private int userEyeIndex;
    private int userShoesIndex;
    public String userSex = "f";
    private MainGameAdapter mailListAdapter = null;
    private MainGameAdapter hallListAdapter = null;
    private MainGameAdapter userListAdapter = null;
    private ImageView coupleModView;
    private ImageView coupleTrousersView;
    private ImageView coupleJacketView;
    private ImageView coupleHairView;
    private ImageView coupleEyeView;
    private ImageView coupleShoesView;
    public String coupleSex;
    private LayoutInflater layoutinflater;

    private static Bitmap setDress(Context context, String str, String str2, int i) {
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

    public void sendMsg(int type, MessageLite message) {
        if (connectionService != null) {
            connectionService.writeData(type, message);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_info_dialog, findViewById(R.id.dialog));
        try {
            User User = new User(b.getString("U"), b.getInt("DR_H"), b.getInt("DR_E"), b.getInt("DR_J"),
                    b.getInt("DR_T"), b.getInt("DR_S"), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView4e = inflate.findViewById(R.id.ol_user_eye);
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
            if (User.getEye() <= 0) {
                imageView4e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_e" + (User.getEye() - 1) + ".png")));
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

    public void mo2907b(ListView listView, List<HashMap> list) {
        listView.setAdapter(new FamilyAdapter(list, layoutinflater, this));
    }

    public void mo2905a(FamilyAdapter fa, FamilyListView listView, List<HashMap> list) {
        fa.upDateList(list);
        fa.notifyDataSetChanged();
        listView.loadComplete();
    }

    public void mo2840a(int i) {
        mailList.remove(i);
        mailListAdapter.updateList(mailList);
        mailListAdapter.notifyDataSetChanged();
        mo2848c();
    }

    public void mo2841a(int i, String str, String str2) {
        if (i == 3) {
            tabHost.setCurrentTab(1);
        }
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle(str);
        jpdialog.setMessage(str2);
        jpdialog.setFirstButton("确定", new DialogDismissClick());
        jpdialog.showDialog();
    }

    public void mo2842a(Bundle bundle) {
//        int i = bundle.getInt("C");
//        int i2 = bundle.getInt("D");
//        systemTextView.setText(bundle.getString("U"));
//        systemTextView.setTextColor(getResources().getColor(i));
//        broadCastTextView.setText(bundle.getString("M"));
//        systemTextView.setTextColor(getResources().getColor(i2));
    }

    public void mo2843a(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("I"), o2.getByte("I")));
        }
        if (hallListAdapter == null) {
            hallListAdapter = new MainGameAdapter(list, (JPApplication) getApplicationContext(), 0, this);
            listView.setAdapter(hallListAdapter);
            return;
        }
        hallListAdapter.updateList(list);
        hallListAdapter.notifyDataSetChanged();
    }

    public void loadDress(int hair, int eye, int jacket, int trousers, int shoes) {
        userTrousersIndex = trousers;
        userJacketIndex = jacket;
        userHairIndex = hair;
        userEyeIndex = eye;
        userShoesIndex = shoes;
        userModView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "m", 1));
        userTrousersView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "t", userTrousersIndex));
        userJacketsView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "j", userJacketIndex));
        userHairView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "h", userHairIndex));
        userEyeView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "e", userEyeIndex));
        userShoesView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "s", userShoesIndex));
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
        new JPDialog(this).setTitle(str3).loadInflate(inflate).setFirstButton(str2, new ChangeBlessingClick(this, textView, i, str)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public void mo2846b(ListView listView, List<Bundle> list) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        if (userListAdapter == null) {
            userListAdapter = new MainGameAdapter(list, (JPApplication) getApplicationContext(), 1, this);
            listView.setAdapter(userListAdapter);
            return;
        }
        userListAdapter.updateList(list);
        userListAdapter.notifyDataSetChanged();
    }

    public void loadClothes(int hair, int eye, int jacket, int trousers, int shoes) {
        coupleModView.setImageBitmap(OLPlayHallRoom.setDress(this, coupleSex, "m", 1));
        coupleTrousersView.setImageBitmap(OLPlayHallRoom.setDress(this, coupleSex, "t", trousers));
        coupleJacketView.setImageBitmap(OLPlayHallRoom.setDress(this, coupleSex, "j", jacket));
        coupleHairView.setImageBitmap(OLPlayHallRoom.setDress(this, coupleSex, "h", hair));
        coupleEyeView.setImageBitmap(OLPlayHallRoom.setDress(this, coupleSex, "e", eye));
        coupleShoesView.setImageBitmap(OLPlayHallRoom.setDress(this, coupleSex, "s", shoes));
    }

    public void mo2848c() {
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
        Editor editor = sharedPreferences.edit();
        editor.putString("mailsString", jSONArray.toString());
        editor.apply();
    }

    public void mo2849c(ListView listView, List<Bundle> list) {
        if (mailListAdapter == null) {
            mailListAdapter = new MainGameAdapter(list, (JPApplication) getApplicationContext(), 2, this);
            listView.setAdapter(mailListAdapter);
            return;
        }
        mailListAdapter.updateList(list);
        mailListAdapter.notifyDataSetChanged();
    }

    public void addFriends(String str) {
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
            jpdialog.showDialog();
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
            userModView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "m", 1));
            userTrousersView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "t", userTrousersIndex));
            userJacketsView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "j", userJacketIndex));
            userHairView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "h", userHairIndex));
            userEyeView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "e", userEyeIndex));
            userShoesView.setImageBitmap(OLPlayHallRoom.setDress(this, userSex, "s", userShoesIndex));
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
        switch (view.getId()) {
            case R.id.ol_player_mod:
            case R.id.ol_dress_button:
                if (lv < 8) {
                    Toast.makeText(this, "您的等级未达到8级,不能进入换衣间!", Toast.LENGTH_SHORT).show();
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
                return;
            case R.id.ol_bonus_button:
                jpprogressBar.show();
                OnlineDailyDTO.Builder builder2 = OnlineDailyDTO.newBuilder();
                builder2.setType(1);
                sendMsg(OnlineProtocolType.DAILY, builder2.build());
                return;
            case R.id.create_family:
                OnlineFamilyDTO.Builder builder1 = OnlineFamilyDTO.newBuilder();
                builder1.setType(3);
                sendMsg(OnlineProtocolType.FAMILY, builder1.build());
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

    public void deleteCp(boolean flag) {
        if (cp > 0) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle("警告");
            jpdialog.setMessage("确定要解除搭档关系吗?");
            jpdialog.setFirstButton("同意", (dialog, which) -> {
                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                builder.setName(String.valueOf(flag));
                builder.setType(3);
                sendMsg(OnlineProtocolType.SET_USER_INFO, builder.build());
                dialog.dismiss();
            }).setSecondButton("取消", new DialogDismissClick());
            jpdialog.showDialog();
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        layoutinflater = LayoutInflater.from(this);
        jpApplication = (JPApplication) getApplication();
        sharedPreferences = getSharedPreferences("mails_" + jpApplication.getAccountName(), MODE_PRIVATE);
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.olplayhallroom);
        SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        jpApplication.setGameMode(GameModeEnum.NORMAL.getCode());
        hallListView = findViewById(R.id.ol_hall_list);
        hallListView.setCacheColorHint(0);
        hallList.clear();
        friendListView = findViewById(R.id.ol_friend_list);
        friendListView.setCacheColorHint(0);
        familyListView = findViewById(R.id.ol_family_list);
        familyListView.setCacheColorHint(0);
        familyListView.setLoadListener(() -> ThreadPoolUtils.execute(() -> {
            familyPageNum++;
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(2);
            builder.setPage(familyPageNum);
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        }));
        mailListView = findViewById(R.id.ol_mail_list);
        mailListView.setCacheColorHint(0);
        friendList.clear();
        familyList.clear();
        connectionService = jpApplication.getConnectionService();
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
        userEyeView = findViewById(R.id.ol_player_eye);
        userShoesView = findViewById(R.id.ol_player_shoes);
        myFamilyContribution = findViewById(R.id.ol_myfamily_contribution);
        myFamilyCount = findViewById(R.id.ol_myfamily_count);
        myFamilyName = findViewById(R.id.ol_myfamily_name);
        myFamilyPic = findViewById(R.id.ol_myfamily_pic);
        myFamilyPosition = findViewById(R.id.ol_myfamily_position);
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
        coupleNameView.setText("岛村抱月");
        Button breakUpCpButton = findViewById(R.id.ol_breakup_button);
        breakUpCpButton.setText("解除搭档");
        Button setBlessingButton = findViewById(R.id.ol_setblessing_button);
        setBlessingButton.setText("设置祝语");
        setBlessingButton.setHint(new SpannableString("输入新祝语"));
        breakUpCpButton.setOnClickListener(this);
        setBlessingButton.setOnClickListener(this);
        Button createFamily = findViewById(R.id.create_family);
        createFamily.setOnClickListener(this);
        LinearLayout myFamilyButton = findViewById(R.id.ol_myfamily_button);
        myFamilyButton.setOnClickListener(this);
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
            TextView tv = tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(0xffffffff);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        tabHost.setOnTabChangedListener(new PlayHallRoomTabChange(this));
        tabHost.setCurrentTab(1);
        switch (getIntent().getIntExtra("HEAD", 0)) {
            case 5:
                Toast.makeText(this, "您的账号重复登录……", Toast.LENGTH_SHORT).show();
                break;
            case 16:
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
        sendMsg(OnlineProtocolType.LOAD_USER, OnlineLoadUserDTO.getDefaultInstance());
    }

    @Override
    protected void onDestroy() {
        hallList.clear();
        friendList.clear();
        familyList.clear();
        mailList.clear();
        JPStack.pop(this);
        super.onDestroy();
    }
}
