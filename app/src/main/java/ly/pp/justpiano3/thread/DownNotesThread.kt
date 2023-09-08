package ly.pp.justpiano3.thread

import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.activity.PianoPlay
import ly.pp.justpiano3.enums.GameModeEnum

class DownNotesThread(
    private val jpApplication: JPApplication,
    private val noteFallDownSpeed: Float,
    private val pianoPlay: PianoPlay
) : Thread() {

    /**
     * 歌曲暂停时，目前的播放进度，内部保存的变量，不对外暴露
     */
    private var pauseProgress: Int? = null

    /**
     * 处于暂停状态的时间累加，作为时间偏移进行计算
     */
    private var progressPauseTime = 0

    override fun run() {
        val startPlayTime = System.currentTimeMillis()
        while (pianoPlay.isPlayingStart) {
            // 和瀑布流原理相同，具体解释详见瀑布流
            var playIntervalTime =
                ((System.currentTimeMillis() - startPlayTime) / noteFallDownSpeed - progressPauseTime).toInt()
            val isPause = !pianoPlay.playView.startFirstNoteTouching ||
                    jpApplication.gameMode == GameModeEnum.PRACTISE.code && !pianoPlay.playView.isTouchRightNote
            if (isPause && pauseProgress == null) {
                pauseProgress = playIntervalTime
            } else if (!isPause && pauseProgress != null) {
                val updatePauseOffset = playIntervalTime - pauseProgress!!
                progressPauseTime += updatePauseOffset
                playIntervalTime -= updatePauseOffset
                pauseProgress = null
            }
            jpApplication.animPosition = (if (isPause) pauseProgress else playIntervalTime)!!
        }
    }
}