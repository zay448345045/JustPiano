package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

final class ReturnHallClick implements OnClickListener {
    private final OLPlayRoom olPlayRoom;

    ReturnHallClick(OLPlayRoom olPlayRoom) {
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        olPlayRoom.jpapplication.setPlaySongsMode(0);
        olPlayRoom.isOnStart = false;
        olPlayRoom.sendMsg((byte) 8, olPlayRoom.roomID0, olPlayRoom.hallID0, olPlayRoom.roomTitleString);
        if (olPlayRoom.playSongs != null) {
            olPlayRoom.jpapplication.stopMusic();
            olPlayRoom.playSongs.isPlayingSongs = false;
            olPlayRoom.playSongs = null;
        }
        Intent intent = new Intent(olPlayRoom, OLPlayHall.class);
        Bundle bundle = new Bundle();
        bundle.putString("hallName", olPlayRoom.hallName);
        bundle.putByte("hallID", olPlayRoom.hallID0);
        dialogInterface.dismiss();
        intent.putExtras(bundle);
        olPlayRoom.startActivity(intent);
        olPlayRoom.finish();
    }
}
