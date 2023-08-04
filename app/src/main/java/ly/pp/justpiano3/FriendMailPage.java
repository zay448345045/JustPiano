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
    public ListView listView;
    public List<JSONObject> list;
    public JPApplication jpApplication;
    public JPProgressBar jpProgressBar;
    public String type = "B";
    public FriendMailPageAdapter friendMailPageAdapter;
    private SharedPreferences sharedPreferences = null;
    private final Editor editor = null;

    static void m3506a(FriendMailPage friendMailPage, ListView listView, String str) {
        int i = 0;
        try {
            int i2;
            JSONArray jSONArray = new JSONArray(str);
            friendMailPage.list = new ArrayList<>();
            int length = jSONArray.length();
            for (i2 = 0; i2 < length; i2++) {
                friendMailPage.list.add(jSONArray.getJSONObject(i2));
            }
            if (friendMailPage.sharedPreferences != null && friendMailPage.editor != null) {
                jSONArray = new JSONArray(friendMailPage.sharedPreferences.getString("mailsString", "[]"));
                length = jSONArray.length();
                for (i2 = 0; i2 < length; i2++) {
                    friendMailPage.list.add(jSONArray.getJSONObject(i2));
                }
                i2 = friendMailPage.list.size();
                jSONArray = new JSONArray();
                while (i < i2) {
                    jSONArray.put(friendMailPage.list.get(i));
                    i++;
                }
                friendMailPage.editor.putString("mailsString", jSONArray.toString());
                friendMailPage.editor.commit();
            }
            friendMailPage.friendMailPageAdapter = new FriendMailPageAdapter(friendMailPage, friendMailPage.list);
            listView.setAdapter(friendMailPage.friendMailPageAdapter);
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
        new JPDialog(this).setTitle(str4).loadInflate(inflate).setFirstButton(str3, new SendMailClick(this, textView, i, str, i2, str2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
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
                list.remove(i2);
                if (friendMailPageAdapter != null) {
                    friendMailPageAdapter.mo3634a(list);
                    friendMailPageAdapter.notifyDataSetChanged();
                }
                if (editor != null) {
                    int size = list.size();
                    JSONArray jSONArray = new JSONArray();
                    while (i3 < size) {
                        jSONArray.put(list.get(i3));
                        i3++;
                    }
                    editor.putString("mailsString", jSONArray.toString());
                    editor.commit();
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
        jpProgressBar.dismiss();
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpApplication = (JPApplication) getApplication();
        type = getIntent().getStringExtra("type");
        setContentView(R.layout.friendmailpage);
        listView = findViewById(R.id.list_view);
        listView.setCacheColorHint(0);
        TextView textView = findViewById(R.id.ol_top_title);
        if (type.equals("B")) {
            textView.setText("聊天屏蔽用户名单");
        } else if (type.endsWith("M")) {
            textView.setText("-邮件列表-");
            sharedPreferences = getSharedPreferences("mails_" + jpApplication.getAccountName(), MODE_PRIVATE);
            //f4028j = sharedPreferences.edit();
        }
        jpProgressBar = new JPProgressBar(this);
        new FriendMailPageTask(this).execute(type, "GetSocialInfo");
    }
}
