package ly.pp.justpiano3.listener.tab;

import android.view.View;
import android.widget.TabHost.OnTabChangeListener;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.online.OLRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineLoadUserInfoDTO;
import protobuf.dto.OnlineLoadUserListDTO;

public final class PlayRoomTabChange implements OnTabChangeListener {
    private final OLRoomActivity olRoomActivity;

    public PlayRoomTabChange(OLRoomActivity olRoomActivity) {
        this.olRoomActivity = olRoomActivity;
    }

    @Override
    public void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olRoomActivity.roomTabs.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            olRoomActivity.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(
                    intValue == i ? R.drawable.selector_ol_orange : R.drawable.selector_ol_blue);
        }
        if (olRoomActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
            olPlayKeyboardRoom.waterfallView.setVisibility(View.INVISIBLE);
        }
        switch (str) {
            case "tab1" -> {
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(olRoomActivity.page);
                olRoomActivity.sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
            }
            case "tab2" -> {
                if (olRoomActivity.msgListView != null && olRoomActivity.msgListView.getAdapter() != null) {
                    olRoomActivity.msgListView.smoothScrollToPositionFromTop(olRoomActivity.msgListView.getAdapter().getCount() - 1, -10000);
                }
            }
            case "tab3" -> {
                if (olRoomActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    if (olRoomActivity.msgListView != null && olRoomActivity.msgListView.getAdapter() != null) {
                        olRoomActivity.msgListView.smoothScrollToPositionFromTop(olRoomActivity.msgListView.getAdapter().getCount() - 1, -10000);
                    }
                    olPlayKeyboardRoom.waterfallView.setVisibility(View.VISIBLE);
                    olPlayKeyboardRoom.onlineWaterfallViewNoteWidthUpdateHandle();
                }
            }
            case "tab4" ->
                    olRoomActivity.sendMsg(OnlineProtocolType.LOAD_USER_LIST, OnlineLoadUserListDTO.getDefaultInstance());
        }
    }
}
