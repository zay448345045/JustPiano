package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.thread.SongPlay;

public final class ReturnMainModeClick implements OnClickListener {

    private final BaseActivity baseactivity;

    public ReturnMainModeClick(BaseActivity baseActivity) {
        baseactivity = baseActivity;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        SongPlay.INSTANCE.stopPlay();
        BaseActivity.returnMainMode(baseactivity);
    }
}
