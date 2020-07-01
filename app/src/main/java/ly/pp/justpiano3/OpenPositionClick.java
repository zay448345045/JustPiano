package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

final class OpenPositionClick implements OnClickListener {
    private final PlayerImageAdapter playerImageAdapter;
    private final PopupWindow popupWindow;
    private final User user;
    private final OLPlayRoom olPlayRoom;

    OpenPositionClick(PlayerImageAdapter playerImageAdapter, PopupWindow popupWindow, User User, OLPlayRoom olPlayRoom) {
        this.playerImageAdapter = playerImageAdapter;
        this.popupWindow = popupWindow;
        user = User;
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void onClick(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            byte[] bArr = new byte[]{user.getPosition(), (byte) 2};
            if (olPlayRoom.playerKind.equals("H") && playerImageAdapter.connectionService != null) {
                playerImageAdapter.connectionService.writeData((byte) 42, playerImageAdapter.roomID, olPlayRoom.hallID0, String.valueOf(user.getPosition()), bArr);
            }
        }
    }
}
