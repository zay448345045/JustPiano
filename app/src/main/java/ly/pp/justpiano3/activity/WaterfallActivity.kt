package ly.pp.justpiano3.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import ly.pp.justpiano3.R
import ly.pp.justpiano3.entity.GlobalSetting
import ly.pp.justpiano3.entity.PmSongData
import ly.pp.justpiano3.entity.WaterfallNote
import ly.pp.justpiano3.utils.MidiDeviceUtil
import ly.pp.justpiano3.utils.PmSongUtil
import ly.pp.justpiano3.utils.SoundEngineUtil
import ly.pp.justpiano3.utils.ThreadPoolUtil
import ly.pp.justpiano3.utils.VibrationUtil
import ly.pp.justpiano3.utils.ViewUtil
import ly.pp.justpiano3.utils.WaterfallUtil
import ly.pp.justpiano3.view.JPProgressBar
import ly.pp.justpiano3.view.KeyboardView
import ly.pp.justpiano3.view.ScrollText
import ly.pp.justpiano3.view.WaterfallView
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WaterfallActivity : BaseActivity(), View.OnTouchListener,
    MidiDeviceUtil.MidiMessageReceiveListener {
    /**
     * 瀑布流view
     */
    private lateinit var waterfallView: WaterfallView

    /**
     * 钢琴键盘view
     */
    private lateinit var keyboardView: KeyboardView

    /**
     * 定时任务执行器，用于播放动画
     */
    private var scheduledExecutor: ScheduledExecutorService? = null

    /**
     * 记录目前是否有按钮处于按压状态，避免多个按钮重复按下
     */
    private var buttonPressing = false

    /**
     * 进度条
     */
    private lateinit var progressBar: JPProgressBar

    /**
     * 是否为自由演奏（钢琴键盘按键时，是否产生自下而上的音块）
     */
    private var freeStyle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.waterfall)
        // 初始化进度条
        progressBar = JPProgressBar(this)
        progressBar.setCancelable(false)
        // 从extras中的数据确定曲目，解析pm文件
        val pmSongData = parseParamsFromIntentExtras()
        val songNameView = findViewById<ScrollText>(R.id.waterfall_song_name)
        songNameView.text = if (freeStyle) "自由演奏" else pmSongData?.songName
        waterfallView = findViewById(R.id.waterfall_view)
        if (!freeStyle) {
            // 瀑布流设置监听某个瀑布音符到达屏幕底部或完全离开屏幕底部时的动作
            waterfallView.setNoteFallListener(object : WaterfallView.NoteFallListener {
                override fun onNoteAppear(waterfallNote: WaterfallNote) {
                    // 瀑布流音符在瀑布流view的顶端出现，目前无操作
                }

                override fun onNoteFallDown(waterfallNote: WaterfallNote) {
                    // 瀑布流音符到达瀑布流view的底部，播放声音并触发键盘view的琴键按压效果
                    SoundEngineUtil.playSound(waterfallNote.pitch, waterfallNote.volume)
                    keyboardView.fireKeyDown(
                        waterfallNote.pitch,
                        waterfallNote.volume,
                        waterfallNote.color
                    )
                }

                override fun onNoteLeave(waterfallNote: WaterfallNote) {
                    // 瀑布流音符完全离开瀑布流view，停止播放声音并触发键盘view的琴键抬起效果
                    SoundEngineUtil.stopPlaySound(waterfallNote.pitch)
                    keyboardView.fireKeyUp(waterfallNote.pitch)
                }
            })
        }
        keyboardView = findViewById(R.id.waterfall_keyboard)
        keyboardView.octaveTagType =
            KeyboardView.OctaveTagType.values()[GlobalSetting.keyboardOctaveTagType]
        // 监听键盘view布局完成，布局完成后，瀑布流即可生成并开始
        ViewUtil.registerViewLayoutObserver(keyboardView) {
            // 传入根据键盘view获取的所有八度坐标，用于绘制八度虚线
            waterfallView.showOctaveLine = GlobalSetting.waterfallOctaveLine
            waterfallView.octaveLineXList = keyboardView.allOctaveLineX
            // 设置音块下落速率，播放速度、移调
            waterfallView.notePlaySpeed = GlobalSetting.waterfallSongSpeed
            // 开启增减白键数量、移动键盘按钮的监听
            findViewById<View>(R.id.waterfall_sub_key).setOnTouchListener(this@WaterfallActivity)
            findViewById<View>(R.id.waterfall_add_key).setOnTouchListener(this@WaterfallActivity)
            findViewById<View>(R.id.waterfall_key_move_left).setOnTouchListener(this@WaterfallActivity)
            findViewById<View>(R.id.waterfall_key_move_right).setOnTouchListener(this@WaterfallActivity)
            // 设置键盘的点击监听
            keyboardView.keyboardListener = (object : KeyboardView.KeyboardListener {
                override fun onKeyDown(pitch: Byte, volume: Byte) {
                    SoundEngineUtil.playSound(
                        (pitch + GlobalSetting.keyboardSoundTune).toByte(),
                        volume
                    )
                    if (GlobalSetting.soundVibration) {
                        VibrationUtil.vibrateOnce(
                            this@WaterfallActivity,
                            GlobalSetting.soundVibrationTime.toLong()
                        )
                    }
                    freeStyleKeyDownHandle(pitch, volume)
                }

                override fun onKeyUp(pitch: Byte) {
                    SoundEngineUtil.stopPlaySound(pitch)
                    freeStyleKeyUpHandle(pitch)
                }
            })
            ThreadPoolUtil.execute {
                // 将pm文件的解析结果转换为瀑布流音符数组，传入view后开始瀑布流绘制
                val waterfallNotes = convertToWaterfallNote(pmSongData, keyboardView)
                waterfallView.startPlay(waterfallNotes, GlobalSetting.waterfallDownSpeed, GlobalSetting.waterfallTune)
                runOnUiThread {
                    progressBar.dismiss()
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)
            ) {
                MidiDeviceUtil.setMidiConnectionListener(this@WaterfallActivity)
            }
        }
    }

    /**
     * 重新确定瀑布流音符长条的左侧和右侧的坐标值
     */
    private fun updateWaterfallNoteLeftRightLocation() {
        for (waterfallNote in waterfallView.waterfallNotes) {
            recomputeWaterfallNoteLeftAndRight(waterfallNote)
        }
        // 自由演奏的音符也重新计算
        for (freeStyleNoteList in waterfallView.freeStyleNotes.values) {
            for (i in freeStyleNoteList.size - 1 downTo 0) {
                freeStyleNoteList.getOrNull(i)?.let {
                    recomputeWaterfallNoteLeftAndRight(it)
                }
            }
        }
        // 强制补帧
        waterfallView.doDrawFrame()
    }

    private fun recomputeWaterfallNoteLeftAndRight(waterfallNote: WaterfallNote) {
        val (left, right) = WaterfallUtil.convertToWaterfallWidth(
            keyboardView,
            waterfallNote.pitch
        )
        waterfallNote.left = left
        waterfallNote.right = right
    }

    private fun parseParamsFromIntentExtras(): PmSongData? {
        if (intent.extras?.getBoolean("freeStyle") == true) {
            freeStyle = true
        }
        val songPath = intent.extras?.getString("songPath")
        return if (songPath.isNullOrEmpty()) {
            intent.extras?.getByteArray("songBytes")?.let { PmSongUtil.parsePmDataByBytes(it) }
        } else {
            PmSongUtil.parsePmDataByFilePath(this, songPath)
        }
    }

    override fun onStop() {
        if (!GlobalSetting.waterfallBackgroundPlay && waterfallView.isPlaying) {
            waterfallView.pausePlay()
        }
        super.onStop()
    }

    override fun onStart() {
        if (!GlobalSetting.waterfallBackgroundPlay && !waterfallView.isPlaying) {
            waterfallView.resumePlay()
        }
        super.onStart()
    }

    override fun onRestart() {
        if (!GlobalSetting.waterfallBackgroundPlay && !waterfallView.isPlaying) {
            waterfallView.resumePlay()
        }
        super.onRestart()
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)
        ) {
            MidiDeviceUtil.removeMidiConnectionListener()
        }
        // 停止播放，释放资源
        waterfallView.stopPlay()
        waterfallView.destroy()
        SoundEngineUtil.stopPlayAllSounds()
        super.onDestroy()
    }

    /**
     * pm文件解析结果转换为瀑布流音符
     */
    private fun convertToWaterfallNote(
        pmSongData: PmSongData?,
        keyboardView: KeyboardView?
    ): Array<WaterfallNote> {
        var startTime = System.currentTimeMillis()
        // 分别处理左右手的音符list，以便寻找每条音轨的上一个音符，插入上边界坐标
        val leftHandWaterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        val rightHandWaterfallNoteList: MutableList<WaterfallNote> = ArrayList()
        // 取pm文件中的解析音符内容，进行瀑布流音符的生成
        pmSongData?.let {
            var totalTime = 0f
            for (i in it.pitchArray.indices) {
                val pitch = it.pitchArray[i]
                // 计算音符播放的累计时间
                totalTime += it.tickArray[i] * it.globalSpeed
                // 用于延时的空音符直接跳过
                if (PmSongUtil.isPmDefaultEmptyFilledData(it, i)) {
                    continue
                }
                val leftHand = it.trackArray[i] > 0
                // 确定瀑布流音符长条的左侧和右侧的坐标值，根据钢琴键盘view中的琴键获取横坐标
                val (left, right) = WaterfallUtil.convertToWaterfallWidth(keyboardView!!, pitch)
                // 初始化瀑布流音符对象，上边界暂时置0
                val waterfallNote =
                    WaterfallNote(
                        left,
                        right,
                        0f,
                        totalTime,
                        if (leftHand) GlobalSetting.waterfallLeftHandColor else GlobalSetting.waterfallRightHandColor,
                        pitch,
                        (it.volumeArray[i] * Byte.MAX_VALUE / 100f).toInt().toByte()
                    )
                // 根据左右手拿到对应的list
                val waterfallNoteListByHand: MutableList<WaterfallNote> =
                    if (leftHand) leftHandWaterfallNoteList else rightHandWaterfallNoteList
                // 填充上一个音符的上边界 = 当前音符的下边界，如果之前的好几个音符的下边界相同（按和弦），那么统一都设置成当前音符的下边界
                fillNoteEndTime(waterfallNoteListByHand, waterfallNote.bottom)
                waterfallNoteListByHand.add(waterfallNote)
                // 更新进度条
                if (System.currentTimeMillis() - startTime > 200) {
                    startTime = System.currentTimeMillis()
                    runOnUiThread {
                        progressBar.text = String.format(
                            "瀑布流正在加载中...%.2f%%", 100f * i / it.pitchArray.size
                        )
                        if (!progressBar.isShowing) {
                            progressBar.show()
                        }
                    }
                }
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
                leftHandWaterfallNoteList[leftHandWaterfallNoteList.size - 1].bottom + WaterfallUtil.NOTE_MAX_HEIGHT
            )
            waterfallNoteList.addAll(leftHandWaterfallNoteList)
        }
        if (rightHandWaterfallNoteList.isNotEmpty()) {
            fillNoteEndTime(
                rightHandWaterfallNoteList,
                rightHandWaterfallNoteList[rightHandWaterfallNoteList.size - 1].bottom + WaterfallUtil.NOTE_MAX_HEIGHT
            )
            waterfallNoteList.addAll(rightHandWaterfallNoteList)
        }
        waterfallNoteList.sortBy { it.bottom }
        for (currentWaterfallNote in waterfallNoteList) {
            if (currentWaterfallNote.top - currentWaterfallNote.bottom > WaterfallUtil.NOTE_MAX_HEIGHT) {
                currentWaterfallNote.top =
                    currentWaterfallNote.bottom + WaterfallUtil.NOTE_MAX_HEIGHT
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
            } while (index >= 0 && waterfallNoteList.size - index < 128 && waterfallNoteList[index].bottom == waterfallNoteList[index + 1].bottom)
        }
    }

    /**
     * 处理按钮触摸事件
     */
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!buttonPressing) {
                    view.isPressed = true
                    updateAddOrSubtract(view.id)
                    buttonPressing = true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.isPressed = false
                stopAddOrSubtract()
                buttonPressing = false
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
                keyboardView.setWhiteKeyNum(keyboardView.whiteKeyNum - 1)
            }

            R.id.waterfall_add_key -> {
                keyboardView.setWhiteKeyNum(keyboardView.whiteKeyNum + 1)
            }

            R.id.waterfall_key_move_left -> {
                keyboardView.setWhiteKeyOffset(keyboardView.whiteKeyOffset - 1)
            }

            R.id.waterfall_key_move_right -> {
                keyboardView.setWhiteKeyOffset(keyboardView.whiteKeyOffset + 1)
            }
        }
        waterfallView.octaveLineXList = keyboardView.allOctaveLineX
        updateWaterfallNoteLeftRightLocation()
        false
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onMidiMessageReceive(pitch: Byte, volume: Byte) {
        if (volume > 0) {
            SoundEngineUtil.playSound(pitch, volume)
            freeStyleKeyDownHandle(pitch, volume)
            keyboardView.fireKeyDown(pitch, volume, null)
        } else {
            SoundEngineUtil.stopPlaySound(pitch)
            freeStyleKeyUpHandle(pitch)
            keyboardView.fireKeyUp(pitch)
        }
    }

    fun freeStyleKeyDownHandle(pitch: Byte, volume: Byte) {
        if (freeStyle) {
            val (left, right) = WaterfallUtil.convertToWaterfallWidth(keyboardView, pitch)
            waterfallView.addFreeStyleWaterfallNote(
                left,
                right,
                pitch,
                volume,
                GlobalSetting.waterfallFreeStyleColor
            )
        }
    }

    fun freeStyleKeyUpHandle(pitch: Byte) {
        if (freeStyle) {
            waterfallView.stopFreeStyleWaterfallNote(pitch)
        }
    }
}