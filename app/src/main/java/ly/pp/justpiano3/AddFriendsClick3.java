package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class AddFriendsClick3 implements OnClickListener {
    private final String f5584b;
    private final OLPlayRoom olPlayRoom;

    AddFriendsClick3(String str, OLPlayRoom olPlayRoom) {
        f5584b = str;
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("T", 1);
            jSONObject.put("I", 0);
            jSONObject.put("F", f5584b);
            olPlayRoom.sendMsg((byte) 31, olPlayRoom.roomID0, olPlayRoom.hallID0, jSONObject.toString());
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
