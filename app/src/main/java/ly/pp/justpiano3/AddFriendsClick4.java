package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class AddFriendsClick4 implements OnClickListener {
    private final String f5429b;
    private final OLPlayHall olPlayHall;

    AddFriendsClick4(String str, OLPlayHall olPlayHall) {
        f5429b = str;
        this.olPlayHall = olPlayHall;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("T", 1);
            jSONObject.put("I", 0);
            jSONObject.put("F", f5429b);
            olPlayHall.sendMsg((byte) 31, (byte) 0, olPlayHall.hallID, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
