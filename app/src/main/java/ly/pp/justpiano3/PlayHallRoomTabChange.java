package ly.pp.justpiano3;

import android.widget.TabHost.OnTabChangeListener;

import ly.pp.justpiano3.protobuf.dto.OnlineFamilyDTO;
import ly.pp.justpiano3.protobuf.dto.OnlineLoadUserInfoDTO;

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
        switch (str) {
            case "tab1":
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(olPlayHallRoom.pageNum);
                olPlayHallRoom.sendMsg(34, builder.build());
                break;
            case "tab4":
                olPlayHallRoom.mailCountsView.setText("");
                builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(2);
                olPlayHallRoom.sendMsg(34, builder.build());
                break;
            case "tab3":
                builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(3);
                olPlayHallRoom.sendMsg(34, builder.build());
                break;
            case "tab5":
                if (olPlayHallRoom.familyPageNum == 0 && olPlayHallRoom.familyList.isEmpty()) {
                    OnlineFamilyDTO.Builder builder1 = OnlineFamilyDTO.newBuilder();
                    builder1.setType(2);
                    builder1.setPage(0);
                    olPlayHallRoom.jpprogressBar.show();
                    olPlayHallRoom.sendMsg(18, builder1.build());
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