package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;

import java.util.Arrays;

/**
 * 瀑布流绘制view
 */
public class WaterfallView extends SurfaceView {

    /**
     * 瀑布的宽度占键盘黑键宽度的百分比
     */
    private static final float BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.8f;

    /**
     * 瀑布流音符最短和最长的持续时间
     */
    public static final int MIN_INTERVAL_TIME = 200;
    public static final int MAX_INTERVAL_TIME = 1500;

    /**
     * 进度条
     */
    private Bitmap progressBarImage;
    private Bitmap progressBarBaseImage;

    /**
     * 进度条绘制坐标范围
     */
    private final RectF progressBarRect = new RectF();
    private RectF progressBarBaseRect;

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
    private WaterfallNoteStatusEnum[] waterfallNoteStatus;

    /**
     * 是否在滑动这个view
     */
    private boolean isScrolling;

    /**
     * 记录按下view时的横坐标
     */
    private float startX;

    /**
     * 瀑布流音块当前状态枚举：初始化状态、正在演奏状态、演奏结束状态
     */
    private enum WaterfallNoteStatusEnum {
        INIT, PLAYING, FINISH;
    }

    /**
     * 监听器
     */
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

    /**
     * 设置瀑布流监听器
     */
    public void setNoteFallListener(NoteFallListener noteFallListener) {
        this.noteFallListener = noteFallListener;
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
        // 初始化音符相关数据、状态
        this.waterfallNotes = waterfallNotes;
        // 曲谱总进度 = 曲谱最后一个音符的结束时间 + 屏幕的高度，因为要等最后一个音符绘制完的时间才是曲谱最终结束的时间
        this.totalProgress = calculateNoteMaxEndTime(waterfallNotes) + getHeight();
        this.waterfallNoteStatus = new WaterfallNoteStatusEnum[waterfallNotes.length];
        Arrays.fill(this.waterfallNoteStatus, WaterfallNoteStatusEnum.INIT);
        // 初始化进度条绘制坐标
        progressBarBaseRect = new RectF(0, 0, getWidth(), progressBarBaseImage.getHeight());
        // 开启绘制线程
        waterfallDownNotesThread = new WaterfallDownNotesThread(this);
        waterfallDownNotesThread.start();
    }

