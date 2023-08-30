package ly.pp.justpiano3.entity

/**
 * 瀑布流音符
 */
data class WaterfallNote(
    /**
     * 绘制区域左边界坐标
     */
    var left: Float,

    /**
     * 绘制区域右边界坐标
     */
    var right: Float,

    /**
     * 绘制区域上边界坐标，以曲谱开始时间为0开始累计计算的坐标值
     */
    var top: Float,

    /**
     * 绘制区域下边界坐标，以曲谱开始时间为0开始累计计算的坐标值
     */
    var bottom: Float,

    /**
     * 是否为左手
     */
    var leftHand: Boolean,

    /**
     * 音高
     */
    var pitch: Int,

    /**
     * 音量
     */
    var volume: Int,
) {

    /**
     * 音符上下坐标的间隔
     */
    fun interval(): Float {
        return top - bottom
    }
}