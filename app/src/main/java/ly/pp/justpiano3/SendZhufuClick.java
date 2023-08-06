package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.OLPlayRoom;
import org.json.JSONException;
import org.json.JSONObject;
import protobuf.dto.OnlineCoupleDTO;

public final class SendZhufuClick implements OnClickListener {
    private final OLPlayRoomInterface olPlayRoomInterface;
    private final JSONObject f5512b;

    public SendZhufuClick(OLPlayRoomInterface olPlayRoomInterface, JSONObject jSONObject) {
        this.olPlayRoomInterface = olPlayRoomInterface;
        f5512b = jSONObject;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            if (olPlayRoomInterface instanceof OLPlayRoom) {
                OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
                OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                builder.setType(5);

                builder.setRoomPosition(f5512b.getInt("I"));

                olPlayRoom.sendMsg(45, builder.build());
                dialogInterface.dismiss();
            } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
                OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
                OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
                builder.setType(5);
                builder.setRoomPosition(f5512b.getInt("I"));
                olPlayKeyboardRoom.sendMsg(45, builder.build());
                dialogInterface.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
