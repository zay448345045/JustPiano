package ly.pp.justpiano3.activity.online;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.listener.SendMessageClick;
import ly.pp.justpiano3.task.PopUserInfoTask;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public final class PopUserInfo extends BaseActivity implements Callback, OnClickListener {
    public JPProgressBar jpprogressBar;
    public int headType;
    public String userName;
    public String keywords = "P";
    private TextView userNameTextView;
    private TextView userSignatureTextView;
    private TextView userGenderTextView;
    private TextView userAgeTextView;
    private ImageView userAvatarImageView;

    public void showUserInfo(String userInfoResult) {
        JSONObject jSONObject;
        Handler handler = new Handler(this);
        PictureHandle pictureHandle = new PictureHandle(handler, 1);
        try {
            userNameTextView.setText(userName);
            pictureHandle.setBitmap(userAvatarImageView, null);
            jSONObject = new JSONObject(userInfoResult);
            userGenderTextView.setText(Objects.equals(jSONObject.getString("sx"), "m") ? "男" : "女");
            userAvatarImageView.setTag(jSONObject.getString("faceID"));
            userAgeTextView.setText((Calendar.getInstance().get(Calendar.YEAR) - jSONObject.getInt("age")) + "岁");
            String signature = jSONObject.get("ms").toString();
            userSignatureTextView.setText(TextUtils.isEmpty(signature) ? "该用户暂未设置个性签名" : signature);
        } catch (Exception e) {
            e.printStackTrace();
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
            if (!userName.equals(OLBaseActivity.kitiName)) {
                headType = 2;
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                jpDialogBuilder.setTitle("好友请求");
                jpDialogBuilder.setMessage("添加[" + userName + "]为好友,确定吗?");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        jSONObject.put("H", 0);
                        jSONObject.put("T", userName);
                        jSONObject.put("F", OLBaseActivity.getAccountName());
                        jSONObject.put("M", "");
                        if (!userName.isEmpty() && !OLBaseActivity.getAccountName().isEmpty()) {
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
            if (!Objects.equals(userName, OLBaseActivity.kitiName)) {
                headType = 2;
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_1);
                TextView textView2 = inflate.findViewById(R.id.title_1);
                TextView textView3 = inflate.findViewById(R.id.title_2);
                inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
                textView3.setVisibility(View.GONE);
                textView2.setText("消息:");
                new JPDialogBuilder(this).setTitle("发私信给" + userName).loadInflate(inflate)
                        .setFirstButton("发送", new SendMessageClick(this, textView,
                                userName, OLBaseActivity.getAccountName()))
                        .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_pop_user_info);
        userNameTextView = findViewById(R.id.user_kitiname);
        userGenderTextView = findViewById(R.id.user_sex);
        userSignatureTextView = findViewById(R.id.user_msg);
        userAvatarImageView = findViewById(R.id.user_face);
        userAgeTextView = findViewById(R.id.user_age);
        findViewById(R.id.add_friend).setOnClickListener(this);
        findViewById(R.id.send_mail).setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        headType = extras.getInt("head");
        userName = extras.getString("userName");
        jpprogressBar = new JPProgressBar(this);
        new PopUserInfoTask(this).execute();
    }
}
