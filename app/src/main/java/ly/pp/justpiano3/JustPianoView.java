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
    private final RectF f4112d;
    private final RectF f4113e;
    private Bitmap f4114f;
    private Bitmap logo;
    private Bitmap f4117i;
    private Bitmap f4118j;
    private String f4119k = "";
    private String f4120l = "";
    private final Paint f4121m;
    private int f4122n = 0;

    public JustPianoView(Context context, JPApplication jPApplication) {
        super(context);
        float f4109a = (float) jPApplication.getWidthPixels();
        float f4110b = (float) jPApplication.getHeightPixels();
        jpapplication = jPApplication;
        f4112d = new RectF(0, 0, f4109a, f4110b);
        f4114f = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        try {
            logo = BitmapFactory.decodeStream(getResources().getAssets().open("drawable/logopiano.jpg"));
            f4117i = jPApplication.loadImage("progress_bar");
            f4118j = jPApplication.loadImage("progress_bar_base");
        } catch (IOException e) {
            e.printStackTrace();
        }
        f4113e = new RectF(0, f4110b - ((float) f4117i.getHeight()), (float) jPApplication.getWidthPixels(), f4110b);
        f4121m = new Paint();
    }

    public final void mo2760a() {
        f4114f.recycle();
        f4114f = null;
        if (logo != null) {
            logo.recycle();
            logo = null;
        }
        if (f4117i != null) {
            f4117i.recycle();
            f4117i = null;
        }
        if (f4118j != null) {
            f4118j.recycle();
            f4118j = null;
        }
    }

    public final void mo2761a(int i, String str, String str2) {
        f4122n = i;
        f4120l = str;
        f4119k = str2;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        f4121m.setARGB(255, 12, 160, 251);
        f4121m.setTextAlign(Align.RIGHT);
        f4121m.setAntiAlias(true);
        float f4123o = 20;
        f4121m.setTextSize(f4123o);
        f4121m.setTypeface(Typeface.DEFAULT_BOLD);
        int I = (jpapplication.getWidthPixels() * f4122n) / 100;
        canvas.drawBitmap(f4114f, null, f4112d, null);
        canvas.drawBitmap(logo, null, f4112d, null);
        canvas.drawBitmap(f4118j, null, f4113e, null);
        canvas.drawBitmap(f4117i, null, new Rect(I - jpapplication.getWidthPixels(), jpapplication.getHeightPixels() - f4117i.getHeight(), I, jpapplication.getHeightPixels()), null);
        canvas.drawText(f4119k + f4120l, (float) jpapplication.getWidthPixels(), (float) (jpapplication.getHeightPixels() - f4117i.getHeight()), f4121m);
    }
}
