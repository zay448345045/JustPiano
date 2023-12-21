package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ly.pp.justpiano3.activity.PopUserInfo;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.task.PopUserInfoTask;

public final class SendMessageClick implements OnClickListener {
    private final PopUserInfo popUserInfo;
    private final TextView textView;
    private final String f5775c;
    private final String f5776d;

    public SendMessageClick(PopUserInfo popUserInfo, TextView textView, String str, String str2) {
        this.popUserInfo = popUserInfo;
        this.textView = textView;
        f5775c = str;
        f5776d = str2;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(textView.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            if (valueOf.isEmpty() || valueOf.equals("'")) {
                Toast.makeText(popUserInfo, "请输入消息内容!", Toast.LENGTH_SHORT).show();
            } else if (valueOf.length() > Consts.MAX_MESSAGE_COUNT) {
                Toast.makeText(popUserInfo, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
            } else {
                jSONObject.put("H", 0);
                jSONObject.put("T", f5775c);
                jSONObject.put("F", f5776d);
                jSONObject.put("M", valueOf);
                if (!f5775c.isEmpty() && !f5776d.isEmpty()) {
                    popUserInfo.keywords = jSONObject.toString();
                    new PopUserInfoTask(popUserInfo).execute();
                }
            }
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
