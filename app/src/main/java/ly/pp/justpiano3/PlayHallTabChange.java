package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

final class PlayHallTabChange implements OnTabChangeListener {
    private final OLPlayHall olPlayHall;

    PlayHallTabChange(OLPlayHall olPlayHall) {
        this.olPlayHall = olPlayHall;
    }

    @Override
    public final void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayHall.tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (intValue == i) {
                olPlayHall.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
            } else {
                olPlayHall.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
            }
        }
        JSONObject jSONObject;
        if (str.equals("tab1")) {
            try {
                jSONObject = new JSONObject();
                jSONObject.put("T", "L");
                jSONObject.put("B", olPlayHall.pageNum);
                olPlayHall.sendMsg((byte) 34, (byte) 0, olPlayHall.hallID, jSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (str.equals("tab3")) {
            olPlayHall.sendMsg((byte) 36, (byte) 0, olPlayHall.hallID, "");
        }
    }
}
