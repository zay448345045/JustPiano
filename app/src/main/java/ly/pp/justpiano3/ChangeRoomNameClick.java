package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class ChangeRoomNameClick implements OnClickListener {
    private final OLPlayRoom olPlayRoom;
    private final EditText name;
    private final EditText password;

    ChangeRoomNameClick(OLPlayRoom olPlayRoom, EditText name, EditText password) {
        this.olPlayRoom = olPlayRoom;
        this.name = name;
        this.password = password;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
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
    }
}
