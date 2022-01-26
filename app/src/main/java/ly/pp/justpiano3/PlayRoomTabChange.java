package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

final class PlayRoomTabChange implements OnTabChangeListener {
    private final OLPlayRoom olPlayRoom;

    PlayRoomTabChange(OLPlayRoom olPlayRoom) {
        this.olPlayRoom = olPlayRoom;
    }

    @Override
    public final void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayRoom.roomTabs.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (intValue == i) {
                olPlayRoom.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
            } else {
                olPlayRoom.roomTabs.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
            }
        }
        JSONObject jSONObject;
        switch (str) {
            case "tab1":
                jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", "L");
                    jSONObject.put("B", olPlayRoom.page);
                    olPlayRoom.sendMsg((byte) 34, olPlayRoom.roomID0, olPlayRoom.hallID0, jSONObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "tab2":
                if (olPlayRoom.msgListView != null && olPlayRoom.msgListView.getAdapter() != null) {
                    olPlayRoom.msgListView.setSelection(olPlayRoom.msgListView.getAdapter().getCount() - 1);
                }
                break;
            case "tab4":
                olPlayRoom.sendMsg((byte) 36, (byte) 0, olPlayRoom.hallID0, "");
                break;
        }
    }
}
