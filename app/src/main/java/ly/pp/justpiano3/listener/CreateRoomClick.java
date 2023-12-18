package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineCreateRoomDTO;

public final class CreateRoomClick implements OnClickListener {
    private final OLPlayHall olPlayHall;
    private final TextView roomNameText;
    private final TextView passwordText;
    private final RadioGroup roomModeRadioGroup;

    public CreateRoomClick(OLPlayHall olPlayHall, TextView textView, TextView textView2, RadioGroup radioGroup) {
        this.olPlayHall = olPlayHall;
        roomNameText = textView;
        passwordText = textView2;
        roomModeRadioGroup = radioGroup;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = 0;
        String roomName = String.valueOf(roomNameText.getText());
        String password = String.valueOf(passwordText.getText());
        if (TextUtils.isEmpty(roomName)) {
            Toast.makeText(olPlayHall, "请输入房间名称!", Toast.LENGTH_SHORT).show();
        } else if (roomName.length() > 8) {
            Toast.makeText(olPlayHall, "确定字数在8个字之内!", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(password) || password.length() <= 8) {
                switch (roomModeRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.group:
                        i2 = 1;
                        break;
                    case R.id.couple:
                        i2 = 2;
                        break;
                    case R.id.keyboard:
                        i2 = 3;
                        break;
                }
                OnlineCreateRoomDTO.Builder builder = OnlineCreateRoomDTO.newBuilder();
                builder.setRoomName(roomName);
                builder.setPassword(password);
                builder.setRoomMode(i2);
                olPlayHall.sendMsg(OnlineProtocolType.CREATE_ROOM, builder.build());
                dialogInterface.dismiss();
            } else {
                Toast.makeText(olPlayHall, "确定密码在8个字之内!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
