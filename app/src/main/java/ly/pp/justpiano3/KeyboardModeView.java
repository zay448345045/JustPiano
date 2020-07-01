package ly.pp.justpiano3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

final class KeyboardModeView extends View {
    final KeyBoard keyboard;
    int f5117a = -1;
    int f5118b = -1;

    public KeyboardModeView(Context context) {
        super(context);
        keyboard = null;
    }

    public KeyboardModeView(KeyBoard keyBoard, Context context) {
        super(context);
        keyboard = keyBoard;
    }

    public final void mo3045a(int i, int i2) {
        f5118b = i2;
        f5117a = i;
        postInvalidate();
    }

    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (keyboard != null) {
            boolean z = keyboard.f4132I;
            canvas.drawBitmap(keyboard.keyboardImage, null, new Rect(0, (int) keyboard.longKeyboardHeight, keyboard.jpapplication.getWidthPixels(), (int) keyboard.f4127D), null);
            canvas.drawBitmap(keyboard.keyboardImage, null, new Rect(0, (int) keyboard.f4127D, keyboard.jpapplication.getWidthPixels(), (int) keyboard.height), null);
            if (z) {
                switch (f5117a) {
                    case 0:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(0, (int) keyboard.longKeyboardHeight, (int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.f4127D), null);
                        break;
                    case 1:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) (keyboard.jpapplication.getWidthDiv8() - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) (keyboard.jpapplication.getWidthDiv8() + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 2:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect((int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.f4127D), null);
                        break;
                    case 3:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 2) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 2) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 4:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.f4127D), null);
                        break;
                    case 5:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.f4127D), null);
                        break;
                    case 6:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 4) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 4) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 7:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.f4127D), null);
                        break;
                    case 8:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 5) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 5) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 9:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.f4127D), null);
                        break;
                    case 10:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 6) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 6) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 11:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.f4127D), null);
                        break;
                    case 12:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 8, (int) keyboard.f4127D), null);
                        break;
                    case 13:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 8) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 8) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 14:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(0, (int) keyboard.f4127D, (int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.height), null);
                        break;
                    case 15:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) (keyboard.jpapplication.getWidthDiv8() - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) (keyboard.jpapplication.getWidthDiv8() + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 16:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect((int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.height), null);
                        break;
                    case 17:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 2) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 2) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 18:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.height), null);
                        break;
                    case 19:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.height), null);
                        break;
                    case 20:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 4) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 4) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 21:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.height), null);
                        break;
                    case 22:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 5) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 5) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 23:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.height), null);
                        break;
                    case 24:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 6) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 6) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 25:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.height), null);
                        break;
                    case 26:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 8, (int) keyboard.height), null);
                        break;
                    case 27:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 8) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 8) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                }
                switch (f5118b) {
                    case 0:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(0, (int) keyboard.longKeyboardHeight, (int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.f4127D), null);
                        break;
                    case 1:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) (keyboard.jpapplication.getWidthDiv8() - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) (keyboard.jpapplication.getWidthDiv8() + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 2:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect((int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.f4127D), null);
                        break;
                    case 3:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 2) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 2) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 4:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.f4127D), null);
                        break;
                    case 5:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.f4127D), null);
                        break;
                    case 6:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 4) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 4) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 7:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.f4127D), null);
                        break;
                    case 8:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 5) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 5) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 9:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.f4127D), null);
                        break;
                    case 10:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 6) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 6) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 11:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.f4127D), null);
                        break;
                    case 12:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.longKeyboardHeight, ((int) keyboard.jpapplication.getWidthDiv8()) * 8, (int) keyboard.f4127D), null);
                        break;
                    case 13:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 8) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.longKeyboardHeight, (int) ((keyboard.jpapplication.getWidthDiv8() * 8) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4129F + 5)), null);
                        break;
                    case 14:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(0, (int) keyboard.f4127D, (int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.height), null);
                        break;
                    case 15:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) (keyboard.jpapplication.getWidthDiv8() - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) (keyboard.jpapplication.getWidthDiv8() + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 16:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect((int) keyboard.jpapplication.getWidthDiv8(), (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.height), null);
                        break;
                    case 17:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 2) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 2) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 18:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 2, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.height), null);
                        break;
                    case 19:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 3, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.height), null);
                        break;
                    case 20:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 4) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 4) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 21:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 4, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.height), null);
                        break;
                    case 22:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 5) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 5) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 23:
                        canvas.drawBitmap(keyboard.whiteMiddleImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 5, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.height), null);
                        break;
                    case 24:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 6) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 6) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                    case 25:
                        canvas.drawBitmap(keyboard.whiteLeftImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 6, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.height), null);
                        break;
                    case 26:
                        canvas.drawBitmap(keyboard.whiteRightImage, null, new Rect(((int) keyboard.jpapplication.getWidthDiv8()) * 7, (int) keyboard.f4127D, ((int) keyboard.jpapplication.getWidthDiv8()) * 8, (int) keyboard.height), null);
                        break;
                    case 27:
                        canvas.drawBitmap(keyboard.blackImage, null, new Rect((int) ((keyboard.jpapplication.getWidthDiv8() * 8) - keyboard.jpapplication.getBlackKeyWidth()), (int) keyboard.f4127D, (int) ((keyboard.jpapplication.getWidthDiv8() * 8) + keyboard.jpapplication.getBlackKeyWidth()), (int) (keyboard.f4130G + 5)), null);
                        break;
                }
            }
            canvas.drawRoundRect(new RectF(0, keyboard.longKeyboardHeight - 1, (float) keyboard.jpapplication.getWidthPixels(), keyboard.f4127D - 1), 5, 5, keyboard.f4140c);
            canvas.drawRoundRect(new RectF(0, keyboard.f4127D, (float) keyboard.jpapplication.getWidthPixels(), keyboard.height), 5, 5, keyboard.f4141d);
            canvas.drawBitmap(keyboard.longKeyboardImage, null, new RectF(0, 0, (float) keyboard.jpapplication.getWidthPixels(), keyboard.longKeyboardHeight), null);
            canvas.drawRoundRect(new RectF((float) ((keyboard.jpapplication.getWidthPixels() / 10) * keyboard.f4152o), 1, ((float) ((keyboard.jpapplication.getWidthPixels() / 10) * keyboard.f4152o)) + (13 * keyboard.f4131H), 29), 3, 3, keyboard.f4140c);
            canvas.drawRoundRect(new RectF((float) ((keyboard.jpapplication.getWidthPixels() / 10) * keyboard.f4153p), 1, ((float) ((keyboard.jpapplication.getWidthPixels() / 10) * keyboard.f4153p)) + (13 * keyboard.f4131H), 29), 3, 3, keyboard.f4141d);

        }
    }
}
