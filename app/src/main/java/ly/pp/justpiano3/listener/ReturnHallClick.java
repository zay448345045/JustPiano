package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import ly.pp.justpiano3.activity.OLPlayRoomInterface;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineQuitRoomDTO;

public final class ReturnHallClick implements OnClickListener {
    private final OLPlayRoomInterface olPlayRoomInterface;

    public ReturnHallClick(OLPlayRoomInterface olPlayRoomInterface) {
        this.olPlayRoomInterface = olPlayRoomInterface;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
            olPlayRoom.jpapplication.setPlaySongsMode(0);
            olPlayRoom.isOnStart = false;
            olPlayRoom.sendMsg(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
            olPlayRoom.jpapplication.stopPlaySong();
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
            olPlayKeyboardRoom.sendMsg(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
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
