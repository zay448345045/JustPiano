package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import protobuf.dto.OnlineFamilyDTO;

final class KickFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    KickFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(6);
        builder.setUserName(olFamily.peopleNow);
        builder.setStatus(1);
        olFamily.sendMsg(18, builder.build());
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
