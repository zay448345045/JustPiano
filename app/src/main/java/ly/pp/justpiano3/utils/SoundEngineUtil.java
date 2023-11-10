package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.os.Process;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javazoom.jl.converter.Converter;
import ly.pp.justpiano3.midi.MidiUtil;

public class SoundEngineUtil {

    /**
     * 聊天音效名称
     */
    public static final String CHAT_SOUND_FILE_NAME = "chat_b5.wav";

    /**
     * 处于延音状态下的音符列表
     */
    private static final Map<Byte, Byte> sustainNotePitchMap = new ConcurrentHashMap<>();

    static {
        try {
            System.loadLibrary("soundengine");
        } catch (Exception e) {
            Process.killProcess(Process.myPid());
            System.exit(1);
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

    public static native void setupAudioStreamNative(int numChannels, int sampleRate);

    public static native void teardownAudioStreamNative();

    public static native void loadWavAssetNative(byte[] wavByteArray);

    public static native void unloadWavAssetsNative();

    private static native void triggerDown(int index, int volume);

    private static native void triggerUp(int index);

    private static native void triggerUpAll();

    public static native void setRecord(boolean record);

    public static native void setRecordFilePath(String recordFilePath);

    public static native void setReverbValue(int reverbValue);

    public static native void setDelayValue(int delayValue);

    private static native void malloc();

    private static native void free();

    private static native void open();

    private static native void close();

    private static native void loadSf2(String path);

    private static native void unloadSf2();

    public static void playSound(byte pitch, byte volume) {
        triggerDown(108 - pitch, volume);
    }

    public static void stopPlaySound(byte pitch) {
        if (MidiDeviceUtil.getSustainPedalStatus()) {
            sustainNotePitchMap.put(pitch, (byte) 0);
        } else {
            triggerUp(108 - pitch);
        }
    }

    public static void stopPlayAllSounds() {
        triggerUpAll();
    }

    public static void playChatSound() {
        triggerDown(88, 127);
    }

    public static void preloadSounds(Context context, int i) {
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

    private static void loadChatWav(Context context) {
        try {
            AssetFileDescriptor assetFD = context.getResources().getAssets().openFd(CHAT_SOUND_FILE_NAME);
            FileInputStream dataStream = assetFD.createInputStream();
            int dataLen = dataStream.available();
            byte[] dataBytes = new byte[dataLen];
            dataStream.read(dataBytes, 0, dataLen);
            loadWavAssetNative(dataBytes);
            assetFD.close();
            dataStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        unloadSf2Sound();
        teardownAudioStreamNative();
        unloadWavAssetsNative();
        for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
            preloadSounds(context, i);
        }
        afterLoadSounds(context);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_list", "original");
        edit.apply();
    }

    public static void afterLoadSounds(Context context) {
        loadChatWav(context);
        setupAudioStreamNative(2, 44100);
    }

    private static String getCopyFile(Context context, File sf2File) {
        File cacheFile = new File(context.getFilesDir(), sf2File.getName());
        try {
            try (InputStream inputStream = new FileInputStream(sf2File)) {
                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

    public static void loadSf2Sound(Context context, File sf2File) {
        malloc();
        open();
        loadSf2(getCopyFile(context, sf2File));
    }

    public static void unloadSf2Sound() {
        unloadSf2();
        close();
        free();
    }
}
