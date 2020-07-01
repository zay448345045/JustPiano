package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

final class ShowTitleClick implements OnClickListener {

    private final OLMelodySelect olMelodySelect;

    ShowTitleClick(OLMelodySelect oLMelodySelect) {
        olMelodySelect = oLMelodySelect;
    }

    public final void onClick(View view) {
        if (olMelodySelect.f4330s) {
            olMelodySelect.f4328q.setVisibility(View.GONE);
            olMelodySelect.f4330s = false;
            olMelodySelect.f4336y.setText(" 显示标题 ");
            return;
        }
        olMelodySelect.f4328q.setVisibility(View.VISIBLE);
        olMelodySelect.f4330s = true;
        olMelodySelect.f4336y.setText(" 隐藏标题 ");
    }
}
