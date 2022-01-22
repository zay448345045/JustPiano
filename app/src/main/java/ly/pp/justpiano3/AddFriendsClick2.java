package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class AddFriendsClick2 implements OnClickListener {
    private final PopUserInfo popUserInfo;

    AddFriendsClick2(PopUserInfo popUserInfo) {
        this.popUserInfo = popUserInfo;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("H", 0);
            jSONObject.put("T", popUserInfo.kitiName);
            jSONObject.put("F", popUserInfo.jpapplication.getAccountName());
            jSONObject.put("M", "");
            if (!popUserInfo.kitiName.isEmpty() && !popUserInfo.jpapplication.getAccountName().isEmpty()) {
                popUserInfo.f4830d = jSONObject.toString();
                new PopUserInfoTask(popUserInfo).execute();
            }
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
