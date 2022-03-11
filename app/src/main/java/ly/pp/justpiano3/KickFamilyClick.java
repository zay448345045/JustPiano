package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import ly.pp.justpiano3.protobuf.request.OnlineRequest;

final class KickFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    KickFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        OnlineRequest.Family.Builder builder = OnlineRequest.Family.newBuilder();
        builder.setType(6);
        builder.setUserName(olFamily.peopleNow);
        builder.setStatus(1);
        olFamily.sendMsg(18, builder.build());
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
