package ly.pp.justpiano3.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ly.pp.justpiano3.R
import ly.pp.justpiano3.midi.MidiUtil
import ly.pp.justpiano3.utils.ImageLoadUtil
import ly.pp.justpiano3.utils.UnitConvertUtil

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    companion object {
        // 黑键宽度占白键宽度的比例
        const val BLACK_KEY_WIDTH_FACTOR = 0.65f

        // 黑键高度占白键高度的比例
        const val BLACK_KEY_HEIGHT_FACTOR = 0.57f

        // 每个八度的音符数量、白键数量、黑键数量
        const val NOTES_PER_OCTAVE = 12
        const val WHITE_NOTES_PER_OCTAVE = 7
        const val BLACK_NOTES_PER_OCTAVE = 5

        // 自定义属性默认值
        private const val DEFAULT_WHITE_KEY_NUM = 8
        private const val DEFAULT_WHITE_KEY_OFFSET = 28
        private const val MIN_WHITE_KEY_OFFSET = 5
        private const val MIN_WHITE_KEY_NUM = 7
        private const val MAX_WHITE_KEY_NUM = 52
        private const val WHITE_KEY_OFFSET_0_MIDI_PITCH = 12

        // 每个八度内白键的索引
        val WHITE_KEY_OFFSET = intArrayOf(0, 2, 4, 5, 7, 9, 11)

        // 每个八度内黑键的索引
        val BLACK_KEY_OFFSET = intArrayOf(1, 3, 6, 8, 10)

        /**
         * 一个八度内要绘制的图像种类，包括黑键、白键右侧(右上角抠掉黑键的，比如do键)、白键MIDDLE(左右都被抠掉黑键的，比如do re mi的re键)、白键左侧
         */
        val KEY_IMAGE_TYPE = arrayOf(
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_RIGHT,
            KeyImageTypeEnum.BLACK_KEY,
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT_AND_RIGHT,
            KeyImageTypeEnum.BLACK_KEY,
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT,
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_RIGHT,
            KeyImageTypeEnum.BLACK_KEY,
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT_AND_RIGHT,
            KeyImageTypeEnum.BLACK_KEY,
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT_AND_RIGHT,
            KeyImageTypeEnum.BLACK_KEY,
            KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT
        )

        // midi音高转白键或黑键索引
        val OCTAVE_PITCH_TO_KEY_INDEX = intArrayOf(0, 0, 1, 1, 2, 3, 2, 4, 3, 5, 4, 6)

        // 按键标签用于测量标签宽度的文字，取最长的文字sol
        private const val OCTAVE_TAG_WORD_SAMPLE = "sol"
        private const val OCTAVE_C = "C"

        // 按键标签显示音名文字
        private val PITCH_NAME_ARRAY = arrayOf("C", "D", "E", "F", "G", "A", "B")

        // 按键标签显示唱名文字
        private val SYLLABLE_NAME_ARRAY = arrayOf("do", "re", "mi", "fa", "sol", "la", "si")
    }

    // 位置变量
    private var viewWidth = 0f
    private var viewHeight = 0f
    private var whiteKeyWidth = 0f
    private var blackKeyHeight = 0f
    private lateinit var keyboardImageRectArray: Array<RectF>

    // 当前页面中显示的所在八度完整区域内的rect和是否按下的boolean标记
    lateinit var whiteKeyRectArray: Array<RectF>
        private set
    lateinit var blackKeyRectArray: Array<RectF>
        private set
    lateinit var notesOnArray: BooleanArray
        private set
    private lateinit var notesOnPaintArray: Array<Paint?>
    private val mFingerMap: MutableMap<Int, Byte> = HashMap()
    var keyboardListener: KeyboardListener? = null
    var whiteKeyNum = 0
        private set
    var whiteKeyOffset = 0
        private set
    var noteOnColor: Int? = null

    // 按键标签种类
    var octaveTagType = OctaveTagType.NONE

    // 按键标签种类枚举
    enum class OctaveTagType {
        NONE, OCTAVE_C, PITCH_NAME, SYLLABLE_NAME
    }

    // 键盘上绘制的标签文字paint
    private var keyboardTextPaint = Paint()

    // 按键标签显示的文字大小
    private var maxOctaveTagFontSize = 15f

    // 素材图片
    private var keyboardImage: Bitmap
    private var whiteKeyPressWithoutRightImage: Bitmap
    private var whiteKeyPressWithoutLeftAndRightImage: Bitmap
    private var whiteKeyPressWithoutLeftImage: Bitmap
    private var blackKeyPressImage: Bitmap

    // 经过裁减处理的素材：88键钢琴的最右侧纯白键及其按压效果
    private var pureWhiteKeyImage: Bitmap
    private var pureWhiteKeyPressImage: Bitmap

    // 经过裁减处理的素材：88键钢琴的最左侧键
    private var leftmostWhiteKeyImage: Bitmap

    /**
     * 颜色过滤器处理 - 对象缓存
     */
    private val colorFilterMap: MutableMap<String, PorterDuffColorFilter> = HashMap()

    /**
     * 是否可点击琴键
     */
    private var pianoKeyTouchable = true

    /**
     * Implement this to receive keyboard events.
     */
    interface KeyboardListener {
        /**
         * This will be called when a key is pressed.
         */
        fun onKeyDown(pitch: Byte, volume: Byte)

        /**
         * This will be called when a key stop to press.
         */
        fun onKeyUp(pitch: Byte)
    }

    enum class KeyImageTypeEnum {
        BLACK_KEY, WHITE_KEY_WITHOUT_LEFT, WHITE_KEY_WITHOUT_LEFT_AND_RIGHT, WHITE_KEY_WITHOUT_RIGHT
    }

    init {
        handleCustomAttrs(context, attrs)
        keyboardImage = cropKeyboardBitmap(ImageLoadUtil.loadSkinImage(context, "key_board_hd"))
        whiteKeyPressWithoutRightImage = ImageLoadUtil.loadSkinImage(context, "white_r")
        whiteKeyPressWithoutLeftAndRightImage = ImageLoadUtil.loadSkinImage(context, "white_m")
        whiteKeyPressWithoutLeftImage = ImageLoadUtil.loadSkinImage(context, "white_l")
        blackKeyPressImage = ImageLoadUtil.loadSkinImage(context, "black")
        pureWhiteKeyImage = cropPureWhiteKeyBitmap(keyboardImage)
        pureWhiteKeyPressImage = cropPureWhiteKeyPressBitmap(
            whiteKeyPressWithoutLeftImage, whiteKeyPressWithoutRightImage
        )
        leftmostWhiteKeyImage = cropLeftmostWhiteKeyBitmap(keyboardImage)
    }

    private fun handleCustomAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView)
        whiteKeyNum =
            typedArray.getInteger(R.styleable.KeyboardView_whiteKeyNum, DEFAULT_WHITE_KEY_NUM)
        whiteKeyOffset =
            typedArray.getInteger(R.styleable.KeyboardView_whiteKeyOffset, DEFAULT_WHITE_KEY_OFFSET)
        pianoKeyTouchable = typedArray.getBoolean(R.styleable.KeyboardView_pianoKeyTouchable, true)
        val octaveTagTypeInt = typedArray.getInteger(R.styleable.KeyboardView_octaveTagType, 0)
        octaveTagType = OctaveTagType.values().getOrElse(octaveTagTypeInt) { OctaveTagType.NONE }
        maxOctaveTagFontSize = UnitConvertUtil.sp2px(context, maxOctaveTagFontSize).toFloat()
        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        blackKeyHeight = BLACK_KEY_HEIGHT_FACTOR * viewHeight
        makeDraw()
    }

    fun changeSkinKeyboardImage(context: Context?) {
        keyboardImage = cropKeyboardBitmap(ImageLoadUtil.loadSkinImage(context, "key_board_hd"))
        whiteKeyPressWithoutRightImage = ImageLoadUtil.loadSkinImage(context, "white_r")
        whiteKeyPressWithoutLeftAndRightImage = ImageLoadUtil.loadSkinImage(context, "white_m")
        whiteKeyPressWithoutLeftImage = ImageLoadUtil.loadSkinImage(context, "white_l")
        blackKeyPressImage = ImageLoadUtil.loadSkinImage(context, "black")
        pureWhiteKeyImage = cropPureWhiteKeyBitmap(keyboardImage)
        pureWhiteKeyPressImage = cropPureWhiteKeyPressBitmap(
            whiteKeyPressWithoutLeftImage,
            whiteKeyPressWithoutRightImage
        )
        leftmostWhiteKeyImage = cropLeftmostWhiteKeyBitmap(keyboardImage)
        postInvalidate()
    }

    /**
     * 获取所有八度键盘图的起始横坐标
     */
    val allOctaveLineX: List<Float>
        get() {
            val octaveLineXList: MutableList<Float> = ArrayList()
            for (keyboardImageRect in keyboardImageRectArray) {
                octaveLineXList.add(keyboardImageRect.left)
            }
            return octaveLineXList
        }

    /**
     * 计算好琴键各种图片要绘制的位置坐标，一般在修改view宽高，动画播放，或者修改白键数量等场景中调用
     */
    private fun makeDraw() {
        // 计算黑白键的宽度
        whiteKeyWidth = viewWidth / whiteKeyNum
        val blackKeyWidth = whiteKeyWidth * BLACK_KEY_WIDTH_FACTOR
        // 计算显示标签的文字大小
        if (octaveTagType != OctaveTagType.NONE) {
            calculateTextSize(whiteKeyWidth)
        }
        // 计算一个八度内，白键的起始偏移个数
        val octaveWhiteKeyOffset = whiteKeyOffset % WHITE_NOTES_PER_OCTAVE
        // 计算最左侧八度需要绘制的左右坐标点
        var left = -octaveWhiteKeyOffset * whiteKeyWidth
        var right = (WHITE_NOTES_PER_OCTAVE - octaveWhiteKeyOffset) * whiteKeyWidth
        val width = right - left
        val keyboardRectList: MutableList<RectF> = ArrayList()
        val whiteKeyRectList: MutableList<RectF> = ArrayList()
        val blackKeyRectList: MutableList<RectF> = ArrayList()
        // 先加一个左侧看不见的八度的，防止动画穿帮
        keyboardRectList.add(RectF(left - width, 0f, right - width, viewHeight))
        var octaveCount = 0
        // 循环添加绘制每一个八度的位置坐标
        while (left < viewWidth) {
            octaveCount++
            // 先添加绘制键盘图的位置坐标，再叠上5个黑键
            keyboardRectList.add(RectF(left, 0f, right, viewHeight))
            val blackKeyLeft1 = left + blackKeyWidth * 1.036f
            blackKeyRectList.add(
                RectF(
                    blackKeyLeft1,
                    0f,
                    blackKeyLeft1 + blackKeyWidth,
                    blackKeyHeight
                )
            )
            val blackKeyLeft2 = left + blackKeyWidth * 2.575f
            blackKeyRectList.add(
                RectF(
                    blackKeyLeft2,
                    0f,
                    blackKeyLeft2 + blackKeyWidth,
                    blackKeyHeight
                )
            )
            val blackKeyLeft3 = left + blackKeyWidth * 5.654f
            blackKeyRectList.add(
                RectF(
                    blackKeyLeft3,
                    0f,
                    blackKeyLeft3 + blackKeyWidth,
                    blackKeyHeight
                )
            )
            val blackKeyLeft4 = left + blackKeyWidth * 7.19f
            blackKeyRectList.add(
                RectF(
                    blackKeyLeft4,
                    0f,
                    blackKeyLeft4 + blackKeyWidth,
                    blackKeyHeight
                )
            )
            val blackKeyLeft5 = left + blackKeyWidth * 8.73f
            blackKeyRectList.add(
                RectF(
                    blackKeyLeft5,
                    0f,
                    blackKeyLeft5 + blackKeyWidth,
                    blackKeyHeight
                )
            )
            left += width
            right += width
        }
        // 再加一个右侧看不见的八度的，防止动画穿帮
        keyboardRectList.add(RectF(left, 0f, right, viewHeight))
        // 最后添加绘制所有白键图的位置坐标
        run {
            var i = -octaveWhiteKeyOffset * whiteKeyWidth
            while (i < right) {
                whiteKeyRectList.add(RectF(i, 0f, i + whiteKeyWidth, viewHeight))
                i += whiteKeyWidth
            }
        }
        // 转换为数组，后续取数组中的位置坐标值做实际的绘制
        keyboardImageRectArray = keyboardRectList.toTypedArray()
        whiteKeyRectArray = whiteKeyRectList.toTypedArray()
        blackKeyRectArray = blackKeyRectList.toTypedArray()
        notesOnArray = BooleanArray(octaveCount * NOTES_PER_OCTAVE)
        notesOnPaintArray = arrayOfNulls(octaveCount * NOTES_PER_OCTAVE)
        for (i in notesOnPaintArray.indices) {
            notesOnPaintArray[i] = Paint(Paint.ANTI_ALIAS_FLAG)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 先绘制所有八度的键盘图
        for (rectF in keyboardImageRectArray) {
            canvas.drawBitmap(keyboardImage, null, rectF, null)
        }
        // 特殊逻辑处理，88键钢琴的最低音和最高音在展示时，需要分别叠加绘制最左侧/最右侧的琴键
        if (whiteKeyOffset == MIN_WHITE_KEY_OFFSET) {
            canvas.drawBitmap(
                leftmostWhiteKeyImage,
                null,
                whiteKeyRectArray[MIN_WHITE_KEY_OFFSET],
                null
            )
        }
        if (whiteKeyOffset + whiteKeyNum == MAX_WHITE_KEY_NUM + MIN_WHITE_KEY_OFFSET) {
            canvas.drawBitmap(
                pureWhiteKeyImage,
                null,
                whiteKeyRectArray[getPitchInScreen(MidiUtil.MAX_PIANO_MIDI_PITCH) / NOTES_PER_OCTAVE * WHITE_NOTES_PER_OCTAVE],
                null
            )
        }
        // 按数组中的位置坐标值，拿图片进行绘制
        drawNotesOn(canvas)
        // 根据按键标签种类绘制按键标签
        drawOctaveTagByType(canvas)
    }

    private fun drawNotesOn(canvas: Canvas) {
        for ((i, noteOn) in notesOnArray.withIndex()) {
            // 如果某个琴键处于按下状态，根据具体图片类型来绘制具体琴键按下的图片，叠在键盘图的上面
            if (noteOn) {
                // 首先特殊逻辑处理，如果88键钢琴键盘的最低音或最高音琴键被按下，不遵从之后的规律
                if (whiteKeyOffset == MIN_WHITE_KEY_OFFSET && i == WHITE_KEY_OFFSET[MIN_WHITE_KEY_OFFSET]) {
                    canvas.drawBitmap(
                        whiteKeyPressWithoutRightImage,
                        null,
                        whiteKeyRectArray[MIN_WHITE_KEY_OFFSET],
                        notesOnPaintArray[i]
                    )
                } else if (whiteKeyOffset + whiteKeyNum == MAX_WHITE_KEY_NUM + MIN_WHITE_KEY_OFFSET
                    && i == getPitchInScreen(MidiUtil.MAX_PIANO_MIDI_PITCH)
                ) {
                    canvas.drawBitmap(
                        pureWhiteKeyPressImage,
                        null,
                        whiteKeyRectArray[getPitchInScreen(MidiUtil.MAX_PIANO_MIDI_PITCH) / NOTES_PER_OCTAVE * WHITE_NOTES_PER_OCTAVE],
                        notesOnPaintArray[i]
                    )
                } else {
                    val currentOctave = i / NOTES_PER_OCTAVE
                    val currentPitchInOctave = i % NOTES_PER_OCTAVE
                    when (KEY_IMAGE_TYPE[currentPitchInOctave]) {
                        KeyImageTypeEnum.BLACK_KEY -> canvas.drawBitmap(
                            blackKeyPressImage,
                            null,
                            blackKeyRectArray[OCTAVE_PITCH_TO_KEY_INDEX[currentPitchInOctave]
                                    + currentOctave * BLACK_NOTES_PER_OCTAVE],
                            notesOnPaintArray[i]
                        )

                        KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT -> canvas.drawBitmap(
                            whiteKeyPressWithoutLeftImage,
                            null,
                            whiteKeyRectArray[OCTAVE_PITCH_TO_KEY_INDEX[currentPitchInOctave]
                                    + currentOctave * WHITE_NOTES_PER_OCTAVE],
                            notesOnPaintArray[i]
                        )

                        KeyImageTypeEnum.WHITE_KEY_WITHOUT_LEFT_AND_RIGHT -> canvas.drawBitmap(
                            whiteKeyPressWithoutLeftAndRightImage,
                            null,
                            whiteKeyRectArray[OCTAVE_PITCH_TO_KEY_INDEX[currentPitchInOctave]
                                    + currentOctave * WHITE_NOTES_PER_OCTAVE],
                            notesOnPaintArray[i]
                        )

                        KeyImageTypeEnum.WHITE_KEY_WITHOUT_RIGHT -> canvas.drawBitmap(
                            whiteKeyPressWithoutRightImage,
                            null,
                            whiteKeyRectArray[OCTAVE_PITCH_TO_KEY_INDEX[currentPitchInOctave]
                                    + currentOctave * WHITE_NOTES_PER_OCTAVE],
                            notesOnPaintArray[i]
                        )
                    }
                }
            }
        }
    }

    private fun drawOctaveTagByType(canvas: Canvas) {
        when (octaveTagType) {
            OctaveTagType.NONE -> {
                return
            }

            OctaveTagType.OCTAVE_C -> {
                for ((index, rectF) in keyboardImageRectArray.withIndex()) {
                    canvas.drawText(
                        OCTAVE_C + (whiteKeyOffset / WHITE_NOTES_PER_OCTAVE + index - 1),
                        rectF.left + (rectF.width() / 7 - keyboardTextPaint.measureText(
                            OCTAVE_TAG_WORD_SAMPLE
                        )) / 2,
                        rectF.bottom - keyboardTextPaint.descent(), keyboardTextPaint
                    )
                }
            }

            OctaveTagType.PITCH_NAME -> {
                for ((index, whiteKeyRect) in whiteKeyRectArray.withIndex()) {
                    canvas.drawText(
                        PITCH_NAME_ARRAY[index % WHITE_NOTES_PER_OCTAVE] + (whiteKeyOffset / WHITE_NOTES_PER_OCTAVE + index / WHITE_NOTES_PER_OCTAVE),
                        whiteKeyRect.left + (whiteKeyRect.width() - keyboardTextPaint.measureText(
                            OCTAVE_TAG_WORD_SAMPLE
                        )) / 2,
                        whiteKeyRect.bottom - keyboardTextPaint.descent(), keyboardTextPaint
                    )
                }
            }

            OctaveTagType.SYLLABLE_NAME -> {
                for ((index, whiteKeyRect) in whiteKeyRectArray.withIndex()) {
                    val text = SYLLABLE_NAME_ARRAY[index % WHITE_NOTES_PER_OCTAVE]
                    canvas.drawText(
                        text,
                        whiteKeyRect.left + (whiteKeyRect.width() - keyboardTextPaint.measureText(
                            text
                        )) / 2,
                        whiteKeyRect.bottom - keyboardTextPaint.descent(), keyboardTextPaint
                    )
                }
            }
        }
    }

    private fun calculateTextSize(width: Float) {
        keyboardTextPaint.textSize = maxOctaveTagFontSize
        // 计算文本宽度
        var textWidth: Float
        do {
            // 循环调整字体大小，直到文本适合在指定的宽度下显示合适
            textWidth = keyboardTextPaint.measureText(OCTAVE_TAG_WORD_SAMPLE)
            keyboardTextPaint.textSize--
        } while (textWidth > width * 0.8)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        if (!pianoKeyTouchable) {
            return true
        }
        val action = event.actionMasked
        // Track individual fingers.
        val pointerIndex = event.actionIndex
        var id = event.getPointerId(pointerIndex)
        // Get the pointer's current position
        var x = event.getX(pointerIndex).coerceAtLeast(0.0f)
        var y = event.getY(pointerIndex).coerceAtLeast(0.0f)
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> onFingerDown(id, x, y)
            MotionEvent.ACTION_MOVE -> {
                val pointerCount = event.pointerCount
                var i = 0
                while (i < pointerCount) {
                    id = event.getPointerId(i)
                    x = event.getX(i).coerceAtLeast(0.0f)
                    y = event.getY(i).coerceAtLeast(0.0f)
                    onFingerMove(id, x, y)
                    i++
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                onFingerUp(id, x, y)
                performClick()
            }

            MotionEvent.ACTION_CANCEL -> onAllFingersUp()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun onFingerDown(id: Int, x: Float, y: Float) {
        val (pitch: Byte, volume: Byte) = calculatePitchAndVolumeByXY(y, x)
        fireKeyDownAndHandleListener(pitch, volume, noteOnColor)
        mFingerMap[id] = pitch
    }

    private fun onFingerMove(id: Int, x: Float, y: Float) {
        val previousPitch = mFingerMap[id]
        if (previousPitch != null) {
            val (pitch: Byte, volume: Byte) = calculatePitchAndVolumeByXY(y, x)
            // Did we change to a new key.
            if (pitch >= 0 && pitch != previousPitch) {
                fireKeyDownAndHandleListener(pitch, volume, noteOnColor)
                fireKeyUpAndHandleListener(previousPitch)
                mFingerMap[id] = pitch
            }
        }
    }

    private fun calculatePitchAndVolumeByXY(y: Float, x: Float): Pair<Byte, Byte> {
        var pitch: Byte = -1
        var volume: Byte = -1
        if (y < blackKeyHeight) {
            pitch = xyToBlackPitch(x, y)
            volume = (y / blackKeyHeight * Byte.MAX_VALUE).toInt().coerceAtMost(Byte.MAX_VALUE.toInt()).toByte()
        }
        if (pitch < 0) {
            pitch = xToWhitePitch(x)
            volume = (y / viewHeight * Byte.MAX_VALUE).toInt().coerceAtMost(Byte.MAX_VALUE.toInt()).toByte()
        }
        return Pair(pitch, volume)
    }

    private fun onFingerUp(id: Int, x: Float, y: Float) {
        val previousPitch = mFingerMap[id]
        if (previousPitch != null) {
            fireKeyUpAndHandleListener(previousPitch)
            mFingerMap.remove(id)
        } else {
            var pitch: Byte = -1
            if (y < blackKeyHeight) {
                pitch = xyToBlackPitch(x, y)
            }
            if (pitch < 0) {
                pitch = xToWhitePitch(x)
            }
            fireKeyUpAndHandleListener(pitch)
        }
    }

    private fun onAllFingersUp() {
        // Turn off all notes.
        for (pitch in mFingerMap.values) {
            fireKeyUpAndHandleListener(pitch)
        }
        mFingerMap.clear()
    }

    private fun fireKeyDownAndHandleListener(pitch: Byte, volume: Byte, color: Int?) {
        keyboardListener?.onKeyDown(pitch, volume.coerceAtMost(Byte.MAX_VALUE))
        fireKeyDown(pitch, volume.coerceAtMost(Byte.MAX_VALUE), color)
    }

    fun fireKeyDown(pitch: Byte, volume: Byte, color: Int?) {
        if (pitch < MidiUtil.MIN_PIANO_MIDI_PITCH || pitch > MidiUtil.MAX_PIANO_MIDI_PITCH) {
            return
        }
        val pitchInScreen = getPitchInScreen(pitch)
        if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.size) {
            return
        }
        if (notesOnArray[pitchInScreen]) {
            return
        }
        notesOnArray[pitchInScreen] = true
        if (notesOnPaintArray[pitchInScreen] == null) {
            notesOnPaintArray[pitchInScreen] = Paint(Paint.ANTI_ALIAS_FLAG)
        }
        val blackKey = isBlackKey(pitch)
        notesOnPaintArray[pitchInScreen]!!.alpha = volume.coerceAtMost(Byte.MAX_VALUE).toInt() shl 1
        if (color != null) {
            // 对于黑键，使用PorterDuff.Mode.ADD模式 + 半透明叠加颜色
            // 对于白键，使用PorterDuff.Mode.MULTIPLY模式 + 不透明叠加颜色，使绘制颜色叠加看起来更为真实
            val handledColor = if (blackKey) Color.argb(
                128, Color.red(color), Color.green(color), Color.blue(color)
            ) else Color.argb(255, Color.red(color), Color.green(color), Color.blue(color))
            var porterDuffColorFilter = colorFilterMap[color.toString() + blackKey]
            if (porterDuffColorFilter == null) {
                porterDuffColorFilter = PorterDuffColorFilter(
                    handledColor,
                    if (blackKey) PorterDuff.Mode.ADD else PorterDuff.Mode.MULTIPLY
                )
                colorFilterMap[color.toString() + blackKey] = porterDuffColorFilter
            }
            notesOnPaintArray[pitchInScreen]!!.colorFilter = porterDuffColorFilter
        } else {
            notesOnPaintArray[pitchInScreen]!!.colorFilter = null
        }
        postInvalidate()
    }

    private fun fireKeyUpAndHandleListener(pitch: Byte) {
        keyboardListener?.onKeyUp(pitch)
        fireKeyUp(pitch)
    }

    fun fireKeyUp(pitch: Byte) {
        if (pitch < MidiUtil.MIN_PIANO_MIDI_PITCH || pitch > MidiUtil.MAX_PIANO_MIDI_PITCH) {
            return
        }
        val pitchInScreen = getPitchInScreen(pitch)
        if (pitchInScreen < 0 || pitchInScreen >= notesOnArray.size) {
            return
        }
        if (!notesOnArray[pitchInScreen]) {
            return
        }
        notesOnArray[pitchInScreen] = false
        postInvalidate()
    }

    /**
     * 根据midi音高，计算屏幕内已经绘制了的所有琴键的音高索引
     * 比如midi音高为60，而view中只绘制（可以理解为显示）了两个八度
     * 那么转换后的音高就应该是以屏幕最左侧绘制的八度的do键为0算起的，一定比60小，比如可能为0，可能为12
     */
    fun getPitchInScreen(pitch: Byte): Int {
        return pitch - WHITE_KEY_OFFSET_0_MIDI_PITCH - whiteKeyOffset / WHITE_NOTES_PER_OCTAVE * NOTES_PER_OCTAVE
    }

    // Convert x to MIDI pitch, Ignores black keys
    private fun xToWhitePitch(x: Float): Byte {
        val whiteKeyOffsetInScreen = (x / whiteKeyWidth + whiteKeyOffset).toInt()
        val octaveWhiteKeyOffset = whiteKeyOffsetInScreen % WHITE_NOTES_PER_OCTAVE
        return (WHITE_KEY_OFFSET_0_MIDI_PITCH + WHITE_KEY_OFFSET[octaveWhiteKeyOffset]
                + whiteKeyOffsetInScreen / WHITE_NOTES_PER_OCTAVE * NOTES_PER_OCTAVE).toByte()
    }

    // Convert x to MIDI pitch, Ignores white keys
    private fun xyToBlackPitch(x: Float, y: Float): Byte {
        for (i in blackKeyRectArray.indices) {
            if (blackKeyRectArray[i].contains(x, y)) {
                val pitch =
                    (WHITE_KEY_OFFSET_0_MIDI_PITCH + BLACK_KEY_OFFSET[i % BLACK_NOTES_PER_OCTAVE]
                            + (i / BLACK_NOTES_PER_OCTAVE + whiteKeyOffset / WHITE_NOTES_PER_OCTAVE) * NOTES_PER_OCTAVE)
                // 88键钢琴的最左侧和最右侧键（理应有以外的黑键的时候），特殊处理，不判断黑键
                if (pitch == MidiUtil.MAX_PIANO_MIDI_PITCH + 1 || pitch == MidiUtil.MIN_PIANO_MIDI_PITCH - 1) {
                    return -1
                }
                return pitch.toByte()
            }
        }
        return -1
    }

    fun setWhiteKeyNum(whiteKeyNum: Int) {
        if (whiteKeyNum < MIN_WHITE_KEY_NUM || whiteKeyNum > MAX_WHITE_KEY_NUM) {
            return
        }
        if (whiteKeyNum + whiteKeyOffset > MAX_WHITE_KEY_NUM + MIN_WHITE_KEY_OFFSET) {
            whiteKeyOffset = MAX_WHITE_KEY_NUM + MIN_WHITE_KEY_OFFSET - whiteKeyNum
        }
        this.whiteKeyNum = whiteKeyNum
        makeDraw()
        postInvalidate()
    }

    fun setWhiteKeyOffset(whiteKeyOffset: Int) {
        if (whiteKeyOffset < MIN_WHITE_KEY_OFFSET || whiteKeyOffset + whiteKeyNum > MAX_WHITE_KEY_NUM + MIN_WHITE_KEY_OFFSET) {
            return
        }
        this.whiteKeyOffset = whiteKeyOffset
        makeDraw()
        postInvalidate()
    }

    /**
     * 裁剪键盘，八个白键的键盘皮肤图要分成七份（乘7/8），裁成一个八度
     *
     * @param keyboardBitmap 原图
     * @return 裁剪后的图像
     */
    private fun cropKeyboardBitmap(keyboardBitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(
            keyboardBitmap, 0, 0,
            (keyboardBitmap.width * 0.875f).toInt(), keyboardBitmap.height, null, false
        )
    }

    /**
     * 裁剪+处理键盘图片素材，获取88键钢琴的最右侧纯白键素材
     *
     * @param keyboardBitmap 7/8裁减后的一个八度的键盘素材原图
     * @return 处理后的图像
     */
    private fun cropPureWhiteKeyBitmap(keyboardBitmap: Bitmap): Bitmap {
        return cropPureWhiteKeyPressBitmap(
            Bitmap.createBitmap(
                keyboardBitmap, (keyboardBitmap.width * 6f / 7f).toInt(), 0,
                (keyboardBitmap.width / 7f).toInt(), keyboardBitmap.height, null, false
            ), Bitmap.createBitmap(
                keyboardBitmap, 0, 0,
                (keyboardBitmap.width / 7f).toInt(), keyboardBitmap.height, null, false
            )
        )
    }

    /**
     * 裁剪+处理键盘图片素材，获取88键钢琴的最右侧纯白键按压效果素材
     *
     * @param whiteKeyPressWithoutLeftBitmap 左侧缺口的白键按压素材图
     * @param whiteKeyPressWithoutRightBitmap 右侧缺口的白键按压素材图
     * @return 处理后的图像
     */
    private fun cropPureWhiteKeyPressBitmap(
        whiteKeyPressWithoutLeftBitmap: Bitmap,
        whiteKeyPressWithoutRightBitmap: Bitmap
    ): Bitmap {
        val whiteKeyHalfLeftBitmap = Bitmap.createBitmap(
            whiteKeyPressWithoutRightBitmap, 0, 0, whiteKeyPressWithoutRightBitmap.width shr 1,
            whiteKeyPressWithoutRightBitmap.height, null, false
        )
        val whiteKeyHalfRightBitmap = Bitmap.createBitmap(
            whiteKeyPressWithoutLeftBitmap,
            whiteKeyPressWithoutLeftBitmap.width shr 1,
            0,
            whiteKeyPressWithoutLeftBitmap.width shr 1,
            whiteKeyPressWithoutLeftBitmap.height,
            null,
            false
        )
        val mergedBitmap = Bitmap.createBitmap(
            whiteKeyPressWithoutLeftBitmap.width,
            whiteKeyPressWithoutLeftBitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(mergedBitmap)
        canvas.drawBitmap(whiteKeyHalfLeftBitmap, 0f, 0f, null)
        canvas.drawBitmap(
            whiteKeyHalfRightBitmap,
            whiteKeyHalfRightBitmap.width.toFloat(),
            0f,
            null
        )
        return mergedBitmap
    }

    /**
     * 裁剪键盘，获取88键钢琴的最左侧白键素材
     *
     * @param keyboardBitmap 7/8裁减后的一个八度的键盘素材原图
     * @return 裁剪后的图像
     */
    private fun cropLeftmostWhiteKeyBitmap(keyboardBitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(
            keyboardBitmap, (keyboardBitmap.width * 3f / 7f).toInt(), 0,
            (keyboardBitmap.width / 7f).toInt(), keyboardBitmap.height, null, false
        )
    }

    /**
     * 根据一个midi音高，判断它是否为黑键
     */
    private fun isBlackKey(pitch: Byte): Boolean {
        val pitchInOctave = pitch % NOTES_PER_OCTAVE
        for (blackKeyOffsetInOctave in BLACK_KEY_OFFSET) {
            if (pitchInOctave == blackKeyOffsetInOctave) {
                return true
            }
        }
        return false
    }
}