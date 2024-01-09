package ly.pp.justpiano3.utils;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * view相关工具
 */
public final class ViewUtil {

    /**
     * 设置view的布局完成回调
     *
     * @param view     view
     * @param runnable 回调内容
     */
    public static void registerViewLayoutObserver(View view, Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                runnable.run();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 设置TextView的文本字体大小
     */
    public static void setTextViewFontSize(int textSize, TextView... textViews) {
        if (textViews != null) {
            for (TextView textView : textViews) {
                textView.setTextSize(textSize);
            }
        }
    }
}
