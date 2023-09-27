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

import java.io.IOException;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class JustPianoView extends View {
    public JPApplication jpapplication;
    private final RectF allScreenRect;
    private final RectF progressBarRect;
    private Bitmap logoBitmap;
    private Bitmap progressBarBitmap;
    private Bitmap progressBarBaseBitmap;
    private String loading = "";
    private String info = "";
    private final Paint paint;
    private final Rect progressBarDynamicRect;
    private int progress = 0;

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

    public JustPianoView(Context context, JPApplication jPApplication) {
        super(context);
        jpapplication = jPApplication;
        allScreenRect = new RectF(0, 0, jpapplication.getWidthPixels(), jpapplication.getHeightPixels());
        try {
            logoBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("drawable/logopiano.jpg"));
            progressBarBitmap = ImageLoadUtil.loadSkinImage(context, "progress_bar");
            progressBarBaseBitmap = ImageLoadUtil.loadSkinImage(context, "progress_bar_base");
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressBarRect = new RectF(0, jpapplication.getHeightPixels() - ((float) progressBarBitmap.getHeight()),
                jPApplication.getWidthPixels(), jpapplication.getHeightPixels());
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
        this.info = (info == null ? "" : info);
        this.loading = loading;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int progressOne = (jpapplication.getWidthPixels() * progress) / 85;
        canvas.drawBitmap(logoBitmap, null, allScreenRect, null);
        canvas.drawBitmap(progressBarBaseBitmap, null, progressBarRect, null);

        progressBarDynamicRect.set(progressOne - jpapplication.getWidthPixels(), jpapplication.getHeightPixels() - progressBarBitmap.getHeight(), progressOne, jpapplication.getHeightPixels());
        canvas.drawBitmap(progressBarBitmap, null, progressBarDynamicRect, null);

        canvas.drawText(loading + info, (float) jpapplication.getWidthPixels(), (float) (jpapplication.getHeightPixels() - progressBarBitmap.getHeight()), paint);
    }
}
