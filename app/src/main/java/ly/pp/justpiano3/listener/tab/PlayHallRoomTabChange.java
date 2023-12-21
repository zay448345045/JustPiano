package ly.pp.justpiano3.listener.tab;

import android.widget.TabHost.OnTabChangeListener;

import java.util.ArrayList;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHallRoom;
import ly.pp.justpiano3.adapter.FamilyAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineLoadUserInfoDTO;

public final class PlayHallRoomTabChange implements OnTabChangeListener {
    private final OLPlayHallRoom olPlayHallRoom;

    public PlayHallRoomTabChange(OLPlayHallRoom olPlayHallRoom) {
        this.olPlayHallRoom = olPlayHallRoom;
    }

    @Override
    public void onTabChanged(String str) {
        int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
        int childCount = olPlayHallRoom.tabHost.getTabWidget().getChildCount();
        for (int i = 0; i < childCount; i++) {
            olPlayHallRoom.tabHost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(
                    intValue == i ? R.drawable.selector_ol_orange : R.drawable.selector_ol_blue);
        }
        switch (str) {
            case "tab1":
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(olPlayHallRoom.pageNum);
                olPlayHallRoom.sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                break;
            case "tab4":
                olPlayHallRoom.mailCountsView.setText("");
                builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(2);
                olPlayHallRoom.sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                break;
            case "tab3":
                builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(3);
                olPlayHallRoom.sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                break;
            case "tab5":
                if (olPlayHallRoom.familyList == null) {
                    olPlayHallRoom.familyList = new ArrayList<>();
                }
                if (olPlayHallRoom.familyPageNum == 0 && olPlayHallRoom.familyList.isEmpty()) {
                    OnlineFamilyDTO.Builder builder1 = OnlineFamilyDTO.newBuilder();
                    builder1.setType(2);
                    builder1.setPage(0);
                    olPlayHallRoom.jpprogressBar.show();
                    olPlayHallRoom.sendMsg(OnlineProtocolType.FAMILY, builder1.build());
                } else {
                    FamilyAdapter familyAdapter = (FamilyAdapter) olPlayHallRoom.familyListView.getAdapter();
                    if (familyAdapter == null) {
                        olPlayHallRoom.bindAdapter(olPlayHallRoom.familyListView, olPlayHallRoom.familyList);
                    } else {
                        olPlayHallRoom.updateFamilyListShow(familyAdapter, olPlayHallRoom.familyListView, olPlayHallRoom.familyList);
                    }
                    olPlayHallRoom.familyListView.post(() -> olPlayHallRoom.familyListView.setSelection(olPlayHallRoom.familyListPosition));
                }
                break;
        }
    }
}