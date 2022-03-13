package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.protobuf.dto.OnlineSetMiniGradeDTO;

final class ShowOrHideMiniGradeClick implements OnClickListener {

    private final PianoPlay pianoPlay;

    ShowOrHideMiniGradeClick(PianoPlay pianoPlay) {
        this.pianoPlay = pianoPlay;
    }

    @Override
    public final void onClick(View view) {
        if (pianoPlay.horizontalListView.getVisibility() == View.VISIBLE) {
            OnlineSetMiniGradeDTO.Builder builder = OnlineSetMiniGradeDTO.newBuilder();
            builder.setMiniGradeOn(false);
            pianoPlay.sendMsg(32, builder.build());
            pianoPlay.showHideGrade.setText("显示成绩");
            pianoPlay.horizontalListView.setVisibility(View.GONE);
        } else if (pianoPlay.horizontalListView.getVisibility() == View.GONE) {
            OnlineSetMiniGradeDTO.Builder builder = OnlineSetMiniGradeDTO.newBuilder();
            builder.setMiniGradeOn(true);
            pianoPlay.sendMsg(32, builder.build());
            pianoPlay.showHideGrade.setText("隐藏成绩");
            pianoPlay.horizontalListView.setVisibility(View.VISIBLE);
        }
    }
}
