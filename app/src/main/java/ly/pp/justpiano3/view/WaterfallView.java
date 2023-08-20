package ly.pp.justpiano3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.thread.WaterfallDownNotesThread;

public class WaterfallView extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * 监听器
     */
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
    }

    public void setNoteFallListener(NoteFallListener noteFallListener) {
        this.noteFallListener = noteFallListener;
    }

    public void addProgress(int progress) {
        this.progress += progress;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        waterfallDownNotesThread = new WaterfallDownNotesThread(this);
        waterfallDownNotesThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (waterfallDownNotesThread != null) {
            waterfallDownNotesThread.setRunning(false);
        }
    }
}
