package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import ly.pp.justpiano3.protobuf.dto.OnlineFamilyDTO;

final class InOutFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    InOutFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(5);
        builder.setFamilyId(Integer.parseInt(olFamily.familyID));
        olFamily.sendMsg(18, builder.build());
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
