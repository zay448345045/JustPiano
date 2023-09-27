package ly.pp.justpiano3.entity

/**
 * midi文件解析的原始音符
 */
data class OriginalNote(
    /**
     * 音符播放时间，距离midi开始演奏时的时间，单位毫秒
     */
    var playTime: Long = 0,

    /**
     * 音符是否为左手
     */
    var leftHand: Boolean = false,

    /**
     * 音符音高
     */
    var pitch: Byte = 0,

    /**
     * 音符音量
     */
    var volume: Byte = 0
)
