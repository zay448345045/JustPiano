package ly.pp.justpiano3.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import ly.pp.justpiano3.entity.WaterfallNote
import ly.pp.justpiano3.utils.SkinImageLoadUtil
import java.util.*
import kotlin.math.abs

/**
 * 瀑布流绘制view
 */
class WaterfallView @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

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
     * 瀑布流音符左右手颜色
     */
    var leftHandNoteColor = 0
    var rightHandNoteColor = 0

    /**
     * 绘制音块瀑布流内容
     */
    lateinit var waterfallNotes: Array<WaterfallNote>

    /**
     * 所有八度虚线的横坐标列表
     */
    var octaveLineXList: List<Int>? = null

    /**
     * 曲谱总进度（总时长），单位毫秒
     */
    private var totalProgress = 0

    /**
     * 瀑布流音块当前状态，不对外暴露
     */
    private lateinit var waterfallNoteStatus: Array<WaterfallNoteStatusEnum?>

    /**
     * 瀑布流音块当前状态枚举：初始化状态、正在演奏状态、演奏结束状态
     */
    private enum class WaterfallNoteStatusEnum {
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
     * 瀑布流循环绘制线程，不对外暴露
     */
    private var waterfallDownNotesThread: WaterfallDownNotesThread? = null

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
         * 瀑布流音符之间的绘制空缺间隔时间，单位毫秒，防止同音高的音符连续绘制时看起来粘在一起，小于这个值的音符将不可见
         */
        const val NOTE_MIN_INTERVAL = 10
    }

    init {
        // 保持屏幕常亮
        holder.setKeepScreenOn(true)
        // 通过皮肤加载背景图、进度条图片
        backgroundImage = SkinImageLoadUtil.loadImage(context, "background_hd")
        progressBarImage = SkinImageLoadUtil.loadImage(context, "progress_bar")
        progressBarBaseImage = SkinImageLoadUtil.loadImage(context, "progress_bar_base")
    }

    /**
     * 开始播放
     */
    fun startPlay(waterfallNotes: Array<WaterfallNote>) {
        // 停止上一次播放，确保不会重复开线程进行播放
        stopPlay()
        // 初始化音符相关数据、状态
        this.waterfallNotes = waterfallNotes
        waterfallNoteStatus = arrayOfNulls(waterfallNotes.size)
        Arrays.fill(waterfallNoteStatus, WaterfallNoteStatusEnum.INIT)
        // 计算曲谱总进度 = 曲谱总时长 + view的高度，因为要等最后一个音符绘制完的时间才是曲谱最终结束的时间
        totalProgress = waterfallNotes.maxOf { it.endTime } + height
        // 初始化背景图的绘制范围
        backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        // 初始化进度条背景图（基底）的绘制坐标
        progressBarBaseRect = RectF(0f, 0f, width.toFloat(), progressBarBaseImage!!.height.toFloat())
        // 初始化进度条的绘制坐标，初始情况下，进度条宽度为0
        progressBarRect = RectF(0f, 0f, 0f, progressBarImage!!.height.toFloat())
        // 开启绘制线程
        waterfallDownNotesThread = WaterfallDownNotesThread()
        waterfallDownNotesThread!!.start()
    }

    /**
     * 暂停播放
     */
    fun pausePlay() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread!!.isRunning) {
            waterfallDownNotesThread!!.isPause = true
        }
    }

    /**
     * 继续播放
     */
    fun resumePlay() {
        if (waterfallDownNotesThread != null && waterfallDownNotesThread!!.isRunning) {
            waterfallDownNotesThread!!.isPause = false
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
        if (waterfallDownNotesThread != null && waterfallDownNotesThread!!.isRunning) {
            waterfallDownNotesThread!!.isRunning = false
        }
        waterfallDownNotesThread = null
    }

    val isPlaying: Boolean
        /**
         * 是否正在播放
         */
        get() = if (waterfallDownNotesThread != null && waterfallDownNotesThread!!.isRunning) {
            !waterfallDownNotesThread!!.isPause
        } else {
            false
        }

    private val playProgress: Int
        /**
         * 获取目前的播放进度，单位毫秒，如果未在播放，返回0
         */
        get() = if (waterfallDownNotesThread != null && waterfallDownNotesThread!!.isRunning) {
            waterfallDownNotesThread!!.progress
        } else 0

    /**
     * 销毁view，释放资源
     */
    fun destroy() {
        // 释放图片资源
        if (progressBarImage != null) {
            progressBarImage!!.recycle()
            progressBarImage = null
        }
        if (progressBarBaseImage != null) {
            progressBarBaseImage!!.recycle()
            progressBarBaseImage = null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // 有触摸按下view时，记录此时按下的坐标，后续计算手指滑动了多少坐标
                lastX = event.x.coerceAtLeast(0.0f)
                // 单独触摸按下view时，触发暂停/继续播放操作
                pauseOrResumePlay()
            }

            MotionEvent.ACTION_MOVE ->
                // 检测是否为滑动：手指的横坐标偏离原来位置10像素，因按压时仅仅是普通点击，滑动的偏移像素很少时也会执行ACTION_MOVE事件
                if (isScrolling || abs(event.x.coerceAtLeast(0.0f) - lastX) > 10) {
                    // 刚刚检测到有滑动时，不管原来是在播放还是暂停，统一触发暂停下落瀑布
                    pausePlay()
                    // 根据目前手指滑动的X坐标的偏移量占总宽度的比例，来计算本次手指滑动的偏移量时间（进度）
                    val moveProgressOffset =
                            ((event.x.coerceAtLeast(0.0f) - lastX) / width * totalProgress).toInt()
                    // 如果经过计算后的进度落在总进度之内，则继续执行设置进度操作
                    if (moveProgressOffset + playProgress in 0..totalProgress) {
                        waterfallDownNotesThread!!.progressOffset += moveProgressOffset
                    }
                    // 更新此时滑动后的手指坐标，后续计算手指滑动了多少坐标
                    lastX = event.x.coerceAtLeast(0.0f)
                    // 有检测到在view上滑动手指，置正在滑动标记为true
                    // 正在滑动标记为true时会停止进行瀑布流监听，避免滑动时播放大量声音和键盘效果
                    // 可参考调用音符监听的代码：WaterfallDownNotesThread.handleWaterfallNoteListener
                    isScrolling = true
                }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isScrolling) {
                    // 在滑动后，手指抬起，则触发继续播放
                    resumePlay()
                    // 清空正在滑动的标记为false
                    isScrolling = false
                }
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        // 返回 true 表示已处理点击事件
        return true
    }

    /**
     * 手动触发把目前键盘上所有按下的音符（PLAYING状态的）进行清除
     */
    private fun triggerAllPlayingStatusNoteLeave() {
        if (noteFallListener != null) {
            for (i in waterfallNotes.indices) {
                if (waterfallNoteStatus[i] == WaterfallNoteStatusEnum.PLAYING) {
                    noteFallListener!!.onNoteLeave(waterfallNotes[i])
                }
            }
        }
    }

    /**
     * 根据当前的进度，重新设置音符的状态
     */
    private fun updateNoteStatusByProgress(progress: Int) {
        // 执行重新设置音符的状态，根据起始时间和结束时间，结合目前进度计算坐标来确定状态
        for (i in waterfallNotes.indices) {
            if (progress - waterfallNotes[i].endTime + NOTE_MIN_INTERVAL > height) {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.FINISH
            } else if (progress - waterfallNotes[i].startTime > height) {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.PLAYING
            } else if (progress - waterfallNotes[i].startTime > 0) {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.APPEAR
            } else {
                waterfallNoteStatus[i] = WaterfallNoteStatusEnum.INIT
            }
        }
    }

    /**
     * 瀑布流双缓冲绘制线程
     */
    private inner class WaterfallDownNotesThread : Thread() {
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
        private var pauseProgress: Int? = null

        /**
         * 播放进度
         */
        var progress = 0

        /**
         * 播放进度的偏移量，用于暂停后继续时的进度更新
         */
        private var progressPauseOffset = 0

        /**
         * 用户手动调节拖动进度条导致的播放进度的偏移量，仅本次拖动生效，拖动结束后此值清零
         */
        var progressOffset = 0

        override fun run() {
            // 记录绘制的起始时间
            val startPlayTime = System.currentTimeMillis()
            // 音块Paint
            val notePaint = Paint()
            notePaint.style = Paint.Style.FILL
            // 每个八度的虚线Paint
            val octaveLinePaint = Paint()
            octaveLinePaint.color = Color.WHITE
            octaveLinePaint.alpha = 128
            octaveLinePaint.strokeWidth = 3f
            octaveLinePaint.style = Paint.Style.STROKE
            octaveLinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            val octaveLinePath = Path()
            // 循环绘制，直到外部有触发终止绘制
            while (isRunning) {
                // 根据系统时间，计算距离开始播放的时间点，间隔多长时间
                // 计算过程中，减掉暂停播放后继续播放所带来的时间差
                var playIntervalTime =
                        (System.currentTimeMillis() - startPlayTime - progressPauseOffset).toInt()
                // 如果处于暂停状态，则存储当前的播放进度，如果突然继续播放了，则移除存储的播放进度
                if (isPause && pauseProgress == null) {
                    // 只有第一次监测到isPause为true时才触发这里，重复置isPause为true，不会重复触发，确保进度保存的是第一次触发暂停时的
                    pauseProgress = playIntervalTime
                } else if (!isPause && pauseProgress != null) {
                    // 第一次监测到isPause为false，更新偏移量，使之后继续播放时能按照刚暂停时的进度来继续
                    // updatePauseOffset可以理解为本次暂停过程一共暂停了多少毫秒，所以需要加偏移量补偿掉这些毫秒
                    // 由于暂停的时候会人工拖动进度条操纵进度的修改，所以需要把人工操纵进度的结果progressOffset也算上
                    // 都处理结束之后再清零progressOffset
                    val updatePauseOffset = playIntervalTime - pauseProgress!! - progressOffset
                    progressPauseOffset += updatePauseOffset
                    // 接下来当前的绘制帧，调整playIntervalTime，也减掉偏移量的改变值，使当前帧的绘制顺滑起来
                    playIntervalTime -= updatePauseOffset
                    // 清零progressOffset，如果不清零，下次拖动修改进度的时候又要变化progressOffset，会导致和前一次的值冲突
                    progressOffset = 0
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
                progress = (if (isPause) pauseProgress else playIntervalTime)!! + progressOffset
                // 如果发现进度大于总进度，说明播放完成，此时标记暂停
                // 如果不标记暂停，progress在曲谱播放结束之后也一直增大，在播放结束后往回拖进度条，可能拉很长时间进度条都没到100%之前
                if (progress > totalProgress) {
                    isPause = true
                }
                // 确定是否有音块到达了view底部或完全移出了view，如果有，调用监听
                handleWaterfallNoteListener()
                // 执行绘制，在锁canvas绘制期间，尽可能执行最少的代码逻辑操作，保证绘制性能
                doDrawWaterfall(notePaint, octaveLinePaint, octaveLinePath)
            }
        }

        /**
         * 执行绘制瀑布流
         */
        private fun doDrawWaterfall(
                notePaint: Paint,
                octaveLinePaint: Paint,
                octaveLinePath: Path
        ) {
            var canvas: Canvas? = null
            try {
                // 获取绘制canvas
                canvas = if (holder.surface.isValid) holder.lockCanvas() else null
                canvas?.let {
                    // 绘制背景图
                    it.drawBitmap(backgroundImage!!, null, backgroundRect!!, null)
                    // 八度虚线绘制
                    drawOctaveLine(it, octaveLinePaint, octaveLinePath)
                    // 音块绘制
                    drawNotes(it, notePaint)
                    // 进度条绘制
                    drawProgressBar(it)
                }
            } finally {
                // 解锁画布
                canvas?.let {
                    if (holder.surface.isValid) {
                        holder.unlockCanvasAndPost(it)
                    }
                }
            }
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
                if (octaveLineX in 0 until width) {
                    octaveLinePath.reset()
                    octaveLinePath.moveTo(octaveLineX.toFloat(), 0f)
                    octaveLinePath.lineTo(octaveLineX.toFloat(), height.toFloat())
                    // 执行八度虚线绘制
                    canvas.drawPath(octaveLinePath, octaveLinePaint)
                }
            }
        }

        /**
         * 绘制所有应该绘制的音块
         */
        private fun drawNotes(canvas: Canvas, notePaint: Paint) {
            for ((index, waterfallNote) in waterfallNotes.withIndex()) {
                // 瀑布流音块当前在view内对用户可见的，才绘制
                if (noteIsVisible(waterfallNote)) {
                    // 根据音符的左右手确定音块的颜色
                    val color = if (waterfallNote.leftHand) leftHandNoteColor else rightHandNoteColor
                    // 根据音符是否为弹奏中状态，确定颜色是否要高亮显示
                    notePaint.color = if (waterfallNoteStatus[index] == WaterfallNoteStatusEnum.PLAYING) highlightColor(color) else color
                    // 根据音符的力度，确定音块绘制的透明度
                    notePaint.alpha = minOf(waterfallNote.volume / 100f * 128 * 2, 255f).toInt()
                    // 绘制音块
                    canvas.drawRect(
                            waterfallNote.left,
                            (progress - waterfallNote.endTime + NOTE_MIN_INTERVAL).toFloat(),
                            waterfallNote.right,
                            (progress - waterfallNote.startTime).toFloat(),
                            notePaint
                    )
                }
            }
        }

        /**
         * 使颜色高亮
         */
        private fun highlightColor(color: Int): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            val alpha = Color.alpha(color)

            val highlightedRed = minOf(red + 50, 255)
            val highlightedGreen = minOf(green + 50, 255)
            val highlightedBlue = minOf(blue + 50, 255)
            val highlightedAlpha = minOf(alpha + 50, 255)

            return Color.argb(highlightedAlpha, highlightedRed, highlightedGreen, highlightedBlue)
        }

        /**
         * 绘制进度条
         */
        private fun drawProgressBar(canvas: Canvas) {
            // 绘制进度条背景（基底）图，它包含完整的一个进度条轮廓
            canvas.drawBitmap(progressBarBaseImage!!, null, progressBarBaseRect!!, null)
            // 修改当前进度条的最右端绘制坐标，进度条最右侧坐标 = 曲谱播放进度百分比 * view的宽度
            progressBarRect!!.right = progress.toFloat() / totalProgress * width
            // 绘制进度条图片
            canvas.drawBitmap(progressBarImage!!, null, progressBarRect!!, null)
        }

        /**
         * 音符是否在view中对用户可见
         */
        private fun noteIsVisible(waterfallNote: WaterfallNote): Boolean {
            // 1、音符的上下左右边界都要在view内，对于左右边界，可以允许有一部分出现在view内
            // 2、音符间隔过小，不绘制
            // 3、进度（毫秒数） - 音符开始时间小于0的时候，音符在view的上方，未开始出现，这种情况下过滤掉，不绘制
            // 4、进度（毫秒数） - 音符结束时间大于view的高度的时候，音符已经完全离开了view的下边界，这种情况下过滤掉，不绘制
            //
            return waterfallNote.right > 0 && waterfallNote.left < width && waterfallNote.interval() > NOTE_MIN_INTERVAL
                    && progress - waterfallNote.startTime > 0 && progress - waterfallNote.endTime + NOTE_MIN_INTERVAL < height
        }

        /**
         * 确定是否有音块到达了view底部或完全移出了view，如果有，调用监听
         */
        private fun handleWaterfallNoteListener() {
            // 如果没有监听器，或者目前view处于拖动来改变进度的情形，则不做任何监听操作
            if (noteFallListener == null || isScrolling) {
                return
            }
            for (i in waterfallNotes.indices) {
                if (waterfallNoteStatus[i] == WaterfallNoteStatusEnum.PLAYING && progress - waterfallNotes[i].endTime + NOTE_MIN_INTERVAL > height) {
                    // 判断到音块刚刚完全离开view时，调用监听，通过叠加音符状态的判断，保证这个监听不会重复调用
                    noteFallListener!!.onNoteLeave(waterfallNotes[i])
                    waterfallNoteStatus[i] = WaterfallNoteStatusEnum.FINISH
                } else if (waterfallNoteStatus[i] == WaterfallNoteStatusEnum.APPEAR && progress - waterfallNotes[i].startTime > height) {
                    // 判断到音块刚刚下落到view的下边界时，调用监听，通过叠加音符状态的判断，保证这个监听不会重复调用
                    noteFallListener!!.onNoteFallDown(waterfallNotes[i])
                    waterfallNoteStatus[i] = WaterfallNoteStatusEnum.PLAYING
                } else if (waterfallNoteStatus[i] == WaterfallNoteStatusEnum.INIT && progress - waterfallNotes[i].startTime > 0) {
                    // 判断到音块刚刚在view的上边界出现时，调用监听，通过叠加音符状态的判断，保证这个监听不会重复调用
                    noteFallListener!!.onNoteAppear(waterfallNotes[i])
                    waterfallNoteStatus[i] = WaterfallNoteStatusEnum.APPEAR
                }
            }
        }
    }
}