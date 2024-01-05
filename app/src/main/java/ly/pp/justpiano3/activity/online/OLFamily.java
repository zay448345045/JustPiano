package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.protobuf.MessageLite;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.FamilyPeopleAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.FamilyPositionEnum;
import ly.pp.justpiano3.handler.android.FamilyHandler;
import ly.pp.justpiano3.listener.ChangeDeclarationClick;
import ly.pp.justpiano3.utils.BizUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPPopupWindow;
import ly.pp.justpiano3.view.JPProgressBar;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineSendMailDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

public final class OLFamily extends OLBaseActivity implements OnClickListener {
    public JPProgressBar jpprogressBar;
    public FamilyPositionEnum familyPositionEnum;
    public TextView declarationTextView;
    public TextView infoTextView;
    public FamilyHandler familyHandler;
    public String familyID;
    private String currentUserName;  // 目前选择人的名字
    private List<Map<String, Object>> familyList;
    private int familyPageNum;
    private String myFamilyPosition;
    private String myFamilyContribution;
    private String myFamilyCount;
    private String myFamilyName;
    private int listPosition;
    private byte[] myFamilyPicArray;
    public List<Map<String, String>> userList = new ArrayList<>();
    public ListView userListView;
    public PopupWindow userInfoPopupWindow;
    private LayoutInflater layoutinflater;
    private Button manageFamilyButton;
    private Button inOutButton;

    public void positionHandle() {
        switch (familyPositionEnum) {
            case LEADER -> {
                manageFamilyButton.setEnabled(true);
                inOutButton.setText("解散家族");
            }
            case VICE_LEADER, MEMBER -> inOutButton.setText("退出家族");
            case NOT_IN_FAMILY -> inOutButton.setText("申请加入");
        }
    }

