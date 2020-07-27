package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendMailPage extends Activity implements Callback {
    public ListView f4019a;
    public List<JSONObject> f4021c;
    public JPApplication jpApplication;
    public JPProgressBar f4023e;
    public String f4024f = "F";
    public FriendMailPageAdapter f4025g;
    private SharedPreferences sharedPreferences = null;
    private Editor f4028j = null;

    static void m3506a(FriendMailPage friendMailPage, ListView listView, String str) {
        int i = 0;
        try {
            int i2;
            JSONArray jSONArray = new JSONArray(str);
            friendMailPage.f4021c = new ArrayList<>();
            int length = jSONArray.length();
            for (i2 = 0; i2 < length; i2++) {
                friendMailPage.f4021c.add(jSONArray.getJSONObject(i2));
            }
            if (friendMailPage.sharedPreferences != null && friendMailPage.f4028j != null) {
                jSONArray = new JSONArray(friendMailPage.sharedPreferences.getString("mailsString", "[]"));
                length = jSONArray.length();
                for (i2 = 0; i2 < length; i2++) {
                    friendMailPage.f4021c.add(jSONArray.getJSONObject(i2));
                }
                i2 = friendMailPage.f4021c.size();
                jSONArray = new JSONArray();
                while (i < i2) {
                    jSONArray.put(friendMailPage.f4021c.get(i));
                    i++;
                }
                friendMailPage.f4028j.putString("mailsString", jSONArray.toString());
                friendMailPage.f4028j.commit();
            }
            friendMailPage.f4025g = new FriendMailPageAdapter(friendMailPage, friendMailPage.f4021c);
            listView.setAdapter(friendMailPage.f4025g);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void m3505a(String str, String str2, int i, int i2) {
        String str3 = "", str4 = "";
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        TextView textView3 = inflate.findViewById(R.id.title_2);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        if (i == 1) {
            textView2.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            str3 = "同意";
            str4 = "同意" + str + "的好友请求";
        } else if (i == 2) {
            textView2.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            str3 = "确定";
            str4 = "刪除好友" + str + "?";
        } else {
            textView2.setText("消息:");
            str3 = "发送";
            str4 = "发私信给" + str;
        }
        new JPDialog(this).setTitle(str4).loadInflate(inflate).setFirstButton(str3, new sendMailClick(this, textView, i, str, i2, str2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public final void mo2683a(int i, int i2, String str, String str2) {
        int i3 = 0;
        switch (i) {
            case 0:
                Intent intent = new Intent();
                intent.putExtra("head", 2);
                intent.putExtra("userKitiName", str);
                intent.setClass(this, PopUserInfo.class);
                startActivity(intent);
                return;
            case 1:
                m3505a(str, jpApplication.getAccountName(), 2, i2);
                return;
            case 2:
                m3505a(str, jpApplication.getAccountName(), 0, 0);
                return;
            case 3:
                switch (str2) {
                    case "":
                        m3505a(str, jpApplication.getAccountName(), 1, 0);
                        return;
                    case "'":
                        m3505a(str, jpApplication.getAccountName(), 3, 0);
                        break;
                    default:
                        m3505a(str, jpApplication.getAccountName(), 0, 0);
                        return;
                }
            case 4:
                f4021c.remove(i2);
                if (f4025g != null) {
                    f4025g.mo3634a(f4021c);
                    f4025g.notifyDataSetChanged();
                }
                if (f4028j != null) {
                    int size = f4021c.size();
                    JSONArray jSONArray = new JSONArray();
                    while (i3 < size) {
                        jSONArray.put(f4021c.get(i3));
                        i3++;
                    }
                    f4028j.putString("mailsString", jSONArray.toString());
                    f4028j.commit();
                    return;
                }
                return;
            default:
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (f4023e.isShowing()) {
            f4023e.dismiss();
        }
        Intent intent = new Intent();
        intent.setClass(this, OLUsersPage.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpApplication = (JPApplication) getApplication();
        f4024f = getIntent().getStringExtra("type");
        setContentView(R.layout.friendmailpage);
        f4019a = findViewById(R.id.list_view);
        f4019a.setCacheColorHint(0);
        TextView f4026h = findViewById(R.id.ol_top_tittle);
        if (f4024f.equals("F")) {
            f4026h.setText("-好友列表-");
        } else if (f4024f.endsWith("M")) {
            f4026h.setText("-邮件列表-");
            sharedPreferences = getSharedPreferences("mails_" + jpApplication.getAccountName(), 0);
            //f4028j = sharedPreferences.edit();
        }
        f4023e = new JPProgressBar(this);
        new FriendMailPageTask(this).execute(f4024f, "GetSocialInfo");
    }
}
