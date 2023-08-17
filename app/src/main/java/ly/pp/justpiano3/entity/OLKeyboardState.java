package ly.pp.justpiano3.entity;

import lombok.Data;

/**
 * 记录在线键盘模式用户状态
 */
@Data
public class OLKeyboardState {

    /**
     * 是否静音
     */
    private boolean muted;

    /**
     * midi键盘是否打开
     */
    private boolean midiKeyboardOn;

    /**
     * 此位置是否有人
     */
    private boolean hasUser;

    /**
     * 此位置是否正在弹奏（闪烁）
     */
    private boolean isPlaying;
}
