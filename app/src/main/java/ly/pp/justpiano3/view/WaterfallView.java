package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceView;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;

/**
 * 瀑布流绘制view
 */
public class WaterfallView extends SurfaceView {

    /**
     * 瀑布的宽度占键盘黑键宽度的百分比
     */
    private static final float BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.85f;

    /**
     * 瀑布流音符最短和最长的持续时间
     */
    public static final int MIN_INTERVAL_TIME = 150;
    public static final int MAX_INTERVAL_TIME = 2000;

    /**
     * 进度条
     */
    private Bitmap progressBarImage;
    private Bitmap progressBarBaseImage;

    /**
     * 进度条绘制坐标范围
     */
    private final RectF progressBarRect = new RectF();
    private final RectF progressBarBaseRect = new RectF();

    /**
     * 绘制音块瀑布流内容
     */
    private WaterfallNote[] waterfallNotes;

    /**
     * 曲谱总进度，单位毫秒
     */
    private int totalProgress;

    /**
     * 瀑布流音块当前状态，不对外暴露
     */
    @Getter
    private int[] waterfallNoteStatus;

    /**
     * 监听器
     */
    @Getter
    @Setter
    private NoteFallListener noteFallListener;

    /**
     * 瀑布流循环绘制线程，不对外暴露
     */
    private WaterfallDownNotesThread waterfallDownNotesThread;

    /**
     * 销毁view，释放资源
     */
    public void destroy() {
        if (progressBarImage != null) {
            progressBarImage.recycle();
        }
        progressBarImage = null;

        if (progressBarBaseImage != null) {
            progressBarBaseImage.recycle();
        }
        progressBarBaseImage = null;
    }

    /**
     * 瀑布流监听器
     */
    public interface NoteFallListener {

        /**
         * 瀑布某个音刚开始落到view的最下方时触发，监听器中不要有耗时太长的操作，避免影响绘制
         */
        void onNoteFallDown(WaterfallNote waterfallNote);

        /**
         * 瀑布某个音彻底从瀑布流view下方消失时触发，监听器中不要有耗时太长的操作，避免影响绘制
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
        progressBarImage = SkinImageLoadUtil.loadImage(context, "progress_bar");
        progressBarBaseImage = SkinImageLoadUtil.loadImage(context, "progress_bar_base");
    }

    /**
     * 开始播放
     */
    public void startPlay(WaterfallNote[] waterfallNotes) {
        stopPlay();
        this.waterfallNotes = waterfallNotes;
        this.totalProgress = calculateTotalProgress(waterfallNotes);
        this.waterfallNoteStatus = new int[waterfallNotes.length];
        waterfallDownNotesThread = new WaterfallDownNotesThread(this);
        waterfallDownNotesThread.start();
    }

    /**
     * 计算曲谱总进度
     */
    private int calculateTotalProgress(WaterfallNote[] waterfallNotes) {
        int max = 0;
        for (WaterfallNote waterfallNote : waterfallNotes) {
            max = Math.max(waterfallNote.getEndTime(), max);
        }
        return max;
    }

