package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Process;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javazoom.jl.converter.Converter;
import ly.pp.justpiano3.midi.MidiUtil;

public final class SoundEngineUtil {

    /**
     * 处于延音状态下的音符列表
     */
    private static final Map<Byte, Byte> sustainNotePitchMap = new ConcurrentHashMap<>();

    static {
        try {
            System.loadLibrary("soundengine");
        } catch (Error e) {
            e.printStackTrace();
            System.exit(0);
            Process.killProcess(Process.myPid());
        }
        // 注册延音踏板状态变更监听器
        MidiDeviceUtil.setMidiSustainPedalListener(status -> {
            if (!status) {
                for (Byte pitch : sustainNotePitchMap.keySet()) {
                    stopPlaySound(pitch);
                }
                sustainNotePitchMap.clear();
            }
        });
    }

    public static native void setupAudioStreamNative();

    public static native void teardownAudioStreamNative();

    public static native void startAudioStreamNative();

    public static native void loadWavAssetNative(byte[] wavByteArray);

    public static native void unloadWavAssetsNative();

    public static native boolean isAudioStreamStart();

    private static native void triggerDown(int index, int volume);

    private static native void triggerUp(int index);

    private static native void triggerUpAll();

    public static native void setRecord(boolean record);

    public static native void setRecordFilePath(String recordFilePath);

    public static native void setReverbValue(int reverbValue);

    public static native void setDelayValue(int delayValue);

    public static native void openFluidSynth();

    public static native void closeFluidSynth();

    public static native void loadSf2(String path);

    public static native void unloadSf2();

    public static native void restartStream();

    private static final long DEVICE_SWITCH_TIME = 500L;

    private static boolean devicesInitialized = false;

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceCallback deviceCallback = new AudioDeviceCallback() {
                @Override
                public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                    if (devicesInitialized) {
                        resetOutput();
                    }
                    devicesInitialized = true;
                }

                @Override
                public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                    resetOutput();
                }

                private void resetOutput() {
                    new Timer(false).schedule(new TimerTask() {
                        @Override
                        public void run() {
                            restartStream();
                        }
                    }, DEVICE_SWITCH_TIME);
                }
            };
            audioManager.registerAudioDeviceCallback(deviceCallback, null);
        }
    }

    public static void playSound(byte pitch, byte volume) {
        if (pitch < MidiUtil.MIN_PIANO_MIDI_PITCH || pitch > MidiUtil.MAX_PIANO_MIDI_PITCH || volume <= 0) {
            return;
        }
        triggerDown(MidiUtil.MAX_PIANO_MIDI_PITCH - pitch, volume);
    }

    public static void stopPlaySound(byte pitch) {
        if (pitch < MidiUtil.MIN_PIANO_MIDI_PITCH || pitch > MidiUtil.MAX_PIANO_MIDI_PITCH) {
            return;
        }
        if (MidiDeviceUtil.getSustainPedalStatus()) {
            sustainNotePitchMap.put(pitch, (byte) 0);
        } else {
            triggerUp(MidiUtil.MAX_PIANO_MIDI_PITCH - pitch);
        }
    }

    public static void stopPlayAllSounds() {
        triggerUpAll();
    }

    public static void loadSoundAssetsNative(Context context, int i) {
        try {
            Converter converter = new Converter();
            converter.convert(context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".mp3", context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".wav");
            loadWavInputStreamByIndex(context, i);
        } catch (Exception e1) {
            try {
                AssetFileDescriptor assetFD = context.getResources().getAssets().openFd("sound/" + i + ".mp3");
                Converter converter = new Converter();
                converter.convert(assetFD.createInputStream(), context.getFilesDir().getAbsolutePath() + "/Sounds/" + i + ".wav", null, null);
                loadWavInputStreamByIndex(context, i);
                assetFD.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private static void loadWavInputStreamByIndex(Context context, int index) throws IOException {
        FileInputStream dataStream = new FileInputStream(context.getFilesDir().getAbsolutePath() + "/Sounds/" + index + ".wav");
        int dataLen = dataStream.available();
        byte[] dataBytes = new byte[dataLen];
        dataStream.read(dataBytes, 0, dataLen);
        loadWavAssetNative(dataBytes);
        dataStream.close();
    }

    public static void reLoadOriginalSounds(Context context) {
        File dir = new File(context.getFilesDir(), "Sounds");
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        teardownAudioStreamNative();
        unloadSf2();
        unloadWavAssetsNative();
        setupAudioStreamNative();
        for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
            loadSoundAssetsNative(context, i);
        }
        startAudioStreamNative();
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_select", "original");
        edit.apply();
    }
}
