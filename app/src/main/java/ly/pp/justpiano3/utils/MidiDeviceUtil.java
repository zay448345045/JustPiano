package ly.pp.justpiano3.utils;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import ly.pp.justpiano3.midi.MidiConnectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MidiDeviceUtil {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            System.loadLibrary("native_midi");
        }
    }

    private static List<MidiConnectionListener> midiConnectionListeners;

    private static MidiManager mMidiManager;

    private static MidiOutputPort midiOutputPort;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void initMidiDevice(Context context) {
        midiConnectionListeners = new ArrayList<>();
        mMidiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
        mMidiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            @Override
            public void onDeviceAdded(MidiDeviceInfo info) {
                openDevice(context, info);
            }

            @Override
            public void onDeviceRemoved(MidiDeviceInfo info) {
                try {
                    for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                        midiConnectionListener.onMidiDisconnect();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        stopReadingMidi();
                    }
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
            }
        }, new Handler(Looper.getMainLooper()));
        // 如果在app开启前就已经连接了midi设备，需要如下的代码来直接尝试检测并开启midi设备
        for (MidiDeviceInfo info : mMidiManager.getDevices()) {
            openDevice(context, info);
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
                        // 安卓版本10及以上：启动C++监听midi设备事件
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            startReadingMidi(device, port.getPortNumber());
                        }
                        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                            midiConnectionListener.onMidiConnect();
                        }
                        Toast.makeText(context, "MIDI设备[" + device.getInfo().getProperties()
                                .getString(MidiDeviceInfo.PROPERTY_NAME) + "]已连接", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }, new Handler(Looper.getMainLooper()));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void addMidiConnectionListener(MidiConnectionListener midiConnectionListener) {
        if (!midiConnectionListeners.contains(midiConnectionListener)) {
            midiConnectionListeners.add(midiConnectionListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void removeMidiConnectionListener(MidiConnectionListener midiConnectionListener) {
        midiConnectionListeners.remove(midiConnectionListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static MidiOutputPort getMidiOutputPort() {
        return midiOutputPort;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static native void startReadingMidi(MidiDevice receiveDevice, int portNumber);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static native void stopReadingMidi();

    /**
     * Called from the native C++ code when MIDI messages are received.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void onNativeMessageReceive(byte pitch, byte volume) {
        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
            midiConnectionListener.onMidiMessageReceive(pitch, volume);
        }
    }
}
