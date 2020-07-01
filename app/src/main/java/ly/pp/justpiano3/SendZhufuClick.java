package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class SendZhufuClick implements OnClickListener {
    private final OLPlayRoom olPlayRoom;
    private final JSONObject f5512b;

    SendZhufuClick(OLPlayRoom olPlayRoom, JSONObject jSONObject) {
        this.olPlayRoom = olPlayRoom;
        f5512b = jSONObject;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("T", 5);
            jSONObject.put("C", f5512b.getInt("I"));
            jSONObject.put("CI", -1);
            jSONObject.put("CT", -1);
            olPlayRoom.sendMsg((byte) 45, olPlayRoom.roomID0, olPlayRoom.hallID0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogInterface.dismiss();
    }
}
