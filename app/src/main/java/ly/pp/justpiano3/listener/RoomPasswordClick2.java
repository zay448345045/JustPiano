package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineEnterRoomDTO;

public final class RoomPasswordClick2 implements OnClickListener {
    private final OLPlayHall olPlayHall;
    private final TextView textView;
    private final byte roomId;

    public RoomPasswordClick2(OLPlayHall olPlayHall, TextView textView, byte b) {
        this.olPlayHall = olPlayHall;
        this.textView = textView;
        roomId = b;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(textView.getText());
        OnlineEnterRoomDTO.Builder builder = OnlineEnterRoomDTO.newBuilder();
        builder.setRoomId(roomId);
        builder.setPassword(valueOf);
        olPlayHall.sendMsg(OnlineProtocolType.ENTER_ROOM, builder.build());
        dialogInterface.dismiss();
    }
}
