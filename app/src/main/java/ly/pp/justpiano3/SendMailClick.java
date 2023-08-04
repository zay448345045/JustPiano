package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class SendMailClick implements OnClickListener {
    private final FriendMailPage friendmailpage;
    private final TextView f6046b;
    private final int f6047c;
    private final String f6048d;
    private final int f6049e;
    private final String f6050f;

    SendMailClick(FriendMailPage friendMailPage, TextView textView, int i, String str, int i2, String str2) {
        friendmailpage = friendMailPage;
        f6046b = textView;
        f6047c = i;
        f6048d = str;
        f6049e = i2;
        f6050f = str2;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f6046b.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            if (f6047c == 0) {
                if (valueOf.isEmpty() || valueOf.equals("'") || valueOf.equals("''")) {
                    Toast.makeText(friendmailpage, "请输入消息内容!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (valueOf.length() > 500) {
                    Toast.makeText(friendmailpage, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String format = SimpleDateFormat.getDateInstance(2, Locale.CHINESE).format(new Date());
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("F", f6048d);
                    jSONObject2.put("M", valueOf);
                    jSONObject2.put("T", format);
                    jSONObject2.put("type", 1);
                    JSONArray jSONArray = new JSONArray();
                    jSONArray.put(jSONObject2);
                    ListView b = friendmailpage.listView;
                    format = jSONArray.toString();
                    FriendMailPage.m3506a(friendmailpage, b, format);
                }
            } else if (f6047c == 2) {
                friendmailpage.list.remove(f6049e);
                if (friendmailpage.friendMailPageAdapter != null) {
                    friendmailpage.friendMailPageAdapter.mo3634a(friendmailpage.list);
                    friendmailpage.friendMailPageAdapter.notifyDataSetChanged();
                }
            }
            jSONObject.put("H", f6047c);
            jSONObject.put("T", f6048d);
            jSONObject.put("F", f6050f);
            jSONObject.put("M", valueOf);
            if (!f6048d.isEmpty() && !f6050f.isEmpty()) {
                valueOf = jSONObject.toString();
                new FriendMailPageTask2(friendmailpage).execute(valueOf, "GetUserInfo");
            }
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
