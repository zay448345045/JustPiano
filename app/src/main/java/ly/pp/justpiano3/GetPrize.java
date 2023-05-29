package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import protobuf.dto.OnlineChallengeDTO;

final class GetPrize implements OnClickListener {
    private final OLChallenge challenge;

    GetPrize(OLChallenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        if (challenge.jpapplication.getConnectionService() != null) {
            OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
            builder.setType(6);
            challenge.jpapplication.getConnectionService().writeData(16, builder.build());
            Toast.makeText(challenge, "领取奖励成功!", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        }
    }
}
