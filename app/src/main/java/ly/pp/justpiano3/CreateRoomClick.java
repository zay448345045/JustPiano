package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class CreateRoomClick implements OnClickListener {
    private final OLPlayHall olPlayHall;
    private final TextView roomNameText;
    private final TextView passwordText;
    private final RadioGroup roomModeRadioGroup;

    CreateRoomClick(OLPlayHall olPlayHall, TextView textView, TextView textView2, RadioGroup radioGroup) {
        this.olPlayHall = olPlayHall;
        roomNameText = textView;
        passwordText = textView2;
        roomModeRadioGroup = radioGroup;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        int i2 = 0;
        String roomName = String.valueOf(roomNameText.getText());
        String password = String.valueOf(passwordText.getText());
        if (roomName.isEmpty()) {
            Toast.makeText(olPlayHall, "请输入房间名称!", Toast.LENGTH_SHORT).show();
        } else if (roomName.length() > 8) {
            Toast.makeText(olPlayHall, "确定字数在8个字之内!", Toast.LENGTH_SHORT).show();
        } else {
            if (password.isEmpty() || password.length() <= 8) {
                switch (roomModeRadioGroup.getCheckedRadioButtonId()) {
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
                    jSONObject.put("N", roomName);
                    jSONObject.put("P", password);
                    jSONObject.put("M", i2);
                    olPlayHall.sendMsg((byte) 6, olPlayHall.hallID, (byte) 0, jSONObject.toString());
                    jSONObject.toString();
                    dialogInterface.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(olPlayHall, "确定密码在8个字之内!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
