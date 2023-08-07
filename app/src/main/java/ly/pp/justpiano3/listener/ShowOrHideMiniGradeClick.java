package ly.pp.justpiano3.listener;

import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineSetMiniGradeDTO;

public final class ShowOrHideMiniGradeClick implements OnClickListener {

    private final PianoPlay pianoPlay;

    public ShowOrHideMiniGradeClick(PianoPlay pianoPlay) {
        this.pianoPlay = pianoPlay;
    }

    @Override
    public void onClick(View view) {
        if (pianoPlay.horizontalListView.getVisibility() == View.VISIBLE) {
            OnlineSetMiniGradeDTO.Builder builder = OnlineSetMiniGradeDTO.newBuilder();
            builder.setMiniGradeOn(false);
            pianoPlay.sendMsg(OnlineProtocolType.SET_MINI_GRADE, builder.build());
            pianoPlay.showHideGrade.setText("显示成绩");
            pianoPlay.horizontalListView.setVisibility(View.GONE);
        } else if (pianoPlay.horizontalListView.getVisibility() == View.GONE) {
            OnlineSetMiniGradeDTO.Builder builder = OnlineSetMiniGradeDTO.newBuilder();
            builder.setMiniGradeOn(true);
            pianoPlay.sendMsg(OnlineProtocolType.SET_MINI_GRADE, builder.build());
            pianoPlay.showHideGrade.setText("隐藏成绩");
            pianoPlay.horizontalListView.setVisibility(View.VISIBLE);
        }
    }
}
