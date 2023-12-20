package ly.pp.justpiano3.adapter;

import static ly.pp.justpiano3.utils.UnitConvertUtil.dp2px;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;

import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.utils.OnlineUtil;
import protobuf.dto.OnlineHallChatDTO;
import protobuf.dto.OnlineRoomChatDTO;

public final class ExpressAdapter extends BaseAdapter {
    private final PopupWindow popupWindow;
    private final int messageType;
    private final Context context;
    private final Integer[] expressSeq;

    public ExpressAdapter(Context context, Integer[] expressSeq, PopupWindow popupWindow, int messageType) {
        this.context = context;
        this.expressSeq = expressSeq;
        this.popupWindow = popupWindow;
        this.messageType = messageType;
    }

    @Override
    public int getCount() {
        return expressSeq.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setPadding(0, 0, 0, 0);
        imageView.setOnClickListener(v -> {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                if (OnlineUtil.getConnectionService() != null) {
                    if (messageType == OnlineProtocolType.HALL_CHAT) {
                        OnlineHallChatDTO.Builder builder = OnlineHallChatDTO.newBuilder();
                        builder.setMessage("//" + i);
                        builder.setUserName("");
                        OnlineUtil.getConnectionService().writeData(messageType, builder.build());
                    } else if (messageType == OnlineProtocolType.ROOM_CHAT) {
                        OnlineRoomChatDTO.Builder builder = OnlineRoomChatDTO.newBuilder();
                        builder.setMessage("//" + i);
                        builder.setUserName("");
                        builder.setColor(99);
                        OnlineUtil.getConnectionService().writeData(messageType, builder.build());
                    }
                }
            }
        });
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight(dp2px(context, 48));
        imageView.setMaxWidth(dp2px(context, 48));
        imageView.setImageResource(expressSeq[i]);
        return imageView;
    }
}
