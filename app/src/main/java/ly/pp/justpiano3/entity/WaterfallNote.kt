package ly.pp.justpiano3.entity

/**
 * 瀑布流音符
 */
data class WaterfallNote(
    /**
     * 绘制区域左边界坐标
     */
    var left: Float = 0f,

    /**
     * 绘制区域右边界坐标
     */
    var right: Float = 0f,

    /**
     * 绘制区域上边界坐标，以曲谱开始时间为0开始累计计算的坐标值
     */
    var top: Float = 0f,

    /**
     * 绘制区域下边界坐标，以曲谱开始时间为0开始累计计算的坐标值
     */
    var bottom: Float = 0f,

    /**
     * 瀑布流音块颜色
     */
    var color: Int = 0,

    /**
     * 音高
     */
    var pitch: Byte = 0,

    /**
     * 音量
     */
    var volume: Byte = 0,
)