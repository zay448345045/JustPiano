package ly.pp.justpiano3.utils

import android.R.id.input
import android.content.Context
import ly.pp.justpiano3.entity.OriginalNote
import ly.pp.justpiano3.entity.PmSongData
import ly.pp.justpiano3.entity.SongData
import ly.pp.justpiano3.midi.MidiUtil
import ly.pp.justpiano3.midi.ShortMessage
import ly.pp.justpiano3.midi.StandardMidiFileReader
import ly.pp.justpiano3.midi.Track
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.ceil
import kotlin.math.roundToInt


object PmSongUtil {

    /**
     * 决定是否为隐藏键音符的临界间隔时间，目前固定为100，单位毫秒
     */
    private const val HIDE_NOTE_TIME_CRITICAL = 100

    /**
     * pm时间间隔过长时，填充的空音符的间隔时间，目前固定为100
     */
    private const val PM_DEFAULT_FILLED_INTERVAL = 100

    /**
     * pm文件全局速度，目前固定值为5
     */
    const val PM_GLOBAL_SPEED = 5

    /**
     * pm时间间隔过长时，填充的空音符数组
     */
    private val PM_DEFAULT_EMPTY_FILLED_DATA =
        byteArrayOf(PM_DEFAULT_FILLED_INTERVAL.toByte(), 1, 110, 3)

    /**
     * 根据文件路径取出pm曲谱id、分类等的正则表达式pattern
     */
    private val pattern: Pattern = Pattern.compile("/([a-zA-Z]+)(\\d+).pm")

    /**
     * 是否为空拍时补充的延时空音符
     */
    fun isPmDefaultEmptyFilledData(pmSongData: PmSongData, index: Int): Boolean {
        return pmSongData.tickArray[index] == PM_DEFAULT_EMPTY_FILLED_DATA[0]
                && pmSongData.trackArray[index] == PM_DEFAULT_EMPTY_FILLED_DATA[1]
                && pmSongData.pitchArray[index] == PM_DEFAULT_EMPTY_FILLED_DATA[2]
                && pmSongData.volumeArray[index] == PM_DEFAULT_EMPTY_FILLED_DATA[3]
    }

    /**
     * 根据pm文件路径获取曲谱id
     */
    fun getPmSongIdByFilePath(filePath: String): Int? {
        val matcher: Matcher = pattern.matcher(filePath)
        if (matcher.find()) {
            return matcher.group(2)?.toInt()
        }
        return null
    }

    /**
     * 根据pm文件路径获取曲谱分类
     */
    fun getPmSongCategoryByFilePath(filePath: String): String? {
        val matcher: Matcher = pattern.matcher(filePath)
        if (matcher.find()) {
            return matcher.group(1)
        }
        return null
    }

    fun parsePmDataByFilePath(context: Context, filePath: String): PmSongData? {
        val inputStream = getPmFileInputStream(context, filePath)
        inputStream?.let { stream ->
            val pmData = ByteArray(stream.available())
            stream.use {
                it.read(pmData)
            }
            return parsePmDataByBytes(pmData)
        }
        return null
    }

