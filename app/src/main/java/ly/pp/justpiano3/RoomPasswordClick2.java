package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

final class RoomPasswordClick2 implements OnClickListener {
    private final OLPlayHall f5377a;
    private final TextView f5378b;
    private final byte f5379c;

    RoomPasswordClick2(OLPlayHall olPlayHall, TextView textView, byte b) {
        f5377a = olPlayHall;
        f5378b = textView;
        f5379c = b;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f5378b.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("I", f5379c);
            jSONObject.put("P", valueOf);
            f5377a.sendMsg((byte) 7, f5377a.hallID, (byte) 0, jSONObject.toString());
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
