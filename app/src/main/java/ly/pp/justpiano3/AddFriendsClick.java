package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import protobuf.dto.OnlineSetUserInfoDTO;

final class AddFriendsClick implements OnClickListener {
    private OLPlayRoomInterface olPlayRoomInterface = null;
    private OLPlayHall olPlayHall = null;
    private OLPlayHallRoom olPlayHallRoom = null;
    private final String name;

    AddFriendsClick(OLPlayRoomInterface olPlayRoomInterface, String name) {
        this.olPlayRoomInterface = olPlayRoomInterface;
        this.name = name;
    }

    AddFriendsClick(OLPlayHall olPlayHall, String name) {
        this.olPlayHall = olPlayHall;
        this.name = name;
    }

    AddFriendsClick(OLPlayHallRoom olPlayHallRoom, String name) {
        this.olPlayHallRoom = olPlayHallRoom;
        this.name = name;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (olPlayRoomInterface != null) {
            if (olPlayRoomInterface instanceof OLPlayRoom) {
                OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
                if (olPlayRoom.jpapplication.getConnectionService() == null) {
                    return;
                }
                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                builder.setType(0);
                builder.setName(name);
                olPlayRoom.jpapplication.getConnectionService().writeData(31, builder.build());
                dialogInterface.dismiss();
            } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
                OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
                if (olPlayKeyboardRoom.jpapplication.getConnectionService() == null) {
                    return;
                }
                OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
                builder.setType(0);
                builder.setName(name);
                olPlayKeyboardRoom.jpapplication.getConnectionService().writeData(31, builder.build());
                dialogInterface.dismiss();
            }
        } else if (olPlayHall != null) {
            if (olPlayHall.jpapplication.getConnectionService() == null) {
                return;
            }
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(0);
            builder.setName(name);
            olPlayHall.jpapplication.getConnectionService().writeData(31, builder.build());
            dialogInterface.dismiss();
        } else if (olPlayHallRoom != null) {
            if (olPlayHallRoom.jpApplication.getConnectionService() == null) {
                return;
            }
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(0);
            builder.setName(name);
            olPlayHallRoom.jpApplication.getConnectionService().writeData(31, builder.build());
            dialogInterface.dismiss();
        }
    }
}
