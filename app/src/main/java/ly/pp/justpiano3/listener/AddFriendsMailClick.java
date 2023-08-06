package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import ly.pp.justpiano3.activity.OLFamily;
import protobuf.dto.OnlineSendMailDTO;

public final class AddFriendsMailClick implements OnClickListener {
    private final OLFamily family;
    private final String name;

    public AddFriendsMailClick(OLFamily family, String name) {
        this.family = family;
        this.name = name;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (family.jpapplication.getConnectionService() == null) {
            return;
        }
        OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
        builder.setMessage("");
        builder.setName(name);
        family.sendMsg(35, builder.build());
        dialogInterface.dismiss();
        Toast.makeText(family, "已向对方发送私信，请等待对方同意!", Toast.LENGTH_SHORT).show();
    }
}
