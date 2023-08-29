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
         * 音符开始时间，以曲谱开始时间为0开始累计计算的值，毫秒
         */
        var startTime: Int,

        /**
         * 音符开始时间，以曲谱开始时间为0开始累计计算的值，毫秒
         */
        var endTime: Int,

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
     * 音符持续时间计算
     */
    fun interval(): Int {
        return endTime - startTime
    }
}