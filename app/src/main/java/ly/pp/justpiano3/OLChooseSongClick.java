package ly.pp.justpiano3;

import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import protobuf.dto.OnlinePlaySongDTO;

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
        OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
        builder.setTune(olRoomSongsAdapter.olPlayRoom.getdiao());
        builder.setSongPath(str1);
        olRoomSongsAdapter.olPlayRoom.sendMsg(15, builder.build());
        Message obtainMessage = olRoomSongsAdapter.olPlayRoom.olPlayRoomHandler.obtainMessage();
        obtainMessage.what = 12;
        olRoomSongsAdapter.olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
    }
}
