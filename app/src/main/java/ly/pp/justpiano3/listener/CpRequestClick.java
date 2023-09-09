package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineCoupleDTO;

public final class CpRequestClick implements OnClickListener {

    private final OLPlayRoom olPlayRoom;
    private final int f5515b;
    private final byte f5516c;
    private final int f5517d;

    public CpRequestClick(OLPlayRoom olPlayRoom, int i, byte b, int i2) {
        this.olPlayRoom = olPlayRoom;
        f5515b = i;
        f5516c = b;
        f5517d = i2;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (f5515b == 1 || f5515b == 2) {
            OnlineCoupleDTO.Builder builder = OnlineCoupleDTO.newBuilder();
            builder.setType(f5515b + 1);
            builder.setCoupleType(f5517d);
            builder.setCoupleRoomPosition(f5516c);
            olPlayRoom.sendMsg(OnlineProtocolType.COUPLE, builder.build());
        }
        dialogInterface.dismiss();
    }
}
