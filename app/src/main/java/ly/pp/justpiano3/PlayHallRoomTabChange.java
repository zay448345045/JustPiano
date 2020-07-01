package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

final class PlayHallRoomTabChange implements OnTabChangeListener {
    private final OLPlayHallRoom olPlayHallRoom;

    PlayHallRoomTabChange(OLPlayHallRoom olPlayHallRoom) {
        this.olPlayHallRoom = olPlayHallRoom;
    }

    @Override
    public final void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayHallRoom.tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (intValue == i) {
                olPlayHallRoom.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
            } else {
                olPlayHallRoom.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
            }
        }
        JSONObject jSONObject;
        switch (str) {
            case "tab1":
                try {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("T", "L");
                    jSONObject2.put("B", olPlayHallRoom.pageNum);
                    olPlayHallRoom.sendMsg((byte) 34, (byte) 0, jSONObject2.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "tab4":
                olPlayHallRoom.mailCounts.setText("");
                jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", "M");
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                olPlayHallRoom.sendMsg((byte) 34, (byte) 0, jSONObject.toString());
                break;
            case "tab3":
                jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", "C");
                } catch (JSONException e22) {
                    e22.printStackTrace();
                }
                olPlayHallRoom.sendMsg((byte) 34, (byte) 0, jSONObject.toString());
                break;
            case "tab5":
                if (olPlayHallRoom.familyPageNum == 0 && olPlayHallRoom.familyList.size() == 0) {
                    jSONObject = new JSONObject();
                    try {
                        jSONObject.put("K", 2);
                        jSONObject.put("B", 0);
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                    olPlayHallRoom.jpprogressBar.show();
                    olPlayHallRoom.sendMsg((byte) 18, (byte) 0, jSONObject.toString());
                } else {
                    FamilyAdapter fa = (FamilyAdapter) olPlayHallRoom.familyListView.getAdapter();
                    if (fa == null) {
                        olPlayHallRoom.mo2907b(olPlayHallRoom.familyListView, olPlayHallRoom.familyList);
                    } else {
                        olPlayHallRoom.mo2905a(fa, olPlayHallRoom.familyListView, olPlayHallRoom.familyList);
                    }
                    olPlayHallRoom.familyListView.post(() -> olPlayHallRoom.familyListView.setSelection(olPlayHallRoom.familyListPosition));
                }
                break;
        }
    }
}