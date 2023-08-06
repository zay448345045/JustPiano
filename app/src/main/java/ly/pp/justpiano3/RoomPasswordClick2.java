package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import ly.pp.justpiano3.activity.OLPlayHall;
import protobuf.dto.OnlineEnterRoomDTO;

public final class RoomPasswordClick2 implements OnClickListener {
    private final OLPlayHall f5377a;
    private final TextView f5378b;
    private final byte f5379c;

    public RoomPasswordClick2(OLPlayHall olPlayHall, TextView textView, byte b) {
        f5377a = olPlayHall;
        f5378b = textView;
        f5379c = b;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f5378b.getText());
        OnlineEnterRoomDTO.Builder builder = OnlineEnterRoomDTO.newBuilder();
        builder.setRoomId(f5379c);
        builder.setPassword(valueOf);
        f5377a.sendMsg(7, builder.build());
        dialogInterface.dismiss();
    }
}
