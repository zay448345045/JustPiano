package ly.pp.justpiano3;

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

public class PopUserInfo extends Activity implements Callback, OnClickListener {
    JPProgressBar jpprogressBar;
    int f4828b = 0;
    String f4829c = "";
    String f4830d = "P";
    JPApplication jpapplication;
    String f4839m = "";
    private TextView f4831e;
    private TextView f4832f;
    private TextView f4833g;
    private TextView f4834h;
    private ImageView f4835i;
    private PictureHandle f4837k;
    private Handler handler;

    static void m3823a(PopUserInfo popUserInfo, String str) {
        JSONObject jSONObject;
        popUserInfo.handler = new Handler(popUserInfo);
        popUserInfo.f4837k = new PictureHandle(popUserInfo.handler, 1);
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONObject = null;
        }
        try {
            popUserInfo.f4831e.setText(popUserInfo.f4829c);
            if (jSONObject.getString("sex").equals("m")) {
                popUserInfo.f4833g.setText("男");
            } else {
                popUserInfo.f4833g.setText("女");
            }
            popUserInfo.f4835i.setTag(jSONObject.getString("faceID"));
            popUserInfo.f4837k.mo3027a(popUserInfo.jpapplication, popUserInfo.f4835i, null);
            popUserInfo.f4834h.setText(jSONObject.getInt("age") + "岁");
            String obj = jSONObject.get("msg").toString();
            if (obj.isEmpty()) {
                obj = "该用户暂未设置个性签名";
            }
            popUserInfo.f4832f.setText(obj);
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
                if (!f4829c.equals(JPApplication.kitiName)) {
                    f4828b = 2;
                    JPDialog jpdialog = new JPDialog(this);
                    jpdialog.setTitle("好友请求");
                    jpdialog.setMessage("添加[" + f4829c + "]为好友,确定吗?");
                    jpdialog.setFirstButton("确定", new AddFriendsClick2(this));
                    jpdialog.setSecondButton("取消", new DialogDismissClick());
                    jpdialog.showDialog();
                    return;
                }
                return;
            case R.id.send_mail:
                if (!f4829c.equals(JPApplication.kitiName)) {
                    f4828b = 2;
                    String str = f4829c;
                    String P = jpapplication.getAccountName();
                    View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                    TextView textView = inflate.findViewById(R.id.text_1);
                    TextView textView2 = inflate.findViewById(R.id.title_1);
                    TextView textView3 = inflate.findViewById(R.id.title_2);
                    inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
                    textView3.setVisibility(View.GONE);
                    textView2.setText("消息:");
                    new JPDialog(this).setTitle("发私信给" + str).loadInflate(inflate).setFirstButton("发送", new SendMessageClick(this, textView, str, P)).setSecondButton("取消", new DialogDismissClick()).showDialog();
                    return;
                }
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.popuserinfo);
        jpapplication = (JPApplication) getApplication();
        f4831e = findViewById(R.id.user_kitiname);
        f4833g = findViewById(R.id.user_sex);
        f4832f = findViewById(R.id.user_msg);
        f4835i = findViewById(R.id.user_face);
        f4834h = findViewById(R.id.user_age);
        Button f4840n = findViewById(R.id.add_friend);
        f4840n.setOnClickListener(this);
        Button f4841o = findViewById(R.id.send_mail);
        f4841o.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        f4828b = extras.getInt("head");
        if (f4828b == 2) {
            f4828b = 1;
            f4840n.setVisibility(View.INVISIBLE);
        }
        f4829c = extras.getString("userKitiName");
        jpprogressBar = new JPProgressBar(this);
        f4839m = "GetUserInfo";
        new PopUserInfoTask(this).execute();
    }
}
