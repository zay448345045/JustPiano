package ly.pp.justpiano3;

import android.content.Context;
import android.util.AttributeSet;

public class ScrollText extends androidx.appcompat.widget.AppCompatTextView {

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
