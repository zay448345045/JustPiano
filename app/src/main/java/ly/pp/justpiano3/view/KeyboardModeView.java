package ly.pp.justpiano3.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.utils.MidiUtil;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyboardModeView extends View {
    private static final int DEFAULT_WHITE_KEY_NUM = 8;
    private static final int DEFAULT_WHITE_KEY_OFFSET = 21;
    private static final int MAX_WHITE_KEY_RIGHT_VALUE = 49;
    private static final int MIN_WHITE_KEY_NUM = 7;

    private static final float BLACK_KEY_HEIGHT_FACTOR = 0.57f;
    public static final float BLACK_KEY_WIDTH_FACTOR = 0.607f;

    private static final int WHITE_KEY_OFFSET_0_MIDI_PITCH = 24;

    /**
     * 一个八度内要绘制的图像种类，包括黑键、白键右侧(右上角抠掉黑键的，比如do键)、白键MIDDLE(左右都被抠掉黑键的，比如do re mi的re键)、白键左侧
     */
    private static final KeyImageTypeEnum[] KEY_IMAGE_TYPE = {
            KeyImageTypeEnum.WHITE_KEY_RIGHT, KeyImageTypeEnum.BLACK_KEY, KeyImageTypeEnum.WHITE_KEY_MIDDLE,
            KeyImageTypeEnum.BLACK_KEY, KeyImageTypeEnum.WHITE_KEY_LEFT, KeyImageTypeEnum.WHITE_KEY_RIGHT,
            KeyImageTypeEnum.BLACK_KEY, KeyImageTypeEnum.WHITE_KEY_MIDDLE, KeyImageTypeEnum.BLACK_KEY,
            KeyImageTypeEnum.WHITE_KEY_MIDDLE, KeyImageTypeEnum.BLACK_KEY, KeyImageTypeEnum.WHITE_KEY_LEFT,
    };

    private enum KeyImageTypeEnum {
        BLACK_KEY, WHITE_KEY_LEFT, WHITE_KEY_MIDDLE, WHITE_KEY_RIGHT
    }

    // midi音高转白键或黑键索引
    private static final int[] PITCH_TO_KEY_INDEX_IN_OCTAVE = {
            0, 0, 1, 1, 2, 3, 2, 4, 3, 5, 4, 6
    };

    // 位置变量
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

    /**
     * 是否在展示动画，展示动画期间不允许重新绘制、修改白键数量等操作
     */
    private boolean isAnimRunning;

    private final Map<Integer, Integer> mFingerMap = new HashMap<>();

    private MusicKeyListener musicKeyListener;

    private int whiteKeyNum;
    private int whiteKeyOffset;
    private int noteOnColor = 0xffffff;

    private Bitmap keyboardImage;
    private Bitmap whiteKeyRightImage;
    private Bitmap whiteKeyMiddleImage;
    private Bitmap whiteKeyLeftImage;
    private Bitmap blackKeyImage;

    /**
     * 颜色过滤器处理 - 对象缓存
     */
    private final Map<String, PorterDuffColorFilter> colorFilterMap = new ConcurrentHashMap<>();

    /**
     * 是否可点击琴键
     */
    private boolean pianoKeyTouchable;

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
        Bitmap keyBoardHd = SkinImageLoadUtil.loadImage(context, "key_board_hd");
        assert keyBoardHd != null;
        keyboardImage = cropKeyboardBitmap(keyBoardHd);
        whiteKeyRightImage = SkinImageLoadUtil.loadImage(context, "white_r");
        whiteKeyMiddleImage = SkinImageLoadUtil.loadImage(context, "white_m");
        whiteKeyLeftImage = SkinImageLoadUtil.loadImage(context, "white_l");
        blackKeyImage = SkinImageLoadUtil.loadImage(context, "black");
    }

    private void handleCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardModeView);
        whiteKeyNum = typedArray.getInteger(R.styleable.KeyboardModeView_whiteKeyNum, DEFAULT_WHITE_KEY_NUM);
        whiteKeyOffset = typedArray.getInteger(R.styleable.KeyboardModeView_whiteKeyOffset, DEFAULT_WHITE_KEY_OFFSET);
        pianoKeyTouchable = typedArray.getBoolean(R.styleable.KeyboardModeView_pianoKeyTouchable, true);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
        blackKeyHeight = BLACK_KEY_HEIGHT_FACTOR * viewHeight;
        makeDraw();
    }

    public void changeSkinKeyboardImage(Context context) {
        Bitmap keyBoardHd = SkinImageLoadUtil.loadImage(context, "key_board_hd");
        assert keyBoardHd != null;
        keyboardImage = cropKeyboardBitmap(keyBoardHd);
        whiteKeyRightImage = SkinImageLoadUtil.loadImage(context, "white_r");
        whiteKeyMiddleImage = SkinImageLoadUtil.loadImage(context, "white_m");
        whiteKeyLeftImage = SkinImageLoadUtil.loadImage(context, "white_l");
        blackKeyImage = SkinImageLoadUtil.loadImage(context, "black");
        postInvalidate();
    }

    /**
     * 获取所有八度键盘图的起始横坐标
     */
    public List<Float> getAllOctaveLineX() {
        List<Float> octaveLineXList = new ArrayList<>();
        for (RectF keyboardImageRect : keyboardImageRectArray) {
            octaveLineXList.add(keyboardImageRect.left);
        }
        return octaveLineXList;
    }

    /**
     * 计算好琴键各种图片要绘制的位置坐标，一般在修改view宽高，动画播放，或者修改白键数量等场景中调用
     */
    private void makeDraw() {
        // 计算黑白键的宽度
        whiteKeyWidth = viewWidth / whiteKeyNum;
        float blackKeyWidth = whiteKeyWidth * BLACK_KEY_WIDTH_FACTOR;
        // 计算一个八度内，白键的起始偏移个数
        int octaveWhiteKeyOffset = whiteKeyOffset % MidiUtil.WHITE_NOTES_PER_OCTAVE;
        // 计算最左侧八度需要绘制的左右坐标点
        float left = -octaveWhiteKeyOffset * whiteKeyWidth;
        float right = (MidiUtil.WHITE_NOTES_PER_OCTAVE - octaveWhiteKeyOffset) * whiteKeyWidth;
        float width = right - left;
        List<RectF> keyboardRectList = new ArrayList<>();
        List<RectF> whiteKeyRectList = new ArrayList<>();
        List<RectF> blackKeyRectList = new ArrayList<>();
        // 先加一个左侧看不见的八度的，防止动画穿帮
        keyboardRectList.add(new RectF(left - width, 0, right - width, viewHeight));
        int octave = 0;
        // 循环添加绘制每一个八度的位置坐标，直到触及view的右边界
        while (left < viewWidth) {
            octave++;
            // 先添加绘制键盘图的位置坐标，再叠上5个黑键
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
        // 最后添加绘制所有白键图的位置坐标
        for (float i = -octaveWhiteKeyOffset * whiteKeyWidth; i < right; i += whiteKeyWidth) {
            whiteKeyRectList.add(new RectF(i, 0, i + whiteKeyWidth, viewHeight));
        }
        // 转换为数组，后续取数组中的位置坐标值做实际的绘制
        keyboardImageRectArray = keyboardRectList.toArray(new RectF[0]);
        whiteKeyRectArray = whiteKeyRectList.toArray(new RectF[0]);
        blackKeyRectArray = blackKeyRectList.toArray(new RectF[0]);
        notesOnArray = new boolean[octave * MidiUtil.NOTES_PER_OCTAVE];
        notesOnPaintArray = new Paint[octave * MidiUtil.NOTES_PER_OCTAVE];
        for (int i = 0; i < notesOnPaintArray.length; i++) {
            notesOnPaintArray[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 先绘制所有八度的键盘图
        for (RectF rectF : keyboardImageRectArray) {
            canvas.drawBitmap(keyboardImage, null, rectF, null);
        }
        // 没有在动画播放期间的话，开始按数组中的位置坐标值，拿图片进行绘制
        if (!isAnimRunning) {
            for (int i = 0; i < notesOnArray.length; i++) {
                // 如果某个琴键处于按下状态，根据具体图片类型来绘制具体琴键按下的图片，叠在键盘图的上面
                if (notesOnArray[i]) {
                    int octaveI = i / MidiUtil.NOTES_PER_OCTAVE;
                    int noteI = i % MidiUtil.NOTES_PER_OCTAVE;
                    switch (KEY_IMAGE_TYPE[noteI]) {
                        case BLACK_KEY:
                            canvas.drawBitmap(blackKeyImage, null, blackKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI]
                                    + octaveI * MidiUtil.BLACK_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        case WHITE_KEY_LEFT:
                            canvas.drawBitmap(whiteKeyLeftImage, null, whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI]
                                    + octaveI * MidiUtil.WHITE_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        case WHITE_KEY_MIDDLE:
                            canvas.drawBitmap(whiteKeyMiddleImage, null, whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI]
                                    + octaveI * MidiUtil.WHITE_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        case WHITE_KEY_RIGHT:
                            canvas.drawBitmap(whiteKeyRightImage, null, whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI]
                                    + octaveI * MidiUtil.WHITE_NOTES_PER_OCTAVE], notesOnPaintArray[i]);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 根据midi音高值，获取对应琴键的绘制绝对坐标位置，供外界取用
     * 注意黑键和白键的绝对坐标位置的宽度有差异
     */
    @Nullable
    public RectF convertPitchToReact(int pitch) {
        int pitchInScreen = getPitchInScreen(pitch);
        // 入参的音高不在键盘view所绘制的琴键范围内，返回空
        if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.length) {
            return null;
        }
        int octaveI = pitchInScreen / MidiUtil.NOTES_PER_OCTAVE;
        int noteI = pitchInScreen % MidiUtil.NOTES_PER_OCTAVE;
        if (KEY_IMAGE_TYPE[noteI] == KeyImageTypeEnum.BLACK_KEY) {
            return blackKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI] + octaveI * MidiUtil.BLACK_NOTES_PER_OCTAVE];
        } else {
            return whiteKeyRectArray[PITCH_TO_KEY_INDEX_IN_OCTAVE[noteI] + octaveI * MidiUtil.WHITE_NOTES_PER_OCTAVE];
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!pianoKeyTouchable) {
            return true;
        }
        int action = event.getActionMasked();
        // Track individual fingers.
        int pointerIndex = event.getActionIndex();
        int id = event.getPointerId(pointerIndex);
        // Get the pointer's current position
        float x = Math.max(event.getX(pointerIndex), 0.0f);
        float y = Math.max(event.getY(pointerIndex), 0.0f);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                onFingerDown(id, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    id = event.getPointerId(i);
                    x = Math.max(event.getX(i), 0.0f);
                    y = Math.max(event.getY(i), 0.0f);
                    onFingerMove(id, x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                onFingerUp(id, x, y);
                performClick();
                break;
            case MotionEvent.ACTION_CANCEL:
                onAllFingersUp();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void onFingerDown(int id, float x, float y) {
        int pitch = -1;
        int volume = -1;
        if (y < blackKeyHeight) {
            pitch = xyToBlackPitch(x, y);
            volume = (int) (y / blackKeyHeight * MidiUtil.MAX_VOLUME);
        }
        if (pitch < 0) {
            pitch = xToWhitePitch(x);
            volume = (int) (y / viewHeight * MidiUtil.MAX_VOLUME);
        }
        fireKeyDownAndHandleListener(pitch, volume, noteOnColor);
        mFingerMap.put(id, pitch);
    }

    private void onFingerMove(int id, float x, float y) {
        Integer previousPitch = mFingerMap.get(id);
        if (previousPitch != null) {
            int pitch = -1;
            int volume = -1;
            if (y < blackKeyHeight) {
                pitch = xyToBlackPitch(x, y);
                volume = (int) (y / blackKeyHeight * MidiUtil.MAX_VOLUME);
            }
            if (pitch < 0) {
                pitch = xToWhitePitch(x);
                volume = (int) (y / viewHeight * MidiUtil.MAX_VOLUME);
            }
            // Did we change to a new key.
            if (pitch >= 0 && pitch != previousPitch) {
                fireKeyDownAndHandleListener(pitch, volume, noteOnColor);
                fireKeyUpAndHandleListener(previousPitch);
                mFingerMap.put(id, pitch);
            }
        }
    }

    private void onFingerUp(int id, float x, float y) {
        Integer previousPitch = mFingerMap.get(id);
        if (previousPitch != null) {
            fireKeyUpAndHandleListener(previousPitch);
            mFingerMap.remove(id);
        } else {
            int pitch = -1;
            if (y < blackKeyHeight) {
                pitch = xyToBlackPitch(x, y);
            }
            if (pitch < 0) {
                pitch = xToWhitePitch(x);
            }
            fireKeyUpAndHandleListener(pitch);
        }
    }

    private void onAllFingersUp() {
        // Turn off all notes.
        for (Integer pitch : mFingerMap.values()) {
            fireKeyUpAndHandleListener(pitch);
        }
        mFingerMap.clear();
    }

    private void fireKeyDownAndHandleListener(int pitch, int volume, int color) {
        if (!isAnimRunning) {
            if (musicKeyListener != null) {
                musicKeyListener.onKeyDown(pitch, Math.min(volume, MidiUtil.MAX_VOLUME));
            }
            fireKeyDown(pitch, volume, color);
        }
    }

    public void fireKeyDown(int pitch, int volume, int color) {
        if (!isAnimRunning) {
            int pitchInScreen = getPitchInScreen(pitch);
            if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.length) {
                return;
            }
            notesOnArray[pitchInScreen] = true;
            if (notesOnPaintArray[pitchInScreen] == null) {
                notesOnPaintArray[pitchInScreen] = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
            boolean blackKey = MidiUtil.isBlackKey(pitch);
            PorterDuffColorFilter porterDuffColorFilter = colorFilterMap.get(String.valueOf(color) + blackKey);
            if (porterDuffColorFilter == null) {
                // 对于黑键，使用PorterDuff.Mode.ADD模式 + 半透明叠加颜色
                // 对于白键，使用PorterDuff.Mode.MULTIPLY模式 + 不透明叠加颜色，使绘制颜色叠加看起来更为真实
                int handledColor = blackKey ? Color.argb(Color.alpha(0x7F000000), Color.red(color), Color.green(color), Color.blue(color))
                        : Color.argb(Color.alpha(0xFF000000), Color.red(color), Color.green(color), Color.blue(color));
                porterDuffColorFilter = new PorterDuffColorFilter(handledColor, blackKey ? PorterDuff.Mode.ADD : PorterDuff.Mode.MULTIPLY);
                colorFilterMap.put(String.valueOf(color) + blackKey, porterDuffColorFilter);
            }
            notesOnPaintArray[pitchInScreen].setColorFilter(porterDuffColorFilter);
            postInvalidate();
        }
    }

    private void fireKeyUpAndHandleListener(int pitch) {
        if (!isAnimRunning) {
            if (musicKeyListener != null) {
                musicKeyListener.onKeyUp(pitch);
            }
            fireKeyUp(pitch);
        }
    }

    public void fireKeyUp(int pitch) {
        if (!isAnimRunning) {
            int pitchInScreen = getPitchInScreen(pitch);
            if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.length) {
                return;
            }
            notesOnArray[pitchInScreen] = false;
            postInvalidate();
        }
    }

    /**
     * 根据midi音高，计算屏幕内已经绘制了的所有琴键的音高索引
     * 比如midi音高为60，而view中只绘制（可以理解为显示）了两个八度，那么转换后的音高就应该是以屏幕最左侧绘制的八度的do键为0算起的，一定比60小，比如可能为0，可能为12
     */
    private int getPitchInScreen(int pitch) {
        return pitch - WHITE_KEY_OFFSET_0_MIDI_PITCH - whiteKeyOffset / MidiUtil.WHITE_NOTES_PER_OCTAVE * MidiUtil.NOTES_PER_OCTAVE;
    }

    // Convert x to MIDI pitch. Ignores black keys.
    private int xToWhitePitch(float x) {
        int whiteKeyOffsetInScreen = (int) ((x / whiteKeyWidth + whiteKeyOffset));
        int octaveWhiteKeyOffset = whiteKeyOffsetInScreen % MidiUtil.WHITE_NOTES_PER_OCTAVE;
        return WHITE_KEY_OFFSET_0_MIDI_PITCH + MidiUtil.WHITE_KEY_OFFSETS[octaveWhiteKeyOffset]
                + whiteKeyOffsetInScreen / MidiUtil.WHITE_NOTES_PER_OCTAVE * MidiUtil.NOTES_PER_OCTAVE;
    }

    // Convert x to MIDI pitch. Ignores white keys.
    private int xyToBlackPitch(float x, float y) {
        for (int i = 0; i < blackKeyRectArray.length; i++) {
            if (blackKeyRectArray[i].contains(x, y)) {
                return WHITE_KEY_OFFSET_0_MIDI_PITCH + MidiUtil.BLACK_KEY_OFFSETS[i % MidiUtil.BLACK_NOTES_PER_OCTAVE]
                        + (i / MidiUtil.BLACK_NOTES_PER_OCTAVE + whiteKeyOffset / MidiUtil.WHITE_NOTES_PER_OCTAVE) * MidiUtil.NOTES_PER_OCTAVE;
            }
        }
        return -1;
    }

    public void setMusicKeyListener(MusicKeyListener musicKeyListener) {
        this.musicKeyListener = musicKeyListener;
    }

    public int getWhiteKeyNum() {
        return whiteKeyNum;
    }

    public void setWhiteKeyNum(int whiteKeyNum, int animInterval) {
        if (whiteKeyNum < MIN_WHITE_KEY_NUM || whiteKeyNum > MAX_WHITE_KEY_RIGHT_VALUE || isAnimRunning) {
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
            makeDraw();
            postInvalidate();
            return;
        }
        anim.setDuration(animInterval);
        isAnimRunning = true;
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
        makeAnimListenerAndStart(anim);
    }

    private void makeAnimListenerAndStart(ValueAnimator anim) {
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimRunning = false;
                makeDraw();
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimRunning = false;
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
        if (whiteKeyOffset < 0 || whiteKeyOffset + whiteKeyNum > MAX_WHITE_KEY_RIGHT_VALUE || isAnimRunning) {
            return;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0f, (this.whiteKeyOffset - whiteKeyOffset) * whiteKeyWidth);
        this.whiteKeyOffset = whiteKeyOffset;
        if (animInterval == 0) {
            makeDraw();
            postInvalidate();
            return;
        }
        anim.setDuration(animInterval);
        isAnimRunning = true;
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
        makeAnimListenerAndStart(anim);
    }

    /**
     * 裁剪键盘，八个白键的键盘皮肤图要分成七份（乘7/8），裁成一个八度
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    private Bitmap cropKeyboardBitmap(Bitmap bitmap) {
        float whiteKeyNum7Div8Factor = 0.875f;
        int newWidth = (int) (bitmap.getWidth() * whiteKeyNum7Div8Factor);
        return Bitmap.createBitmap(bitmap, 0, 0, newWidth, bitmap.getHeight(), null, false);
    }
}
