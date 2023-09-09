package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import ly.pp.justpiano3.midi.MidiConnectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MIDI_SERVICE;

public class MidiUtil {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            System.loadLibrary("native_midi");
        }
    }

    /**
     * 每个八度的音符数量、白键数量、黑键数量
     */
    public static final int NOTES_PER_OCTAVE = 12;
    public static final int WHITE_NOTES_PER_OCTAVE = 7;
    public static final int BLACK_NOTES_PER_OCTAVE = 5;

    /**
     * 每个八度内白键的索引
     */
    public static final int[] WHITE_KEY_OFFSETS = {
            0, 2, 4, 5, 7, 9, 11
    };

    /**
     * 每个八度内黑键的索引
     */
    public static final int[] BLACK_KEY_OFFSETS = {
            1, 3, 6, 8, 10
    };

    /**
     * 最大音量值
     */
    public static final byte MAX_VOLUME = 127;

    private static List<MidiConnectionListener> midiConnectionListeners;

    private static MidiManager mMidiManager;

    private static MidiOutputPort midiOutputPort;

    public static void initMidiDevice(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            midiConnectionListeners = new ArrayList<>();
            mMidiManager = (MidiManager) context.getSystemService(MIDI_SERVICE);
            mMidiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                @Override
                public void onDeviceAdded(MidiDeviceInfo info) {
                    openDevice(context, info);
                }

                @Override
                public void onDeviceRemoved(MidiDeviceInfo info) {
                    try {
                        if (midiOutputPort != null) {
                            midiOutputPort.close();
                            Toast.makeText(context, "MIDI设备已断开", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "请重新连接MIDI设备", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        midiOutputPort = null;
                    }
                    // 更高版本的安卓：启动C++监听midi设备事件
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        stopReadingMidi();
                    } else {
                        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                            midiConnectionListener.onMidiDisconnect();
                        }
                    }
                }
            }, new Handler(Looper.getMainLooper()));

            // 如果在app开启前就已经连接了midi设备，需要如下的代码来直接尝试检测并开启midi设备
            for (MidiDeviceInfo info : mMidiManager.getDevices()) {
                openDevice(context, info);
            }

            // 如果安卓版本更高，使用native midi，注册onNativeMessageReceive为java回调
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                registerCallBack();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void openDevice(Context context, MidiDeviceInfo info) {
        mMidiManager.openDevice(info, device -> {
            if (device != null) {
                MidiDeviceInfo.PortInfo[] ports = device.getInfo().getPorts();
                for (MidiDeviceInfo.PortInfo port : ports) {
                    if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                        midiOutputPort = device.openOutputPort(port.getPortNumber());
                        // 更高版本的安卓：启动C++监听midi设备事件
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            startReadingMidi(device, port.getPortNumber());
                        } else {
                            for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                                midiConnectionListener.onMidiConnect();
                            }
                        }
                        break;
                    }
                }
                Toast.makeText(context, "MIDI设备已连接", Toast.LENGTH_SHORT).show();
            }
        }, new Handler(Looper.getMainLooper()));
    }

    public static void addMidiConnectionListener(MidiConnectionListener midiConnectionListener) {
        if (!midiConnectionListeners.contains(midiConnectionListener)) {
            midiConnectionListeners.add(midiConnectionListener);
        }
    }

    public static void removeMidiConnectionListener(MidiConnectionListener midiConnectionListener) {
        midiConnectionListeners.remove(midiConnectionListener);
    }

    public static MidiOutputPort getMidiOutputPort() {
        return midiOutputPort;
    }

    private static native void registerCallBack();

    private static native void startReadingMidi(MidiDevice receiveDevice, int portNumber);

    private static native void stopReadingMidi();

    /**
     * Called from the native code when MIDI messages are received.
     *
     * @param message
     */
    private static void onNativeMessageReceive(final byte[] message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            switch ((message[0] & 0xF0) >> 4) {
                case 0x09:
                    for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                        midiConnectionListener.onMidiReceiveMessage(message[1], message[2]);
                    }
                    Log.i("TAG", "showReceivedMessage: pitch = " + message[1] + " volume = " + message[2]);
                    break;
                case 0x08:
                    for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                        midiConnectionListener.onMidiReceiveMessage(message[1], (byte) 0);
                    }
                    Log.i("TAG", "showReceivedMessage: pitch = " + message[1] + " volume = " + message[2]);
                    break;
            }
        }
    }

    /**
     * 根据一个midi音高，判断它是否为黑键
     */
    public static boolean isBlackKey(byte pitch) {
        int pitchInOctave = pitch % NOTES_PER_OCTAVE;
        for (int blackKeyOffsetInOctave : BLACK_KEY_OFFSETS) {
            if (pitchInOctave == blackKeyOffsetInOctave) {
                return true;
            }
        }
        return false;
    }
}
