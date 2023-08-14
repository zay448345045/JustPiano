package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.OLFamily;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineFamilyDTO;

public final class KickFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    public KickFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(6);
        builder.setUserName(olFamily.peopleNow);
        builder.setStatus(1);
        olFamily.sendMsg(OnlineProtocolType.FAMILY, builder.build());
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
