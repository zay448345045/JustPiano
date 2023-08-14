package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.activity.OLPlayRoom;

public final class ReturnMainModeClick implements OnClickListener {

    private final BaseActivity baseactivity;

    public ReturnMainModeClick(BaseActivity baseActivity) {
        baseactivity = baseActivity;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (baseactivity instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) baseactivity;
            if (olPlayRoom.playSongs != null) {
                olPlayRoom.playSongs.isPlayingSongs = false;
                olPlayRoom.playSongs = null;
            }
        }
        BaseActivity.returnMainMode(baseactivity);
    }
}
