package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.thread.WaterfallDownNotesThread;

import java.io.IOException;

public class WaterfallView extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * 瀑布的宽度占键盘黑键宽度的百分比
     */
    private static final float BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.9f;

    /**
     * 瀑布流音符最短和最长的持续时间
     */
    public static final int MIN_INTERVAL_TIME = 100;
    public static final int MAX_INTERVAL_TIME = 2000;

    /**
     * 背景、进度条
     */
    @Getter
    private Bitmap backgroundImage;
    @Getter
    RectF backgroundRectF;
    @Getter
    private Bitmap barImage;

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
     * 播放总体进度
     */
    @Getter
    @Setter
    private int progress;

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
        void onNoteFallDown(int pitch, int volume);

        /**
         * 瀑布某个音彻底从瀑布流view下方消失时触发
         */
        void onNoteLeave(int pitch, int volume);
    }

    public WaterfallView(Context context) {
        this(context, null);
    }

    public WaterfallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterfallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        getHolder().setKeepScreenOn(true);
        backgroundImage = loadImage(context, "background_hd");
        barImage = loadImage(context, "bar");
    }

    public void addProgress(int progress) {
        this.progress += progress;
    }

    /**
     * 开始绘制
     */
    public void startPlay(WaterfallNote[] waterfallNotes) {
        this.progress = 0;
        this.waterfallNotes = waterfallNotes;
        stopPlay();
        waterfallDownNotesThread = new WaterfallDownNotesThread(this);
        waterfallDownNotesThread.start();
    }

    public void stopPlay() {
        if (waterfallDownNotesThread != null) {
            waterfallDownNotesThread.setRunning(false);
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

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stopPlay();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.backgroundRectF = new RectF(getLeft(), getTop(), getLeft() + w, getTop() + h);
    }

    private Bitmap loadImage(Context context, String str) {
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_list", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
            } catch (Exception e) {
                try {
                    return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
