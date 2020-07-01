package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class KickFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    KickFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("K", 6);
            jSONObject.put("F", olFamily.peopleNow);
            jSONObject.put("S", 1);
            olFamily.sendMsg((byte) 18, (byte) 0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