    private void inOutFamily() {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        switch (familyPositionEnum) {
            case LEADER -> jpDialogBuilder.setTitle("警告").setMessage("确定要解散家族吗?");
            case VICE_LEADER, MEMBER ->
                    jpDialogBuilder.setTitle("警告").setMessage("确定要退出家族吗?");
            case NOT_IN_FAMILY ->
                    jpDialogBuilder.setTitle("提示").setMessage("申请加入家族需要族长或副族长的批准!");
        }
        jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                    OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                    builder.setType(5);
                    builder.setFamilyId(Integer.parseInt(familyID));
                    sendMsg(OnlineProtocolType.FAMILY, builder.build());
                    dialog.dismiss();
                    jpprogressBar.show();
                })
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
    }

    public void loadManageFamilyPopupWindow(Bundle bundle) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_family_manage, null);
        Button button = inflate.findViewById(R.id.ol_family_levelup);
        if (bundle.getInt("R", 0) == 1) {
            button.setEnabled(true);
        }
        TextView info = inflate.findViewById(R.id.ol_family_levelup_info);
        info.setText(bundle.getString("I", "不断提升您的等级与考级，即可将您的家族升级为人数更多、规模更大的家族!"));
        PopupWindow popupWindow = new JPPopupWindow(this);
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
        inflate.findViewById(R.id.ol_family_changedecl).setOnClickListener(this);
        button.setOnClickListener(this);
        inflate.findViewById(R.id.ol_family_changepic).setOnClickListener(this);
        inflate.findViewById(R.id.ol_family_changetest).setOnClickListener(this);
        popupWindow.showAtLocation(manageFamilyButton, Gravity.CENTER, 0, 0);
    }

    /**
     * 显示个人资料
     */
    public void showInfoDialog(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_user_info_dialog, findViewById(R.id.dialog));
        try {
            User user = new User(bundle.getString("U"), bundle.getInt("DR_H"), bundle.getInt("DR_E"), bundle.getInt("DR_J"),
                    bundle.getInt("DR_T"), bundle.getInt("DR_S"), bundle.getString("S"), bundle.getInt("LV"), bundle.getInt("CL"));
            ImageView userModView = inflate.findViewById(R.id.ol_user_mod);
            ImageView userTrousersView = inflate.findViewById(R.id.ol_user_trousers);
            ImageView userJacketView = inflate.findViewById(R.id.ol_user_jacket);
            ImageView userHairView = inflate.findViewById(R.id.ol_user_hair);
            ImageView userEyeView = inflate.findViewById(R.id.ol_user_eye);
            ImageView userShoesView = inflate.findViewById(R.id.ol_user_shoes);
            TextView userInfoTextView = inflate.findViewById(R.id.user_info);
            TextView userSignatureTextView = inflate.findViewById(R.id.user_psign);
            ImageLoadUtil.setUserDressImageBitmap(this, user, userModView, userTrousersView, userJacketView, userHairView, userEyeView, userShoesView);
            int lv = bundle.getInt("LV");
            userInfoTextView.setText("用户名称:" + bundle.getString("U")
                    + "\n用户等级:LV." + lv
                    + "\n经验进度:" + bundle.getInt("E") + "/" + BizUtil.getTargetExp(lv)
                    + "\n考级进度:CL." + bundle.getInt("CL")
                    + "\n所在家族:" + bundle.getString("F")
                    + "\n在线曲库冠军数:" + bundle.getInt("W")
                    + "\n在线曲库弹奏总分:" + bundle.getInt("SC"));
            userSignatureTextView.setText("个性签名:\n" + (bundle.getString("P").isEmpty() ? "无" : bundle.getString("P")));
            new JPDialogBuilder(this).setWidth(288).setTitle("个人资料").loadInflate(inflate)
                    .setFirstButton("加为好友", (dialog, which) -> {
                        if (OnlineUtil.getConnectionService() == null) {
                            return;
                        }
                        OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
                        builder.setMessage("");
                        builder.setName(user.getPlayerName());
                        sendMsg(OnlineProtocolType.SEND_MAIL, builder.build());
                        dialog.dismiss();
                        Toast.makeText(this, "已向对方发送私信，请等待对方同意!", Toast.LENGTH_SHORT).show();
                    })
                    .setSecondButton("确定", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(0);
        sendMsg(OnlineProtocolType.FAMILY, builder.build());
        Intent intent = new Intent(this, OLPlayHallRoom.class);
        intent.putExtra("HEAD", 16);
        intent.putExtra("pageNum", familyPageNum);
        intent.putExtra("position", listPosition);
        intent.putExtra("myFamilyPosition", myFamilyPosition);
        intent.putExtra("myFamilyContribution", myFamilyContribution);
        intent.putExtra("myFamilyCount", myFamilyCount);
        intent.putExtra("myFamilyName", myFamilyName);
        intent.putExtra("myFamilyPicArray", myFamilyPicArray);
        intent.putExtra("familyList", (Serializable) familyList);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.manage_family) {
            jpprogressBar.show();
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(8);
            builder.setFamilyId(Integer.parseInt(familyID));
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        } else if (id == R.id.in_out) {
            if (userInfoPopupWindow != null && userInfoPopupWindow.isShowing()) {
                userInfoPopupWindow.dismiss();
            }
            inOutFamily();
        } else if (id == R.id.ol_chat_b) {
            if (userInfoPopupWindow != null && userInfoPopupWindow.isShowing()) {
                userInfoPopupWindow.dismiss();
            }
            showSendDialog(currentUserName, 0);
        } else if (id == R.id.ol_kickout_b) {
            if (userInfoPopupWindow != null && userInfoPopupWindow.isShowing()) {
                userInfoPopupWindow.dismiss();
            }
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("确定要将Ta移出家族吗?");
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                        OnlineFamilyDTO.Builder familyBuilder = OnlineFamilyDTO.newBuilder();
                        familyBuilder.setType(6);
                        familyBuilder.setUserName(currentUserName);
                        familyBuilder.setStatus(1);
                        sendMsg(OnlineProtocolType.FAMILY, familyBuilder.build());
                        dialog.dismiss();
                        jpprogressBar.show();
                    })
                    .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } else if (id == R.id.ol_showinfo_b) {
            if (userInfoPopupWindow != null && userInfoPopupWindow.isShowing()) {
                userInfoPopupWindow.dismiss();
            }
            if (OnlineUtil.getConnectionService() != null) {
                OnlineUserInfoDialogDTO.Builder builder1 = OnlineUserInfoDialogDTO.newBuilder();
                builder1.setName(currentUserName);
                sendMsg(OnlineProtocolType.USER_INFO_DIALOG, builder1.build());
            }
        } else if (id == R.id.ol_couple_b) {  // 提升/撤职副族长
            if (userInfoPopupWindow != null && userInfoPopupWindow.isShowing()) {
                userInfoPopupWindow.dismiss();
            }
            jpprogressBar.show();
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(7);
            builder.setUserName(currentUserName);
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        } else if (id == R.id.ol_chat_black) {  // 族长转移
            if (userInfoPopupWindow != null && userInfoPopupWindow.isShowing()) {
                userInfoPopupWindow.dismiss();
            }
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("确定要把族长转移给Ta吗?");
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                        OnlineFamilyDTO.Builder familyBuilder = OnlineFamilyDTO.newBuilder();
                        familyBuilder.setType(11);
                        familyBuilder.setUserName(currentUserName);
                        familyBuilder.setStatus(0);
                        sendMsg(OnlineProtocolType.FAMILY, familyBuilder.build());
                        dialog.dismiss();
                        jpprogressBar.show();
                    })
                    .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } else if (id == R.id.ol_family_changedecl) {
            showSendDialog(" ", 1);
        } else if (id == R.id.ol_family_changepic) {
            JPDialogBuilder jpDialogBuilder;
            jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("当前版本不支持家族族徽的上传，请至官网上传");
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
            jpDialogBuilder.buildAndShowDialog();
        } else if (id == R.id.ol_family_changetest) {
        } else if (id == R.id.ol_family_levelup) {
            OnlineFamilyDTO.Builder builder;
            builder = OnlineFamilyDTO.newBuilder();
            builder.setType(10);
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        } else if (id == R.id.column_position) {
            Collections.sort(userList, (map1, map2) -> map1.get("P").compareTo(map2.get("P")));
            bindFamilyPeopleListViewAdapter(userListView, userList);
        } else if (id == R.id.column_family_name) {
            Collections.sort(userList, (map1, map2) -> map2.get("O").compareTo(map1.get("O")));
            bindFamilyPeopleListViewAdapter(userListView, userList);
        } else if (id == R.id.column_last_login_time) {
            Collections.sort(userList, new Comparator<>() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                @Override
                public int compare(Map<String, String> map1, Map<String, String> map2) {
                    try {
                        return dateFormat.parse(map2.get("D")).compareTo(dateFormat.parse(map1.get("D")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            bindFamilyPeopleListViewAdapter(userListView, userList);
        } else if (id == R.id.column_level) {
            Collections.sort(userList, (map1, map2) -> Integer.compare(
                    Integer.parseInt(map2.get("L")), Integer.parseInt(map1.get("L"))));
            bindFamilyPeopleListViewAdapter(userListView, userList);
        } else if (id == R.id.column_contribution) {
            Collections.sort(userList, (map1, map2) -> Integer.compare(
                    Integer.parseInt(map2.get("C")), Integer.parseInt(map1.get("C"))));
            bindFamilyPeopleListViewAdapter(userListView, userList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        familyHandler = new FamilyHandler(this);
        Bundle bundle = getIntent().getExtras();
        familyID = bundle.getString("familyID");
        familyPageNum = bundle.getInt("pageNum");
        listPosition = bundle.getInt("position");
        myFamilyContribution = bundle.getString("myFamilyContribution");
        myFamilyCount = bundle.getString("myFamilyCount");
        myFamilyName = bundle.getString("myFamilyName");
        myFamilyPosition = bundle.getString("myFamilyPosition");
        myFamilyPicArray = bundle.getByteArray("myFamilyPicArray");
        familyList = (List<Map<String, Object>>) getIntent().getSerializableExtra("familyList");
        jpprogressBar = new JPProgressBar(this);
        jpprogressBar.show();
        layoutinflater = LayoutInflater.from(this);
        setContentView(R.layout.ol_family);
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(1);
        builder.setFamilyId(Integer.parseInt(familyID));
        sendMsg(OnlineProtocolType.FAMILY, builder.build());
        inOutButton = findViewById(R.id.in_out);
        inOutButton.setOnClickListener(this);
        manageFamilyButton = findViewById(R.id.manage_family);
        manageFamilyButton.setOnClickListener(this);
        userListView = findViewById(R.id.family_people_list);
        declarationTextView = findViewById(R.id.declaration);
        infoTextView = findViewById(R.id.info_text);
        findViewById(R.id.column_position).setOnClickListener(this);
        findViewById(R.id.column_family_name).setOnClickListener(this);
        findViewById(R.id.column_last_login_time).setOnClickListener(this);
        findViewById(R.id.column_level).setOnClickListener(this);
        findViewById(R.id.column_contribution).setOnClickListener(this);
        if (myFamilyPicArray != null && myFamilyPicArray.length > 1) {
            ((ImageView) findViewById(R.id.column_pic)).setImageBitmap(
                    BitmapFactory.decodeByteArray(myFamilyPicArray, 0, myFamilyPicArray.length));
        }
    }

    public PopupWindow loadUserInfoPopupWindow(String name, FamilyPositionEnum userPosition) {
        PopupWindow userInfoPopupWindow = new JPPopupWindow(this);
        View userOperationView = LayoutInflater.from(this).inflate(R.layout.ol_room_user_operation, null);
        Button showInfoButton = userOperationView.findViewById(R.id.ol_showinfo_b);  // 个人资料
        Button mailSendButton = userOperationView.findViewById(R.id.ol_chat_b);  // 私信
        Button kickOutButton = userOperationView.findViewById(R.id.ol_kickout_b);  // 移出家族
        Button positionChangeButton = userOperationView.findViewById(R.id.ol_couple_b);  // 提升/撤职副族长
        Button leaderChangeButton = userOperationView.findViewById(R.id.ol_chat_black);  // 转移族长
        mailSendButton.setText("发送私信");
        kickOutButton.setText("移出家族");
        leaderChangeButton.setText("族长转移");
        userOperationView.findViewById(R.id.ol_closepos_b).setVisibility(View.GONE);
        userInfoPopupWindow.setContentView(userOperationView);
        userInfoPopupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
        if (familyPositionEnum == FamilyPositionEnum.MEMBER || familyPositionEnum == FamilyPositionEnum.NOT_IN_FAMILY
                || (familyPositionEnum == FamilyPositionEnum.VICE_LEADER && userPosition != FamilyPositionEnum.MEMBER)) {
            kickOutButton.setVisibility(View.GONE);
        }
        if (name.equals(OLBaseActivity.getKitiName())) {
            kickOutButton.setVisibility(View.GONE);
            mailSendButton.setVisibility(View.GONE);
        }
        if (familyPositionEnum != FamilyPositionEnum.LEADER) {
            positionChangeButton.setVisibility(View.GONE);
            leaderChangeButton.setVisibility(View.GONE);
        } else {
            switch (userPosition) {
                case VICE_LEADER -> positionChangeButton.setText("撤职副族长");
                case MEMBER -> positionChangeButton.setText("晋升副族长");
                default -> {
                    leaderChangeButton.setVisibility(View.GONE);
                    positionChangeButton.setVisibility(View.GONE);
                }
            }
            positionChangeButton.setOnClickListener(this);
        }
        if (familyPositionEnum == FamilyPositionEnum.NOT_IN_FAMILY) {
            showInfoButton.setVisibility(View.GONE);
            mailSendButton.setVisibility(View.GONE);
        }
        mailSendButton.setOnClickListener(this);
        showInfoButton.setOnClickListener(this);
        kickOutButton.setOnClickListener(this);
        leaderChangeButton.setOnClickListener(this);
        currentUserName = name;
        this.userInfoPopupWindow = userInfoPopupWindow;
        return userInfoPopupWindow;
    }

    // 发送私信和祝语,type = 0 私信，type = 1 改变宣言
    private void showSendDialog(String userName, int type) {
        String buttonText;
        String title;
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        inflate.findViewById(R.id.title_2).setVisibility(View.GONE);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView2.setText("内容:");
        if (type == 0) {
            buttonText = "发送";
            title = "发送私信给:" + userName;
        } else if (type == 1) {
            title = "设置家族宣言";
            buttonText = "修改";
            textView.setText(declarationTextView.getText().toString().substring(6));
        } else {
            return;
        }
        new JPDialogBuilder(this).setTitle(title).loadInflate(inflate)
                .setFirstButton(buttonText, new ChangeDeclarationClick(this, textView, type, userName))
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
    }

    public void bindFamilyPeopleListViewAdapter(ListView listView, List<Map<String, String>> list) {
        listView.setAdapter(new FamilyPeopleAdapter(list, layoutinflater, this));
    }

    public void sendMsg(int type, MessageLite msg) {
        if (OnlineUtil.getConnectionService() != null) {
            OnlineUtil.getConnectionService().writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }
}
