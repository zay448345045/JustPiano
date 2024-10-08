package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.IOException;

import ly.pp.justpiano3.utils.ImageLoadUtil;

public class JustPianoView extends View {
    private RectF allScreenRect;
    private RectF progressBarRect;
    private Bitmap logoBitmap;
    private Bitmap progressBarBitmap;
    private Bitmap progressBarBaseBitmap;
    private String loading;
    private String info;
    private final Paint paint;
    private final Rect progressBarDynamicRect;
    private int progress;

    // 在构造函数或初始化块中设置Paint的属性
    {
        progressBarDynamicRect = new Rect();
        paint = new Paint();
        paint.setARGB(240, 248, 204, 48);
        paint.setTextAlign(Align.RIGHT);
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public JustPianoView(Context context) {
        super(context);
    }

    private void init() {
        allScreenRect = new RectF(0, 0, getWidth(), getHeight());
        try {
            logoBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("drawable/logopiano.webp"));
            progressBarBitmap = ImageLoadUtil.loadSkinImage(getContext(), "progress_bar");
            progressBarBaseBitmap = ImageLoadUtil.loadSkinImage(getContext(), "progress_bar_base");
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressBarRect = new RectF(0, getHeight() - ((float) progressBarBitmap.getHeight()),
                getWidth(), getHeight());
    }

    public final void destroy() {
        if (logoBitmap != null) {
            logoBitmap.recycle();
            logoBitmap = null;
        }
        if (progressBarBitmap != null) {
            progressBarBitmap.recycle();
            progressBarBitmap = null;
        }
        if (progressBarBaseBitmap != null) {
            progressBarBaseBitmap.recycle();
            progressBarBaseBitmap = null;
        }
    }

    public final void updateProgressAndInfo(int progress, String info, String loading) {
        this.progress = progress;
        this.info = info == null ? "" : info;
        this.loading = loading == null ? "" : loading;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            init();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (allScreenRect == null) {
            init();
        }
        int progressOne = (getWidth() * progress) / 88;
        canvas.drawBitmap(logoBitmap, null, allScreenRect, null);
        canvas.drawBitmap(progressBarBaseBitmap, null, progressBarRect, null);
        progressBarDynamicRect.set(progressOne - getWidth(), getHeight() - progressBarBitmap.getHeight(), progressOne, getHeight());
        canvas.drawBitmap(progressBarBitmap, null, progressBarDynamicRect, null);
        canvas.drawText(loading + info, (float) getWidth(), (float) (getHeight() - progressBarBitmap.getHeight()), paint);
    }
}
