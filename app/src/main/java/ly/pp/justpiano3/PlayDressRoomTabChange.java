package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

final class PlayDressRoomTabChange implements OnTabChangeListener {
    private final OLPlayDressRoom olPlayDressRoom;

    PlayDressRoomTabChange(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayDressRoom.tabhost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (intValue == i) {
                olPlayDressRoom.tabhost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
            } else {
                olPlayDressRoom.tabhost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
            }
        }
    }
}
