package ly.pp.justpiano3.midi;

/**
 * 监听midi键盘连接和断开连接
 */
public interface MidiConnectionListener {

    /**
     * midi键盘连接
     */
    void onMidiConnect();

    /**
     * midi键盘断开连接
     */
    void onMidiDisconnect();

    /**
     * midi键盘接收到消息
     * 注意：仅用于Android Q及以上，使用C++回调通知接收到设备消息的情形
     */
    void onMidiReceiveMessage(byte pitch, byte volume);
}
