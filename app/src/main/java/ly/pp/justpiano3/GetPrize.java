package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

final class GetPrize implements OnClickListener {
    private final OLChallenge challenge;

    GetPrize(OLChallenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (challenge.jpapplication.getConnectionService() != null) {
            try {
                challenge.jpapplication.getConnectionService().writeData((byte) 16, (byte) 0, challenge.hallID, "6", null);
                Toast.makeText(challenge, "领取奖励成功!", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
