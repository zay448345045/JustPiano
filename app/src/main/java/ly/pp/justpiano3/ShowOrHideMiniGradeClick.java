package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class ShowOrHideMiniGradeClick implements OnClickListener {

    private final PianoPlay pianoPlay;

    ShowOrHideMiniGradeClick(PianoPlay pianoPlay) {
        this.pianoPlay = pianoPlay;
    }

    @Override
    public final void onClick(View view) {
        JSONObject jSONObject = new JSONObject();
        if (pianoPlay.horizontalListView.getVisibility() == View.VISIBLE) {
            try {
                jSONObject.put("S", 0);
                pianoPlay.sendMsg((byte) 32, (byte) 0, jSONObject.toString(), null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pianoPlay.showHideGrade.setText("显示成绩");
            pianoPlay.horizontalListView.setVisibility(View.GONE);
        } else if (pianoPlay.horizontalListView.getVisibility() == View.GONE) {
            try {
                jSONObject.put("S", 1);
                pianoPlay.sendMsg((byte) 32, (byte) 0, jSONObject.toString(), null);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            pianoPlay.showHideGrade.setText("隐藏成绩");
            pianoPlay.horizontalListView.setVisibility(View.VISIBLE);
        }
    }
}
