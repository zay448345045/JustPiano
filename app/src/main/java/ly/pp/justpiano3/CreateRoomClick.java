package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class CreateRoomClick implements OnClickListener {
    private final OLPlayHall f5381a;
    private final TextView f5382b;
    private final TextView f5383c;
    private final RadioGroup f5384d;

    CreateRoomClick(OLPlayHall olPlayHall, TextView textView, TextView textView2, RadioGroup radioGroup) {
        f5381a = olPlayHall;
        f5382b = textView;
        f5383c = textView2;
        f5384d = radioGroup;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        int i2 = 0;
        String valueOf = String.valueOf(f5382b.getText());
        String valueOf2 = String.valueOf(f5383c.getText());
        if (valueOf.isEmpty()) {
            Toast.makeText(f5381a, "请输入房间名称!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > 8) {
            Toast.makeText(f5381a, "确定字数在8个字之内!", Toast.LENGTH_SHORT).show();
        } else {
            if (valueOf2.isEmpty() || valueOf2.length() <= 8) {
                switch (f5384d.getCheckedRadioButtonId()) {
                    case R.id.group:
                        i2 = 1;
                        break;
                    case R.id.couple:
                        i2 = 2;
                        break;
                    case R.id.keyboard:
                        i2 = 3;
                        break;
                }
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("N", valueOf);
                    jSONObject.put("P", valueOf2);
                    jSONObject.put("M", i2);
                    f5381a.sendMsg((byte) 6, (byte) 0, f5381a.hallID, jSONObject.toString());
                    jSONObject.toString();
                    dialogInterface.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(f5381a, "确定密码在8个字之内!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