    /**
     * 暂停播放
     */
    public void pausePlay() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            waterfallDownNotesThread.setPause(true);
        }
    }

    /**
     * 继续播放
     */
    public void resumePlay() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            waterfallDownNotesThread.setPause(false);
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            waterfallDownNotesThread.setRunning(false);
        }
        waterfallDownNotesThread = null;
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            return !waterfallDownNotesThread.isPause();
        } else {
            return false;
        }
    }

    /**
     * 获取目前的播放进度，单位毫秒，如果未在播放，返回0
     */
    public int getPlayProgress() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            return waterfallDownNotesThread.getProgress();
        }
        return 0;
    }

    /**
     * 获取曲谱总时长
     */
    public int getTotalProgress() {
        return totalProgress;
    }

    /**
     * 设置当前播放进度，单位毫秒
     */
    public void setPlayProgress(int progress) {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            // 读取当前播放进度，更新偏移量即可，之后绘制时的时间会根据偏移量进行计算，后续就会在新的进度上继续绘制了
            waterfallDownNotesThread.setProgressOffset(waterfallDownNotesThread.getProgressOffset() + waterfallDownNotesThread.getProgress() - progress);
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

    /**
     * 绘制线程
     */
    private static final class WaterfallDownNotesThread extends Thread {

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
                // 暂停线程，控制最高绘制帧率
                sleepThreadFrameTime();
                // 根据系统时间，计算距离开始播放的时间点，间隔多长时间
                // 计算过程中，减掉进度偏移量，进度偏移量可能为用户手动拖动进度时设置的，也可能为暂停播放后继续播放所带来的时间差
                int playIntervalTime = (int) (System.currentTimeMillis() - startPlayTime - progressOffset);
                // 如果处于暂停状态，则存储当前的播放进度，如果突然继续播放了，则移除存储的播放进度
                if (pause && pauseProgress == null) {
                    pauseProgress = playIntervalTime;
                } else if (!pause && pauseProgress != null) {
                    // 更新偏移量，使之后继续播放时能按照刚暂停时的进度来继续
                    progressOffset += playIntervalTime - pauseProgress;
                    // 接下来当前的绘制帧，调整playIntervalTime，也减掉偏移量的改变值，使当前帧的绘制顺滑起来
                    playIntervalTime -= playIntervalTime - pauseProgress;
                    pauseProgress = null;
                }
                // 根据当前是否暂停，取出进度，进行绘制坐标计算
                progress = pause ? pauseProgress : playIntervalTime;
                // 执行绘制，在锁canvas期间，尽可能执行最少的代码逻辑操作
                doDrawWaterfallNotes(rightHandPaint, leftHandPaint);
                // 确定是否有音块到达了屏幕底部或完全移出了屏幕，如果有，调用监听
                handleWaterfallNoteListener();
            }
        }

        /**
         * 暂停线程，控制最高绘制帧率
         */
        private void sleepThreadFrameTime() {
            // 在绘制线程中的sleep数值若比系统刷新率确定的每帧时间短，则sleep的值可能会被忽略，此处限定每帧最小间隔10毫秒绘制
            // 即是说，系统刷新率如果设置比100Hz要高的情况下，会强制100Hz绘制，设置比100Hz低的情况下，绘制频率取决于系统设置的刷新率
            // 因此，计算绘制坐标等，不要依赖这个sleep的时间，而要手动取系统时间计时，用时间差来计算坐标
            try {
                WaterfallDownNotesThread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 执行绘制可见的瀑布流音块
         */
        private void doDrawWaterfallNotes(Paint rightHandPaint, Paint leftHandPaint) {
            Canvas canvas = null;
            try {
                // 获取绘制canvas
                canvas = waterfallView.getHolder().lockCanvas();
                if (canvas != null) {
                    // 清空画布，之后开始绘制
                    canvas.drawColor(Color.BLACK);
                    // 进度条绘制
                    drawProgressBar(canvas);
                    // 音块绘制
                    for (WaterfallNote waterfallNote : waterfallView.waterfallNotes) {
                        // 瀑布流音块在view内对用户可见的，才绘制
                        if (noteIsVisible(waterfallNote)) {
                            canvas.drawRect(waterfallNote.getLeft(), progress - waterfallNote.getEndTime(),
                                    waterfallNote.getRight(), progress - waterfallNote.getStartTime(),
                                    waterfallNote.isLeftHand() ? leftHandPaint : rightHandPaint);
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

        /**
         * 绘制进度条
         */
        private void drawProgressBar(Canvas canvas) {
            waterfallView.progressBarBaseRect.set(0, 0, waterfallView.getWidth(), waterfallView.progressBarBaseImage.getHeight());
            canvas.drawBitmap(waterfallView.progressBarBaseImage, null, waterfallView.progressBarBaseRect, null);
            waterfallView.progressBarRect.set(0, 0, (float) waterfallView.getWidth() * progress / waterfallView.getTotalProgress(),
                    waterfallView.progressBarBaseImage.getHeight());
            canvas.drawBitmap(waterfallView.progressBarImage, null, waterfallView.progressBarRect, null);
        }

        /**
         * 音符是否在view中对用户可见
         */
        private boolean noteIsVisible(WaterfallNote waterfallNote) {
            // 进度（毫秒数） - 音符开始时间小于0的时候，瀑布流还是在屏幕上未开始出现的状态，这种情况下过滤掉，不绘制
            // 结束时间同理，即是说，在屏幕内的才绘制
            return progress - waterfallNote.getStartTime() > 0 && (progress - waterfallNote.getEndTime()) < waterfallView.getHeight();
        }

        /**
         * 确定是否有音块到达了屏幕底部或完全移出了屏幕，如果有，调用监听
         */
        private void handleWaterfallNoteListener() {
            WaterfallView.NoteFallListener noteFallListener = waterfallView.getNoteFallListener();
            if (noteFallListener == null) {
                return;
            }
            int[] waterfallNoteStatus = waterfallView.getWaterfallNoteStatus();
            for (int i = 0; i < waterfallView.waterfallNotes.length; i++) {
                // 音块刚下落到下边界时，触发琴键按下效果，音块刚离开时，触发琴键抬起效果
                if (waterfallNoteStatus[i] == 0 && progress - waterfallView.waterfallNotes[i].getStartTime() > waterfallView.getHeight()) {
                    noteFallListener.onNoteFallDown(waterfallView.waterfallNotes[i]);
                    waterfallView.getWaterfallNoteStatus()[i] = 1;
                } else if (waterfallNoteStatus[i] == 1 && progress - waterfallView.waterfallNotes[i].getEndTime() > waterfallView.getHeight()) {
                    noteFallListener.onNoteLeave(waterfallView.waterfallNotes[i]);
                    waterfallView.getWaterfallNoteStatus()[i] = 2;
                }
            }
        }
    }
}
