package ly.pp.justpiano3.utils;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.entity.GlobalSetting;

public class MidiDeviceUtil {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            System.loadLibrary("midi");
        }
    }

    private static MidiManager midiManager;

    /**
     * 处于开启（使用）状态的midi设备列表，key：设备id+设备名称，value：设备端口（用于连接和断开）
     */
    private static final Map<String, MidiOutputPort> midiDevicePortMap = new ConcurrentHashMap<>();

    private static JPMidiReceiver midiReceiver;

    /**
     * 此接口监听midi键盘连接、断开连接及接收midi设备事件
     */
    public interface MidiMessageReceiveListener {

        /**
         * midi键盘连接
         */
        default void onMidiConnect(String deviceIdAndName) {
            // nothing
        }

        /**
         * midi键盘断开连接
         */
        default void onMidiDisconnect(String deviceIdAndName) {
            // nothing
        }

        /**
         * midi键盘接收到消息，注意它不一定在主线程调用
         *
         * @param pitch  原始midi音高
         * @param volume midi音符力度
         */
        void onMidiMessageReceive(byte pitch, byte volume);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static class JPMidiReceiver extends MidiReceiver {

        /**
         * midi监听器
         */
        private final MidiMessageReceiveListener midiMessageReceiveListener;

        /**
         * 构造，传入MidiConnectionListener
         */
        private JPMidiReceiver(MidiMessageReceiveListener midiMessageReceiveListener) {
            this.midiMessageReceiveListener = midiMessageReceiveListener;
        }

        @Override
        public void onSend(byte[] msg, int offset, int count, long timestamp) {
            // 此为java层回调midi音符事件，保持和C++层调用一致的API，API使用方对使用了C++ midi还是java midi无感知
            for (int i = offset; i < offset + count; i += 3) {
                if ((msg[i] & 0xF0) == 0x90) {
                    midiMessageReceiveListener.onMidiMessageReceive(
                            (byte) (msg[i + 1] + GlobalSetting.INSTANCE.getMidiKeyboardTune()), msg[i + 2]);
                } else if ((msg[i] & 0xF0) == 0x80) {
                    midiMessageReceiveListener.onMidiMessageReceive(
                            (byte) (msg[i + 1] + GlobalSetting.INSTANCE.getMidiKeyboardTune()), (byte) 0);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void initMidiDevice(Context context) {
        midiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
        midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            @Override
            public void onDeviceAdded(MidiDeviceInfo info) {
                openDevice(context, info);
            }

            @Override
            public void onDeviceRemoved(MidiDeviceInfo info) {
                closeDevice(context, info);
            }
        }, new Handler(Looper.getMainLooper()));
        // 如果在app开启前就已经连接了midi设备，需要如下的代码来直接尝试检测并开启midi设备
        for (MidiDeviceInfo info : midiManager.getDevices()) {
            openDevice(context, info);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void openDevice(Context context, MidiDeviceInfo info) {
        if (midiManager == null || info == null) {
            return;
        }
        midiManager.openDevice(info, device -> {
            if (device != null) {
                String deviceIdAndName = getDeviceIdAndName(device.getInfo());
                MidiDeviceInfo.PortInfo[] ports = device.getInfo().getPorts();
                for (MidiDeviceInfo.PortInfo port : ports) {
                    if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                        MidiOutputPort midiOutputPort = device.openOutputPort(port.getPortNumber());
                        // 安卓版本10及以上：启动C++监听midi，否则注册java的midi监听
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            startReadingMidi(device, port.getPortNumber());
                        } else if (midiReceiver != null) {
                            midiOutputPort.connect(midiReceiver);
                        }
                        // 回调midi设备连接事件
                        if (midiReceiver != null) {
                            midiReceiver.midiMessageReceiveListener.onMidiConnect(deviceIdAndName);
                        }
                        midiDevicePortMap.put(deviceIdAndName, midiOutputPort);
                        Toast.makeText(context, "MIDI设备[" + deviceIdAndName.substring(
                                deviceIdAndName.indexOf('-') + 1) + "]已连接", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }, new Handler(Looper.getMainLooper()));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void closeDevice(Context context, MidiDeviceInfo info) {
        if (midiManager == null || info == null) {
            return;
        }
        try {
            String deviceIdAndName = getDeviceIdAndName(info);
            MidiOutputPort midiOutputPort = midiDevicePortMap.get(deviceIdAndName);
            if (midiOutputPort == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                stopReadingMidi(midiOutputPort.getPortNumber());
            } else if (midiReceiver != null) {
                midiOutputPort.disconnect(midiReceiver);
            }
            midiOutputPort.close();
            // 回调midi设备断开事件
            if (midiReceiver != null) {
                midiReceiver.midiMessageReceiveListener.onMidiDisconnect(deviceIdAndName);
            }
            midiDevicePortMap.remove(deviceIdAndName);
            Toast.makeText(context, "MIDI设备[" + deviceIdAndName.substring(
                    deviceIdAndName.indexOf('-') + 1) + "]已断开", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getDeviceIdAndName(MidiDeviceInfo deviceInfo) {
        String name = deviceInfo.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
        return deviceInfo.getId() + "-" + (StringUtil.isNullOrEmpty(name) ? "未知设备" : name);
    }

    /**
     * 现在是否有（至少一台）midi设备连接了极品并且保持着开启状态
     */
    public static boolean hasMidiDeviceConnected() {
        return !midiDevicePortMap.isEmpty();
    }

    /**
     * 获取所有连接的midi设备的名称列表（不一定是启用状态）
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<MidiDeviceInfo> getAllMidiDeviceList() {
        List<MidiDeviceInfo> deviceIdAndNamdList = new ArrayList<>();
        if (midiManager != null) {
            Collections.addAll(deviceIdAndNamdList, midiManager.getDevices());
        }
        return deviceIdAndNamdList;
    }

    /**
     * 获取所有启用状态的midi设备的名称列表
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<String> getAllConnectedMidiDeviceIdAndNameList() {
        return new ArrayList<>(midiDevicePortMap.keySet());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void addMidiConnectionListener(MidiMessageReceiveListener midiMessageReceiveListener) {
        if (midiManager == null) {
            return;
        }
        if (midiReceiver != null) {
            removeMidiConnectionListener(midiMessageReceiveListener);
        }
        midiReceiver = new JPMidiReceiver(midiMessageReceiveListener);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            for (MidiDeviceInfo info : midiManager.getDevices()) {
                MidiOutputPort midiOutputPort = midiDevicePortMap.get(getDeviceIdAndName(info));
                if (midiOutputPort != null) {
                    midiOutputPort.connect(midiReceiver);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void removeMidiConnectionListener(MidiMessageReceiveListener midiMessageReceiveListener) {
        if (midiManager == null) {
            return;
        }
        if (midiReceiver != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                for (MidiDeviceInfo info : midiManager.getDevices()) {
                    MidiOutputPort midiOutputPort = midiDevicePortMap.get(getDeviceIdAndName(info));
                    if (midiOutputPort != null) {
                        midiOutputPort.disconnect(midiReceiver);
                    }
                }
            }
            midiReceiver = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static native void startReadingMidi(MidiDevice receiveDevice, int portNumber);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static native void stopReadingMidi(int portNumber);

    /**
     * Called from the native C++ code when MIDI messages are received
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void onNativeMessageReceive(byte pitch, byte volume) {
        if (midiReceiver != null) {
            midiReceiver.midiMessageReceiveListener.onMidiMessageReceive(
                    (byte) (pitch + GlobalSetting.INSTANCE.getMidiKeyboardTune()), volume);
        }
    }
}
