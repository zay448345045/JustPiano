package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.Toast;

final class KickOutRoomClick implements OnClickListener {
    private final PlayerImageAdapter playerImageAdapter;
    private final PopupWindow popupWindow;
    private final User user;
    private final OLPlayRoom olPlayRoom;

    KickOutRoomClick(PlayerImageAdapter playerImageAdapter, PopupWindow popupWindow, User User, OLPlayRoom olPlayRoom) {
        this.playerImageAdapter = playerImageAdapter;
        this.popupWindow = popupWindow;
        user = User;
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void onClick(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            byte[] bArr = new byte[]{user.getPosition()};
            if (olPlayRoom.playerKind.equals("H") && playerImageAdapter.connectionService != null) {
                if (!user.getStatus().equals("N") && !user.getStatus().equals("F") && !user.getStatus().equals("B")) {
                    Toast.makeText(olPlayRoom, "玩家当前状态不能被移出!", Toast.LENGTH_SHORT).show();
                } else {
                    playerImageAdapter.connectionService.writeData((byte) 9, playerImageAdapter.roomID, olPlayRoom.hallID0, user.getPlayerName(), bArr);
                }
            }
        }
    }
}
