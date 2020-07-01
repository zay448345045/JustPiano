package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

final class OLSendMailClick implements OnClickListener {
    private final MainGameAdapter mainGameAdapter;
    private final String message;
    private final String to;

    OLSendMailClick(MainGameAdapter mainGameAdapter, String str, String str2) {
        this.mainGameAdapter = mainGameAdapter;
        message = str;
        to = str2;
    }

    @Override
    public final void onClick(View view) {
        if (!(mainGameAdapter.activity instanceof OLPlayHallRoom)) {
            return;
        }
        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) mainGameAdapter.activity;
        switch (message) {
            case "":
                olPlayHallRoom.addFriends(to);
                return;
            case "\'":
                olPlayHallRoom.deleteCp(true);
                return;
            case "\'\'":
                olPlayHallRoom.letInFamily(to);
                break;
        }
        olPlayHallRoom.sendMail(to, 0);
    }
}
