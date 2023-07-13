package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;
import protobuf.dto.OnlineHallChatDTO;
import protobuf.dto.OnlineRoomChatDTO;

final class ExpressClick implements OnClickListener {
    private final ExpressAdapter expressAdapter;
    private final int expressID;

    ExpressClick(ExpressAdapter expressAdapter, int i) {
        this.expressAdapter = expressAdapter;
        expressID = i;
    }

    @Override
    public final void onClick(View view) {
        if (expressAdapter.popupWindow != null && expressAdapter.popupWindow.isShowing()) {
            expressAdapter.popupWindow.dismiss();
            if (expressAdapter.connectionService != null) {
                if (expressAdapter.messageType == 12) {
                    OnlineHallChatDTO.Builder builder = OnlineHallChatDTO.newBuilder();
                    builder.setMessage("//" + expressID);
                    builder.setUserName("");
                    expressAdapter.connectionService.writeData(expressAdapter.messageType, builder.build());
                } else if (expressAdapter.messageType == 13) {
                    OnlineRoomChatDTO.Builder builder = OnlineRoomChatDTO.newBuilder();
                    builder.setMessage("//" + expressID);
                    builder.setUserName("");
                    builder.setColor(99);
                    expressAdapter.connectionService.writeData(expressAdapter.messageType, builder.build());
                }
            }
        }
    }
}
