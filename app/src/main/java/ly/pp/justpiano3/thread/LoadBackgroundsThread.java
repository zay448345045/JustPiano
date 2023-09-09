package ly.pp.justpiano3.thread;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.GameModeEnum;
import ly.pp.justpiano3.view.PlayView;

public final class LoadBackgroundsThread extends Thread {
    private final PlayView playView;
    private final SurfaceHolder surfaceholder;
    private Canvas canvas;
    private final float widthDiv120;
    private final float longKeyboardHeight;
    private float f6021g;
    private float f6022h;
    private final Paint f6023i;
    private final Paint f6024j;
    private final Paint f6025k;
    private final JPApplication jpapplication;
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

    public LoadBackgroundsThread(JPApplication jPApplication, PlayView playView, PianoPlay pianoPlay) {
        jpapplication = jPApplication;
        this.playView = playView;
        surfaceholder = playView.surfaceholder;
        widthDiv120 = (float) (jPApplication.getWidthPixels() / 120);
        longKeyboardHeight = (float) playView.longKeyboardImage.getHeight();
        f6023i = new Paint();
        f6023i.setARGB(200, 255, 125, 25);//深橙色
        f6023i.setStrokeWidth(2.0f);
        f6023i.setStyle(Paint.Style.STROKE);
        f6024j = new Paint();
        f6024j.setColor(0xff00ff00);
        f6025k = new Paint();
        f6025k.setARGB(255, 255, 125, 25);
        f6028n = new Rect(0, 0, jPApplication.getWidthPixels(), (int) jPApplication.getHalfHeightSub20());
        f6029o = new Rect(0, (int) jPApplication.getHalfHeightSub20(), playView.screenWidth, jPApplication.getWhiteKeyHeight());
        backgroundRect = new Rect(0, 0, playView.screenWidth, jPApplication.getWhiteKeyHeight());
        this.pianoPlay = pianoPlay;
    }

    @Override
    public void run() {
        long startPlayTime = System.currentTimeMillis();
        while (pianoPlay.isPlayingStart) {
            // 时间计算部分：和瀑布流原理相同，具体解释详见瀑布流
            int playIntervalTime = (int) ((System.currentTimeMillis() - startPlayTime) / GlobalSetting.INSTANCE.getNotesDownSpeed() - progressPauseTime);
            boolean isPause = !pianoPlay.playView.startFirstNoteTouching ||
                    (jpapplication.getGameMode() == GameModeEnum.PRACTISE.getCode() && !pianoPlay.playView.isTouchRightNote);
            if (isPause && pauseProgress == null) {
                pauseProgress = playIntervalTime;
            } else if (!isPause && pauseProgress != null) {
                int updatePauseOffset = playIntervalTime - pauseProgress;
                progressPauseTime += updatePauseOffset;
                playIntervalTime -= updatePauseOffset;
                pauseProgress = null;
            }
            jpapplication.setAnimPosition(isPause ? pauseProgress : playIntervalTime);
            // 绘制部分
            try {
                canvas = surfaceholder.lockCanvas(backgroundRect);
                if (canvas != null) {
                    canvas.drawBitmap(playView.backgroundImage, null, f6028n, null);
                    canvas.drawBitmap(playView.barImage, null, f6029o, null);
                    if (GlobalSetting.INSTANCE.getRoughLine() != 1) {
                        canvas.drawBitmap(playView.roughLineImage, null, new RectF(0.0f, (float) (jpapplication.getHeightPixels() * 0.49) - playView.roughLineImage.getHeight(), (float) jpapplication.getWidthPixels(), (float) (jpapplication.getHeightPixels() * 0.49)), null);
                    }
                    if (jpapplication.getGameMode() != GameModeEnum.HEAR.getCode()) {  // 不是欣赏模式
                        playView.mo2930b(canvas);
                    } else {
                        playView.mo2931c(canvas);
                    }
                }
                if (canvas != null && GlobalSetting.INSTANCE.getLoadLongKeyboard()) {
                    canvas.drawBitmap(playView.longKeyboardImage, null, new RectF(0.0f, 0.0f, (float) jpapplication.getWidthPixels(), longKeyboardHeight), null);
                    canvas.drawRoundRect(new RectF((float) (((jpapplication.getWidthPixels() / 10) * playView.noteMod12) + 1), 1.0f, (((float) ((jpapplication.getWidthPixels() / 10) * playView.noteMod12)) + (13.0f * widthDiv120)) + 1.0f, 29.0f), 3.0f, 3.0f, f6023i);
                    switch (playView.currentPlayNote.noteValue % 12) {
                        case 0:
                        case 2:
                        case 4:
                        case 5:
                        case 7:
                        case 9:
                        case 11:
                            f6021g = 20.0f;
                            break;
                        case 1:
                        case 3:
                        case 6:
                        case 8:
                        case 10:
                            f6021g = 10.0f;
                            break;
                    }
                    switch (playView.f4813n % 12) {
                        case 0:
                        case 2:
                        case 4:
                        case 5:
                        case 11:
                        case 9:
                        case 7:
                            f6022h = 20.0f;
                            break;
                        case 1:
                        case 3:
                        case 6:
                        case 10:
                        case 8:
                            f6022h = 10.0f;
                            break;
                    }
                    canvas.drawArc(new RectF(((float) playView.currentPlayNote.noteValue) * widthDiv120, f6021g, ((float) (playView.currentPlayNote.noteValue + 1)) * widthDiv120, f6021g + widthDiv120), 0.0f, 360.0f, false, f6024j);
                    canvas.drawArc(new RectF(((float) playView.f4813n) * widthDiv120, f6022h, ((float) (playView.f4813n + 1)) * widthDiv120, f6022h + widthDiv120), 0.0f, 360.0f, false, f6025k);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                playView.mo2929a(canvas);
                if (canvas != null && surfaceholder.getSurface().isValid()) {
                    surfaceholder.unlockCanvasAndPost(canvas);
                }
                canvas = null;
            }
        }
    }
}
