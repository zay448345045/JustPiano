package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class InOutFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    InOutFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("K", 5);
            jSONObject.put("I", olFamily.familyID);
            olFamily.sendMsg((byte) 18, (byte) 0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
