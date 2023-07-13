package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import io.netty.util.internal.StringUtil;
import protobuf.dto.OnlineChangeRoomInfoDTO;

final class ChangeRoomNameClick implements OnClickListener {
    private final OLPlayRoomInterface olPlayRoomInterface;
    private final EditText name;
    private final EditText password;

    ChangeRoomNameClick(OLPlayRoomInterface olPlayRoomInterface, EditText name, EditText password) {
        this.olPlayRoomInterface = olPlayRoomInterface;
        this.name = name;
        this.password = password;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
            String name = this.name.getText().toString();
            String password = this.password.getText().toString();
            if (StringUtil.isNullOrEmpty(name)) {
                Toast.makeText(olPlayRoom, "房名不能为空!", Toast.LENGTH_SHORT).show();
            } else if (name.length() > 8 || password.length() > 8) {
                Toast.makeText(olPlayRoom, "房名或密码只能在八个字以下!", Toast.LENGTH_SHORT).show();
            } else {
                OnlineChangeRoomInfoDTO.Builder builder = OnlineChangeRoomInfoDTO.newBuilder();
                builder.setRoomName(name);
                builder.setRoomPassword(password);
                olPlayRoom.sendMsg(14, builder.build());
                dialogInterface.dismiss();
            }
        } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
            String name = this.name.getText().toString();
            String password = this.password.getText().toString();
            if (StringUtil.isNullOrEmpty(name)) {
                Toast.makeText(olPlayKeyboardRoom, "房名不能为空!", Toast.LENGTH_SHORT).show();
            } else if (name.length() > 8 || password.length() > 8) {
                Toast.makeText(olPlayKeyboardRoom, "房名或密码只能在八个字以下!", Toast.LENGTH_SHORT).show();
            } else {
                OnlineChangeRoomInfoDTO.Builder builder = OnlineChangeRoomInfoDTO.newBuilder();
                builder.setRoomName(name);
                builder.setRoomPassword(password);
                olPlayKeyboardRoom.sendMsg(14, builder.build());
                dialogInterface.dismiss();
            }
        }
    }
}
