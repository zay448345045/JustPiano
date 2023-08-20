package ly.pp.justpiano3.thread;

import android.graphics.Canvas;
import android.graphics.Paint;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.view.WaterfallView;

public final class WaterfallDownNotesThread extends Thread {

    /**
     * 是否在运行
     */
    @Getter
    @Setter
    private boolean running;

    /**
     * 瀑布流view
     */
    private final WaterfallView waterfallView;

    public WaterfallDownNotesThread(WaterfallView waterfallView) {
        this.waterfallView = waterfallView;
        this.running = true;
    }

    @Override
    public void run() {
        Paint paint = new Paint();
        paint.setColor(0xff00ff00);
        while (running) {
            Canvas canvas = null;
            try {
                // TODO 停顿时间配置化
                int intervalTime = 1;
                waterfallView.addProgress(intervalTime);
                WaterfallDownNotesThread.sleep(intervalTime);
                canvas = waterfallView.getHolder().lockCanvas();
                if (canvas != null) {
                    // 开始绘制操作
                    canvas.drawCircle(444, waterfallView.getProgress(), 47, paint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    waterfallView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
