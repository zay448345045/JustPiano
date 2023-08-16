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
}
