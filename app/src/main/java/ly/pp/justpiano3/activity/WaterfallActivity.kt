package ly.pp.justpiano3.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.RectF
import android.media.midi.MidiReceiver
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import ly.pp.justpiano3.R
import ly.pp.justpiano3.constant.MidiConstants
import ly.pp.justpiano3.entity.GlobalSetting
import ly.pp.justpiano3.entity.GlobalSetting.midiKeyboardTune
import ly.pp.justpiano3.entity.PmSongData
import ly.pp.justpiano3.entity.WaterfallNote
import ly.pp.justpiano3.midi.MidiConnectionListener
import ly.pp.justpiano3.midi.MidiFramer
import ly.pp.justpiano3.utils.MidiUtil
import ly.pp.justpiano3.utils.PmSongUtil
import ly.pp.justpiano3.utils.SoundEngineUtil
import ly.pp.justpiano3.view.KeyboardModeView
import ly.pp.justpiano3.view.ScrollText
import ly.pp.justpiano3.view.WaterfallView
import ly.pp.justpiano3.view.WaterfallView.NoteFallListener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.experimental.and

class WaterfallActivity : Activity(), OnTouchListener, MidiConnectionListener {
    /**
     * 瀑布流view
     */
    private lateinit var waterfallView: WaterfallView

    /**
     * 钢琴键盘view
     */
    private lateinit var keyboardModeView: KeyboardModeView

    /**
     * 用于播放动画
     */
    private var scheduledExecutor: ScheduledExecutorService? = null

    /**
     * midi键盘协议解析器
     */
    private var midiFramer: MidiReceiver? = null

