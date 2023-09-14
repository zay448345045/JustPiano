package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyBoardView extends View {
    public Map<Integer, Integer> touchNoteSet = new ConcurrentHashMap<>(16);
    public PlayView playView;
    private Bitmap keyBoardImage;
    private Rect keyBoardRect;
    private List<Rect> fireRectArray;
    private List<Rect> keyRectArray;

    public KeyBoardView(Context context) {
        super(context);
    }

    public KeyBoardView(Context context, PlayView playView) {
        super(context);
        this.playView = playView;
        keyBoardImage = playView.keyboardImage;
        keyBoardRect = new Rect(0, playView.jpapplication.getWhiteKeyHeight(), playView.jpapplication.getWidthPixels(), playView.jpapplication.getHeightPixels());
        fireRectArray = playView.jpapplication.getFireRectArray(playView);
        keyRectArray = playView.jpapplication.getKeyRectArray();
    }

    public final void updateTouchNoteNum() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(keyBoardImage, null, keyBoardRect, null);
        for (int i : touchNoteSet.keySet()) {
            m3520a(canvas, fireRectArray.get(i), keyRectArray.get(i), playView, i);
        }
    }

    private void m3520a(Canvas canvas, Rect rect, Rect rect2, PlayView playView, int i) {
        switch (i) {
            case 0:
            case 12:
            case 5:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyRightImage, null, rect2, null);
                return;
            case 1:
            case 10:
            case 8:
            case 6:
            case 3:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.blackKeyImage, null, rect2, null);
                return;
            case 2:
            case 9:
            case 7:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyMiddleImage, null, rect2, null);
                return;
            case 4:
            case 11:
                canvas.drawBitmap(playView.fireImage, null, rect, null);
                canvas.drawBitmap(playView.whiteKeyLeftImage, null, rect2, null);
                return;
            default:
        }
    }
}