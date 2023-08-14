package ly.pp.justpiano3.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class ScrollText extends AppCompatTextView {

    public ScrollText(Context context) {
        super(context);
    }

    public ScrollText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ScrollText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
