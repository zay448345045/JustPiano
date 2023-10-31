package ly.pp.justpiano3.utils

import android.graphics.RectF
import ly.pp.justpiano3.view.KeyboardView

/**
 * 瀑布流Util
 */
class WaterfallUtil {

    companion object {

        /**
         * 根据一个midi音高，判断它是否为黑键
         */
        private fun isBlackKey(pitch: Byte): Boolean {
            val pitchInOctave = pitch % NOTES_PER_OCTAVE
            for (blackKeyOffsetInOctave in KeyboardView.BLACK_KEY_OFFSETS) {
                if (pitchInOctave == blackKeyOffsetInOctave) {
                    return true
                }
            }
            return false
        }

        /**
         * 将琴键RectF的宽度，转换成瀑布流长条的宽度（略小于琴键的宽度）
         * 返回值为瀑布流音符横坐标的左边界和右边界
         */
        fun convertWidthToWaterfallWidth(pitch: Byte, rectF: RectF?): Pair<Float, Float> {
            if (rectF == null) {
                return Pair(-1f, -1f)
            }
            // 根据比例计算瀑布流的宽度
            val waterfallWidth = if (isBlackKey(pitch)) rectF.width() * BLACK_KEY_WATERFALL_WIDTH_FACTOR
            else rectF.width() * KeyboardView.BLACK_KEY_WIDTH_FACTOR * BLACK_KEY_WATERFALL_WIDTH_FACTOR
            // 根据中轴线和新的宽度计算坐标，返回
            return Pair(rectF.centerX() - waterfallWidth / 2, rectF.centerX() + waterfallWidth / 2)
        }

        /**
         * 每个八度的音符数量
         */
        private const val NOTES_PER_OCTAVE = 12

        /**
         * 瀑布的宽度占键盘黑键宽度的百分比
         */
        private const val BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.8f

        /**
         * 瀑布流音符最大高度
         */
        const val NOTE_MAX_HEIGHT = 1000
    }
}