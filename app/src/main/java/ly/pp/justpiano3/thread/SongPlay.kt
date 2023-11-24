package ly.pp.justpiano3.thread

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.enums.PlaySongsModeEnum
import ly.pp.justpiano3.utils.PmSongUtil
import ly.pp.justpiano3.utils.SoundEngineUtil
import ly.pp.justpiano3.utils.ThreadPoolUtil

object SongPlay {

    /**
     * 播放模式
     */
    var playSongsMode = PlaySongsModeEnum.ONCE

    /**
     * 自定义线程池产生的协程作用域
     */
    private val threadPoolScope =
        CoroutineScope(ThreadPoolUtil.THREAD_POOL_EXECUTOR.asCoroutineDispatcher())

    /**
     * 协程job
     */
    private var job: Job? = null

    /**
     * 播放结束时切换下一首曲谱的回调
     */
    var callBack: CallBack? = null

    /**
     * 开始播放
     */
    fun startPlay(context: Context, songFilePath: String, tune: Int) {
        startPlay(context, songFilePath, 0, tune)
    }

    /**
     * 开始播放，支持指定起始播放进度
     */
    fun startPlay(context: Context, songFilePath: String, songProgress: Int, tune: Int) {
        PmSongUtil.parsePmDataByFilePath(context, songFilePath)?.let {
            job?.cancel()
            job = threadPoolScope.launch {
                val playingPitchMap: MutableMap<Byte, Byte> = mutableMapOf()
                var totalProgressTime = 0
                for (i in it.pitchArray.indices) {
                    if (!isActive) {
                        return@launch
                    }
                    // 先累加，过滤掉在入参给出的曲谱进度之前的音符
                    val delayTime = it.tickArray[i] * it.globalSpeed
                    totalProgressTime += delayTime
                    if (totalProgressTime < songProgress) {
                        continue
                    }
                    // 当前音符间隔大于0且不是曲谱的第一个音符，则触发延时
                    if (delayTime > 0 && i > 0) {
                        delay(delayTime.toLong())
                        // 延时后，判断如果不是pm文件中用于延音的空音符，则清理（停止播放）当前音轨正在播放中的音符
                        if (!PmSongUtil.isPmDefaultEmptyFilledData(it, i)) {
                            val iterator = playingPitchMap.entries.iterator()
                            while (iterator.hasNext()) {
                                val entry = iterator.next()
                                if (entry.value == it.trackArray[i]) {
                                    SoundEngineUtil.stopPlaySound(entry.key)
                                    iterator.remove()
                                }
                            }
                        }
                    }
                    // 执行播放当前音符，并把当前音符也加入到正在播放的音符之中，用于之后的清理（按音轨停止）
                    val pitch = (it.pitchArray[i] + tune).toByte()
                    SoundEngineUtil.playSound(pitch,
                        (it.volumeArray[i] * Byte.MAX_VALUE / 100f).toInt().toByte()
                    )
                    playingPitchMap[pitch] = it.trackArray[i]
                }
                // 播放完成之后，最后一次清理（停止）所有正在播放的音符
                playingPitchMap.values.forEach { SoundEngineUtil.stopPlaySound((it)) }
                playingPitchMap.clear()
                // 获取下一首应该播放的曲谱（如果本地或房间选择了连续播放等选项的话）
                val nextSongFilePath = computeNextSongByPlaySongsMode(songFilePath)
                // 延时一小段时间，然后触发播放下一首曲谱的回调
                delay(1000)
                callBack?.onSongChangeNext(nextSongFilePath)
            }
        }
    }

    private fun computeNextSongByPlaySongsMode(currentSongFilePath: String): String? {
        when (playSongsMode) {
            PlaySongsModeEnum.RECYCLE -> return currentSongFilePath
            PlaySongsModeEnum.RANDOM -> {
                val songs = JPApplication.getSongDatabase().songDao()
                    .getSongByRightHandDegreeWithRandom(0, 10)
                return if (songs.isEmpty()) null else songs[0].filePath
            }

            PlaySongsModeEnum.FAVOR_RANDOM -> {
                val songInFavoriteWithRandom =
                    JPApplication.getSongDatabase().songDao().getSongInFavoriteWithRandom()
                return if (songInFavoriteWithRandom.isEmpty()) null else songInFavoriteWithRandom[0].filePath
            }

            PlaySongsModeEnum.FAVOR -> {
                val favoriteSongList = JPApplication.getSongDatabase().songDao().getFavoriteSongs()
                for ((index, song) in favoriteSongList.withIndex()) {
                    if (song.filePath == currentSongFilePath) {
                        return favoriteSongList[if (index == favoriteSongList.size - 1) 0 else index + 1].filePath
                    }
                }
                return null
            }

            else -> return null
        }
    }

    /**
     * 停止播放
     */
    fun stopPlay() {
        job?.cancel()
        job = null
        SoundEngineUtil.stopPlayAllSounds()
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return job?.isActive == true
    }

    /**
     * 回调
     */
    interface CallBack {
        /**
         * This will be called when song changes to the next and ready to start playing
         */
        fun onSongChangeNext(songFilePath: String?)
    }
}