package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayHallRoom;
import ly.pp.justpiano3.activity.OLRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.utils.OnlineUtil;
import protobuf.dto.OnlineSetUserInfoDTO;

public final class AddFriendsClick implements OnClickListener {
    private OLRoomActivity olRoomActivity = null;
    private OLPlayHall olPlayHall = null;
    private OLPlayHallRoom olPlayHallRoom = null;
    private final String name;

    public AddFriendsClick(OLRoomActivity olRoomActivity, String name) {
        this.olRoomActivity = olRoomActivity;
        this.name = name;
    }

    public AddFriendsClick(OLPlayHall olPlayHall, String name) {
        this.olPlayHall = olPlayHall;
        this.name = name;
    }

    public AddFriendsClick(OLPlayHallRoom olPlayHallRoom, String name) {
        this.olPlayHallRoom = olPlayHallRoom;
        this.name = name;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (olRoomActivity != null) {
            if (OnlineUtil.getConnectionService() == null) {
                return;
            }
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(0);
            builder.setName(name);
            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.SET_USER_INFO, builder.build());
            dialogInterface.dismiss();
        } else if (olPlayHall != null) {
            if (OnlineUtil.getConnectionService() == null) {
                return;
            }
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(0);
            builder.setName(name);
            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.SET_USER_INFO, builder.build());
            dialogInterface.dismiss();
        } else if (olPlayHallRoom != null) {
            if (OnlineUtil.getConnectionService() == null) {
                return;
            }
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(0);
            builder.setName(name);
            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.SET_USER_INFO, builder.build());
            dialogInterface.dismiss();
        }
    }
}
