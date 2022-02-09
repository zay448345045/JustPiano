package ly.pp.justpiano3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyboardModeView extends View {
    private static final int NOTES_PER_OCTAVE = 12;
    private static final int WHITE_NOTES_PER_OCTAVE = 7;
    private static final int BLACK_NOTES_PER_OCTAVE = 5;
    private static final int DEFAULT_WHITE_KEY_NUM = 8;
    private static final int DEFAULT_WHITE_KEY_OFFSET = 21;
    private static final int MAX_WHITE_KEY_RIGHT_VALUE = 49;
    private static final int MIN_WHITE_KEY_NUM = 7;
    private static final int MAX_VOLUME = 127;

    private static final float BLACK_KEY_HEIGHT_FACTOR = 0.57f;
    private static final float BLACK_KEY_WIDTH_FACTOR = 0.607f;

    private static final int WHITE_KEY_OFFSET_0_MIDI_PITCH = 24;

    private static final int[] WHITE_KEY_OFFSETS = {
            0, 2, 4, 5, 7, 9, 11
    };

    private static final int[] BLACK_KEY_OFFSETS = {
            1, 3, 6, 8, 10
    };

    // -1为黑键、0为left、1为middle、2为right
    private static final int[] KEY_IMAGE_TYPE = {
            2, -1, 1, -1, 0, 2, -1, 1, -1, 1, -1, 0
    };

    // midi音高转白键或黑键索引
    private static final int[] PITCH_TO_KEY_INDEX_IN_OCTAVE = {
            0, 0, 1, 1, 2, 3, 2, 4, 3, 5, 4, 6
    };

    // Geometry.
    private float viewWidth;
    private float viewHeight;
    private float whiteKeyWidth;
    private float blackKeyHeight;

    private RectF[] keyboardImageRectArray;
    // 当前页面中显示的所在八度完整区域内的rect和是否按下的boolean标记
    private RectF[] whiteKeyRectArray;
    private RectF[] blackKeyRectArray;
    private boolean[] notesOnArray;
    private Paint[] notesOnPaintArray;
    private boolean cancelled;

    private final Map<Integer, Integer> mFingerMap = new HashMap<>();

    private final List<MusicKeyListener> mListeners = new ArrayList<>();

    private int whiteKeyNum;
    private int whiteKeyOffset;
    private int noteOnColor = -1;

    private Bitmap keyboardImage;
    private Bitmap whiteKeyRightImage;
    private Bitmap whiteKeyMiddleImage;
    private Bitmap whiteKeyLeftImage;
    private Bitmap blackKeyImage;

    /**
     * Implement this to receive keyboard events.
     */
    public interface MusicKeyListener {

        /**
         * This will be called when a key is pressed.
         */
        void onKeyDown(int pitch, int volume);

        /**
         * This will be called when a key stop to press.
         */
        void onKeyUp(int pitch);
    }

    public KeyboardModeView(Context context) {
        this(context, null);
    }

    public KeyboardModeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardModeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleCustomAttrs(context, attrs);
        Bitmap keyBoardHd = loadImage(context, "key_board_hd");
        assert keyBoardHd != null;
        keyboardImage = cropKeyboardBitmap(keyBoardHd);
        whiteKeyRightImage = loadImage(context, "white_r");
        whiteKeyMiddleImage = loadImage(context, "white_m");
        whiteKeyLeftImage = loadImage(context, "white_l");
        blackKeyImage = loadImage(context, "black");
    }

    private void handleCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardModeView);
        whiteKeyNum = typedArray.getInteger(R.styleable.KeyboardModeView_whiteKeyNum, DEFAULT_WHITE_KEY_NUM);
        whiteKeyOffset = typedArray.getInteger(R.styleable.KeyboardModeView_whiteKeyOffset, DEFAULT_WHITE_KEY_OFFSET);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
        blackKeyHeight = BLACK_KEY_HEIGHT_FACTOR * viewHeight;
        makeDraw();
    }

    public void changeImage(Context context) {
        Bitmap keyBoardHd = loadImage(context, "key_board_hd");
        assert keyBoardHd != null;
        keyboardImage = cropKeyboardBitmap(keyBoardHd);
        whiteKeyRightImage = loadImage(context, "white_r");
        whiteKeyMiddleImage = loadImage(context, "white_m");
        whiteKeyLeftImage = loadImage(context, "white_l");
        blackKeyImage = loadImage(context, "black");
        postInvalidate();
    }

    private void makeDraw() {
        whiteKeyWidth = viewWidth / whiteKeyNum;
        float blackKeyWidth = whiteKeyWidth * BLACK_KEY_WIDTH_FACTOR;
        int octaveWhiteKeyOffset = whiteKeyOffset % WHITE_NOTES_PER_OCTAVE;
        float left = -octaveWhiteKeyOffset * whiteKeyWidth;
        float right = (WHITE_NOTES_PER_OCTAVE - octaveWhiteKeyOffset) * whiteKeyWidth;
        float width = right - left;
        List<RectF> keyboardRectList = new ArrayList<>();
        List<RectF> whiteKeyRectList = new ArrayList<>();
        List<RectF> blackKeyRectList = new ArrayList<>();
        // 先加一个左侧看不见的八度的，防止动画穿帮
        keyboardRectList.add(new RectF(left - width, 0, right - width, viewHeight));
        int octave = 0;
        while (left < viewWidth) {
            octave++;
            keyboardRectList.add(new RectF(left, 0, right, viewHeight));
            float blackKeyLeft1 = left + blackKeyWidth * 1.15f;
            blackKeyRectList.add(new RectF(blackKeyLeft1, 0, blackKeyLeft1 + blackKeyWidth, blackKeyHeight));
            float blackKeyLeft2 = left + blackKeyWidth * 2.8f;
            blackKeyRectList.add(new RectF(blackKeyLeft2, 0, blackKeyLeft2 + blackKeyWidth, blackKeyHeight));
            float blackKeyLeft3 = left + blackKeyWidth * 6.09f;
            blackKeyRectList.add(new RectF(blackKeyLeft3, 0, blackKeyLeft3 + blackKeyWidth, blackKeyHeight));
            float blackKeyLeft4 = left + blackKeyWidth * 7.74f;
            blackKeyRectList.add(new RectF(blackKeyLeft4, 0, blackKeyLeft4 + blackKeyWidth, blackKeyHeight));
            float blackKeyLeft5 = left + blackKeyWidth * 9.39f;
            blackKeyRectList.add(new RectF(blackKeyLeft5, 0, blackKeyLeft5 + blackKeyWidth, blackKeyHeight));
            left += width;
            right += width;
        }
        // 再加一个右侧看不见的八度的，防止动画穿帮
        keyboardRectList.add(new RectF(left, 0, right, viewHeight));
        for (float i = -octaveWhiteKeyOffset * whiteKeyWidth; i < right; i += whiteKeyWidth) {
            whiteKeyRectList.add(new RectF(i, 0, i + whiteKeyWidth, viewHeight));
        }
        keyboardImageRectArray = keyboardRectList.toArray(new RectF[0]);
        whiteKeyRectArray = whiteKeyRectList.toArray(new RectF[0]);
        blackKeyRectArray = blackKeyRectList.toArray(new RectF[0]);
        notesOnArray = new boolean[octave * NOTES_PER_OCTAVE];
        notesOnPaintArray = new Paint[octave * NOTES_PER_OCTAVE];
        for (int i = 0; i < notesOnPaintArray.length; i++) {
            notesOnPaintArray[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (RectF rectF : keyboardImageRectArray) {
            canvas.drawBitmap(keyboardImage, null, rectF, null);
        }
        if (!cancelled) {
            for (int i = 0; i < notesOnArray.length; i++) {
                if (notesOnArray[i]) {
                    int octaveI = i / NOTES_PER_OCTAVE;
                    int noteI = i % NOTES_PER_OCTAVE;
                    switch (KEY_IMAGE_TYPE[noteI]) {
                        case -1:
                            canvas.drawBitmap(blackKeyImage, null, blackKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI] + octaveI * BLACK_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        case 0:
                            canvas.drawBitmap(whiteKeyLeftImage, null, whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI] + octaveI * WHITE_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        case 1:
                            canvas.drawBitmap(whiteKeyMiddleImage, null, whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI] + octaveI * WHITE_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        case 2:
                            canvas.drawBitmap(whiteKeyRightImage, null, whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI] + octaveI * WHITE_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getActionMasked();
        // Track individual fingers.
        int pointerIndex = event.getActionIndex();
        int id = event.getPointerId(pointerIndex);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        // Some devices can return negative x or y, which can cause an array exception.
        x = Math.max(x, 0.0f);
        y = Math.max(y, 0.0f);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                onFingerDown(id, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    id = event.getPointerId(i);
                    x = event.getX(i);
                    y = event.getY(i);
                    x = Math.max(x, 0.0f);
                    y = Math.max(y, 0.0f);
                    onFingerMove(id, x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                onFingerUp(id, x, y);
                break;
            case MotionEvent.ACTION_CANCEL:
                onAllFingersUp();
                break;
            default:
                break;
        }
        return true;
    }

    private void onFingerDown(int id, float x, float y) {
        int pitch = -1;
        int volume = -1;
        if (y < blackKeyHeight) {
            pitch = xyToBlackPitch(x, y);
            volume = (int) (y / blackKeyHeight * MAX_VOLUME);
        }
        if (pitch < 0) {
            pitch = xToWhitePitch(x);
            volume = (int) (y / viewHeight * MAX_VOLUME);
        }
        fireKeyDown(pitch, volume, noteOnColor, true);
        mFingerMap.put(id, pitch);
    }

    private void onFingerMove(int id, float x, float y) {
        Integer previousPitch = mFingerMap.get(id);
        if (previousPitch != null) {
            int pitch = -1;
            int volume = -1;
            if (y < blackKeyHeight) {
                pitch = xyToBlackPitch(x, y);
                volume = (int) (y / blackKeyHeight * MAX_VOLUME);
            }
            if (pitch < 0) {
                pitch = xToWhitePitch(x);
                volume = (int) (y / viewHeight * MAX_VOLUME);
            }
            // Did we change to a new key.
            if (pitch >= 0 && pitch != previousPitch) {
                fireKeyDown(pitch, volume, noteOnColor, true);
                fireKeyUp(previousPitch, true);
                mFingerMap.put(id, pitch);
            }
        }
    }

    private void onFingerUp(int id, float x, float y) {
        Integer previousPitch = mFingerMap.get(id);
        if (previousPitch != null) {
            fireKeyUp(previousPitch, true);
            mFingerMap.remove(id);
        } else {
            int pitch = -1;
            if (y < blackKeyHeight) {
                pitch = xyToBlackPitch(x, y);
            }
            if (pitch < 0) {
                pitch = xToWhitePitch(x);
            }
            fireKeyUp(pitch, true);
        }
    }

    private void onAllFingersUp() {
        // Turn off all notes.
        for (Integer pitch : mFingerMap.values()) {
            fireKeyUp(pitch, true);
        }
        mFingerMap.clear();
    }

    public void fireKeyDown(int pitch, int volume, int kuangColorIndex, boolean trigListener) {
        if (!cancelled) {
            if (trigListener) {
                for (MusicKeyListener listener : mListeners) {
                    listener.onKeyDown(pitch, volume);
                }
            }
            int pitchInScreen = pitch - WHITE_KEY_OFFSET_0_MIDI_PITCH - whiteKeyOffset / WHITE_NOTES_PER_OCTAVE * NOTES_PER_OCTAVE;
            if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.length) {
                return;
            }
            notesOnArray[pitchInScreen] = true;
            if (kuangColorIndex >= 0 && kuangColorIndex < Consts.kuangColorFilter.length) {
                notesOnPaintArray[pitchInScreen].setColorFilter(Consts.kuangColorFilter[kuangColorIndex]);
            }
            invalidate();
        }
    }

    public void fireKeyUp(int pitch, boolean trigListener) {
        if (!cancelled) {
            if (trigListener) {
                for (MusicKeyListener listener : mListeners) {
                    listener.onKeyUp(pitch);
                }
            }
            int pitchInScreen = pitch - WHITE_KEY_OFFSET_0_MIDI_PITCH - whiteKeyOffset / WHITE_NOTES_PER_OCTAVE * NOTES_PER_OCTAVE;
            if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.length) {
                return;
            }
            notesOnArray[pitchInScreen] = false;
            invalidate();
        }
    }

    // Convert x to MIDI pitch. Ignores black keys.
    private int xToWhitePitch(float x) {
        int whiteKeyOffsetInScreen = (int) ((x / whiteKeyWidth + whiteKeyOffset));
        int octaveWhiteKeyOffset = whiteKeyOffsetInScreen % WHITE_NOTES_PER_OCTAVE;
        return WHITE_KEY_OFFSET_0_MIDI_PITCH + WHITE_KEY_OFFSETS[octaveWhiteKeyOffset]
                + whiteKeyOffsetInScreen / WHITE_NOTES_PER_OCTAVE * NOTES_PER_OCTAVE;
    }

    // Convert x to MIDI pitch. Ignores white keys.
    private int xyToBlackPitch(float x, float y) {
        for (int i = 0; i < blackKeyRectArray.length; i++) {
            if (blackKeyRectArray[i].contains(x, y)) {
                return WHITE_KEY_OFFSET_0_MIDI_PITCH + BLACK_KEY_OFFSETS[i % BLACK_NOTES_PER_OCTAVE]
                        + (i / BLACK_NOTES_PER_OCTAVE + whiteKeyOffset / WHITE_NOTES_PER_OCTAVE) * NOTES_PER_OCTAVE;
            }
        }
        return -1;
    }

    public void addMusicKeyListener(MusicKeyListener musicKeyListener) {
        mListeners.add(musicKeyListener);
    }

    public int getWhiteKeyNum() {
        return whiteKeyNum;
    }

    public void setWhiteKeyNum(int whiteKeyNum, int animInterval) {
        if (whiteKeyNum < MIN_WHITE_KEY_NUM || whiteKeyNum > MAX_WHITE_KEY_RIGHT_VALUE || cancelled) {
            return;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(1, (float) this.whiteKeyNum / whiteKeyNum);
        boolean rightAnim = false;
        if (whiteKeyNum + whiteKeyOffset > MAX_WHITE_KEY_RIGHT_VALUE) {
            whiteKeyOffset = MAX_WHITE_KEY_RIGHT_VALUE - whiteKeyNum;
            rightAnim = true;
        }
        this.whiteKeyNum = whiteKeyNum;
        if (animInterval == 0) {
            return;
        }
        anim.setDuration(animInterval);
        cancelled = true;
        float[] oriLeft = new float[keyboardImageRectArray.length];
        float[] oriRight = new float[keyboardImageRectArray.length];
        for (int i = 0; i < keyboardImageRectArray.length; i++) {
            oriLeft[i] = keyboardImageRectArray[i].left;
            oriRight[i] = keyboardImageRectArray[i].right;
        }
        boolean finalRightAnim = rightAnim;
        anim.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            for (int i = 0; i < keyboardImageRectArray.length; i++) {
                keyboardImageRectArray[i].left = finalRightAnim ? (oriLeft[i] - viewWidth) * currentValue + viewWidth : oriLeft[i] * currentValue;
                keyboardImageRectArray[i].right = finalRightAnim ? (oriRight[i] - viewWidth) * currentValue + viewWidth : oriRight[i] * currentValue;
            }
            postInvalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cancelled = false;
                makeDraw();
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                cancelled = false;
                makeDraw();
                postInvalidate();
            }
        });
        anim.start();
    }

    public int getNoteOnColor() {
        return noteOnColor;
    }

    public void setNoteOnColor(int noteOnColor) {
        this.noteOnColor = noteOnColor;
    }

    public int getWhiteKeyOffset() {
        return whiteKeyOffset;
    }

    public void setWhiteKeyOffset(int whiteKeyOffset, int animInterval) {
        if (whiteKeyOffset < 0 || whiteKeyOffset + whiteKeyNum > MAX_WHITE_KEY_RIGHT_VALUE || cancelled) {
            return;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0f, (this.whiteKeyOffset - whiteKeyOffset) * whiteKeyWidth);
        this.whiteKeyOffset = whiteKeyOffset;
        if (animInterval == 0) {
            return;
        }
        anim.setDuration(animInterval);
        cancelled = true;
        float[] oriLeft = new float[keyboardImageRectArray.length];
        float[] oriRight = new float[keyboardImageRectArray.length];
        for (int i = 0; i < keyboardImageRectArray.length; i++) {
            oriLeft[i] = keyboardImageRectArray[i].left;
            oriRight[i] = keyboardImageRectArray[i].right;
        }
        anim.addUpdateListener(animation -> {
            float currentValue = (float) animation.getAnimatedValue();
            for (int i = 0; i < keyboardImageRectArray.length; i++) {
                keyboardImageRectArray[i].left = oriLeft[i] + currentValue;
                keyboardImageRectArray[i].right = oriRight[i] + currentValue;
            }
            postInvalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cancelled = false;
                makeDraw();
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                cancelled = false;
                makeDraw();
                postInvalidate();
            }
        });
        anim.start();
    }

    /**
     * 裁剪键盘
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    private Bitmap cropKeyboardBitmap(Bitmap bitmap) {
        float whiteKeyNum7Div8Factor = 0.875f;
        int newWidth = (int) (bitmap.getWidth() * whiteKeyNum7Div8Factor);
        return Bitmap.createBitmap(bitmap, 0, 0, newWidth, bitmap.getHeight(), null, false);
    }

    public final Bitmap loadImage(Context context, String str) {
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_list", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
            } catch (Exception e) {
                try {
                    return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            return BitmapFactory.decodeStream(getResources().getAssets().open("drawable/" + str + ".png"));
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }
}
