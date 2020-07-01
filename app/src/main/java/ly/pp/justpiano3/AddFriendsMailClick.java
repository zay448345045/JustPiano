package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

final class AddFriendsMailClick implements OnClickListener {
    private final OLFamily family;
    private final String name;

    AddFriendsMailClick(OLFamily family, String name) {
        this.family = family;
        this.name = name;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (family.jpapplication.getConnectionService() == null) {
            return;
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("T", name);
            jSONObject.put("M", "");
            family.sendMsg((byte) 35, (byte) 0, jSONObject.toString());
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(family, "已向对方发送私信，请等待对方同意!", Toast.LENGTH_SHORT).show();
    }
}
