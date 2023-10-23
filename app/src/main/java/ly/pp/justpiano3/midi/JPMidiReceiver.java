package ly.pp.justpiano3.midi;

import android.media.midi.MidiReceiver;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ly.pp.justpiano3.entity.GlobalSetting;

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
        // 保持和C++层调用一致的API，API使用方对使用了C++ midi还是java midi无感知
        for (int i = offset; i < offset + count; i += 3) {
            if ((msg[i] & 0xF0) == 0x90) {
                midiConnectionListener.onMidiMessageReceive(
                        (byte) (msg[i + 1] + GlobalSetting.INSTANCE.getMidiKeyboardTune()), msg[i + 2]);
            } else if ((msg[i] & 0xF0) == 0x80) {
                midiConnectionListener.onMidiMessageReceive(
                        (byte) (msg[i + 1] + GlobalSetting.INSTANCE.getMidiKeyboardTune()), (byte) 0);
            }
        }
    }
}
