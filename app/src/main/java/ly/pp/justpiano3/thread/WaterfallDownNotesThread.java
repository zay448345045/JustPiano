package ly.pp.justpiano3.thread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.view.WaterfallView;

public final class WaterfallDownNotesThread extends Thread {

    /**
     * 是否在运行
     */
    @Getter
    @Setter
    private boolean running;

    /**
     * 绘制的起始时间
     */
    private long startDrawTime;

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
        // 记录上一次绘制时间，确定休眠时间，使绘制的间隔时间更稳定
        startDrawTime = System.currentTimeMillis();
        // TODO 颜色不这么写死
        Paint rightHandPaint = new Paint();
        Paint leftHandPaint = new Paint();
        rightHandPaint.setColor(0x7fffcc00);
        leftHandPaint.setColor(0x7f66FFFF);
        int sleepTime = 16;
        while (running) {
            Canvas canvas = null;
            try {
                canvas = waterfallView.getHolder().lockCanvas();
                if (canvas != null) {
                    // 开始绘制操作
//                    canvas.drawBitmap(waterfallView.getBackgroundImage(), null, waterfallView.getBackgroundRectF(), null);
                    canvas.drawColor(Color.BLACK);
                    for (WaterfallNote waterfallNote : waterfallView.getWaterfallNotes()) {
                        // 进度（毫秒数） - 音符开始时间小于0的时候，瀑布流还是在屏幕上未开始出现的状态，这种情况下过滤掉，不绘制
                        // 结束时间同理，在屏幕内的才绘制
                        int intervalTime = (int) (System.currentTimeMillis() - startDrawTime);
                        if (intervalTime - waterfallNote.getStartTime() > 0 && (intervalTime - waterfallNote.getEndTime()) / sleepTime <= waterfallView.getHeight() / sleepTime) {
                            // 绘制长方形
                            canvas.drawRect(waterfallNote.getLeft(), intervalTime - waterfallNote.getEndTime(),
                                    waterfallNote.getRight(), intervalTime - waterfallNote.getStartTime(),
                                    waterfallNote.isLeftHand() ? leftHandPaint : rightHandPaint);
                            // 音块刚下落到下边界时，触发琴键按下效果，音块刚离开时，触发琴键抬起效果
                            if (noteJustInViewBottom(intervalTime, sleepTime, waterfallNote)) {
                                waterfallView.getNoteFallListener().onNoteFallDown(waterfallNote);
                                waterfallNote.setStatus(WaterfallNote.WaterfallNoteStatusEnum.PLAYING);
                            } else if (noteJustLeaveView(intervalTime, sleepTime, waterfallNote)) {
                                waterfallView.getNoteFallListener().onNoteLeave(waterfallNote);
                                waterfallNote.setStatus(WaterfallNote.WaterfallNoteStatusEnum.LEAVE);
                            }
                        }
                    }
                }
                WaterfallDownNotesThread.sleep(sleepTime);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    waterfallView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private boolean noteJustInViewBottom(int intervalTime, int sleepTime, WaterfallNote waterfallNote) {
        return waterfallNote.getStatus() == WaterfallNote.WaterfallNoteStatusEnum.INIT
                && Math.abs(intervalTime - waterfallNote.getStartTime() - waterfallView.getHeight()) < 50;
    }

    private boolean noteJustLeaveView(int intervalTime, int sleepTime, WaterfallNote waterfallNote) {
        return waterfallNote.getStatus() == WaterfallNote.WaterfallNoteStatusEnum.PLAYING
                && Math.abs(intervalTime - waterfallNote.getEndTime() - waterfallView.getHeight()) < 50;
    }
}
