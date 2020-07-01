package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class CpRequestClick implements OnClickListener {

    private final OLPlayRoom olPlayRoom;
    private final int f5515b;
    private final byte f5516c;
    private final int f5517d;

    CpRequestClick(OLPlayRoom olPlayRoom, int i, byte b, int i2) {
        this.olPlayRoom = olPlayRoom;
        f5515b = i;
        f5516c = b;
        f5517d = i2;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        try {
            if (f5515b == 1 || f5515b == 2) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("T", f5515b + 1);
                jSONObject.put("C", olPlayRoom.user.getPlayerName());
                jSONObject.put("CI", f5516c);
                jSONObject.put("CT", f5517d);
                olPlayRoom.sendMsg((byte) 45, olPlayRoom.roomID0, olPlayRoom.hallID0, jSONObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogInterface.dismiss();
    }
}