    private fun getPmFileInputStream(context: Context, filePath: String): InputStream? {
        return try {
            context.resources.assets.open(filePath)
        } catch (ignore: Exception) {
            try {
                FileInputStream(
                    context.filesDir.absolutePath + "/Songs/" + filePath.substring(8)
                )
            } catch (ignore: Exception) {
                try {
                    FileInputStream(
                        context.filesDir.absolutePath + "/ImportSongs/" + filePath.substring(8)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    fun parsePmDataByBytes(pmData: ByteArray): PmSongData? {
        try {
            // 获取曲谱名称
            var songName = "未命名曲谱"
            var songNameLength = 0
            for (i in pmData.indices) {
                if (pmData[i] == '\n'.code.toByte()) {
                    songName = String(pmData, 0, i, StandardCharsets.UTF_8)
                    songNameLength = i
                    break
                }
            }
            // 曲谱时长计算
            var i = songNameLength + 2
            var songTime = 0
            while (i < pmData.size) {
                songTime += pmData[i] * PM_GLOBAL_SPEED
                i += 4
            }
            songTime /= 1000
            // 读取左手难度
            val leftHandDegree = pmData[songNameLength + 1] / 10f
            // 填充音符数据
            val pmDataGroupSize = (pmData.size - (songNameLength + 4)) / 4
            val noteArray = ByteArray(pmDataGroupSize)
            val tickArray = ByteArray(pmDataGroupSize)
            val trackArray = ByteArray(pmDataGroupSize)
            val volumeArray = ByteArray(pmDataGroupSize)
            var i1 = 0
            var i2 = 0
            var i3 = 0
            var i4 = 0
            for (index in songNameLength + 2 until pmData.size - 2) {
                when ((index - songNameLength) % 4) {
                    0 -> {
                        noteArray[i1++] = pmData[index]
                    }

                    1 -> {
                        volumeArray[i2++] = pmData[index]
                    }

                    2 -> {
                        tickArray[i3++] = pmData[index]
                    }

                    3 -> {
                        trackArray[i4++] = pmData[index]
                    }
                }
            }
            // 读取右手难度
            val rightHandDegree = pmData[pmData.size - 1] / 10f
            // 数据组装
            return PmSongData(
                songName, leftHandDegree, rightHandDegree, songTime, PM_GLOBAL_SPEED,
                noteArray, tickArray, trackArray, volumeArray
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * midi文件转pm文件
     *
     * @param midiFile midi文件
     * @param pmFile   pm文件
     * @return 曲谱信息
     */
    fun midiFileToPmFile(midiFile: File, pmFile: File): SongData {
        val originalNoteList = parseMidiOriginalNote(midiFile)
            .sortedBy { originalNote -> originalNote.playTime }
        // 获取左手和右手的音符列表
        val leftHandNoteList: MutableList<OriginalNote> = ArrayList()
        val rightHandNoteList: MutableList<OriginalNote> = ArrayList()
        for (originalNote in originalNoteList) {
            if (originalNote.leftHand) {
                leftHandNoteList.add(originalNote)
            } else {
                rightHandNoteList.add(originalNote)
            }
        }
        val songName = midiFile.name.substring(0, midiFile.name.indexOf('.'))
        val rightHandDegree = calculateDegree(rightHandNoteList)
        val leftHandDegree = calculateDegree(leftHandNoteList)
        return SongData(
            calculateSongLength(originalNoteList),
            rightHandDegree,
            leftHandDegree,
            0,
            0,
            0,
            0,
            createPmFile(pmFile, songName, rightHandDegree, leftHandDegree, originalNoteList),
            midiFile
        )
    }

    /**
     * 转换pm，生成临时pm文件
     */
    private fun createPmFile(
        pmFile: File, songName: String, rightHandDegree: Int, leftHandDegree: Int,
        originalNoteList: List<OriginalNote>
    ): File {
        if (pmFile.exists()) {
            pmFile.delete()
            pmFile.createNewFile()
        }
        FileOutputStream(pmFile).use { fileOutputStream ->
            fileOutputStream.write(songName.toByteArray(StandardCharsets.UTF_8))
            fileOutputStream.write('\n'.code)
            fileOutputStream.write(leftHandDegree)
            // 记录上一个音符的起始播放时间，用于计算时间间隔
            var lastNotePlayTime = 0L
            for ((playTime, leftHand, pitch, volume) in originalNoteList) {
                // 实际的音符时间间隔 = 当前音符的起始播放时间 - 上一个音符的起始播放时间
                var intervalTime = (playTime - lastNotePlayTime).toInt()
                // 若时间间隔过高，防止byte字节溢出，则需进行拆解（先循环填充空音符的时间间隔，之后填充剩余的时间间隔）
                while (intervalTime > PM_DEFAULT_FILLED_INTERVAL * PM_GLOBAL_SPEED) {
                    fileOutputStream.write(PM_DEFAULT_EMPTY_FILLED_DATA)
                    intervalTime -= PM_DEFAULT_FILLED_INTERVAL * PM_GLOBAL_SPEED
                }
                // 写入pm音符数据，元素依次为：时间间隔/全局速度、左右手、音高、力度
                fileOutputStream.write(intervalTime / PM_GLOBAL_SPEED)
                fileOutputStream.write(if (leftHand) 1 else 0)
                fileOutputStream.write(pitch.toInt())
                fileOutputStream.write(ceil(volume * 100.0 / 128).toInt())
                // 更新上一个音符的起始播放时间
                lastNotePlayTime = playTime
            }
            fileOutputStream.write(PM_GLOBAL_SPEED)
            fileOutputStream.write(rightHandDegree)
            fileOutputStream.flush()
        }
        return pmFile
    }

    /**
     * 解析midi，输出原始音符列表
     */
    private fun parseMidiOriginalNote(midiFile: File): List<OriginalNote> {
        val originalNoteList: MutableList<OriginalNote> = ArrayList()
        // midi文件转化为midi序列，相当于解析了
        val sequence = StandardMidiFileReader().getSequence(midiFile)
        // 这个缓存就在计算midi中所有的速度变化事件，计算好了之后缓存下来，传入，就不用每次都计算了，提性能用的
        val tempoCache = MidiUtil.TempoCache(sequence)
        // 提取midi的所有音轨，过滤掉无音符的音轨
        val filteredTracks: MutableList<Track> = ArrayList()
        for (track in sequence.tracks) {
            if (hasNote(track)) {
                filteredTracks.add(track)
            }
        }
        val tracks = filteredTracks.toTypedArray()
        for (i in tracks.indices) {
            for (j in 0 until tracks[i].size()) {
                val event = tracks[i][j]
                // 如果事件为ShortMessage且为音符按下的事件
                if (event.message is ShortMessage) {
                    val shortMessage = event.message as ShortMessage
                    if (shortMessage.command == ShortMessage.NOTE_ON) {
                        // 取出音符的音高和力度，力度大于0一定是音符按下了
                        // 力度等于0时，比如museScore导出的midi，力度等于0其实是音符抬起的意思，虽然它不是ShortMessage.NOTE_OFF类型
                        // 其他一些软件导出的mid，许多都是按照midi标准来的，ShortMessage.NOTE_OFF类型表示音符抬起
                        val note = shortMessage.data1
                        val velocity = shortMessage.data2
                        if (velocity > 0) {
                            // tick换算为实际的时间，看JDK源码得知，考虑到了变速，按tick划分变速，计算没有问题
                            val time = MidiUtil.tick2microsecond(sequence, event.tick, tempoCache)
                            originalNoteList.add(
                                OriginalNote(
                                    time / 1000,
                                    i > 0,
                                    note.toByte(),
                                    velocity.toByte()
                                )
                            )
                        }
                    }
                }
            }
        }
        return originalNoteList
    }

    /**
     * 判断音轨中是否有音符
     */
    private fun hasNote(track: Track?): Boolean {
        if (track == null || track.size() == 0) {
            return false
        }
        val trackSize = track.size()
        for (i in 0 until trackSize) {
            val message = track[i].message
            if (message is ShortMessage) {
                if (message.command == ShortMessage.NOTE_ON) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 计算曲谱时长，单位秒
     */
    private fun calculateSongLength(originalNoteList: List<OriginalNote>): Int {
        var maxPlayTime = 0L
        for (originalNote in originalNoteList) {
            if (originalNote.playTime > maxPlayTime) {
                maxPlayTime = originalNote.playTime
            }
        }
        return (maxPlayTime / 1000f).roundToInt()
    }

    /**
     * 给定音符列表，计算难度（客户端显示难度 * 10，返回参数范围在0～100之间）
     */
    private fun calculateDegree(originalNoteList: List<OriginalNote>?): Int {
        if (originalNoteList.isNullOrEmpty()) {
            return 0
        }
        // 非隐藏键音块数量
        var showingNoteCount = 1
        // 每个非隐藏键音符的难度累加，音符的难度 = 1 / 相邻两个音符的时间间隔
        var allNoteDegree = 0f
        // 临时变量，用于记录每一个非隐藏音块的开始时间
        var lastShowingNoteStartTime: Long? = null
        for (i in 0 until originalNoteList.size - 1) {
            if (lastShowingNoteStartTime == null) {
                lastShowingNoteStartTime = originalNoteList[i].playTime
            }
            val noteTimeInterval = originalNoteList[i + 1].playTime - originalNoteList[i].playTime
            // 时间差大于隐藏键的临界值，执行计算非隐藏键音符的难度
            if (noteTimeInterval >= HIDE_NOTE_TIME_CRITICAL) {
                // 每个非隐藏键音符的难度 = 两个非隐藏键音符的持续时间间隔的倒数
                allNoteDegree += (1.0 / (originalNoteList[i + 1].playTime - lastShowingNoteStartTime)).toFloat()
                lastShowingNoteStartTime = null
                showingNoteCount++
            }
        }
        // 曲谱难度 = 1000 * 所有非隐藏键音符的难度之和 / 非隐藏键音符数量
        return (10 * 1000f * allNoteDegree / showingNoteCount).roundToInt()
    }
}