    companion object {
        /**
         * 瀑布的宽度占键盘黑键宽度的百分比
         */
        const val BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.8f

        /**
         * 瀑布流音符最大高度
         */
        const val NOTE_MAX_HEIGHT = 1000

        /**
         * 瀑布流音符左右手颜色（透明度无效，透明度目前根据音符力度确定）
         */
        const val LEFT_HAND_NOTE_COLOR = 0x2BBBFB
        const val RIGHT_HAND_NOTE_COLOR = 0xFF802D
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.waterfall)
        // 从extras中的数据确定曲目，解析pm文件
        val pmSongData = parsePmFileFromIntentExtras()
        val songNameView = findViewById<ScrollText>(R.id.waterfall_song_name)
        songNameView.text = pmSongData?.songName
        waterfallView = findViewById(R.id.waterfall_view)
        // 瀑布流设置监听某个瀑布音符到达屏幕底部或完全离开屏幕底部时的动作
        waterfallView.setNoteFallListener(object : NoteFallListener {
            override fun onNoteAppear(waterfallNote: WaterfallNote?) {
                // 瀑布流音符在瀑布流view的顶端出现，目前无操作
            }

            override fun onNoteFallDown(waterfallNote: WaterfallNote?) {
                // 瀑布流音符到达瀑布流view的底部，播放声音并触发键盘view的琴键按压效果
                SoundEngineUtil.playSound(waterfallNote!!.pitch, waterfallNote.volume)
                keyboardModeView.fireKeyDown(
                    waterfallNote.pitch,
                    waterfallNote.volume,
                    if (waterfallNote.leftHand) LEFT_HAND_NOTE_COLOR else RIGHT_HAND_NOTE_COLOR
                )
            }

            override fun onNoteLeave(waterfallNote: WaterfallNote?) {
                // 瀑布流音符完全离开瀑布流view，触发键盘view的琴键抬起效果
                keyboardModeView.fireKeyUp(waterfallNote!!.pitch)
            }
        })
        keyboardModeView = findViewById(R.id.waterfall_keyboard)
        // 监听键盘view布局完成，布局完成后，瀑布流即可生成并开始
        keyboardModeView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 传入根据键盘view获取的所有八度坐标，用于绘制八度虚线
                waterfallView.octaveLineXList = keyboardModeView.allOctaveLineX
                // 设置瀑布流音符的左右手颜色
                waterfallView.leftHandNoteColor = LEFT_HAND_NOTE_COLOR
                waterfallView.rightHandNoteColor = RIGHT_HAND_NOTE_COLOR
                // 设置音块下落速率，播放速度
                waterfallView.notePlaySpeed = GlobalSetting.waterfallSongSpeed
                // 将pm文件的解析结果转换为瀑布流音符数组，传入view后开始瀑布流绘制
                val waterfallNotes = convertToWaterfallNote(pmSongData, keyboardModeView)
                waterfallView.startPlay(waterfallNotes, GlobalSetting.waterfallDownSpeed)
                // 开启增减白键数量、移动键盘按钮的监听
                findViewById<View>(R.id.waterfall_sub_key).setOnTouchListener(this@WaterfallActivity)
                findViewById<View>(R.id.waterfall_add_key).setOnTouchListener(this@WaterfallActivity)
                findViewById<View>(R.id.waterfall_key_move_left).setOnTouchListener(this@WaterfallActivity)
                findViewById<View>(R.id.waterfall_key_move_right).setOnTouchListener(this@WaterfallActivity)
                // 设置键盘的点击监听，键盘按下时播放对应琴键的声音
                keyboardModeView.setMusicKeyListener(object : KeyboardModeView.MusicKeyListener {
                    override fun onKeyDown(pitch: Byte, volume: Byte) {
                        SoundEngineUtil.playSound(pitch, volume)
                    }

                    override fun onKeyUp(pitch: Byte) {
                        // nothing
                    }
                })
                // 移除布局监听，避免重复调用
                keyboardModeView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (MidiUtil.getMidiOutputPort() != null && midiFramer == null) {
                    midiFramer = MidiFramer(object : MidiReceiver() {
                        override fun onSend(data: ByteArray, offset: Int, count: Int, timestamp: Long) {
                            midiConnectHandle(data)
                        }
                    })
                    MidiUtil.getMidiOutputPort().connect(midiFramer)
                    MidiUtil.addMidiConnectionListener(this)
                }
            }
        }
    }

    /**
     * 重新确定瀑布流音符长条的左侧和右侧的坐标值
     */
    private fun updateWaterfallNoteLeftRightLocation(
        waterfallNotes: Array<WaterfallNote>,
        keyboardModeView: KeyboardModeView
    ) {
        for (waterfallNote in waterfallNotes) {
            val (left, right) = convertWidthToWaterfallWidth(
                MidiUtil.isBlackKey(waterfallNote.pitch),
                keyboardModeView.convertPitchToReact(waterfallNote.pitch)
            )
            waterfallNote.left = left
            waterfallNote.right = right
        }
    }

    private fun parsePmFileFromIntentExtras(): PmSongData? {
        val songPath = intent.extras?.getString("songPath")
        return if (songPath.isNullOrEmpty()) {
            intent.extras?.getByteArray("songBytes")?.let { PmSongUtil.parsePmDataByBytes(it) }
        } else {
            PmSongUtil.parsePmDataByFilePath(this, songPath)
        }
    }

    override fun onStop() {
        if (waterfallView.isPlaying) {
            waterfallView.pausePlay()
        }
        super.onStop()
    }

    override fun onStart() {
        if (!waterfallView.isPlaying) {
            waterfallView.resumePlay()
        }
        super.onStart()
    }

    override fun onRestart() {
        if (!waterfallView.isPlaying) {
            waterfallView.resumePlay()
        }
        super.onRestart()
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (MidiUtil.getMidiOutputPort() != null) {
                    if (midiFramer != null) {
                        MidiUtil.getMidiOutputPort().disconnect(midiFramer)
                        midiFramer = null
                    }
                    MidiUtil.removeMidiConnectionStart(this)
                }
            }
        }
        // 停止播放，释放资源
        waterfallView.stopPlay()
        waterfallView.destroy()
        super.onDestroy()
    }

    /**
     * pm文件解析结果转换为瀑布流音符
     */
    private fun convertToWaterfallNote(
        pmSongData: PmSongData?,
        keyboardModeView: KeyboardModeView?
    ): Array<WaterfallNote> {
        // 分别处理左右手的音符list，以便寻找每条音轨的上一个音符，插入上边界坐标
        val leftHandWaterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        val rightHandWaterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        // 取pm文件中的解析音符内容，进行瀑布流音符的生成
        pmSongData?.let {
            var totalTime = 0f
            for (i in it.pitchArray.indices) {
                val pitch = it.pitchArray[i]
                val volume = it.volumeArray[i]
                // 计算音符播放的累计时间
                totalTime += it.tickArray[i] * it.globalSpeed
                val leftHand = it.trackArray[i] > 0
                // 确定瀑布流音符长条的左侧和右侧的坐标值，根据钢琴键盘view中的琴键获取横坐标
                val (left, right) = convertWidthToWaterfallWidth(
                    MidiUtil.isBlackKey(pitch),
                    keyboardModeView!!.convertPitchToReact(pitch)
                )
                // 初始化瀑布流音符对象，上边界暂时置0
                val waterfallNote = WaterfallNote(left, right, 0f, totalTime, leftHand, pitch, volume)
                // 根据左右手拿到对应的list
                val waterfallNoteListByHand: MutableList<WaterfallNote> =
                    if (leftHand) leftHandWaterfallNoteList else rightHandWaterfallNoteList
                // 填充上一个音符的上边界 = 当前音符的下边界，如果之前的好几个音符的下边界相同（按和弦），那么统一都设置成当前音符的下边界
                fillNoteEndTime(waterfallNoteListByHand, waterfallNote.bottom)
                waterfallNoteListByHand.add(waterfallNote)
            }
        }
        // 左右手音符列表合并，做后置处理
        return waterfallNoteListAfterHandle(leftHandWaterfallNoteList, rightHandWaterfallNoteList)
    }

    /**
     * 音符list后置处理，订正一些时间间隔情况，最后转为数组
     *
     * 1、处理最后一个音符上边界没有写进去的情况，直接写入音符高度的最大值
     * 2、合并左右手两条音轨的音符list，按音符的下边界坐标进行排序
     * 3、处理音符高度过大的情况，订正高度为最大值
     */
    private fun waterfallNoteListAfterHandle(
        leftHandWaterfallNoteList: List<WaterfallNote>,
        rightHandWaterfallNoteList: List<WaterfallNote>
    ): Array<WaterfallNote> {
        val waterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        // 每个音轨的最后一个音符，按之前的逻辑，没有填充到上边界，这里直接填充最后一个音符的上边界 = 最后一个音符的下边界 + 音符最大高度
        if (leftHandWaterfallNoteList.isNotEmpty()) {
            fillNoteEndTime(
                leftHandWaterfallNoteList,
                leftHandWaterfallNoteList[leftHandWaterfallNoteList.size - 1].bottom + NOTE_MAX_HEIGHT
            )
            waterfallNoteList.addAll(leftHandWaterfallNoteList)
        }
        if (rightHandWaterfallNoteList.isNotEmpty()) {
            fillNoteEndTime(
                rightHandWaterfallNoteList,
                rightHandWaterfallNoteList[rightHandWaterfallNoteList.size - 1].bottom + NOTE_MAX_HEIGHT
            )
            waterfallNoteList.addAll(rightHandWaterfallNoteList)
        }
        waterfallNoteList.sortBy { it.bottom }
        for (currentWaterfallNote in waterfallNoteList) {
            if (currentWaterfallNote.top - currentWaterfallNote.bottom > NOTE_MAX_HEIGHT) {
                currentWaterfallNote.top = currentWaterfallNote.bottom + NOTE_MAX_HEIGHT
            }
        }
        return waterfallNoteList.toTypedArray()
    }

    /**
     * 填充上一个音符的上边界 = 当前音符的下边界，如果之前的好几个音符的下边界相同（按和弦），那么统一都设置成当前音符的下边界
     *
     * @param waterfallNoteList 音符list
     * @param noteBottom       给定的音符下边界坐标
     */
    private fun fillNoteEndTime(waterfallNoteList: List<WaterfallNote>, noteBottom: Float) {
        // 取list中上一个元素（音符），填充它的下坐标为当前音符的（累计）开始时间
        if (waterfallNoteList.isNotEmpty()) {
            // 循环向前寻找之前的音符
            var index = waterfallNoteList.size - 1
            do {
                val lastWaterfallNote = waterfallNoteList[index]
                // 设置上一个音符的下边界
                lastWaterfallNote.top = lastWaterfallNote.bottom.coerceAtLeast(noteBottom)
                index--
                // 如果上一个音符的上边界和当前音符的上边界相同，则表示同时按下，此时循环，继续设定两个音符的结束时间相同即可
            } while (index >= 0 && waterfallNoteList[index].bottom == waterfallNoteList[index + 1].bottom)
        }
    }

    /**
     * 将琴键RectF的宽度，转换成瀑布流长条的宽度（略小于琴键的宽度）
     * 返回值为瀑布流音符横坐标的左边界和右边界
     */
    private fun convertWidthToWaterfallWidth(isBlack: Boolean, rectF: RectF?): Pair<Float, Float> {
        if (rectF == null) {
            return Pair(-1f, -1f)
        }
        // 根据比例计算瀑布流的宽度
        val waterfallWidth = if (isBlack) rectF.width() * BLACK_KEY_WATERFALL_WIDTH_FACTOR
        else rectF.width() * KeyboardModeView.BLACK_KEY_WIDTH_FACTOR * BLACK_KEY_WATERFALL_WIDTH_FACTOR
        // 根据中轴线和新的宽度计算坐标，返回
        return Pair(rectF.centerX() - waterfallWidth / 2, rectF.centerX() + waterfallWidth / 2)
    }

    /**
     * 处理按钮触摸事件
     */
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.isPressed = true
                updateAddOrSubtract(view.id)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.isPressed = false
                stopAddOrSubtract()
                view.performClick()
            }
        }
        return true
    }

    /**
     * 处理按钮长按，定时发送消息
     */
    private fun updateAddOrSubtract(viewId: Int) {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor()
        scheduledExecutor?.scheduleWithFixedDelay({
            val message = Message.obtain()
            message.what = viewId
            handler.sendMessage(message)
        }, 0, 80, TimeUnit.MILLISECONDS)
    }

    /**
     * 停止定时发送消息
     */
    private fun stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor!!.shutdownNow()
            scheduledExecutor = null
        }
    }

    /**
     * 处理按钮长按的消息
     */
    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            R.id.waterfall_sub_key -> {
                keyboardModeView.setWhiteKeyNum(keyboardModeView.whiteKeyNum - 1, 0)
                waterfallView.octaveLineXList = keyboardModeView.allOctaveLineX
                updateWaterfallNoteLeftRightLocation(
                    waterfallView.waterfallNotes,
                    keyboardModeView
                )
            }

            R.id.waterfall_add_key -> {
                keyboardModeView.setWhiteKeyNum(keyboardModeView.whiteKeyNum + 1, 0)
                waterfallView.octaveLineXList = keyboardModeView.allOctaveLineX
                updateWaterfallNoteLeftRightLocation(
                    waterfallView.waterfallNotes,
                    keyboardModeView
                )
            }

            R.id.waterfall_key_move_left -> {
                keyboardModeView.setWhiteKeyOffset(keyboardModeView.whiteKeyOffset - 1, 0)
                waterfallView.octaveLineXList = keyboardModeView.allOctaveLineX
                updateWaterfallNoteLeftRightLocation(
                    waterfallView.waterfallNotes,
                    keyboardModeView
                )
            }

            R.id.waterfall_key_move_right -> {
                keyboardModeView.setWhiteKeyOffset(keyboardModeView.whiteKeyOffset + 1, 0)
                waterfallView.octaveLineXList = keyboardModeView.allOctaveLineX
                updateWaterfallNoteLeftRightLocation(
                    waterfallView.waterfallNotes,
                    keyboardModeView
                )
            }
        }
        false
    }

    override fun onMidiConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (MidiUtil.getMidiOutputPort() != null && midiFramer == null) {
                    midiFramer = MidiFramer(object : MidiReceiver() {
                        override fun onSend(data: ByteArray, offset: Int, count: Int, timestamp: Long) {
                            midiConnectHandle(data)
                        }
                    })
                    MidiUtil.getMidiOutputPort().connect(midiFramer)
                }
            }
        }
    }

    override fun onMidiDisconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (midiFramer != null) {
                    MidiUtil.getMidiOutputPort().disconnect(midiFramer)
                    midiFramer = null
                }
            }
        }
    }

    fun midiConnectHandle(data: ByteArray) {
        val command = (data[0] and MidiConstants.STATUS_COMMAND_MASK)
        val pitch = (data[1] + midiKeyboardTune).toByte()
        if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
            keyboardModeView.fireKeyDown(pitch, data[2], null)
            SoundEngineUtil.playSound(pitch, data[2])
        } else if (command == MidiConstants.STATUS_NOTE_OFF || (command == MidiConstants.STATUS_NOTE_ON && data[2] <= 0)) {
            keyboardModeView.fireKeyUp(pitch)
        }
    }
}