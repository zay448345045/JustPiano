package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
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

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.FamilyPeopleAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.FamilyPositionEnum;
import ly.pp.justpiano3.handler.android.FamilyHandler;
import ly.pp.justpiano3.listener.ChangeDeclarationClick;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPPopupWindow;
import ly.pp.justpiano3.view.JPProgressBar;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineSendMailDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

public final class OLFamily extends OLBaseActivity implements OnClickListener {
    private JPApplication jpApplication;
    public JPProgressBar jpprogressBar;
    public FamilyPositionEnum position;
    public TextView declaration;
    public TextView info;
    public FamilyHandler familyHandler;
    public String familyID;
    private String peopleNow;  // 目前选择人的名字
    private List<Map<String, Object>> familyList;
    private int familyPageNum;
    private String myFamilyPosition;
    private String myFamilyContribution;
    private String myFamilyCount;
    private String myFamilyName;
    private int listPosition;
    private byte[] myFamilyPicArray;
    public List<Map<String, String>> peopleList = new ArrayList<>();
    public ListView peopleListView;
    public PopupWindow infoWindow;
    private LayoutInflater layoutinflater;
    private Button manageFamily;
    private Button inOut;

    public void positionHandle() {
        switch (position) {
            case LEADER -> {
                manageFamily.setEnabled(true);
                inOut.setText("解散家族");
            }
            case VICE_LEADER, MEMBER -> inOut.setText("退出家族");
            case NOT_IN_FAMILY -> inOut.setText("申请加入");
        }
    }

    private void inOutFamily() {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        switch (position) {
            case LEADER -> jpDialogBuilder.setTitle("警告").setMessage("确定要解散家族吗?");
            case VICE_LEADER, MEMBER -> jpDialogBuilder.setTitle("警告").setMessage("确定要退出家族吗?");
            case NOT_IN_FAMILY -> jpDialogBuilder.setTitle("提示").setMessage("申请加入家族需要族长或副族长的批准!");
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

    public void loadManageFamilyPopupWindow(Bundle b) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_family_manage, null);
        Button button = inflate.findViewById(R.id.ol_family_levelup);
        if (b.getInt("R", 0) == 1) {
            button.setEnabled(true);
        }
        TextView info = inflate.findViewById(R.id.ol_family_levelup_info);
        info.setText(b.getString("I", "不断提升您的等级与考级，即可将您的家族升级为人数更多、规模更大的家族!"));
        PopupWindow popupWindow = new JPPopupWindow(this);
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
        inflate.findViewById(R.id.ol_family_changedecl).setOnClickListener(this);
        button.setOnClickListener(this);
        inflate.findViewById(R.id.ol_family_changepic).setOnClickListener(this);
        inflate.findViewById(R.id.ol_family_changetest).setOnClickListener(this);
        popupWindow.showAtLocation(manageFamily, Gravity.CENTER, 0, 0);
    }

