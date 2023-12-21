package ly.pp.justpiano3.thread;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.view.PlayView;

public final class LoadBackgroundsThread extends Thread {
    private final PlayView playView;
    private final SurfaceHolder surfaceholder;
    private Canvas canvas;
    private final Rect f6028n;
    private final Rect f6029o;
    private final Rect backgroundRect;
    private final PianoPlay pianoPlay;

    /**
     * 歌曲暂停时，目前的播放进度，内部保存的变量，不对外暴露
     */
    private Integer pauseProgress;

    /**
     * 处于暂停状态的时间累加，作为时间偏移进行计算
     */
    private int progressPauseTime;

    public LoadBackgroundsThread(PlayView playView, PianoPlay pianoPlay) {
        this.playView = playView;
        surfaceholder = playView.surfaceholder;
        f6028n = new Rect(0, 0, playView.getWidth(), (int) playView.halfHeightSub20);
        f6029o = new Rect(0, (int) playView.halfHeightSub20, playView.screenWidth, playView.whiteKeyHeight);
        backgroundRect = new Rect(0, 0, playView.screenWidth, playView.whiteKeyHeight);
        this.pianoPlay = pianoPlay;
    }

    @Override
    public void run() {
        long startPlayTime = System.currentTimeMillis();
        while (pianoPlay.isPlayingStart) {
            // 时间计算部分：和瀑布流原理相同，具体解释详见瀑布流
            int playIntervalTime = (int) ((System.currentTimeMillis() - startPlayTime) / GlobalSetting.INSTANCE.getNotesDownSpeed() - progressPauseTime);
            boolean isPause = !playView.startFirstNoteTouching ||
                    (GlobalSetting.INSTANCE.getLocalPlayMode() == LocalPlayModeEnum.PRACTISE && !playView.isTouchRightNote);
            if (isPause && pauseProgress == null) {
                pauseProgress = playIntervalTime;
            } else if (!isPause && pauseProgress != null) {
                int updatePauseOffset = playIntervalTime - pauseProgress;
                progressPauseTime += updatePauseOffset;
                playIntervalTime -= updatePauseOffset;
                pauseProgress = null;
            }
            int progress = isPause ? pauseProgress : playIntervalTime;
            playView.progress = progress;
            // 绘制部分
            try {
                canvas = surfaceholder.lockCanvas(backgroundRect);
                if (canvas != null) {
                    // 绘制背景图、判断线
                    canvas.drawBitmap(playView.backgroundImage, null, f6028n, null);
                    canvas.drawBitmap(playView.barImage, null, f6029o, null);
                    if (GlobalSetting.INSTANCE.getRoughLine() != 1) {
                        canvas.drawBitmap(playView.roughLineImage, null,
                                new RectF(0f, (float) (playView.getHeight() * 0.49) - playView.roughLineImage.getHeight(),
                                        (float) playView.getWidth(), (float) (playView.getHeight() * 0.49)), null);
                    }
                    // 绘制吊线和音块
                    playView.drawLineAndNotes(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 绘制进度条，（若进度已满则）处理播放完成
                playView.drawProgressAndFinish(progress, canvas);
                if (canvas != null && surfaceholder.getSurface().isValid()) {
                    try {
                        surfaceholder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                canvas = null;
            }
        }
    }
}
