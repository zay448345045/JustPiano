package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.thread.WaterfallDownNotesThread;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;

/**
 * 瀑布流绘制view
 */
public class WaterfallView extends SurfaceView {

    /**
     * 瀑布的宽度占键盘黑键宽度的百分比
     */
    private static final float BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.9f;

    /**
     * 瀑布流音符最短和最长的持续时间
     */
    public static final int MIN_INTERVAL_TIME = 150;
    public static final int MAX_INTERVAL_TIME = 2000;

    /**
     * 进度条
     */
    @Getter
    private final Bitmap barImage;

    /**
     * 绘制音块瀑布流内容
     */
    @Getter
    private WaterfallNote[] waterfallNotes;

    /**
     * 监听器
     */
    @Getter
    @Setter
    private NoteFallListener noteFallListener;

    /**
     * 瀑布流循环绘制线程
     */
    private WaterfallDownNotesThread waterfallDownNotesThread;

    /**
     * 瀑布流监听器
     */
    public interface NoteFallListener {

        /**
         * 瀑布某个音刚开始落到view的最下方时触发
         */
        void onNoteFallDown(WaterfallNote waterfallNote);

        /**
         * 瀑布某个音彻底从瀑布流view下方消失时触发
         */
        void onNoteLeave(WaterfallNote waterfallNote);
    }

    public WaterfallView(Context context) {
        this(context, null);
    }

    public WaterfallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterfallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().setKeepScreenOn(true);
        barImage = SkinImageLoadUtil.loadImage(context, "bar");
    }

    /**
     * 开始绘制
     */
    public void startPlay(WaterfallNote[] waterfallNotes) {
        this.waterfallNotes = waterfallNotes;
        stopPlay();
        waterfallDownNotesThread = new WaterfallDownNotesThread(this);
        waterfallDownNotesThread.start();
    }

    public void stopPlay() {
        if (waterfallDownNotesThread != null) {
            waterfallDownNotesThread.setRunning(false);
            waterfallDownNotesThread = null;
        }
    }

    /**
     * 将琴键RectF的宽度，转换成瀑布流长条的宽度（略小于琴键的宽度）
     */
    public static RectF convertWidthToWaterfallWidth(boolean isBlack, RectF rectF) {
        if (rectF == null) {
            return null;
        }
        // 根据比例计算瀑布流的宽度
        float waterfallWidth = isBlack ? rectF.width() * BLACK_KEY_WATERFALL_WIDTH_FACTOR
                : rectF.width() * KeyboardModeView.BLACK_KEY_WIDTH_FACTOR * BLACK_KEY_WATERFALL_WIDTH_FACTOR;
        // 建立新的坐标，返回
        return new RectF(rectF.centerX() - waterfallWidth / 2, rectF.top, rectF.centerX() + waterfallWidth / 2, rectF.bottom);
    }
}
