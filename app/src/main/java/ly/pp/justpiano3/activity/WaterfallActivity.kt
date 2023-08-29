package ly.pp.justpiano3.activity

import android.app.Activity
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.util.Pair
import io.netty.util.internal.StringUtil
import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.R
import ly.pp.justpiano3.entity.WaterfallNote
import ly.pp.justpiano3.utils.ColorUtil
import ly.pp.justpiano3.utils.MidiUtil
import ly.pp.justpiano3.utils.SoundEngineUtil
import ly.pp.justpiano3.view.KeyboardModeView
import ly.pp.justpiano3.view.ScrollText
import ly.pp.justpiano3.view.WaterfallView
import ly.pp.justpiano3.view.WaterfallView.NoteFallListener
import ly.pp.justpiano3.view.play.PmFileParser
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WaterfallActivity : Activity(), OnTouchListener {
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

    companion object {
        /**
         * 瀑布的宽度占键盘黑键宽度的百分比
         */
        const val BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.8f

        /**
         * 瀑布流音符播放时的最长的持续时间
         */
        const val NOTE_PLAY_MAX_INTERVAL_TIME = 1200

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
        val pmFileParser = parsePmFileFromIntentExtras()
        val songNameView = findViewById<ScrollText>(R.id.waterfall_song_name)
        songNameView.text = pmFileParser.songName
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
                        waterfallNote.pitch, waterfallNote.volume,
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
                        // 将pm文件的解析结果转换为瀑布流音符数组，传入view后开始瀑布流绘制
                        val waterfallNotes = convertToWaterfallNote(pmFileParser, keyboardModeView)
                        waterfallView.startPlay(waterfallNotes)
                        // 开启增减白键数量、移动键盘按钮的监听
                        findViewById<View>(R.id.waterfall_sub_key).setOnTouchListener(this@WaterfallActivity)
                        findViewById<View>(R.id.waterfall_add_key).setOnTouchListener(this@WaterfallActivity)
                        findViewById<View>(R.id.waterfall_key_move_left).setOnTouchListener(this@WaterfallActivity)
                        findViewById<View>(R.id.waterfall_key_move_right).setOnTouchListener(this@WaterfallActivity)
                        // 设置键盘的点击监听，播放对应琴键的声音
                        keyboardModeView.setMusicKeyListener(object : KeyboardModeView.MusicKeyListener {
                            override fun onKeyDown(pitch: Int, volume: Int) {
                                SoundEngineUtil.playSound(pitch, volume)
                            }

                            override fun onKeyUp(pitch: Int) {
                                // nothing
                            }
                        })
                        // 移除布局监听，避免重复调用
                        keyboardModeView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
    }

    /**
     * 重新确定瀑布流音符长条的左侧和右侧的坐标值
     */
    private fun updateWaterfallNoteLeftRightLocation(
            waterfallNotes: Array<WaterfallNote>,
            keyboardModeView: KeyboardModeView
    ) {
        for (waterfallNote in waterfallNotes) {
            val noteLeftRightLocation = convertWidthToWaterfallWidth(
                    MidiUtil.isBlackKey(waterfallNote.pitch),
                    keyboardModeView.convertPitchToReact(waterfallNote.pitch)
            )
            if (noteLeftRightLocation != null) {
                waterfallNote.left = noteLeftRightLocation.first
                waterfallNote.right = noteLeftRightLocation.second
            } else {
                // 说明音符的音高落在了键盘之外，直接将坐标置为-1，不绘制即可
                waterfallNote.left = (-1).toFloat()
                waterfallNote.right = (-1).toFloat()
            }
        }
    }

    private fun parsePmFileFromIntentExtras(): PmFileParser {
        val pmFileParser: PmFileParser
        val songPath = intent.extras?.getString("songPath")
        pmFileParser = if (StringUtil.isNullOrEmpty(songPath)) {
            val songBytes = intent.extras?.getByteArray("songBytes")
            PmFileParser(songBytes)
        } else {
            PmFileParser(this, songPath)
        }
        return pmFileParser
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
        // 停止播放
        waterfallView.stopPlay()
        waterfallView.destroy()
        super.onDestroy()
    }

    /**
     * pm文件解析结果转换为瀑布流音符
     */
    private fun convertToWaterfallNote(
            pmFileParser: PmFileParser,
            keyboardModeView: KeyboardModeView?
    ): Array<WaterfallNote> {
        // 分别处理左右手的音符list，以便寻找每条音轨的上一个音符，插入结束时间
        val leftHandWaterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        val rightHandWaterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        var totalTime = 0
        // 取pm文件中的解析音符内容，进行瀑布流音符的生成
        for (i in pmFileParser.noteArray.indices) {
            val pitch = pmFileParser.noteArray[i].toInt()
            // 计算音符播放的累计时间
            totalTime += pmFileParser.tickArray[i] * pmFileParser.pm_2
            val leftHand = pmFileParser.trackArray[i].toInt() != 0
            // 确定瀑布流音符长条的左侧和右侧的坐标值，根据钢琴键盘view中的琴键获取横坐标
            val noteLeftRightLocation = convertWidthToWaterfallWidth(
                    MidiUtil.isBlackKey(pitch),
                    keyboardModeView!!.convertPitchToReact(pitch)
            )
            if (noteLeftRightLocation != null) {
                val waterfallNote = WaterfallNote(noteLeftRightLocation.first, noteLeftRightLocation.second,
                        totalTime, 0, leftHand, pitch, pmFileParser.volumeArray[i].toInt())
                // 根据左右手拿到对应的list
                val waterfallNoteListByHand: MutableList<WaterfallNote> =
                        if (leftHand) leftHandWaterfallNoteList else rightHandWaterfallNoteList
                // 填充上一个音符的结束时间 = 当前音符的起始时间，如果之前的好几个音符的起始时间相同（按和弦），那么统一都设置成当前音符的起始时间
                fillNoteEndTime(waterfallNoteListByHand, waterfallNote.startTime)
                waterfallNoteListByHand.add(waterfallNote)
            }
        }
        // 左右手音符列表合并，做后置处理
        return waterfallNoteListAfterHandle(leftHandWaterfallNoteList, rightHandWaterfallNoteList)
    }

    /**
     * 音符list后置处理，订正一些时间间隔情况，最后转为数组
     *
     *
     * 1、处理最后一个音符间隔没有写进去的情况，直接写入间隔最大值
     * 2、合并左右手两条音轨的音符list，按音符的起始时间进行排序
     * 3、处理音符间隔太大的情况，订正间隔为最大值
     * 4、所有音符乘以节拍比率数值，整体控制速度
     * 5、转为数组返回
     */
    private fun waterfallNoteListAfterHandle(
            leftHandWaterfallNoteList: List<WaterfallNote>,
            rightHandWaterfallNoteList: List<WaterfallNote>
    ): Array<WaterfallNote> {
        val waterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        // 每个音轨的最后一个音符，按之前的逻辑，没有填充结束时间，这里直接填充最后一个音符的结束时间 = 最后一个音符的起始时间 + 持续时间最大值
        if (leftHandWaterfallNoteList.isNotEmpty()) {
            fillNoteEndTime(
                    leftHandWaterfallNoteList,
                    leftHandWaterfallNoteList[leftHandWaterfallNoteList.size - 1].startTime + NOTE_PLAY_MAX_INTERVAL_TIME
            )
            waterfallNoteList.addAll(leftHandWaterfallNoteList)
        }
        if (rightHandWaterfallNoteList.isNotEmpty()) {
            fillNoteEndTime(
                    rightHandWaterfallNoteList,
                    rightHandWaterfallNoteList[rightHandWaterfallNoteList.size - 1].startTime + NOTE_PLAY_MAX_INTERVAL_TIME
            )
            waterfallNoteList.addAll(rightHandWaterfallNoteList)
        }
        waterfallNoteList.sortWith { (_, _, startTime): WaterfallNote, (_, _, startTime1): WaterfallNote ->
            startTime.compareTo(startTime1)
        }
        val jpApplication = application as JPApplication
        for (currentWaterfallNote in waterfallNoteList) {
            if (currentWaterfallNote.interval() > NOTE_PLAY_MAX_INTERVAL_TIME) {
                currentWaterfallNote.endTime =
                        currentWaterfallNote.startTime + NOTE_PLAY_MAX_INTERVAL_TIME
            }
            currentWaterfallNote.startTime =
                    (currentWaterfallNote.startTime / jpApplication.setting.tempSpeed).toInt()
            currentWaterfallNote.endTime =
                    (currentWaterfallNote.endTime / jpApplication.setting.tempSpeed).toInt()
        }
        return waterfallNoteList.toTypedArray()
    }

    /**
     * 填充音符的结束时间，如果之前的好几个音符的起始时间相同（按和弦），那么统一都设置成这个时间
     *
     * @param waterfallNoteList 音符list
     * @param noteEndTime       给定的音符结束时间
     */
    private fun fillNoteEndTime(waterfallNoteList: List<WaterfallNote>, noteEndTime: Int) {
        // 取list中上一个元素（音符），填充它的结束时间为当前音符的（累计）开始时间
        if (waterfallNoteList.isNotEmpty()) {
            // 循环向前寻找之前的音符
            var index = waterfallNoteList.size - 1
            do {
                val lastWaterfallNote = waterfallNoteList[index]
                // 设置上一个音符的结束时间
                lastWaterfallNote.endTime = lastWaterfallNote.startTime.coerceAtLeast(noteEndTime)
                index--
                // 如果上一个音符的开始时间和当前音符的开始时间相同，则表示同时按下，此时循环，继续设定两个音符的结束时间相同即可
            } while (index >= 0 && waterfallNoteList[index].startTime == waterfallNoteList[index + 1].startTime)
        }
    }

    /**
     * 将琴键RectF的宽度，转换成瀑布流长条的宽度（略小于琴键的宽度）
     * 返回值为瀑布流音符横坐标的左边界和右边界
     */
    private fun convertWidthToWaterfallWidth(isBlack: Boolean, rectF: RectF?): Pair<Float, Float>? {
        if (rectF == null) {
            return null
        }
        // 根据比例计算瀑布流的宽度
        val waterfallWidth =
                if (isBlack) rectF.width() * BLACK_KEY_WATERFALL_WIDTH_FACTOR else rectF.width() * KeyboardModeView.BLACK_KEY_WIDTH_FACTOR * BLACK_KEY_WATERFALL_WIDTH_FACTOR
        // 根据中轴线和新的宽度计算坐标，返回
        return Pair.create(
                rectF.centerX() - waterfallWidth / 2,
                rectF.centerX() + waterfallWidth / 2
        )
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val action = event.action
        val id = view.id
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                view.isPressed = true
                updateAddOrSubtract(id)
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
}