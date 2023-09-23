package ly.pp.justpiano3.midi;

/**
 * 监听midi键盘连接和断开连接
 */
public interface MidiConnectionListener {

    /**
     * midi键盘连接，在主线程调用
     */
    void onMidiConnect();

    /**
     * midi键盘断开连接，在主线程调用
     */
    void onMidiDisconnect();

    /**
     * midi键盘接收到消息，不一定在主线程调用
     *
     * @param pitch  原始midi音高
     * @param volume midi音符力度
     */
    void onMidiMessageReceive(byte pitch, byte volume);
}
