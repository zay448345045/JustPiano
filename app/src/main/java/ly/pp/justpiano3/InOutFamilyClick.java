package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.OLFamily;
import protobuf.dto.OnlineFamilyDTO;

public final class InOutFamilyClick implements OnClickListener {
    private final OLFamily olFamily;

    public InOutFamilyClick(OLFamily olFamily) {
        this.olFamily = olFamily;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
        builder.setType(5);
        builder.setFamilyId(Integer.parseInt(olFamily.familyID));
        olFamily.sendMsg(18, builder.build());
        dialogInterface.dismiss();
        olFamily.jpprogressBar.show();
    }
}
