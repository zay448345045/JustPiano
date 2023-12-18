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
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.entity.GlobalSetting;

public class MidiDeviceUtil {

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            System.loadLibrary("midi");
        }
    }

    private static MidiManager midiManager;

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * MIDI设备的延音踏板状态（开/关）
     */
    private static final AtomicBoolean sustainPedal = new AtomicBoolean(false);

    private static MidiSustainPedalListener midiSustainPedalListener;

    /**
     * midi设备延音踏板开关状态变更监听
     */
    public interface MidiSustainPedalListener {

        /**
         * 延音踏板开关状态变更
         *
         * @param status 变更后的状态
         */
        void onChange(boolean status);
    }

    /**
     * 处于开启（使用）状态的midi设备列表，key：设备id+设备名称，value：设备端口（用于连接和断开）
     */
    private static final Map<String, MidiOutputPort> midiDevicePortMap = new ConcurrentHashMap<>();

    private static JPMidiReceiver midiReceiver;

    /**
     * 此接口监听midi设备连接、断开连接及接收midi设备事件
     */
    public interface MidiDeviceListener {

        /**
         * midi键盘连接
         */
        default void onMidiConnect(MidiDeviceInfo midiDeviceInfo) {
            // nothing
        }

        /**
         * midi键盘断开连接
         */
        default void onMidiDisconnect(MidiDeviceInfo midiDeviceInfo) {
            // nothing
        }

        /**
         * midi键盘接收到的音符消息，避免在此方法内直接做耗时长的操作
         *
         * @param pitch  原始midi音高
         * @param volume midi音符力度
         */
        default void onMidiMessageReceive(byte pitch, byte volume) {
            // nothing
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static class JPMidiReceiver extends MidiReceiver {

        /**
         * midi监听器
         */
        private final MidiDeviceListener midiDeviceListener;

        /**
         * 构造，传入MidiConnectionListener
         */
        private JPMidiReceiver(MidiDeviceListener midiDeviceListener) {
            this.midiDeviceListener = midiDeviceListener;
        }

        @Override
        public void onSend(byte[] msg, int offset, int count, long timestamp) {
            for (int i = offset; i < offset + count; i += 3) {
                onMidiMessageReceive(msg[i], msg[i + 1], msg[i + 2]);
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
        }, mainHandler);
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
                try {
                    String deviceIdAndName = getDeviceIdAndName(device.getInfo());
                    for (MidiDeviceInfo.PortInfo port : device.getInfo().getPorts()) {
                        if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                            MidiOutputPort midiOutputPort = device.openOutputPort(port.getPortNumber());
                            // 安卓版本10及以上：启动C++监听midi，否则注册java的midi监听
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                startReadingMidi(device, port.getPortNumber(), device.getInfo().getId());
                            } else if (midiReceiver != null) {
                                midiOutputPort.connect(midiReceiver);
                            }
                            midiDevicePortMap.put(deviceIdAndName, midiOutputPort);
                            // 回调midi设备连接事件
                            if (midiReceiver != null) {
                                midiReceiver.midiDeviceListener.onMidiConnect(device.getInfo());
                            }
                            Toast.makeText(context, "MIDI设备[" + deviceIdAndName.substring(
                                    deviceIdAndName.indexOf('-') + 1) + "]已连接", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, mainHandler);
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
                stopReadingMidi(info.getId());
            } else if (midiReceiver != null) {
                midiOutputPort.disconnect(midiReceiver);
            }
            midiOutputPort.close();
            midiDevicePortMap.remove(deviceIdAndName);
            // 回调midi设备断开事件
            if (midiReceiver != null) {
                midiReceiver.midiDeviceListener.onMidiDisconnect(info);
            }
            mainHandler.post(() -> Toast.makeText(context, "MIDI设备[" + deviceIdAndName.substring(
                    deviceIdAndName.indexOf('-') + 1) + "]已断开", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getDeviceIdAndName(MidiDeviceInfo deviceInfo) {
        if (deviceInfo == null) {
            return null;
        }
        String name = deviceInfo.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
        return deviceInfo.getId() + "-" + (TextUtils.isEmpty(name) ? "未知设备" : name);
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
        List<MidiDeviceInfo> midiDeviceInfoList = new ArrayList<>();
        if (midiManager != null) {
            for (MidiDeviceInfo deviceInfo : midiManager.getDevices()) {
                if (deviceInfo != null) {
                    for (MidiDeviceInfo.PortInfo port : deviceInfo.getPorts()) {
                        if (port.getType() == MidiDeviceInfo.PortInfo.TYPE_OUTPUT) {
                            midiDeviceInfoList.add(deviceInfo);
                            break;
                        }
                    }
                }
            }
        }
        return midiDeviceInfoList;
    }

    /**
     * 获取所有启用状态的midi设备的名称列表
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<String> getAllConnectedMidiDeviceIdAndNameList() {
        return new ArrayList<>(midiDevicePortMap.keySet());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setMidiConnectionListener(MidiDeviceListener midiDeviceListener) {
        if (midiManager == null) {
            return;
        }
        if (midiReceiver != null) {
            removeMidiConnectionListener();
        }
        midiReceiver = new JPMidiReceiver(midiDeviceListener);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            for (MidiDeviceInfo info : midiManager.getDevices()) {
                MidiOutputPort midiOutputPort = midiDevicePortMap.get(getDeviceIdAndName(info));
                if (midiOutputPort != null) {
                    try {
                        midiOutputPort.connect(midiReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void removeMidiConnectionListener() {
        if (midiManager == null) {
            return;
        }
        if (midiReceiver != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                for (MidiDeviceInfo info : midiManager.getDevices()) {
                    MidiOutputPort midiOutputPort = midiDevicePortMap.get(getDeviceIdAndName(info));
                    if (midiOutputPort != null) {
                        try {
                            midiOutputPort.disconnect(midiReceiver);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            midiReceiver = null;
        }
    }

    public static void setMidiSustainPedalListener(MidiSustainPedalListener midiSustainPedalListener) {
        MidiDeviceUtil.midiSustainPedalListener = midiSustainPedalListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static native void startReadingMidi(MidiDevice receiveDevice, int portNumber, int deviceId);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static native void stopReadingMidi(int deviceId);

    /**
     * C++和java接收到midi数据后均会调用此方法，此方法中不要有长时间的阻塞操作，可能导致midi设备接收C++线程挂掉
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void onMidiMessageReceive(int value1, byte value2, byte value3) {
        int midiEventType = value1 & 0xF0;
        if (midiEventType == 0x90) {
            // 音符按下
            if (midiReceiver != null) {
                midiReceiver.midiDeviceListener.onMidiMessageReceive(
                        (byte) (value2 + GlobalSetting.INSTANCE.getMidiKeyboardTune()), value3);
            }
        } else if (midiEventType == 0x80) {
            // 音符抬起
            if (midiReceiver != null) {
                midiReceiver.midiDeviceListener.onMidiMessageReceive(
                        (byte) (value2 + GlobalSetting.INSTANCE.getMidiKeyboardTune()), (byte) 0);
            }
        } else if (midiEventType == 0xB0 && (value2 & 0xFF) == 64) {
            // 延音踏板，midi的CC控制器64号判断
            changeSustainPedalStatus((value3 & 0xFF) >= 64);
        }
    }

    /**
     * 修改延音踏板状态
     */
    private static void changeSustainPedalStatus(boolean currentSustainPedalValue) {
        if (sustainPedal.getAndSet(currentSustainPedalValue) != currentSustainPedalValue
                && !GlobalSetting.INSTANCE.getForceEnableSustainPedal()
                && midiSustainPedalListener != null) {
            midiSustainPedalListener.onChange(currentSustainPedalValue);
        }
    }

    /**
     * 获取延音踏板当前状态
     */
    public static boolean getSustainPedalStatus() {
        return GlobalSetting.INSTANCE.getForceEnableSustainPedal() || sustainPedal.get();
    }
}
