package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.listener.SendMessageClick;
import ly.pp.justpiano3.task.PopUserInfoTask;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class PopUserInfo extends Activity implements Callback, OnClickListener {
    public JPProgressBar jpprogressBar;
    public int headType = 0;
    public String kitiName = "";
    public String f4830d = "P";
    public JPApplication jpapplication;
    public String f4839m = "";
    private TextView userKitiName;
    private TextView userPSign;
    private TextView userSex;
    private TextView userAge;
    private ImageView userFace;
    private PictureHandle pictureHandle;
    private Handler handler;

    public static void m3823a(PopUserInfo popUserInfo, String str) {
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
//            popUserInfo.userFace.setTag(jSONObject.getString("faceID"));
//            popUserInfo.pictureHandle.mo3027a(popUserInfo.jpapplication, popUserInfo.userFace, null);
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
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_friend:
                if (!kitiName.equals(JPApplication.kitiName)) {
                    headType = 2;
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                    jpDialogBuilder.setTitle("好友请求");
                    jpDialogBuilder.setMessage("添加[" + kitiName + "]为好友,确定吗?");
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("H", 0);
                            jSONObject.put("T", kitiName);
                            jSONObject.put("F", jpapplication.getAccountName());
                            jSONObject.put("M", "");
                            if (!kitiName.isEmpty() && !jpapplication.getAccountName().isEmpty()) {
                                f4830d = jSONObject.toString();
                                new PopUserInfoTask(this).execute();
                            }
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    jpDialogBuilder.setSecondButton("取消", ((dialog, which) -> dialog.dismiss()));
                    jpDialogBuilder.buildAndShowDialog();
                    return;
                }
                return;
            case R.id.send_mail:
                if (!kitiName.equals(JPApplication.kitiName)) {
                    headType = 2;
                    String str = kitiName;
                    String P = jpapplication.getAccountName();
                    View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                    TextView textView = inflate.findViewById(R.id.text_1);
                    TextView textView2 = inflate.findViewById(R.id.title_1);
                    TextView textView3 = inflate.findViewById(R.id.title_2);
                    inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
                    textView3.setVisibility(View.GONE);
                    textView2.setText("消息:");
                    new JPDialogBuilder(this).setTitle("发私信给" + str).loadInflate(inflate)
                            .setFirstButton("发送", new SendMessageClick(this, textView, str, P))
                            .setSecondButton("取消", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
                    return;
                }
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_pop_user_info);
        jpapplication = (JPApplication) getApplication();
        userKitiName = findViewById(R.id.user_kitiname);
        userSex = findViewById(R.id.user_sex);
        userPSign = findViewById(R.id.user_msg);
        userFace = findViewById(R.id.user_face);
        userAge = findViewById(R.id.user_age);
        Button f4840n = findViewById(R.id.add_friend);
        f4840n.setOnClickListener(this);
        Button f4841o = findViewById(R.id.send_mail);
        f4841o.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        headType = extras.getInt("head");
        if (headType == 2) {
            headType = 1;
            f4840n.setVisibility(View.INVISIBLE);
        }
        kitiName = extras.getString("userKitiName");
        jpprogressBar = new JPProgressBar(this);
        f4839m = "GetUserInfo";
        new PopUserInfoTask(this).execute();
    }
}
