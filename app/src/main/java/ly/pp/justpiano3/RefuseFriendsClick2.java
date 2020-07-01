package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class RefuseFriendsClick2 implements OnClickListener {
    private final String f5432b;
    private final OLPlayHall olPlayHall;

    RefuseFriendsClick2(String str, OLPlayHall olPlayHall) {
        f5432b = str;
        this.olPlayHall = olPlayHall;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("T", 1);
            jSONObject.put("I", 1);
            jSONObject.put("F", f5432b);
            olPlayHall.sendMsg((byte) 31, (byte) 0, olPlayHall.hallID, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
