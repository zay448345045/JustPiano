package ly.pp.justpiano3.listener;

import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.adapter.OLRoomSongsAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.thread.PlaySongs;
import protobuf.dto.OnlinePlaySongDTO;

public final class OLChooseSongClick implements OnClickListener {
    private final OLRoomSongsAdapter olRoomSongsAdapter;
    private final String string;

    public OLChooseSongClick(OLRoomSongsAdapter olRoomSongsAdapter, String str) {
        this.olRoomSongsAdapter = olRoomSongsAdapter;
        string = str;
    }

    @Override
    public void onClick(View view) {
        PlaySongs.setSongPath(string);
        OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
        builder.setTune(olRoomSongsAdapter.olPlayRoom.getdiao());
        builder.setSongPath(string.substring(6, string.length() - 3));
        olRoomSongsAdapter.olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
        Message obtainMessage = olRoomSongsAdapter.olPlayRoom.olPlayRoomHandler.obtainMessage();
        obtainMessage.what = 12;
        olRoomSongsAdapter.olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
    }
}
