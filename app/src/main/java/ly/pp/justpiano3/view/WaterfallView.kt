package ly.pp.justpiano3.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import ly.pp.justpiano3.entity.GlobalSetting
import ly.pp.justpiano3.entity.WaterfallNote
import ly.pp.justpiano3.midi.MidiUtil
import ly.pp.justpiano3.utils.ImageLoadUtil
import ly.pp.justpiano3.utils.ObjectPool
import ly.pp.justpiano3.utils.PmSongUtil
import ly.pp.justpiano3.utils.SoundEngineUtil
import java.util.Arrays
import kotlin.math.abs
import kotlin.math.min

/**
 * 瀑布流绘制view
 */
class WaterfallView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    /**
     * 背景图及背景的绘制范围
     */
    private var backgroundImage: Bitmap? = null
    private var backgroundRect: RectF? = null

    /**
     * 进度条图片与进度条背景（基底）图片，及它们的绘制范围
     */
    private var progressBarImage: Bitmap? = null
    private var progressBarBaseImage: Bitmap? = null
    private var progressBarRect: RectF? = null
    private var progressBarBaseRect: RectF? = null

    /**
     * 整个瀑布流视图的透明度
     */
    var viewAlpha = 255

    /**
     * 瀑布流音块下落速率
     */
    private var noteFallDownSpeed = 0.8f

    /**
     * 曲谱播放速度
     */
    var notePlaySpeed = 1f

    /**
     * 绘制音块瀑布流内容
     */
    lateinit var waterfallNotes: Array<WaterfallNote>

    /**
     * 自由演奏瀑布流音块内容，key：音高，value：音块列表
     */
    var freeStyleNotes: MutableMap<Byte, MutableList<WaterfallNote>> = mutableMapOf()

    /**
     * 自由演奏瀑布流音块对象池
     */
    private var waterfallNoteObjectPool: ObjectPool<WaterfallNote> = ObjectPool<WaterfallNote>(
        { WaterfallNote() }, 1024
    )

    /**
     * 是否显示八度虚线
     */
    var showOctaveLine: Boolean = true

    /**
     * 所有八度虚线的横坐标列表
     */
    var octaveLineXList: List<Float>? = null

    /**
     * 曲谱总进度
     */
    private var totalProgress = 0f

    /**
     * 瀑布流音块当前状态
     */
    private lateinit var noteStatus: Array<NoteStatus?>

    /**
     * 瀑布流音块状态枚举：初始化状态、出现在屏幕中状态、正在演奏状态、演奏结束状态
     */
    private enum class NoteStatus {
        INIT, APPEAR, PLAYING, FINISH
    }

    /**
     * 是否在滑动这个view
     */
    private var isScrolling = false

    /**
     * 记录手指按下或当前滑动时的初始横坐标，会根据此值计算手指滑动的坐标偏移量
     */
    private var lastX = 0f

    /**
     * 瀑布流循环绘制线程
     */
    private var drawNotesThread: DrawNotesThread? = null

    /**
     * 瀑布流监听器
     */
    private var noteFallListener: NoteFallListener? = null

    /**
     * 瀑布流监听器，使用监听时，不要有耗时太长的操作，避免影响绘制
     */
    interface NoteFallListener {
        /**
         * 瀑布某个音刚在view最上方出现时触发
         */
        fun onNoteAppear(waterfallNote: WaterfallNote?)

        /**
         * 瀑布某个音刚开始落到view的最下方时触发
         */
        fun onNoteFallDown(waterfallNote: WaterfallNote?)

        /**
         * 瀑布某个音彻底从瀑布流view下方消失时触发
         */
        fun onNoteLeave(waterfallNote: WaterfallNote?)
    }

    /**
     * 设置瀑布流监听器
     */
    fun setNoteFallListener(noteFallListener: NoteFallListener?) {
        this.noteFallListener = noteFallListener
    }

    companion object {

        /**
         * 瀑布流音符之间的绘制空缺间隔时间，单位毫秒，防止同音高的音符连续绘制时看起来粘在一起
         */
        private const val NOTE_MIN_INTERVAL = 15
    }

    init {
        // 注册surfaceHolder回调
        holder.addCallback(this)
        // 设置背景透明
        setBackgroundColor(Color.TRANSPARENT)
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        // 保持屏幕常亮
        holder.setKeepScreenOn(true)
        // 通过皮肤加载背景图、进度条图片
        backgroundImage = ImageLoadUtil.loadFileImage(GlobalSetting.waterfallBackgroundPic)
        if (backgroundImage == null) {
            backgroundImage = ImageLoadUtil.loadSkinImage(context, "waterfall")
        }
        progressBarImage = ImageLoadUtil.loadSkinImage(context, "progress_bar")
        progressBarBaseImage = ImageLoadUtil.loadSkinImage(context, "progress_bar_base")
        // 初始化自由演奏音符内存分配
        for (i in 0..Byte.MAX_VALUE) {
            freeStyleNotes[i.toByte()] = mutableListOf()
        }
    }

    /**
     * 开始播放
     */
    fun startPlay(waterfallNotes: Array<WaterfallNote>, noteFallDownSpeed: Float) {
        // 停止上一次播放，确保不会重复开线程进行播放
        stopPlay()
        // 存储音块下落速率的值
        this.noteFallDownSpeed = noteFallDownSpeed
        // 初始化音符数据，对每个音符乘以音块速率的值，改变音符的高度间隔
        for (i in waterfallNotes.indices) {
            waterfallNotes[i].bottom *= noteFallDownSpeed
            waterfallNotes[i].top *= noteFallDownSpeed
        }
        this.waterfallNotes = waterfallNotes
        // 初始化音符状态
        noteStatus = arrayOfNulls(this.waterfallNotes.size)
        Arrays.fill(noteStatus, NoteStatus.INIT)
        // 初始化绘制线程，根据view的高度确定绘制范围参数
        drawNotesThread = DrawNotesThread()
        buildShowingRectWithWidthAndHeight(width, height)
        // 开启绘制线程
        drawNotesThread!!.start()
    }

    /**
     * 根据宽度和高度确定绘制范围
     */
    private fun buildShowingRectWithWidthAndHeight(width: Int, height: Int) {
        // 计算曲谱总进度 = 所有音块（其实是最后一个音块）上边界的最大值 + view的高度（预留最后一个音块落下时的时间）
        totalProgress = (this.waterfallNotes.maxOfOrNull { it.top } ?: 0f) + height
        // 初始化背景图的绘制范围
        backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        // 初始化进度条背景图（基底）的绘制坐标
        progressBarBaseRect =
            RectF(0f, 0f, width.toFloat(), progressBarBaseImage!!.height.toFloat())
        // 初始化进度条的绘制坐标，初始情况下，进度条宽度为0
        progressBarRect = RectF(0f, 0f, 0f, progressBarImage!!.height.toFloat())
        // 初始化音块缓冲区绘制范围
        drawNotesThread?.notesBufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        drawNotesThread?.notesBufferCanvas = Canvas(drawNotesThread?.notesBufferBitmap!!)
    }

    /**
     * 暂停播放
     */
    fun pausePlay() {
        if (drawNotesThread != null && drawNotesThread!!.isRunning) {
            // 暂停播放时，停止播放所有音符
            SoundEngineUtil.stopPlayAllSounds()
            drawNotesThread!!.isPause = true
        }
    }

    /**
     * 继续播放
     */
    fun resumePlay() {
        if (drawNotesThread != null && drawNotesThread!!.isRunning) {
            drawNotesThread!!.isPause = false
        }
    }

    /**
     * 暂停或继续播放
     */
    private fun pauseOrResumePlay() {
        if (isPlaying) {
            pausePlay()
        } else {
            resumePlay()
        }
    }

    /**
     * 停止播放
     */
    fun stopPlay() {
        if (drawNotesThread != null && drawNotesThread!!.isRunning) {
            drawNotesThread!!.isRunning = false
        }
        drawNotesThread = null
    }

    val isPlaying: Boolean
        /**
         * 是否正在播放
         */
        get() = if (drawNotesThread != null && drawNotesThread!!.isRunning) {
            !drawNotesThread!!.isPause
        } else {
            false
        }

    private val playProgress: Float
        /**
         * 获取目前的播放进度，单位毫秒，如果未在播放，返回0
         */
        get() = if (drawNotesThread != null && drawNotesThread!!.isRunning) {
            drawNotesThread!!.progress
        } else 0f

    /**
     * 销毁view，释放资源
     */
    fun destroy() {
        // 释放图片资源
        backgroundImage?.recycle()
        progressBarImage?.recycle()
        progressBarBaseImage?.recycle()
    }

    /**
     * 强制补帧，若目前瀑布流没有在持续绘制，则补充绘制一帧
     */
    fun doDrawFrame() {
        drawNotesThread?.canvasDraw = true
    }

    /**
     * 增加自由演奏模式的瀑布流音块（音块会从下到上移动），只是增加，音块会无限长，直到调用停止
     */
    fun addFreeStyleWaterfallNote(
        left: Float,
        right: Float,
        pitch: Byte,
        volume: Byte,
        color: Int
    ) {
        if (pitch < MidiUtil.MIN_PIANO_MIDI_PITCH || pitch > MidiUtil.MAX_PIANO_MIDI_PITCH) {
            return
        }
        val freeStyleNote = waterfallNoteObjectPool.acquire()
        freeStyleNote.left = left
        freeStyleNote.right = right
        freeStyleNote.top = height + playProgress
        freeStyleNote.bottom = Float.MAX_VALUE
        freeStyleNote.color = color
        freeStyleNote.pitch = pitch
        freeStyleNote.volume = volume
        freeStyleNotes[pitch]?.add(freeStyleNote)
    }

    /**
     * 停止继续延长自由演奏模式的瀑布流音块
     */
    fun stopFreeStyleWaterfallNote(pitch: Byte) {
        val freeStyleNoteList = freeStyleNotes[pitch] ?: return
        for (i in freeStyleNoteList.indices.reversed()) {
            freeStyleNoteList.getOrNull(i)?.let {
                if (it.bottom > height + playProgress) {
                    it.bottom = height + playProgress
                }
            }
        }
    }

    /**
     * 处理触摸/滑动事件
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        // 不存在音符时，屏蔽所有事件
        if (waterfallNotes.isEmpty()) {
            return false
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 有触摸按下view时，记录此时按下的坐标，后续计算手指滑动了多少坐标
                lastX = event.x.coerceAtLeast(0f)
                // 单独触摸按下view时，触发暂停/继续播放操作
                pauseOrResumePlay()
            }

            MotionEvent.ACTION_MOVE -> {
                // 检测是否为滑动：手指的横坐标偏离原来位置10像素，因按压时仅仅是普通点击，滑动的偏移像素很少时也会执行ACTION_MOVE事件
                if (isScrolling || abs(event.x.coerceAtLeast(0f) - lastX) > 10) {
                    // 刚刚检测到有滑动时，不管原来是在播放还是暂停，统一触发暂停下落瀑布
                    pausePlay()
                    // 根据目前手指滑动的X坐标的偏移量占总宽度的比例，来计算本次手指滑动的偏移量时间（进度）
                    val moveProgressOffset =
                        (event.x.coerceAtLeast(0f) - lastX) / width * totalProgress
                    // 如果经过计算后的进度落在总进度之内，则继续执行设置进度操作
                    if (moveProgressOffset + playProgress in 0f..totalProgress) {
                        drawNotesThread!!.progressScrollOffset += moveProgressOffset
                    }
                    // 更新此时滑动后的手指坐标，后续计算手指滑动了多少坐标
                    lastX = event.x.coerceAtLeast(0f)
                    // 有检测到在view上滑动手指，置正在滑动标记为true
                    // 正在滑动标记为true时会停止进行瀑布流监听，避免滑动时播放大量声音和键盘效果
                    // 可参考调用音符监听的代码：WaterfallDownNotesThread.handleWaterfallNoteListener
                    isScrolling = true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isScrolling) {
                    // 在滑动后，手指抬起，则触发继续播放
                    resumePlay()
                    // 清空正在滑动的标记为false
                    isScrolling = false
                    // 强制补帧
                    doDrawFrame()
                } else {
                    // 如果只是通过点按（没有滑动）导致触发的手指抬起操作，则触发点击事件，不做任何操作
                    performClick()
                }
            }
        }
        return true
    }

    /**
     * 触发点击事件
     */
    override fun performClick(): Boolean {
        super.performClick()
        // 返回 true 表示已处理点击事件
        return true
    }

    /**
     * 手动触发把目前键盘上所有按下的音符（PLAYING状态的）进行清除
     */
    private fun triggerAllPlayingStatusNoteLeave() {
        noteFallListener?.let {
            for (i in waterfallNotes.indices) {
                if (noteStatus[i] == NoteStatus.PLAYING) {
                    it.onNoteLeave(waterfallNotes[i])
                }
            }
        }
    }

    /**
     * 根据当前的进度，重新设置音符的状态
     */
    private fun updateNoteStatusByProgress(progress: Float) {
        // 执行重新设置音符的状态，根据起始时间和结束时间，结合目前进度计算坐标来确定状态
        for (i in waterfallNotes.indices) {
            if (progress - waterfallNotes[i].top + NOTE_MIN_INTERVAL > height) {
                noteStatus[i] = NoteStatus.FINISH
            } else if (progress - waterfallNotes[i].bottom > height) {
                noteStatus[i] = NoteStatus.PLAYING
            } else if (progress - waterfallNotes[i].bottom > 0) {
                noteStatus[i] = NoteStatus.APPEAR
            } else {
                noteStatus[i] = NoteStatus.INIT
            }
        }
    }

    /**
     * 瀑布流双缓冲绘制线程
     */
    private inner class DrawNotesThread : Thread() {
        /**
         * 是否在运行
         */
        var isRunning = true

        /**
         * 是否暂停
         */
        var isPause = false

        /**
         * 歌曲暂停时，目前的播放进度，内部保存的变量，不对外暴露
         */
        var pauseProgress: Float? = null

        /**
         * 播放进度
         */
        var progress: Float = 0f

        /**
         * 处于暂停状态的时间累加，作为时间偏移进行计算
         */
        var progressPauseTime: Float = 0f

        /**
         * 用户手动调节拖动进度条导致的播放进度的偏移量，仅本次拖动生效，本次拖动结束后此值清零
         */
        var progressScrollOffset: Float = 0f

        /**
         * 音块绘制缓冲bitmap
         */
        lateinit var notesBufferBitmap: Bitmap

        /**
         * 音块绘制缓冲canvas
         */
        lateinit var notesBufferCanvas: Canvas

        /**
         * 是否执行绘制
         */
        var canvasDraw = true

        override fun run() {
            // 先初始化绘制Paint对象，避免绘制时进行频繁的创建对象
            val octaveLinePaint = Paint()
            octaveLinePaint.color = Color.WHITE
            octaveLinePaint.alpha = 64
            octaveLinePaint.strokeWidth = 3f
            octaveLinePaint.style = Paint.Style.STROKE
            octaveLinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            val notePaint = Paint()
            val octaveLinePath = Path()
            val alphaPaint = Paint()
            // 记录绘制的起始时间
            val startPlayTime = System.currentTimeMillis()
            // 循环绘制，直到外部有触发终止绘制
            while (isRunning) {
                // 根据系统时间，计算距离开始播放的时间点，间隔多长时间
                // 需要乘以播放速度和音块速率，可整体控制音块的下落速度和音块速率，最后减掉暂停播放后继续播放所带来的时间差
                var playIntervalTime =
                    (System.currentTimeMillis() - startPlayTime) * notePlaySpeed * noteFallDownSpeed - progressPauseTime
                // 如果处于暂停状态，则存储当前的播放进度，如果突然继续播放了，则移除存储的播放进度
                if (isPause && pauseProgress == null) {
                    // 只有第一次监测到isPause为true时才触发这里，重复置isPause为true，不会重复触发，确保进度保存的是第一次触发暂停时的
                    pauseProgress = playIntervalTime
                } else if (!isPause && pauseProgress != null) {
                    // 第一次监测到isPause为false，更新偏移量，使之后继续播放时能按照刚暂停时的进度来继续
                    // updatePauseOffset可以理解为本次暂停过程一共暂停了多少毫秒，所以需要加偏移量补偿掉这些毫秒
                    // 由于暂停的时候会人工拖动进度条操纵进度的修改，所以需要把人工操纵进度的结果progressScrollOffset也算上
                    // 都处理结束之后再清零progressScrollOffset
                    val updatePauseOffset =
                        playIntervalTime - pauseProgress!! - progressScrollOffset
                    progressPauseTime += updatePauseOffset
                    // 接下来当前的绘制帧，调整playIntervalTime，也减掉偏移量的改变值，使当前帧的绘制顺滑起来
                    playIntervalTime -= updatePauseOffset
                    // 清零progressScrollOffset，如果不清零，下次拖动修改进度的时候又要变化progressOffset，会导致和前一次的值冲突
                    progressScrollOffset = 0f
                    // 清除暂停时保存的当时的进度值
                    pauseProgress = null
                    // 以下两行代码内容主要用于拖动进度时，手指抬起后的继续播放，进度发生变化的情形，需要做处理
                    // 1、获取此时键盘中保持按压状态的音符，手动触发来把目前键盘上所有按下的音符进行清除
                    // 可以这么说，如果是改变了进度之后继续播放的，不清除键盘上留有的按下的音符，这些音符就一直在键盘上保持按下了
                    triggerAllPlayingStatusNoteLeave()
                    // 2、根据当前进度重新计算所有音符的状态，如果没有这一步，进度回溯的时候，之前已经弹奏完成的音符便不会再次触发按下了
                    // 可参考调用音符监听的代码：WaterfallDownNotesThread.handleWaterfallNoteListener
                    updateNoteStatusByProgress(playIntervalTime)
                }
                // 根据当前是否暂停，取出进度，进行绘制坐标计算，设置进度时要加上用户当前在手动拖动进度时设置的进度偏移时间
                progress = (if (isPause && pauseProgress != null)
                    pauseProgress!! else playIntervalTime) + progressScrollOffset
                // 如果发现进度大于总进度，说明播放完成，此时标记暂停
                // 如果不标记暂停，progress在曲谱播放结束之后也一直增大，在播放结束后往回拖进度条，可能拉很长时间进度条都没到100%之前
                if (waterfallNotes.isNotEmpty() && progress > totalProgress) {
                    isPause = true
                    // 强制补帧
                    doDrawFrame()
                }
                // 检测并调用瀑布流音块监听
                handleWaterfallNoteListener()
                // 先在缓冲区执行绘制所有音块
                val noteCount = drawNotesOnBufferBitmap(notesBufferCanvas, notePaint)
                // 判断有音块，且view为显示状态，且瀑布流未暂停也未滑动时才实际执行绘制
                // 如果第一次发现无音块时，或第一次发现view被显示时，也绘制且只绘制一帧，进行补帧，随后停止
                if (canvasDraw || isPressed || (noteCount > 0 && visibility == VISIBLE && (isScrolling || !isPause))) {
                    // 执行屏幕绘制，在锁canvas绘制期间，尽可能执行最少的代码逻辑操作，保证绘制性能
                    // 更新变量的值，当绘制方法返回false时表示没有绘制成功，就接着再给绘制的机会
                    canvasDraw = !doDrawWaterfall(alphaPaint, octaveLinePaint, octaveLinePath)
                } else {
                    canvasDraw = visibility != VISIBLE || noteCount > 0
                    // 未触发绘制，避免死循环消耗太多CPU，按刷新率60算，整体休眠约一帧的时间
                    sleep(10)
                }
                // 避免死循环消耗太多CPU，休眠一个音块的最低声音单位的时间
                sleep(PmSongUtil.PM_GLOBAL_SPEED.toLong())
            }
        }

        /**
         * 执行绘制瀑布流
         */
        private fun doDrawWaterfall(
            alphaPaint: Paint,
            octaveLinePaint: Paint,
            octaveLinePath: Path
        ): Boolean {
            var canvas: Canvas? = null
            try {
                // 获取绘制canvas，优先使用硬件加速
                canvas = if (holder.surface.isValid) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        holder.lockHardwareCanvas() ?: holder.lockCanvas()
                    } else {
                        holder.lockCanvas()
                    }
                } else null
                canvas?.let {
                    // 透明底色
                    it.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    // 设置整体绘制的透明度
                    alphaPaint.alpha = viewAlpha
                    // 绘制背景图
                    it.drawBitmap(backgroundImage!!, null, backgroundRect!!, alphaPaint)
                    // 八度虚线绘制
                    if (showOctaveLine) {
                        drawOctaveLine(it, octaveLinePaint, octaveLinePath)
                    }
                    // 将缓冲区中计算好的所有音块进行统一绘制
                    it.drawBitmap(notesBufferBitmap, 0f, 0f, null)
                    // 进度条绘制
                    drawProgressBar(it)
                }
            } finally {
                // 解锁画布
                canvas?.let {
                    if (holder.surface.isValid) {
                        try {
                            holder.unlockCanvasAndPost(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            return canvas != null
        }

        /**
         * 绘制八度虚线
         */
        private fun drawOctaveLine(canvas: Canvas, octaveLinePaint: Paint, octaveLinePath: Path) {
            if (octaveLineXList == null) {
                return
            }
            // 设置每个八度虚线的坐标
            for (octaveLineX in octaveLineXList!!) {
                if (octaveLineX in 0f..width.toFloat()) {
                    octaveLinePath.reset()
                    octaveLinePath.moveTo(octaveLineX, 0f)
                    octaveLinePath.lineTo(octaveLineX, height.toFloat())
                    // 执行八度虚线绘制
                    canvas.drawPath(octaveLinePath, octaveLinePaint)
                }
            }
        }

        /**
         * 在缓冲bitmap中绘制所有应该绘制的音块，返回音块总数，后续判断总数是否为0决定是否执行实际绘制
         */
        private fun drawNotesOnBufferBitmap(canvas: Canvas, notePaint: Paint): Int {
            var noteCount = 0
            // 先清空缓冲bitmap中的内容
            notesBufferBitmap.eraseColor(Color.TRANSPARENT)
            // 执行计算绘制瀑布流音符
            for ((index, waterfallNote) in waterfallNotes.withIndex()) {
                // 瀑布流音块当前在view内对用户可见的，才绘制
                if (noteIsVisible(waterfallNote)) {
                    noteCount++
                    // 根据音符是否为弹奏中状态，确定颜色是否要高亮显示
                    notePaint.color =
                        if (noteStatus[index] == NoteStatus.PLAYING) highlightColor(waterfallNote.color) else waterfallNote.color
                    // 根据音符的力度，确定音块绘制的透明度
                    notePaint.alpha = minOf(waterfallNote.volume / 100f * 128 * 2, 255f).toInt()
                    // 绘制音块
                    canvas.drawRect(
                        waterfallNote.left,
                        progress - waterfallNote.top + NOTE_MIN_INTERVAL,
                        waterfallNote.right,
                        progress - waterfallNote.bottom,
                        notePaint
                    )
                }
            }
            // 执行计算绘制自由演奏音块
            for (freeStyleNoteList in freeStyleNotes.values) {
                for (i in freeStyleNoteList.indices.reversed()) {
                    freeStyleNoteList.getOrNull(i)?.let {
                        noteCount++
                        notePaint.color = it.color
                        // 根据音符的力度，确定音块绘制的透明度
                        notePaint.alpha = minOf(it.volume * 2, 255)
                        // 绘制自由演奏音块，它自下而上移动
                        canvas.drawRect(
                            it.left,
                            it.top - progress,
                            it.right,
                            min(height.toFloat(), it.bottom - progress),
                            notePaint
                        )
                        // 如果音块已经移动到了屏幕外，则从list中删除
                        if (it.bottom - progress < 0f) {
                            waterfallNoteObjectPool.release(freeStyleNoteList.removeAt(i))
                        }
                    }
                }
            }
            return noteCount
        }

        /**
         * 使颜色高亮
         */
        private fun highlightColor(color: Int): Int {
            // 取颜色值的透明度、红绿蓝值，分别增加，营造亮度增加效果
            val highlightedRed = minOf(Color.red(color) + 50, 255)
            val highlightedGreen = minOf(Color.green(color) + 50, 255)
            val highlightedBlue = minOf(Color.blue(color) + 50, 255)
            val highlightedAlpha = minOf(Color.alpha(color) + 50, 255)
            return Color.argb(highlightedAlpha, highlightedRed, highlightedGreen, highlightedBlue)
        }

        /**
         * 绘制进度条
         */
        private fun drawProgressBar(canvas: Canvas) {
            if (waterfallNotes.isNotEmpty()) {
                // 绘制进度条背景（基底）图，它包含完整的一个进度条轮廓
                canvas.drawBitmap(progressBarBaseImage!!, null, progressBarBaseRect!!, null)
                // 修改当前进度条的最右端绘制坐标，进度条最右侧坐标 = 曲谱播放进度百分比 * view的宽度
                progressBarRect!!.right = progress / totalProgress * width
                // 绘制进度条图片
                canvas.drawBitmap(progressBarImage!!, null, progressBarRect!!, null)
            }
        }

        /**
         * 音符是否在view中对用户可见
         */
        private fun noteIsVisible(waterfallNote: WaterfallNote): Boolean {
            // 1、音符的上下左右边界都要在view内，对于左右边界，可以允许有一部分出现在view内
            // 2、进度 - 音符下边界小于0的时候，音符在view的上方，未开始出现，这种情况下过滤掉，不绘制
            // 3、进度 - 音符上边界大于view的高度的时候，音符已经完全离开了view的下边界，这种情况下过滤掉，不绘制
            // 4、音高 - 必须在88键钢琴范围内
            return waterfallNote.right > 0 && waterfallNote.left < width
                    && progress - waterfallNote.bottom > 0
                    && progress - waterfallNote.top + NOTE_MIN_INTERVAL < height
                    && waterfallNote.pitch >= MidiUtil.MIN_PIANO_MIDI_PITCH
                    && waterfallNote.pitch <= MidiUtil.MAX_PIANO_MIDI_PITCH
        }

        /**
         * 检测并调用瀑布流音块监听
         */
        private fun handleWaterfallNoteListener() {
            // 如果没有监听器，或者目前view处于拖动来改变进度的情形，则不做任何监听操作
            if (waterfallNotes.isEmpty() || noteFallListener == null || isScrolling) {
                return
            }
            for (i in waterfallNotes.indices) {
                if (noteStatus[i] == NoteStatus.PLAYING && progress - waterfallNotes[i].top + NOTE_MIN_INTERVAL > height) {
                    // 判断到音块刚刚完全离开view时，调用监听，通过叠加音符状态的判断，保证这个监听不会重复调用
                    noteFallListener!!.onNoteLeave(waterfallNotes[i])
                    noteStatus[i] = NoteStatus.FINISH
                } else if (noteStatus[i] == NoteStatus.APPEAR && progress - waterfallNotes[i].bottom > height) {
                    // 判断到音块刚刚下落到view的下边界时，调用监听，通过叠加音符状态的判断，保证这个监听不会重复调用
                    noteFallListener!!.onNoteFallDown(waterfallNotes[i])
                    noteStatus[i] = NoteStatus.PLAYING
                } else if (noteStatus[i] == NoteStatus.INIT && progress - waterfallNotes[i].bottom > 0) {
                    // 判断到音块刚刚在view的上边界出现时，调用监听，通过叠加音符状态的判断，保证这个监听不会重复调用
                    noteFallListener!!.onNoteAppear(waterfallNotes[i])
                    noteStatus[i] = NoteStatus.APPEAR
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // view的宽高改变，执行重新计算背景图等绘制范围，然后强制补帧
        if (drawNotesThread?.isRunning == true) {
            buildShowingRectWithWidthAndHeight(w, h)
            holder.setFixedSize(w, h)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // 后台切换到前台时，强制补一帧
        doDrawFrame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // nothing
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // nothing
    }
}