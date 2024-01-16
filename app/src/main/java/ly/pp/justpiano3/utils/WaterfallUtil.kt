package ly.pp.justpiano3.utils

import android.graphics.RectF
import ly.pp.justpiano3.view.KeyboardView

/**
 * 瀑布流Util
 */
class WaterfallUtil {

    companion object {

        /**
         * 根据一个键盘view的琴键坐标信息，计算出（瀑布流音块）长条的坐标和宽度（略小于琴键的宽度）
         * 返回值为瀑布流音符横坐标的左边界和右边界
         */
        @JvmStatic
        fun convertToWaterfallWidth(keyboardView: KeyboardView?, pitch: Byte): Pair<Float, Float> {
            val rectF = convertPitchToReact(keyboardView, pitch) ?: return Pair(-1f, -1f)
            // 根据比例计算瀑布流的宽度
            val waterfallWidth =
                if (isBlackKey(pitch)) rectF.width() * BLACK_KEY_WATERFALL_WIDTH_FACTOR
                else rectF.width() * KeyboardView.BLACK_KEY_WIDTH_FACTOR * BLACK_KEY_WATERFALL_WIDTH_FACTOR
            // 根据中轴线和新的宽度计算坐标，返回
            return Pair(rectF.centerX() - waterfallWidth / 2, rectF.centerX() + waterfallWidth / 2)
        }

        /**
         * 根据一个midi音高，判断它是否为黑键
         */
        private fun isBlackKey(pitch: Byte): Boolean {
            val pitchInOctave = pitch % NOTES_PER_OCTAVE
            for (blackKeyOffsetInOctave in KeyboardView.BLACK_KEY_OFFSET) {
                if (pitchInOctave == blackKeyOffsetInOctave) {
                    return true
                }
            }
            return false
        }

        /**
         * 根据midi音高值，获取对应琴键的绘制绝对坐标位置，供外界取用
         * 注意黑键和白键的绝对坐标位置的宽度有差异
         */
        private fun convertPitchToReact(keyboardView: KeyboardView?, pitch: Byte): RectF? {
            if (keyboardView?.notesOnArray == null) {
                return null
            }
            val pitchInScreen = keyboardView.getPitchInScreen(pitch)
            // 入参的音高不在键盘view所绘制的琴键范围内，返回空
            if (pitchInScreen < 0 || pitchInScreen >= keyboardView.notesOnArray!!.size) {
                return null
            }
            val octaveI = pitchInScreen / KeyboardView.NOTES_PER_OCTAVE
            val noteI = pitchInScreen % KeyboardView.NOTES_PER_OCTAVE
            return if (KeyboardView.KEY_IMAGE_TYPE[noteI] == KeyboardView.KeyImageTypeEnum.BLACK_KEY) {
                keyboardView.blackKeyRectArray?.get(
                    KeyboardView.OCTAVE_PITCH_TO_KEY_INDEX[noteI] +
                            octaveI * KeyboardView.BLACK_NOTES_PER_OCTAVE
                )
            } else {
                keyboardView.whiteKeyRectArray?.get(
                    KeyboardView.OCTAVE_PITCH_TO_KEY_INDEX[noteI] +
                            octaveI * KeyboardView.WHITE_NOTES_PER_OCTAVE
                )
            }
        }

        /**
         * 每个八度的音符数量
         */
        private const val NOTES_PER_OCTAVE = 12

        /**
         * 瀑布的宽度占键盘黑键宽度的百分比
         */
        private const val BLACK_KEY_WATERFALL_WIDTH_FACTOR = 0.75f

        /**
         * 瀑布流音符最大高度
         */
        const val NOTE_MAX_HEIGHT = 1000
    }
}