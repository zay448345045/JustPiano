package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class ChangeRoomNameClick implements OnClickListener {
    private final OLPlayRoomInterface olPlayRoomInterface;
    private final EditText name;
    private final EditText password;

    ChangeRoomNameClick(OLPlayRoomInterface olPlayRoomInterface, EditText name, EditText password) {
        this.olPlayRoomInterface = olPlayRoomInterface;
        this.name = name;
        this.password = password;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
            JSONObject jSONObject = new JSONObject();
            String name = this.name.getText().toString();
            String password = this.password.getText().toString();
            try {
                if (name.isEmpty()) {
                    Toast.makeText(olPlayRoom, "房名不能为空!", Toast.LENGTH_SHORT).show();
                } else if (name.length() > 8 || password.length() > 8) {
                    Toast.makeText(olPlayRoom, "房名或密码只能在八个字以下!", Toast.LENGTH_SHORT).show();
                } else {
                    jSONObject.put("R", name);
                    jSONObject.put("P", password);
                    olPlayRoom.sendMsg((byte) 14, olPlayRoom.roomID0, olPlayRoom.hallID0, jSONObject.toString());
                    dialogInterface.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
            JSONObject jSONObject = new JSONObject();
            String name = this.name.getText().toString();
            String password = this.password.getText().toString();
            try {
                if (name.isEmpty()) {
                    Toast.makeText(olPlayKeyboardRoom, "房名不能为空!", Toast.LENGTH_SHORT).show();
                } else if (name.length() > 8 || password.length() > 8) {
                    Toast.makeText(olPlayKeyboardRoom, "房名或密码只能在八个字以下!", Toast.LENGTH_SHORT).show();
                } else {
                    jSONObject.put("R", name);
                    jSONObject.put("P", password);
                    olPlayKeyboardRoom.sendMsg((byte) 14, olPlayKeyboardRoom.roomID0, olPlayKeyboardRoom.hallID0, jSONObject.toString());
                    dialogInterface.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
