package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class SendMailClick2 implements OnClickListener {
    private final OLPlayRoom olPlayRoom;
    private final TextView textView;
    private final String f5509c;

    SendMailClick2(OLPlayRoom olPlayRoom, TextView textView, String str) {
        this.olPlayRoom = olPlayRoom;
        this.textView = textView;
        f5509c = str;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(textView.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            if (valueOf.isEmpty() || valueOf.equals("'")) {
                Toast.makeText(olPlayRoom, "请输入信件内容!", Toast.LENGTH_SHORT).show();
            } else if (valueOf.length() > 300) {
                Toast.makeText(olPlayRoom, "确定在三百字之内!", Toast.LENGTH_SHORT).show();
            } else {
                jSONObject.put("T", f5509c);
                jSONObject.put("M", valueOf);
                if (!f5509c.isEmpty()) {
                    olPlayRoom.connectionService.writeData((byte) 35, (byte) 0, (byte) 0, jSONObject.toString(), null);
                }
                dialogInterface.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
