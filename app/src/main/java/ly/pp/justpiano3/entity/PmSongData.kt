package ly.pp.justpiano3.entity

/**
 * pm文件解析数据
 */
data class PmSongData(
    /**
     * 曲谱名称，读取文件头结果
     */
    val songName: String,

    /**
     * 左手难度，读取文件结果
     */
    val leftHandDegree: Float,

    /**
     * 右手难度，读取文件结果
     */
    val rightHandDegree: Float,

    /**
     * 曲谱时长(单位秒)计算结果
     */
    val songTime: Int,

    /**
     * 曲谱全局速度，此值是文件倒数第二个字节，当前固定为5，不读取文件
     */
    var globalSpeed: Int,

    /**
     * 音符音高数组，读取文件结果
     */
    val pitchArray: ByteArray,

    /**
     * 音符时间间隔数组，读取文件结果
     */
    val tickArray: ByteArray,

    /**
     * 音符音轨数组，读取文件结果
     */
    val trackArray: ByteArray,

    /**
     * 音符音量数组，读取文件结果
     */
    val volumeArray: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PmSongData
        if (songName != other.songName) return false
        if (leftHandDegree != other.leftHandDegree) return false
        if (rightHandDegree != other.rightHandDegree) return false
        if (songTime != other.songTime) return false
        if (globalSpeed != other.globalSpeed) return false
        if (!pitchArray.contentEquals(other.pitchArray)) return false
        if (!tickArray.contentEquals(other.tickArray)) return false
        if (!trackArray.contentEquals(other.trackArray)) return false
        if (!volumeArray.contentEquals(other.volumeArray)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = songName.hashCode()
        result = 31 * result + leftHandDegree.hashCode()
        result = 31 * result + rightHandDegree.hashCode()
        result = 31 * result + songTime
        result = 31 * result + globalSpeed
        result = 31 * result + pitchArray.contentHashCode()
        result = 31 * result + tickArray.contentHashCode()
        result = 31 * result + trackArray.contentHashCode()
        result = 31 * result + volumeArray.contentHashCode()
        return result
    }
}