package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

public class KeyBoard extends Activity {
    float longKeyboardHeight;
    float f4127D;
    float height;
    float f4129F;
    float f4130G;
    float f4131H;
    boolean f4132I = true;
    float f4133J = 0;
    boolean f4134K = true;
    JPApplication jpapplication;
    int f4138a = -1;
    int f4139b = -1;
    Paint f4140c;
    Paint f4141d;
    Bitmap keyboardImage;
    Bitmap whiteRightImage;
    Bitmap whiteMiddleImage;
    Bitmap whiteLeftImage;
    Bitmap blackImage;
    Bitmap longKeyboardImage;
    int f4149l = 0;
    int f4152o = 6;
    int f4153p = 5;
    float f4154q;
    float f4155r;
    double widthDiv8;
    double f4157t;
    double f4158u;
    double f4159v;
    double f4160w;
    double width;

    private static Bitmap loadImage(Context context, String str) {
        try {
            return BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/" + str + ".png"));
        } catch (IOException e) {
            return null;
        }
    }

    public final int eventPositionToTouchNoteNum(float f, float f2, int i) {
        int i2 = 2;
        int i3 = 1;
        if (f2 < longKeyboardHeight) {
            if (f2 < longKeyboardHeight) {
                switch (i) {
                    case MotionEvent.ACTION_DOWN:
                        f4133J = f;
                        if (f <= ((float) ((jpapplication.getWidthPixels() / 10) * f4152o)) || f >= ((float) ((jpapplication.getWidthPixels() / 10) * f4152o)) + (f4131H * 13)) {
                            if (f > ((float) ((jpapplication.getWidthPixels() / 10) * f4153p)) && f < ((float) ((jpapplication.getWidthPixels() / 10) * f4153p)) + (f4131H * 13)) {
                                f4141d.setColor(-1);
                                f4134K = false;
                                break;
                            }
                        }
                        f4140c.setColor(-1);
                        f4134K = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!f4134K) {
                            f4141d.setARGB(200, 255, 125, 25);
                            break;
                        }
                        f4140c.setColor(-16711936);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!f4134K) {
                            while (i2 < 9) {
                                if (f > ((float) ((jpapplication.getWidthPixels() / 10) * i2)) && f < ((float) ((jpapplication.getWidthPixels() / 10) * (i2 + 1))) + (f4131H * 13)) {
                                    f4153p = i2;
                                }
                                i2++;
                            }
                            break;
                        }
                        while (i2 < 9) {
                            if (f > ((float) ((jpapplication.getWidthPixels() / 10) * i2)) && f < ((float) ((jpapplication.getWidthPixels() / 10) * (i2 + 1))) + (f4131H * 13)) {
                                f4152o = i2;
                            }
                            i2++;
                        }
                        break;
                }
            }
        }
        if (f2 <= f4129F) {
            if (Math.abs(widthDiv8 - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 1;
            }
            if (Math.abs(f4157t - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 3;
            }
            if (Math.abs(f4158u - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 6;
            }
            if (Math.abs(f4159v - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 8;
            }
            if (Math.abs(f4160w - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 10;
            }
            if (Math.abs(width - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 13;
            }
        } else if (f2 >= f4127D && f2 <= f4130G) {
            if (Math.abs(widthDiv8 - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 15;
            }
            if (Math.abs(f4157t - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 17;
            }
            if (Math.abs(f4158u - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 20;
            }
            if (Math.abs(f4159v - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 22;
            }
            if (Math.abs(f4160w - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 24;
            }
            if (Math.abs(width - ((double) f)) < ((double) jpapplication.getBlackKeyWidth())) {
                return 27;
            }
        }
        if (f2 < f4127D) {
            while (i3 < 9) {
                if (f >= ((float) (i3 - 1)) * jpapplication.getWidthDiv8() && f < ((float) i3) * jpapplication.getWidthDiv8()) {
                    switch (i3) {
                        case 1:
                            return 0;
                        case 2:
                            return 2;
                        case 3:
                            return 4;
                        case 4:
                            return 5;
                        case 5:
                            return 7;
                        case 6:
                            return 9;
                        case 7:
                            return 11;
                        case 8:
                            return 12;
                        default:
                            break;
                    }
                }
                i3++;
            }
        } else if (f2 >= f4127D) {
            while (i3 < 9) {
                if (f >= ((float) (i3 - 1)) * jpapplication.getWidthDiv8() && f < ((float) i3) * jpapplication.getWidthDiv8()) {
                    switch (i3) {
                        case 1:
                            return 14;
                        case 2:
                            return 16;
                        case 3:
                            return 18;
                        case 4:
                            return 19;
                        case 5:
                            return 21;
                        case 6:
                            return 23;
                        case 7:
                            return 25;
                        case 8:
                            return 26;
                        default:
                            break;
                    }
                }
                i3++;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        keyboardImage = null;
        whiteRightImage = null;
        whiteMiddleImage = null;
        whiteLeftImage = null;
        blackImage = null;
        longKeyboardImage = null;
        Intent intent = new Intent();
        intent.setClass(this, PlayModeSelect.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        widthDiv8 = jpapplication.getWidthPixels() / 8f;
        f4157t = (jpapplication.getWidthPixels() / 4f);
        f4158u = (jpapplication.getWidthPixels() / 2f);
        f4159v = (jpapplication.getWidthPixels() * 0.625);
        f4160w = (jpapplication.getWidthPixels() * 0.75f);
        width = jpapplication.getWidthPixels();
        keyboardImage = KeyBoard.loadImage(this, "key_board_hd");
        whiteRightImage = KeyBoard.loadImage(this, "white_r");
        whiteMiddleImage = KeyBoard.loadImage(this, "white_m");
        whiteLeftImage = KeyBoard.loadImage(this, "white_l");
        blackImage = KeyBoard.loadImage(this, "black");
        longKeyboardImage = KeyBoard.loadImage(this, "keyboard_long");
        jpapplication.setWidthDiv8(jpapplication.getWidthPixels() / 8f);
        jpapplication.mo2724e(jpapplication.getHeightPixels() / 3.4f);
        jpapplication.setBlackKeyWidth(jpapplication.getWidthPixels() * 0.0413f);
        keyboardImage = KeyBoard.loadImage(this, "key_board_hd");
        height = (float) jpapplication.getHeightPixels();
        longKeyboardHeight = (float) longKeyboardImage.getHeight();
        float f4163z = (height - (float) longKeyboardImage.getHeight()) / 2;
        f4127D = longKeyboardHeight + f4163z;
        f4129F = longKeyboardHeight + ((f4163z * 14) / 25);
        f4130G = longKeyboardHeight + f4163z * 1.56f;
        f4131H = jpapplication.getWidthPixels() / 120f;
        f4140c = new Paint();
        f4140c.setColor(0xff00ff00);
        f4140c.setStrokeWidth(2);
        f4140c.setStyle(Style.STROKE);
        f4141d = new Paint();
        f4141d.setARGB(200, 255, 125, 25);
        f4141d.setStrokeWidth(2);
        f4141d.setStyle(Style.STROKE);
        View keyboardModeView = new KeyboardModeView(this, this);
        setContentView(keyboardModeView);
        keyboardModeView.setOnTouchListener(new KeyboardTouchListener(this, (KeyboardModeView) keyboardModeView));
    }
}
