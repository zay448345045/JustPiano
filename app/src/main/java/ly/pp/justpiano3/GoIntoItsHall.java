package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final class GoIntoItsHall implements OnClickListener {

    private final byte hallId;
    private final OLPlayHallRoom olPlayHallRoom;

    GoIntoItsHall(byte b, OLPlayHallRoom olPlayHallRoom) {
        hallId = b;
        this.olPlayHallRoom = olPlayHallRoom;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (hallId > (byte) 0) {
            olPlayHallRoom.sendMsg((byte) 29, hallId, "");
        }
    }
}
