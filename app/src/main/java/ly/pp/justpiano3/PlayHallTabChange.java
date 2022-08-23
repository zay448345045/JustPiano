package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

import ly.pp.justpiano3.protobuf.dto.OnlineLoadUserInfoDTO;
import ly.pp.justpiano3.protobuf.dto.OnlineLoadUserListDTO;

final class PlayHallTabChange implements OnTabChangeListener {
    private final OLPlayHall olPlayHall;

    PlayHallTabChange(OLPlayHall olPlayHall) {
        this.olPlayHall = olPlayHall;
    }

    @Override
    public void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayHall.tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (intValue == i) {
                olPlayHall.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
            } else {
                olPlayHall.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
            }
        }
        if (str.equals("tab1")) {
            OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
            builder.setType(1);
            builder.setPage(olPlayHall.pageNum);
            olPlayHall.sendMsg(34, builder.build());
        } else if (str.equals("tab3")) {
            olPlayHall.sendMsg(36, OnlineLoadUserListDTO.getDefaultInstance());
        }
    }
}
