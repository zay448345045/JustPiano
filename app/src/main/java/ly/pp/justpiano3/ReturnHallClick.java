package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

final class ReturnHallClick implements OnClickListener {
    private final OLPlayRoomInterface olPlayRoomInterface;

    ReturnHallClick(OLPlayRoomInterface olPlayRoomInterface) {
        this.olPlayRoomInterface = olPlayRoomInterface;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
            olPlayRoom.jpapplication.setPlaySongsMode(0);
            olPlayRoom.isOnStart = false;
            olPlayRoom.sendMsg((byte) 8, olPlayRoom.roomID0, olPlayRoom.hallID0, null);
            if (olPlayRoom.playSongs != null) {
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
        } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
            olPlayKeyboardRoom.sendMsg((byte) 8, olPlayKeyboardRoom.roomID0, olPlayKeyboardRoom.hallID0, null);
            Intent intent = new Intent(olPlayKeyboardRoom, OLPlayHall.class);
            Bundle bundle = new Bundle();
            bundle.putString("hallName", olPlayKeyboardRoom.hallName);
            bundle.putByte("hallID", olPlayKeyboardRoom.hallID0);
            dialogInterface.dismiss();
            intent.putExtras(bundle);
            olPlayKeyboardRoom.startActivity(intent);
            olPlayKeyboardRoom.finish();
        }
    }
}
