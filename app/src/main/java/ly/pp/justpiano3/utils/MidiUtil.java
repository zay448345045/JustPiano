package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import ly.pp.justpiano3.midi.MidiConnectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MIDI_SERVICE;

public class MidiUtil {

    private static List<MidiConnectionListener> midiConnectionListeners;

    private static MidiManager mMidiManager;

    private static MidiOutputPort midiOutputPort;

    public static void initMidiDevice(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                midiConnectionListeners = new ArrayList<>();
                mMidiManager = (MidiManager) context.getSystemService(MIDI_SERVICE);
                mMidiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                    @Override
                    public void onDeviceAdded(MidiDeviceInfo info) {
                        mMidiManager.openDevice(info, device -> {
                            if (device != null) {
                                MidiDeviceInfo.PortInfo[] ports = device.getInfo().getPorts();
                                for (MidiDeviceInfo.PortInfo port : ports) {
                                    if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                                        midiOutputPort = device.openOutputPort(port.getPortNumber());
                                        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                                            midiConnectionListener.onMidiConnect();
                                        }
                                        break;
                                    }
                                }
                                Toast.makeText(context, "MIDI设备已连接", Toast.LENGTH_SHORT).show();
                            }
                        }, new Handler(Looper.getMainLooper()));
                    }

                    @Override
                    public void onDeviceRemoved(MidiDeviceInfo info) {
                        for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                            midiConnectionListener.onMidiDisconnect();
                        }
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
                    }
                }, new Handler(Looper.getMainLooper()));
                for (MidiDeviceInfo info : mMidiManager.getDevices()) {
                    mMidiManager.openDevice(info, device -> {
                        if (device != null) {
                            MidiDeviceInfo.PortInfo[] ports = device.getInfo().getPorts();
                            for (MidiDeviceInfo.PortInfo port : ports) {
                                if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                                    midiOutputPort = device.openOutputPort(port.getPortNumber());
                                    for (MidiConnectionListener midiConnectionListener : midiConnectionListeners) {
                                        midiConnectionListener.onMidiConnect();
                                    }
                                    break;
                                }
                            }
                            Toast.makeText(context, "MIDI设备已连接", Toast.LENGTH_SHORT).show();
                        }
                    }, new Handler(Looper.getMainLooper()));
                }
            }
        }
    }

    public static void addMidiConnectionListener(MidiConnectionListener midiConnectionListener) {
        midiConnectionListeners.add(midiConnectionListener);
    }

    public static void removeMidiConnectionStart(MidiConnectionListener midiConnectionListener) {
        midiConnectionListeners.remove(midiConnectionListener);
    }

    public static MidiOutputPort getMidiOutputPort() {
        return midiOutputPort;
    }
}
