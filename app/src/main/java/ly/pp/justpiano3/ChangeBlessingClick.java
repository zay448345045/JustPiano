package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class ChangeBlessingClick implements OnClickListener {

    private final OLPlayHallRoom olPlayHallRoom;
    private final TextView f5458b;
    private final int f5459c;
    private final String f5460d;

    ChangeBlessingClick(OLPlayHallRoom olPlayHallRoom, TextView textView, int i, String str) {
        this.olPlayHallRoom = olPlayHallRoom;
        f5458b = textView;
        f5459c = i;
        f5460d = str;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        String valueOf = String.valueOf(f5458b.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            if (valueOf.isEmpty() || valueOf.equals("'")) {
                Toast.makeText(olPlayHallRoom, "请输入内容!", Toast.LENGTH_SHORT).show();
            } else if (valueOf.length() > 500) {
                Toast.makeText(olPlayHallRoom, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
            } else if (f5459c == 0 && !f5460d.isEmpty()) {
                jSONObject.put("T", f5460d);
                jSONObject.put("M", valueOf);
                olPlayHallRoom.connectionService.writeData((byte) 35, (byte) 0, (byte) 0, jSONObject.toString(), null);
                olPlayHallRoom.mailList.clear();
                String format = SimpleDateFormat.getDateInstance(2, Locale.CHINESE).format(new Date());
                Bundle bundle = new Bundle();
                bundle.putString("F", f5460d);
                bundle.putString("M", valueOf);
                bundle.putString("T", format);
                bundle.putInt("type", 1);
                olPlayHallRoom.mailList.add(bundle);
                format = olPlayHallRoom.sharedPreferences.getString("mailsString", "[]");
                try {
                    JSONArray jSONArray = new JSONArray(format);
                    int length = jSONArray.length();
                    for (int i2 = 0; i2 < length; i2++) {
                        JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                        Bundle bundle2 = new Bundle();
                        if (jSONObject2.has("type")) {
                            bundle2.putInt("type", jSONObject2.getInt("type"));
                        }
                        bundle2.putString("F", jSONObject2.getString("F"));
                        bundle2.putString("M", jSONObject2.getString("M"));
                        bundle2.putString("T", jSONObject2.getString("T"));
                        olPlayHallRoom.mailList.add(bundle2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                olPlayHallRoom.mo2848c();
                olPlayHallRoom.mo2849c(olPlayHallRoom.mailListView, olPlayHallRoom.mailList);
            } else if (f5459c == 1) {
                jSONObject.put("T", 4);
                jSONObject.put("M", valueOf);
                olPlayHallRoom.coupleBlessView.setText("祝语:\n" + valueOf);
                olPlayHallRoom.connectionService.writeData((byte) 31, (byte) 0, (byte) 0, jSONObject.toString(), null);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }
}
