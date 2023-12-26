package ly.pp.justpiano3.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.listener.SendMessageClick;
import ly.pp.justpiano3.task.PopUserInfoTask;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public final class PopUserInfo extends BaseActivity implements Callback, OnClickListener {
    public JPProgressBar jpprogressBar;
    public int headType;
    public String kitiName = "";
    public String keywords = "P";
    private TextView userKitiName;
    private TextView userPSign;
    private TextView userSex;
    private TextView userAge;
    private ImageView userFace;
    private PictureHandle pictureHandle;
    private Handler handler;

    public static void showUserInfo(PopUserInfo popUserInfo, String str) {
        JSONObject jSONObject;
        popUserInfo.handler = new Handler(popUserInfo);
        popUserInfo.pictureHandle = new PictureHandle(popUserInfo.handler, 1);
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONObject = null;
        }
        try {
            popUserInfo.userKitiName.setText(popUserInfo.kitiName);
            String sex = jSONObject.getString("sx");
            if (sex.equals("m")) {
                popUserInfo.userSex.setText("男");
            } else if (sex.equals("f")) {
                popUserInfo.userSex.setText("女");
            }
            popUserInfo.userFace.setTag(jSONObject.getString("faceID"));
            popUserInfo.pictureHandle.setBitmap(popUserInfo.userFace, null);
            int age = jSONObject.getInt("age");
            Calendar cal = Calendar.getInstance();
            popUserInfo.userAge.setText((cal.get(Calendar.YEAR) - age) + "岁");
            String obj = jSONObject.get("ms").toString();
            if (obj.isEmpty()) {
                obj = "该用户暂未设置个性签名";
            }
            popUserInfo.userPSign.setText(obj);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_friend) {
            if (!kitiName.equals(OLBaseActivity.kitiName)) {
                headType = 2;
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                jpDialogBuilder.setTitle("好友请求");
                jpDialogBuilder.setMessage("添加[" + kitiName + "]为好友,确定吗?");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("H", 0);
                        jSONObject.put("T", kitiName);
                        jSONObject.put("F", OLBaseActivity.getAccountName());
                        jSONObject.put("M", "");
                        if (!kitiName.isEmpty() && !OLBaseActivity.getAccountName().isEmpty()) {
                            keywords = jSONObject.toString();
                            new PopUserInfoTask(this).execute();
                        }
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.buildAndShowDialog();
            }
        } else if (id == R.id.send_mail) {
            if (!kitiName.equals(OLBaseActivity.kitiName)) {
                headType = 2;
                String str = kitiName;
                String P = OLBaseActivity.getAccountName();
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_1);
                TextView textView2 = inflate.findViewById(R.id.title_1);
                TextView textView3 = inflate.findViewById(R.id.title_2);
                inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);
                textView2.setText("消息:");
                new JPDialogBuilder(this).setTitle("发私信给" + str).loadInflate(inflate)
                        .setFirstButton("发送", new SendMessageClick(this, textView, str, P))
                        .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_pop_user_info);
        userKitiName = findViewById(R.id.user_kitiname);
        userSex = findViewById(R.id.user_sex);
        userPSign = findViewById(R.id.user_msg);
        userFace = findViewById(R.id.user_face);
        userAge = findViewById(R.id.user_age);
        findViewById(R.id.add_friend).setOnClickListener(this);
        findViewById(R.id.send_mail).setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        headType = extras.getInt("head");
        kitiName = extras.getString("userKitiName");
        jpprogressBar = new JPProgressBar(this);
        new PopUserInfoTask(this).execute();
    }
}
