package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class CreateFamily implements OnClickListener {

    private final OLPlayHallRoom olPlayHallRoom;
    private JPDialog jpDialog;

    CreateFamily(JPDialog jpDialog, OLPlayHallRoom olPlayHallRoom) {
        this.olPlayHallRoom = olPlayHallRoom;
        this.jpDialog = jpDialog;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String name = jpDialog.getEditTextString();
        if (name.isEmpty()) {
            Toast.makeText(olPlayHallRoom, "家族名称不能为空!", Toast.LENGTH_SHORT).show();
        } else if (name.length() > 8) {
            Toast.makeText(olPlayHallRoom, "家族名称只能在八个字以下!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                dialogInterface.dismiss();
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("K", 4);
                jSONObject.put("N", name);
                olPlayHallRoom.sendMsg((byte) 18, (byte) 0, jSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
