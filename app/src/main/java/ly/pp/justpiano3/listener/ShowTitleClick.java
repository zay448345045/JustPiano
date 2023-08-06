package ly.pp.justpiano3.listener;

import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.OLMelodySelect;

final class ShowTitleClick implements OnClickListener {

    private final OLMelodySelect olMelodySelect;

    ShowTitleClick(OLMelodySelect oLMelodySelect) {
        olMelodySelect = oLMelodySelect;
    }

    public void onClick(View view) {
        if (olMelodySelect.f4330s) {
            olMelodySelect.f4328q.setVisibility(View.GONE);
            olMelodySelect.f4330s = false;
            olMelodySelect.showTitleButton.setText(" 显示标题 ");
            return;
        }
        olMelodySelect.f4328q.setVisibility(View.VISIBLE);
        olMelodySelect.f4330s = true;
        olMelodySelect.showTitleButton.setText(" 隐藏标题 ");
    }
}
