package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import ly.pp.justpiano3.activity.OLChallenge;
import protobuf.dto.OnlineChallengeDTO;

public final class GetPrizeClick implements OnClickListener {
    private final OLChallenge challenge;

    public GetPrizeClick(OLChallenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (challenge.jpapplication.getConnectionService() != null) {
            OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
            builder.setType(6);
            challenge.jpapplication.getConnectionService().writeData(16, builder.build());
            Toast.makeText(challenge, "领取奖励成功!", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        }
    }
}
