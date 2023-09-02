package ly.pp.justpiano3.entity

/**
 * 记录在线键盘模式用户状态
 */
data class OLKeyboardState (
    /**
     * 是否静音
     */
    var isMuted: Boolean,

    /**
     * midi键盘是否打开
     */
    var isMidiKeyboardOn: Boolean,

    /**
     * 此位置是否有人
     */
    var isHasUser: Boolean,

    /**
     * 此位置是否正在弹奏（闪烁）
     */
    var isPlaying: Boolean,
)