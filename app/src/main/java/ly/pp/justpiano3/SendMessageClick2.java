package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class SendMessageClick2 implements OnClickListener {
    private final OLPlayHall olPlayHall;
    private final TextView textview;
    private final String str;

    SendMessageClick2(OLPlayHall olPlayHall, TextView textView, String string) {
        this.olPlayHall = olPlayHall;
        textview = textView;
        str = string;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(textview.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            if (valueOf.isEmpty() || valueOf.equals("'")) {
                Toast.makeText(olPlayHall, "请输入信件内容!", Toast.LENGTH_SHORT).show();
            } else if (valueOf.length() > 300) {
                Toast.makeText(olPlayHall, "确定在三百字之内!", Toast.LENGTH_SHORT).show();
            } else {
                jSONObject.put("T", str);
                jSONObject.put("M", valueOf);
                if (!str.isEmpty()) {
                    olPlayHall.cs.writeData((byte) 35, (byte) 0, (byte) 0, jSONObject.toString(), null);
                }
                dialogInterface.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
