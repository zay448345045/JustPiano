package ly.pp.justpiano3;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final class KeyboardTouchListener implements OnTouchListener {
    private final KeyBoard keyBoard;
    private final KeyboardModeView keyboardModeView;

    KeyboardTouchListener(KeyBoard keyBoard, KeyboardModeView keyboardModeView) {
        this.keyBoard = keyBoard;
        this.keyboardModeView = keyboardModeView;
    }

    @Override
    public final boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float x;
        float y;
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                keyBoard.f4154q = motionEvent.getX();
                keyBoard.f4155r = motionEvent.getY();
                keyBoard.f4138a = keyBoard.eventPositionToTouchNoteNum(keyBoard.f4154q, keyBoard.f4155r, MotionEvent.ACTION_DOWN);
                if (keyBoard.f4138a > 13) {
                    action = (keyBoard.f4138a - 14) + ((keyBoard.f4153p + keyBoard.jpapplication.getBadu()) * 12);
                    if (keyBoard.f4155r >= keyBoard.longKeyboardHeight) {
                        keyBoard.jpapplication.playSound(action, 64);
                    }
                } else {
                    action = keyBoard.f4138a + ((keyBoard.f4152o + keyBoard.jpapplication.getBadu()) * 12);
                    if (keyBoard.f4155r >= keyBoard.longKeyboardHeight) {
                        keyBoard.jpapplication.playSound(action, 64);
                    }
                }
                keyBoard.f4149l = keyBoard.f4138a;
                break;
            case MotionEvent.ACTION_UP:
                keyBoard.f4154q = motionEvent.getX();
                keyBoard.f4155r = motionEvent.getY();
                keyBoard.eventPositionToTouchNoteNum(keyBoard.f4154q, keyBoard.f4155r, MotionEvent.ACTION_UP);
                keyBoard.f4138a = -1;
                keyBoard.f4139b = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                x = motionEvent.getX();
                y = motionEvent.getY();
                keyBoard.f4138a = keyBoard.eventPositionToTouchNoteNum(x, y, MotionEvent.ACTION_MOVE);
                if (y > keyBoard.longKeyboardHeight && keyBoard.f4138a != keyBoard.f4149l) {
                    if (keyBoard.f4138a > 13) {
                        action = (keyBoard.f4138a - 14) + ((keyBoard.f4153p + keyBoard.jpapplication.getBadu()) * 12);
                        if (y >= keyBoard.longKeyboardHeight) {
                            keyBoard.jpapplication.playSound(action, 64);
                        }
                    } else {
                        action = keyBoard.f4138a + ((keyBoard.f4152o + keyBoard.jpapplication.getBadu()) * 12);
                        if (y >= keyBoard.longKeyboardHeight) {
                            keyBoard.jpapplication.playSound(action, 64);
                        }
                    }
                    keyBoard.f4149l = keyBoard.f4138a;
                    break;
                }
//            case MotionEvent.ACTION_POINTER_DOWN:
//                action = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> 8;
//                y = motionEvent.getX(action);
//                x = motionEvent.getY(action);
//                keyBoard.f4139b = keyBoard.eventPositionToTouchNoteNum(y, x, MotionEvent.ACTION_POINTER_DOWN);
//                int q;
//                if (keyBoard.f4139b <= 13) {
//                    q = keyBoard.f4139b + ((keyBoard.f4152o + keyBoard.jpapplication.getBadu()) * 12);
//                    if (x >= keyBoard.longKeyboardHeight) {
//                        keyBoard.f4136M = keyBoard.jpapplication.playSound(q, 100f);
//                        break;
//                    }
//                }
//                q = (keyBoard.f4139b - 14) + ((keyBoard.f4153p + keyBoard.jpapplication.getBadu()) * 12);
//                if (x >= keyBoard.longKeyboardHeight) {
//                    keyBoard.f4135L = keyBoard.jpapplication.playSound(q, 100f);
//                    break;
//                }
//                break;
        }
        keyboardModeView.mo3045a(keyBoard.f4138a, keyBoard.f4139b);
        return true;
    }
}
