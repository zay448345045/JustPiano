package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.thread.SongPlay;
import protobuf.dto.OnlineQuitRoomDTO;

public final class ReturnHallClick implements OnClickListener {
    private final OLPlayRoomActivity olPlayRoomActivity;

    public ReturnHallClick(OLPlayRoomActivity olPlayRoomActivity) {
        this.olPlayRoomActivity = olPlayRoomActivity;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        olPlayRoomActivity.onStart = false;
        olPlayRoomActivity.sendMsg(OnlineProtocolType.QUIT_ROOM, OnlineQuitRoomDTO.getDefaultInstance());
        SongPlay.INSTANCE.stopPlay();
        Intent intent = new Intent(olPlayRoomActivity, OLPlayHall.class);
        Bundle bundle = new Bundle();
        bundle.putString("hallName", olPlayRoomActivity.hallName);
        bundle.putByte("hallID", olPlayRoomActivity.hallId);
        dialogInterface.dismiss();
        intent.putExtras(bundle);
        olPlayRoomActivity.startActivity(intent);
        olPlayRoomActivity.finish();
    }
}
