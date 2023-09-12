package ly.pp.justpiano3.listener.tab;

import android.widget.TabHost.OnTabChangeListener;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.OLPlayRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineLoadUserInfoDTO;
import protobuf.dto.OnlineLoadUserListDTO;

public final class PlayRoomTabChange implements OnTabChangeListener {
    private final OLPlayRoomActivity olPlayRoomActivity;

    public PlayRoomTabChange(OLPlayRoomActivity olPlayRoomActivity) {
        this.olPlayRoomActivity = olPlayRoomActivity;
    }

    @Override
    public void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayRoomActivity.roomTabs.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (intValue == i) {
                olPlayRoomActivity.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
            } else {
                olPlayRoomActivity.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
            }
        }
        switch (str) {
            case "tab1":
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(olPlayRoomActivity.page);
                olPlayRoomActivity.sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                break;
            case "tab2":
                if (olPlayRoomActivity.msgListView != null && olPlayRoomActivity.msgListView.getAdapter() != null) {
                    olPlayRoomActivity.msgListView.setSelection(olPlayRoomActivity.msgListView.getAdapter().getCount() - 1);
                }
                break;
            case "tab3":
                if (olPlayRoomActivity instanceof OLPlayKeyboardRoom) {
                    olPlayRoomActivity.sendMsg(OnlineProtocolType.LOAD_USER_LIST, OnlineLoadUserListDTO.getDefaultInstance());
                }
                break;
            case "tab4":
                olPlayRoomActivity.sendMsg(OnlineProtocolType.LOAD_USER_LIST, OnlineLoadUserListDTO.getDefaultInstance());
                break;
        }
    }
}
