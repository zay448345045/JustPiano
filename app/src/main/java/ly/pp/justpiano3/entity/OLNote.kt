package ly.pp.justpiano3.entity

/**
 * 音符 缓存在队列用于传输
 */
data class OLNote(
    /**
     * 时间
     */
    var absoluteTime: Long,

    /**
     * 音高
     */
    var pitch: Int,

    /**
     * 音量
     */
    var volume: Int
)