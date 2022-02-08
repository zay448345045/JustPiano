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
     * 弹奏音符量速度
     */
    private int speed;

    /**
     * midi键盘是否打开
     */
    private boolean midiKeyboardOn;

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isMidiKeyboardOn() {
        return midiKeyboardOn;
    }

    public void setMidiKeyboardOn(boolean midiKeyboardOn) {
        this.midiKeyboardOn = midiKeyboardOn;
    }
}
