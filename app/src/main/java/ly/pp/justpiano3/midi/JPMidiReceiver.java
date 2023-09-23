package ly.pp.justpiano3.midi;

import android.media.midi.MidiReceiver;
import android.os.Build;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class JPMidiReceiver extends MidiReceiver {

    /**
     * midi监听器
     */
    private final MidiConnectionListener midiConnectionListener;

    /**
     * 构造，传入MidiConnectionListener
     */
    public JPMidiReceiver(MidiConnectionListener midiConnectionListener) {
        this.midiConnectionListener = midiConnectionListener;
    }

    @Override
    public void onSend(byte[] msg, int offset, int count, long timestamp) {
        // 保持和C++层调用一致的API：onMidiMessageReceive
        for (int i = offset; i < offset + count; i += 3) {
            if ((msg[i] & 0xF0) == 0x90) {
                midiConnectionListener.onMidiMessageReceive(msg[i + 1], msg[i + 2]);
            } else if ((msg[i] & 0xF0) == 0x80) {
                midiConnectionListener.onMidiMessageReceive(msg[i + 1], (byte) 0);
            }
        }
    }
}
