package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class SendZhufuClick implements OnClickListener {
    private final OLPlayRoomInterface olPlayRoomInterface;
    private final JSONObject f5512b;

    SendZhufuClick(OLPlayRoomInterface olPlayRoomInterface, JSONObject jSONObject) {
        this.olPlayRoomInterface = olPlayRoomInterface;
        f5512b = jSONObject;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
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
        } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("T", 5);
                jSONObject.put("C", f5512b.getInt("I"));
                jSONObject.put("CI", -1);
                jSONObject.put("CT", -1);
                olPlayKeyboardRoom.sendMsg((byte) 45, olPlayKeyboardRoom.roomID0, olPlayKeyboardRoom.hallID0, jSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialogInterface.dismiss();
        }
    }
}
