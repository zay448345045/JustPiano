package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

final class ClosePositionClick implements OnClickListener {
    private final PlayerImageAdapter playerImageAdapter;
    private final PopupWindow popupWindow;
    private final User use;
    private final OLPlayRoom olPlayRoom;

    ClosePositionClick(PlayerImageAdapter playerImageAdapter, PopupWindow popupWindow, User User, OLPlayRoom olPlayRoom) {
        this.playerImageAdapter = playerImageAdapter;
        this.popupWindow = popupWindow;
        use = User;
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void onClick(View view) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            byte[] bArr = new byte[]{use.getPosition(), (byte) 1};
            if (olPlayRoom.playerKind.equals("H") && playerImageAdapter.connectionService != null) {
                playerImageAdapter.connectionService.writeData((byte) 42, playerImageAdapter.roomID, olPlayRoom.hallID0, use.getPlayerName(), bArr);
            }
        }
    }
}
