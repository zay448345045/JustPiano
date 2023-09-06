package ly.pp.justpiano3.thread

import android.content.Context
import kotlinx.coroutines.*
import ly.pp.justpiano3.JPApplication
import ly.pp.justpiano3.enums.PlaySongsModeEnum
import ly.pp.justpiano3.utils.SoundEngineUtil
import ly.pp.justpiano3.view.play.PmFileParser

object SongPlay {

    /**
     * 播放模式
     */
    var playSongsMode = PlaySongsModeEnum.ONCE

    /**
     * 协程job
     */
    private var job: Job? = null

    /**
     * 播放结束时的回调
     */
    var callBack: CallBack? = null

    /**
     * 开始播放
     */
    fun startPlay(context: Context, songFilePath: String, tune: Int) {
        if (job != null) {
            stopPlay()
        }
        val pmFileParser = PmFileParser(context, songFilePath)
        job = MainScope().launch {
            for (i in pmFileParser.noteArray.indices) {
                if (!isActive) {
                    return@launch
                }
                delay(pmFileParser.tickArray[i].toLong() * pmFileParser.pmGlobalSpeed)
                SoundEngineUtil.playSound(pmFileParser.noteArray[i] + tune, pmFileParser.volumeArray[i].toInt())
            }
            delay(1000)
            val nextSongFilePath = computeNextSongByPlaySongsMode(songFilePath, playSongsMode)
            if (callBack != null && !nextSongFilePath.isNullOrEmpty()) {
                callBack!!.onSongChangeNext(nextSongFilePath)
            }
        }
    }

    private fun computeNextSongByPlaySongsMode(songFilePath: String, playSongsMode: PlaySongsModeEnum): String? {
        when (playSongsMode) {
            PlaySongsModeEnum.RECYCLE -> return songFilePath
            PlaySongsModeEnum.RANDOM -> {
                val songs = JPApplication.getSongDatabase().songDao().getSongByRightHandDegreeWithRandom(0, 10)
                return if (songs.isEmpty()) null else songs[0].filePath
            }
            PlaySongsModeEnum.FAVOR_RANDOM -> {
                val songInFavoriteWithRandom = JPApplication.getSongDatabase().songDao().getSongInFavoriteWithRandom()
                return if (songInFavoriteWithRandom.isEmpty()) null else songInFavoriteWithRandom[0].filePath
            }
            PlaySongsModeEnum.FAVOR -> {
                val favoriteSongList = JPApplication.getSongDatabase().songDao().getFavoriteSongs()
                for ((index, song) in favoriteSongList.withIndex()) {
                    if (song.filePath == songFilePath) {
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
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return job != null && job!!.isActive
    }

    /**
     * 回调
     */
    interface CallBack {
        /**
         * This will be called when song changes to the next and ready to start playing
         */
        fun onSongChangeNext(songFilePath: String)
    }
}