package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.activity.OLRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineChangeRoomInfoDTO;

public final class ChangeRoomNameClick implements OnClickListener {
    private final OLRoomActivity olRoomActivity;
    private final EditText name;
    private final EditText password;

    public ChangeRoomNameClick(OLRoomActivity olRoomActivity, EditText name, EditText password) {
        this.olRoomActivity = olRoomActivity;
        this.name = name;
        this.password = password;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String name = this.name.getText().toString();
        String password = this.password.getText().toString();
        if (StringUtil.isNullOrEmpty(name)) {
            Toast.makeText(olRoomActivity, "房名不能为空!", Toast.LENGTH_SHORT).show();
        } else if (name.length() > 8 || password.length() > 8) {
            Toast.makeText(olRoomActivity, "房名或密码只能在八个字以下!", Toast.LENGTH_SHORT).show();
        } else {
            OnlineChangeRoomInfoDTO.Builder builder = OnlineChangeRoomInfoDTO.newBuilder();
            builder.setRoomName(name);
            builder.setRoomPassword(password);
            olRoomActivity.sendMsg(OnlineProtocolType.CHANGE_ROOM_INFO, builder.build());
            dialogInterface.dismiss();
        }
    }
}