    /**
     * 计算曲谱总进度
     */
    private int calculateNoteMaxEndTime(WaterfallNote[] waterfallNotes) {
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
            return waterfallDownNotesThread.progress;
        }
        return 0;
    }

    /**
     * 设置当前播放进度，单位毫秒
     */
    public void setPlayProgress(int progress) {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread.isRunning()) {
            // 读取当前播放进度，更新偏移量即可，之后绘制时的时间会根据偏移量进行计算，后续就会在新的进度上继续绘制了
            waterfallDownNotesThread.setProgressOffset(getPlayProgress() - progress);
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
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 有触摸按下view时，记录此时按下的坐标，便于后续手指滑动了多少坐标的计算
                startX = Math.max(event.getX(), 0.0f);
                // 单独触摸按下view时，触发暂停/继续播放操作
                if (isPlaying()) {
                    pausePlay();
                } else {
                    resumePlay();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 有检测到在view上滑动手指，置正在滑动标记为true，正在滑动时将停止进行"音符到达屏幕底部或离开屏幕时"的监听
                // 检测标准为，手指的横坐标偏离原来位置10像素，此阈值待摸索
                if (isScrolling || Math.abs(Math.max(event.getX(), 0.0f) - startX) > 10) {
                    isScrolling = true;
                    // 刚刚检测到有滑动时，不管原来是在播放还是暂停，统一触发暂停下落瀑布
                    pausePlay();
                    // 根据目前手指滑动的X坐标和记录的刚按下view时的X坐标的差（就是横坐标的偏移量）占总宽度的比例，来按比例调整歌曲的进度
                    int calculatedProgress = (int) (getPlayProgress() + (Math.max(event.getX(), 0.0f) - startX) / getWidth() * totalProgress);
                    setPlayProgress(calculatedProgress);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isScrolling) {
                    // 重新设置了进度后，要重置所有音符的状态（新进度之前的音符为完成状态，新进度之后的音符为初始化状态等）
                    updateNoteStatusByProgress(getPlayProgress());
                    // 重置状态后，需要手动触发来把目前键盘上所有按下的音符进行清除，这里进行所有音符离开屏幕的监听调用
                    // 可以这么说，如果改变了进度之后不清除键盘上留有的按下的音符，改变进度继续播放后，多余的音符就一直在键盘上保持按下状态了
                    if (noteFallListener != null) {
                        for (WaterfallNote waterfallNote : waterfallNotes) {
                            noteFallListener.onNoteLeave(waterfallNote);
                        }
                    }
                    // 一切设置进度的操作都准备完成后，触发继续播放
                    resumePlay();
                    // 清空正在滑动的标记为false
                    isScrolling = false;
                }
                // 如果只是通过点按（没有滑动）导致触发的手指抬起操作，则不做任何操作
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 根据当前的进度，重新设置音符的状态
     */
    private void updateNoteStatusByProgress(int progress) {
        for (int i = 0; i < waterfallNotes.length; i++) {
            if (waterfallNotes[i].getStartTime() + getHeight() < progress) {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.FINISH;
            } else if (waterfallNotes[i].getEndTime() + getHeight() < progress) {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.PLAYING;
            } else {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.INIT;
            }
        }
    }

    /**
     * 瀑布流双缓冲绘制线程
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
         * 播放进度的偏移量，用于暂停后继续时的进度更新
         */
        @Getter
        @Setter
        private int progressPauseOffset;

        /**
         * 播放进度的偏移量，用于手动调节进度时的进度更新
         */
        @Getter
        @Setter
        private int progressOffset;

        /**
         * 瀑布流view
         */
        private final WaterfallView waterfallView;

        /**
         * 瀑布流绘制线程初始化
         */
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
            // 循环绘制，直到外部有触发终止绘制
            while (running) {
                // 暂停线程，控制最高绘制帧率
                sleepThreadFrameTime();
                // 根据系统时间，计算距离开始播放的时间点，间隔多长时间
                // 计算过程中，减掉暂停播放后继续播放所带来的时间差
                int playIntervalTime = (int) (System.currentTimeMillis() - startPlayTime - progressPauseOffset);
                // 如果处于暂停状态，则存储当前的播放进度，如果突然继续播放了，则移除存储的播放进度
                if (pause && pauseProgress == null) {
                    pauseProgress = playIntervalTime;
                } else if (!pause && pauseProgress != null) {
                    // 更新偏移量，使之后继续播放时能按照刚暂停时的进度来继续
                    // updatePauseOffset可以理解为本次暂停过程一共暂停了多少毫秒，所以需要加偏移量补偿掉这些毫秒
                    // 由于暂停的时候会人工拖动进度条操纵进度的修改，所以需要把人工操纵进度的结果progressOffset也算上
                    // 都处理结束之后再清零progressOffset
                    int updatePauseOffset = playIntervalTime - pauseProgress + progressOffset;
                    progressPauseOffset += updatePauseOffset;
                    // 接下来当前的绘制帧，调整playIntervalTime，也减掉偏移量的改变值，使当前帧的绘制顺滑起来
                    playIntervalTime -= updatePauseOffset;
                    // 清零progressOffset，如果不清零，下次拖动修改进度的时候又要变化progressOffset，会导致和前一次progressOffset的变化冲突
                    progressOffset = 0;
                    // 清除暂停时保存的当时的进度值
                    pauseProgress = null;
                }
                // 根据当前是否暂停，取出进度，进行绘制坐标计算
                // 要减掉用户手动拖动进度时设置的进度偏移时间，因为用户在暂停播放时会操纵进度的修改
                progress = (pause ? pauseProgress : playIntervalTime) - progressOffset;
                // 执行绘制，在锁canvas期间，尽可能执行最少的代码逻辑操作
                doDrawWaterfall(rightHandPaint, leftHandPaint);
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
         * 执行绘制瀑布流
         */
        private void doDrawWaterfall(Paint rightHandPaint, Paint leftHandPaint) {
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
                    drawNotes(rightHandPaint, leftHandPaint, canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 解锁画布
                if (canvas != null) {
                    waterfallView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }

        /**
         * 绘制进度条
         */
        private void drawProgressBar(Canvas canvas) {
            // 绘制进度条背景（基底）图，它包含完整的一个进度条轮廓
            canvas.drawBitmap(waterfallView.progressBarBaseImage, null, waterfallView.progressBarBaseRect, null);
            // 修改当前进度条的绘制坐标矩形位置，注意矩形的右侧坐标就是进度条百分比进度值 * view的宽度
            waterfallView.progressBarRect.set(0, 0, (float) progress / waterfallView.totalProgress * waterfallView.getWidth(),
                    waterfallView.progressBarBaseImage.getHeight());
            // 绘制进度条图片
            canvas.drawBitmap(waterfallView.progressBarImage, null, waterfallView.progressBarRect, null);
        }

        /**
         * 绘制所有应该绘制的音块
         */
        private void drawNotes(Paint rightHandPaint, Paint leftHandPaint, Canvas canvas) {
            for (WaterfallNote waterfallNote : waterfallView.waterfallNotes) {
                // 瀑布流音块当前在view内对用户可见的，才绘制
                if (noteIsVisible(waterfallNote)) {
                    canvas.drawRect(waterfallNote.getLeft(), progress - waterfallNote.getEndTime(),
                            waterfallNote.getRight(), progress - waterfallNote.getStartTime(),
                            waterfallNote.isLeftHand() ? leftHandPaint : rightHandPaint);
                }
            }
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
            // 如果没有监听器，或者目前view处于拖动来改变进度的情形，则不做任何监听操作
            if (waterfallView.noteFallListener == null || waterfallView.isScrolling) {
                return;
            }
            for (int i = 0; i < waterfallView.waterfallNotes.length; i++) {
                WaterfallNoteStatusEnum currentNoteStatus = waterfallView.waterfallNoteStatus[i];
                // 音块刚下落到下边界时，触发琴键按下效果，音块刚离开时，触发琴键抬起效果
                if (currentNoteStatus == WaterfallNoteStatusEnum.INIT
                        && progress - waterfallView.waterfallNotes[i].getStartTime() > waterfallView.getHeight()) {
                    waterfallView.noteFallListener.onNoteFallDown(waterfallView.waterfallNotes[i]);
                    waterfallView.waterfallNoteStatus[i] = WaterfallNoteStatusEnum.PLAYING;
                } else if ((currentNoteStatus == WaterfallNoteStatusEnum.INIT || currentNoteStatus == WaterfallNoteStatusEnum.PLAYING)
                        && progress - waterfallView.waterfallNotes[i].getEndTime() > waterfallView.getHeight()) {
                    waterfallView.noteFallListener.onNoteLeave(waterfallView.waterfallNotes[i]);
                    waterfallView.waterfallNoteStatus[i] = WaterfallNoteStatusEnum.FINISH;
                }
            }
        }
    }
}
