package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import ly.pp.justpiano3.PlayView;

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
            playView.jpapplication.m3520a(canvas, fireRectArray.get(i), keyRectArray.get(i), playView, i);
        }
    }
}