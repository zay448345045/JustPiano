package ly.pp.justpiano3;

import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

final class OLChooseSongClick implements OnClickListener {
    private final OLRoomSongsAdapter olRoomSongsAdapter;
    private final String string;

    OLChooseSongClick(OLRoomSongsAdapter olRoomSongsAdapter, String str) {
        this.olRoomSongsAdapter = olRoomSongsAdapter;
        string = str;
    }

    @Override
    public final void onClick(View view) {
        String str1 = string.substring(6, string.length() - 3);
        olRoomSongsAdapter.olPlayRoom.jpapplication.setNowSongsName(str1);
        olRoomSongsAdapter.olPlayRoom.sendMsg((byte) 15, olRoomSongsAdapter.olPlayRoom.roomID0, olRoomSongsAdapter.olPlayRoom.hallID0,
                (olRoomSongsAdapter.olPlayRoom.getdiao() + 20) + str1);
        Message obtainMessage = olRoomSongsAdapter.olPlayRoom.olPlayRoomHandler.obtainMessage();
        obtainMessage.what = 12;
        olRoomSongsAdapter.olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
    }
}
