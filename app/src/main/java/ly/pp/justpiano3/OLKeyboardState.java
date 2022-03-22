package ly.pp.justpiano3;

/**
 * 记录在线键盘模式用户状态
 */
class OLKeyboardState {

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

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMidiKeyboardOn() {
        return midiKeyboardOn;
    }

    public void setMidiKeyboardOn(boolean midiKeyboardOn) {
        this.midiKeyboardOn = midiKeyboardOn;
    }

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(boolean hasUser) {
        this.hasUser = hasUser;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
