package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class AddFriendsClick implements OnClickListener {
    private OLPlayRoomInterface olPlayRoomInterface = null;
    private OLPlayHall olPlayHall = null;
    private final String name;

    AddFriendsClick(OLPlayRoomInterface olPlayRoomInterface, String name) {
        this.olPlayRoomInterface = olPlayRoomInterface;
        this.name = name;
    }

    AddFriendsClick(OLPlayHall olPlayHall, String name) {
        this.olPlayHall = olPlayHall;
        this.name = name;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface != null) {
            if (olPlayRoomInterface instanceof OLPlayRoom) {
                OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
                if (olPlayRoom.jpapplication.getConnectionService() == null) {
                    return;
                }
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("F", name);
                    jSONObject.put("T", 0);
                    olPlayRoom.jpapplication.getConnectionService().writeData((byte) 31, olPlayRoom.roomID0, olPlayRoom.hallID0, jSONObject.toString(), null);
                    dialogInterface.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
                OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
                if (olPlayKeyboardRoom.jpapplication.getConnectionService() == null) {
                    return;
                }
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("F", name);
                    jSONObject.put("T", 0);
                    olPlayKeyboardRoom.jpapplication.getConnectionService().writeData((byte) 31, olPlayKeyboardRoom.roomID0, olPlayKeyboardRoom.hallID0, jSONObject.toString(), null);
                    dialogInterface.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (olPlayHall != null) {
            if (olPlayHall.jpapplication.getConnectionService() == null) {
                return;
            }
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("F", name);
                jSONObject.put("T", 0);
                olPlayHall.jpapplication.getConnectionService().writeData((byte) 31, (byte) 0, (byte) 0, jSONObject.toString(), null);
                dialogInterface.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
