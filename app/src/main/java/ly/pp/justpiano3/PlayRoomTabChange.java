package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import ly.pp.justpiano3.protobuf.request.OnlineRequest;

final class PlayRoomTabChange implements OnTabChangeListener {
    private final OLPlayRoomInterface olPlayRoomInterface;

    PlayRoomTabChange(OLPlayRoomInterface olPlayRoomInterface) {
        this.olPlayRoomInterface = olPlayRoomInterface;
    }

    @Override
    public final void onTabChanged(String str) {
        if (olPlayRoomInterface instanceof OLPlayRoom) {
            OLPlayRoom olPlayRoom = (OLPlayRoom) olPlayRoomInterface;
            int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
            int childCount = olPlayRoom.roomTabs.getTabWidget().getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (intValue == i) {
                    olPlayRoom.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
                } else {
                    olPlayRoom.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
                }
            }
            switch (str) {
                case "tab1":
                    OnlineRequest.LoadUserInfo.Builder builder = OnlineRequest.LoadUserInfo.newBuilder();
                    builder.setType(1);
                    builder.setPage(olPlayRoom.page);
                    olPlayRoom.sendMsg(34, builder.build());
                    break;
                case "tab2":
                    if (olPlayRoom.msgListView != null && olPlayRoom.msgListView.getAdapter() != null) {
                        olPlayRoom.msgListView.setSelection(olPlayRoom.msgListView.getAdapter().getCount() - 1);
                    }
                    break;
                case "tab4":
                    olPlayRoom.sendMsg(36, OnlineRequest.LoadUserList.getDefaultInstance());
                    break;
            }
        } else if (olPlayRoomInterface instanceof OLPlayKeyboardRoom) {
            OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) olPlayRoomInterface;
            int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
            int childCount = olPlayKeyboardRoom.roomTabs.getTabWidget().getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (intValue == i) {
                    olPlayKeyboardRoom.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
                } else {
                    olPlayKeyboardRoom.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
                }
            }
            JSONObject jSONObject;
            switch (str) {
                case "tab1":
                    OnlineRequest.LoadUserInfo.Builder builder = OnlineRequest.LoadUserInfo.newBuilder();
                    builder.setType(1);
                    builder.setPage(olPlayKeyboardRoom.page);
                    olPlayKeyboardRoom.sendMsg(34, builder.build());
                    break;
                case "tab2":
                    if (olPlayKeyboardRoom.msgListView != null && olPlayKeyboardRoom.msgListView.getAdapter() != null) {
                        olPlayKeyboardRoom.msgListView.setSelection(olPlayKeyboardRoom.msgListView.getAdapter().getCount() - 1);
                    }
                    break;
                case "tab3":
                    olPlayKeyboardRoom.sendMsg(36, OnlineRequest.LoadUserList.getDefaultInstance());
                    break;
            }
        }
    }
}
