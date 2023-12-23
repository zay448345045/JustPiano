package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayKeyBoardView extends View {
    public Map<Integer, Integer> touchNoteMap = new ConcurrentHashMap<>(16);
    public PlayView playView;
    private Bitmap keyBoardImage;
    private Rect keyBoardRect;
    private List<Rect> fireRectArray;
    private List<Rect> keyRectArray;

    public PlayKeyBoardView(Context context) {
        super(context);
    }

    public PlayKeyBoardView(Context context, PlayView playView) {
        super(context);
        this.playView = playView;
        keyBoardImage = playView.keyboardImage;
        keyBoardRect = new Rect(0, playView.whiteKeyHeight, playView.getWidth(), playView.getHeight());
        fireRectArray = playView.getFireRectArray();
        keyRectArray = playView.getKeyRectArray();
    }

    public final void updateTouchNoteNum() {
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(keyBoardImage, null, keyBoardRect, null);
        for (int i : touchNoteMap.keySet()) {
            drawKeyboardFire(canvas, fireRectArray.get(i), keyRectArray.get(i), playView, i);
        }
    }

    private void drawKeyboardFire(Canvas canvas, Rect rect, Rect rect2, PlayView playView, int i) {
        switch (i) {
            case 0, 12, 5 -> {
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyRightImage, null, rect2, null);
                return;
            }
            case 1, 10, 8, 6, 3 -> {
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.blackKeyImage, null, rect2, null);
                return;
            }
            case 2, 9, 7 -> {
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyMiddleImage, null, rect2, null);
                return;
            }
            case 4, 11 -> {
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyLeftImage, null, rect2, null);
                return;
            }
            default -> {
            }
        }
    }
}