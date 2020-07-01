package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class ChangeDeclarationClick implements OnClickListener {

    private final OLFamily family;
    private final TextView f5458b;
    private final int f5459c;
    private final String f5460d;

    ChangeDeclarationClick(OLFamily family, TextView textView, int i, String str) {
        this.family = family;
        f5458b = textView;
        f5459c = i;
        f5460d = str;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f5458b.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            if (valueOf.isEmpty() || valueOf.equals("\'")) {
                Toast.makeText(family, "请输入内容!", Toast.LENGTH_SHORT).show();
            } else if (valueOf.length() > 300) {
                Toast.makeText(family, "确定在三百字之内!", Toast.LENGTH_SHORT).show();
            } else if (f5459c == 0) {
                dialogInterface.dismiss();
                jSONObject.put("T", f5460d);
                jSONObject.put("M", valueOf);
                family.sendMsg((byte) 35, (byte) 0, jSONObject.toString());
            } else if (f5459c == 1) {
                dialogInterface.dismiss();
                jSONObject.put("K", 9);
                jSONObject.put("I", valueOf);
                family.declaration.setText("家族宣言:\n" + valueOf);
                family.sendMsg((byte) 18, (byte) 0, jSONObject.toString());
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }
}