    /**
     * 显示个人资料
     */
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
            if (infoWindow != null && infoWindow.isShowing()) {
                infoWindow.dismiss();
            }
            inOutFamily();
        } else if (id == R.id.ol_chat_b) {
            if (infoWindow != null && infoWindow.isShowing()) {
                infoWindow.dismiss();
            }
            showSendDialog(peopleNow, 0);
        } else if (id == R.id.ol_kickout_b) {
            if (infoWindow != null && infoWindow.isShowing()) {
                infoWindow.dismiss();
            }
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("确定要将Ta移出家族吗?");
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                        OnlineFamilyDTO.Builder familyBuilder = OnlineFamilyDTO.newBuilder();
                        familyBuilder.setType(6);
                        familyBuilder.setUserName(peopleNow);
                        familyBuilder.setStatus(1);
                        sendMsg(OnlineProtocolType.FAMILY, familyBuilder.build());
                        dialog.dismiss();
                        jpprogressBar.show();
                    })
                    .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } else if (id == R.id.ol_showinfo_b) {
            if (infoWindow != null && infoWindow.isShowing()) {
                infoWindow.dismiss();
            }
            if (OnlineUtil.getConnectionService() != null) {
                OnlineUserInfoDialogDTO.Builder builder1 = OnlineUserInfoDialogDTO.newBuilder();
                builder1.setName(peopleNow);
                sendMsg(OnlineProtocolType.USER_INFO_DIALOG, builder1.build());
            }
        } else if (id == R.id.ol_couple_b) {  //提升/撤职副族长
            OnlineFamilyDTO.Builder builder;
            if (infoWindow != null && infoWindow.isShowing()) {
                infoWindow.dismiss();
            }
            jpprogressBar.show();
            builder = OnlineFamilyDTO.newBuilder();
            builder.setType(7);
            builder.setUserName(peopleNow);
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        } else if (id == R.id.ol_family_changedecl) {
            showSendDialog(" ", 1);
        } else if (id == R.id.ol_family_changepic) {
            JPDialogBuilder jpDialogBuilder;
            jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("当前版本不支持家族族徽的上传，请至官网上传");
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
            jpDialogBuilder.buildAndShowDialog();
//                Intent intent = new Intent("android.intent.action.GET_CONTENT");
//                intent.addCategory("android.intent.category.OPENABLE");
//                intent.setType("image/*");
//                startActivityForResult(intent, 2);
        } else if (id == R.id.ol_family_changetest) {
        } else if (id == R.id.ol_family_levelup) {
            OnlineFamilyDTO.Builder builder;
            builder = OnlineFamilyDTO.newBuilder();
            builder.setType(10);
            sendMsg(OnlineProtocolType.FAMILY, builder.build());
        } else if (id == R.id.column_position) {
            Collections.sort(peopleList, (map1, map2) -> map1.get("P").compareTo(map2.get("P")));
            bindFamilyPeopleListViewAdapter(peopleListView, peopleList);
        } else if (id == R.id.column_family_name) {
            Collections.sort(peopleList, (map1, map2) -> map2.get("O").compareTo(map1.get("O")));
            bindFamilyPeopleListViewAdapter(peopleListView, peopleList);
        } else if (id == R.id.column_last_login_time) {
            Collections.sort(peopleList, new Comparator<Map<String, String>>() {
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
            bindFamilyPeopleListViewAdapter(peopleListView, peopleList);
        } else if (id == R.id.column_level) {
            Collections.sort(peopleList, (map1, map2) -> Integer.compare(
                    Integer.parseInt(map2.get("L")), Integer.parseInt(map1.get("L"))));
            bindFamilyPeopleListViewAdapter(peopleListView, peopleList);
        } else if (id == R.id.column_contribution) {
            Collections.sort(peopleList, (map1, map2) -> Integer.compare(
                    Integer.parseInt(map2.get("C")), Integer.parseInt(map1.get("C"))));
            bindFamilyPeopleListViewAdapter(peopleListView, peopleList);
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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        jpApplication = (JPApplication) getApplication();
        setContentView(R.layout.ol_family);
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(1);
        builder.setFamilyId(Integer.parseInt(familyID));
        sendMsg(OnlineProtocolType.FAMILY, builder.build());
        inOut = findViewById(R.id.in_out);
        inOut.setOnClickListener(this);
        manageFamily = findViewById(R.id.manage_family);
        manageFamily.setOnClickListener(this);
        peopleListView = findViewById(R.id.family_people_list);
        declaration = findViewById(R.id.declaration);
        info = findViewById(R.id.info_text);
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

    public PopupWindow loadInfoPopupWindow(String name, FamilyPositionEnum userPosition) {
        PopupWindow popupWindow = new JPPopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_room_user_operation, null);
        Button showInfoButton = inflate.findViewById(R.id.ol_showinfo_b);  //个人资料
        Button mailSendButton = inflate.findViewById(R.id.ol_chat_b);  //私信
        Button kickOutButton = inflate.findViewById(R.id.ol_kickout_b);  //移出家族
        Button positionChangeButton = inflate.findViewById(R.id.ol_couple_b);  //提升/撤职副族长
        inflate.findViewById(R.id.ol_chat_black).setVisibility(View.GONE);
        mailSendButton.setText("发送私信");
        kickOutButton.setText("移出家族");
        inflate.findViewById(R.id.ol_closepos_b).setVisibility(View.GONE);
        popupWindow.setContentView(inflate);
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable._none, getTheme()));
        if (position == FamilyPositionEnum.MEMBER || position == FamilyPositionEnum.NOT_IN_FAMILY
                || (position == FamilyPositionEnum.VICE_LEADER && userPosition != FamilyPositionEnum.MEMBER)) {
            kickOutButton.setVisibility(View.GONE);
        }
        if (name.equals(jpApplication.getKitiName())) {
            kickOutButton.setVisibility(View.GONE);
            mailSendButton.setVisibility(View.GONE);
        }
        if (position != FamilyPositionEnum.LEADER) {
            positionChangeButton.setVisibility(View.GONE);
        } else {
            switch (userPosition) {
                case VICE_LEADER -> positionChangeButton.setText("撤职副族长");
                case MEMBER -> positionChangeButton.setText("晋升副族长");
                default -> positionChangeButton.setVisibility(View.GONE);
            }
            positionChangeButton.setOnClickListener(this);
        }
        if (position == FamilyPositionEnum.NOT_IN_FAMILY) {
            showInfoButton.setVisibility(View.GONE);
            mailSendButton.setVisibility(View.GONE);
        }
        mailSendButton.setOnClickListener(this);
        showInfoButton.setOnClickListener(this);
        kickOutButton.setOnClickListener(this);
        peopleNow = name;
        infoWindow = popupWindow;
        return popupWindow;
    }

    //发送私信和祝语,i = 0私信，i = 1改变宣言
    private void showSendDialog(String str, int i) {
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
            str3 = "设置家族宣言";
            str2 = "修改";
            textView.setText(declaration.getText().toString().substring(6));
        } else {
            return;
        }
        new JPDialogBuilder(this).setTitle(str3).loadInflate(inflate)
                .setFirstButton(str2, new ChangeDeclarationClick(this, textView, i, str))
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
    }

    public void bindFamilyPeopleListViewAdapter(ListView listView, List<Map<String, String>> list) {
        listView.setAdapter(new FamilyPeopleAdapter(list, jpApplication, layoutinflater, this));
    }

    public void sendMsg(int type, MessageLite msg) {
        if (OnlineUtil.getConnectionService() != null) {
            OnlineUtil.getConnectionService().writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null && requestCode == 2 && resultCode == Activity.RESULT_OK) {
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(data.getData());
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                byte[] pic = ImageResizerUtil.compressScale(bitmap);
//                JSONObject jSONObject = new JSONObject();
//                jSONObject.put("K", 11);
//                jSONObject.put("J", GZIPUtil.arrayToZIP(pic));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
