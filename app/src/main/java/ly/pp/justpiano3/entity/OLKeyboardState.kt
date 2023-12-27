package ly.pp.justpiano3.entity

import java.io.Serializable

/**
 * 记录在线键盘模式用户状态
 */
data class OLKeyboardState(
    /**
     * 是否静音
     */
    var muted: Boolean,

    /**
     * midi键盘是否打开
     */
    var midiKeyboardOn: Boolean,

    /**
     * 此位置是否有人
     */
    var hasUser: Boolean,
) : Serializable {}