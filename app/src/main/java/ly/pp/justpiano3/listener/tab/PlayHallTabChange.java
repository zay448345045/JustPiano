package ly.pp.justpiano3.listener.tab;

import android.widget.TabHost.OnTabChangeListener;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineLoadUserInfoDTO;
import protobuf.dto.OnlineLoadUserListDTO;

public final class PlayHallTabChange implements OnTabChangeListener {
    private final OLPlayHall olPlayHall;

    public PlayHallTabChange(OLPlayHall olPlayHall) {
        this.olPlayHall = olPlayHall;
    }

    @Override
    public void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayHall.tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            olPlayHall.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(
                    intValue == i ? R.drawable.selector_ol_orange : R.drawable.selector_ol_blue);
        }
        if (str.equals("tab1")) {
            OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
            builder.setType(1);
            builder.setPage(olPlayHall.pageNum);
            olPlayHall.sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
        } else if (str.equals("tab3")) {
            olPlayHall.sendMsg(OnlineProtocolType.LOAD_USER_LIST, OnlineLoadUserListDTO.getDefaultInstance());
        }
    }
}
