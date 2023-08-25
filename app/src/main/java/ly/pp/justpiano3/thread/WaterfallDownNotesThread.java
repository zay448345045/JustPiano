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
     * 是否暂停
     */
    @Getter
    @Setter
    private boolean pause;

    /**
     * 歌曲暂停时，目前的播放进度，内部保存的变量，不对外暴露
     */
    private Integer pauseProgress;

    /**
     * 播放进度
     */
    @Getter
    private int progress;

    /**
     * 播放进度的偏移量，用于暂停后继续时，或手动调节进度时的进度更新，
     */
    @Getter
    @Setter
    private int progressOffset;

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
        // 记录绘制的起始时间
        long startPlayTime = System.currentTimeMillis();
        // TODO 颜色不这么写死
        Paint rightHandPaint = new Paint();
        Paint leftHandPaint = new Paint();
        rightHandPaint.setColor(0x7fffcc00);
        leftHandPaint.setColor(0x7f66FFFF);
        while (running) {
            Canvas canvas = null;
            try {
                // sleep虽然写了固定值，但由于设备刷新率等原因，在绘制线程中的sleep数值会自动变化，不可信，需要根据时间戳进行计算坐标和进度
                WaterfallDownNotesThread.sleep(10);
                // 根据系统时间，计算距离开始播放的时间点，间隔多长时间
                // 计算过程中，减掉用户手动拖动进度时设置的进度偏移值
                int playIntervalTime = (int) (System.currentTimeMillis() - startPlayTime - progressOffset);
                // 如果处于暂停状态，则存储当前的播放进度，如果突然继续播放了，则移除存储的播放进度
                if (pause && pauseProgress == null) {
                    pauseProgress = playIntervalTime;
                } else if (!pause && pauseProgress != null) {
                    // 更新偏移量，使之后继续播放时能按照刚暂停时的进度来继续
                    progressOffset += playIntervalTime - pauseProgress;
                    playIntervalTime -= playIntervalTime - pauseProgress;
                    pauseProgress = null;
                }
                // 根据当前是否暂停，取出进度，进行绘制坐标计算
                progress = pause ? pauseProgress : playIntervalTime;
                // 获取绘制canvas
                canvas = waterfallView.getHolder().lockCanvas();
                if (canvas != null) {
                    // 清空画布，之后开始绘制
                    canvas.drawColor(Color.BLACK);
                    for (WaterfallNote waterfallNote : waterfallView.getWaterfallNotes()) {
                        // 瀑布流音块在view内对用户可见的，才绘制
                        if (noteIsVisible(waterfallNote)) {
                            canvas.drawRect(waterfallNote.getLeft(), progress - waterfallNote.getEndTime(),
                                    waterfallNote.getRight(), progress - waterfallNote.getStartTime(),
                                    waterfallNote.isLeftHand() ? leftHandPaint : rightHandPaint);
                            // 音块刚下落到下边界时，触发琴键按下效果，音块刚离开时，触发琴键抬起效果
                            if (noteJustInViewBottom(waterfallNote)) {
                                waterfallView.getNoteFallListener().onNoteFallDown(waterfallNote);
                                waterfallNote.setStatus(WaterfallNote.WaterfallNoteStatusEnum.PLAYING);
                            } else if (noteJustLeaveView(waterfallNote)) {
                                waterfallView.getNoteFallListener().onNoteLeave(waterfallNote);
                                waterfallNote.setStatus(WaterfallNote.WaterfallNoteStatusEnum.LEAVE);
                            }
                        }
                    }
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

    /**
     * 音符是否在view中对用户可见
     */
    private boolean noteIsVisible(WaterfallNote waterfallNote) {
        // 进度（毫秒数） - 音符开始时间小于0的时候，瀑布流还是在屏幕上未开始出现的状态，这种情况下过滤掉，不绘制
        // 结束时间同理，在屏幕内的才绘制
        return progress - waterfallNote.getStartTime() > 0 && (progress - waterfallNote.getEndTime()) <= waterfallView.getHeight();
    }

    /**
     * 音符是否已经到达view底部
     */
    private boolean noteJustInViewBottom(WaterfallNote waterfallNote) {
        // 代码最后的30表示提前30毫秒触发，因为这里如果写的很小的话，遇到低刷新率等情况导致sleep时间过长的时候，
        // progress变量的变化幅度过大，会引起这个判断永远为false，错过了这个音符掉落事件的触发
        return waterfallNote.getStatus() == WaterfallNote.WaterfallNoteStatusEnum.INIT
                && waterfallNote.getStartTime() + waterfallView.getHeight() - progress < 30;
    }

    /**
     * 音符是否已经完全离开view底部
     */
    private boolean noteJustLeaveView(WaterfallNote waterfallNote) {
        return waterfallNote.getStatus() == WaterfallNote.WaterfallNoteStatusEnum.PLAYING
                && waterfallNote.getEndTime() + waterfallView.getHeight() - progress < 30;
    }
}
