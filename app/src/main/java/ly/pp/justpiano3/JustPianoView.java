package ly.pp.justpiano3;

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

public class JustPianoView extends View {
    JPApplication jpapplication;
    private final RectF allScreenRect;
    private final RectF progressBarRect;
    private Bitmap logoBitmap;
    private Bitmap progressBarBitmap;
    private Bitmap progressBarBaseBitmap;
    private String loading = "";
    private String info = "";
    private final Paint paint;
    private int progress = 0;

    public JustPianoView(Context context, JPApplication jPApplication) {
        super(context);
        float widthPixels = (float) jPApplication.getWidthPixels();
        float heightPixels = (float) jPApplication.getHeightPixels();
        jpapplication = jPApplication;
        allScreenRect = new RectF(0, 0, widthPixels, heightPixels);
        try {
            logoBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("drawable/logopiano.jpg"));
            progressBarBitmap = jPApplication.loadImage("progress_bar");
            progressBarBaseBitmap = jPApplication.loadImage("progress_bar_base");
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressBarRect = new RectF(0, heightPixels - ((float) progressBarBitmap.getHeight()), (float) jPApplication.getWidthPixels(), heightPixels);
        paint = new Paint();
    }

    public final void mo2760a() {
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

    public final void mo2761a(int i, String str, String str2) {
        progress = i;
        info = str;
        loading = str2;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setARGB(240, 248, 204, 48);
        paint.setTextAlign(Align.RIGHT);
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int progressOne = (jpapplication.getWidthPixels() * progress) / 85;
        canvas.drawBitmap(logoBitmap, null, allScreenRect, null);
        canvas.drawBitmap(progressBarBaseBitmap, null, progressBarRect, null);
        canvas.drawBitmap(progressBarBitmap, null, new Rect(progressOne - jpapplication.getWidthPixels(), jpapplication.getHeightPixels() - progressBarBitmap.getHeight(), progressOne, jpapplication.getHeightPixels()), null);
        canvas.drawText(loading + info, (float) jpapplication.getWidthPixels(), (float) (jpapplication.getHeightPixels() - progressBarBitmap.getHeight()), paint);
    }
}
