package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.converter.Converter;

public class SoundEngineUtil {

    /**
     * 聊天音效名称
     */
    public static final String CHAT_SOUND_FILE_NAME = "chat_b5.wav";

    static {
        System.loadLibrary("soundengine");
    }

    public static native void setupAudioStreamNative(int numChannels, int sampleRate);

    public static native void teardownAudioStreamNative();

    public static native void loadWavAssetNative(byte[] wavByteArray, int index, float pan);

    public static native void unloadWavAssetsNative();

    private static native void triggerDown(int index, int volume);

    private static native void triggerUp(int index);

    private static native void triggerUpAll();

    public static native void setRecord(boolean record);

    public static native void setRecordFilePath(String recordFilePath);

    public static byte playSound(byte pitch, byte volume) {
        if (enableSf2Synth && sf2SynthPtr != null) {
            noteOn(sf2SynthPtr, 0, pitch, volume);
            return pitch;
        } else {
            if (pitch >= 24 && pitch <= 108 && volume > 0) {
                triggerDown(108 - pitch, volume);
                return pitch;
            }
        }
        return 0;
    }

    public static void stopPlaySound(byte pitch) {
        if (pitch >= 24 && pitch <= 108) {
            triggerUp(108 - pitch);
        }
    }

    public static void stopPlayAllSounds() {
        triggerUpAll();
    }

    public static void setReverb(int soundReverb) {
        setReverbValue(sf2SynthPtr != null ? sf2SynthPtr : 0, soundReverb);
    }

    public static void setDelay(int soundDelay) {
        setDelayValue(soundDelay);
    }

    public static void playChatSound() {
        triggerDown(85, 127);
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
        loadWavAssetNative(dataBytes, index, 0);
        dataStream.close();
    }

    private static void loadChatWav(Context context) {
        try {
            AssetFileDescriptor assetFD = context.getResources().getAssets().openFd(CHAT_SOUND_FILE_NAME);
            FileInputStream dataStream = assetFD.createInputStream();
            int dataLen = dataStream.available();
            byte[] dataBytes = new byte[dataLen];
            dataStream.read(dataBytes, 0, dataLen);
            loadWavAssetNative(dataBytes, 0, 0);
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
        teardownAudioStreamNative();
        unloadWavAssetsNative();
        for (int i = 108; i >= 24; i--) {
            preloadSounds(context, i);
        }
        afterLoadSounds(context);
        unloadSf2Sound();
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_list", "original");
        edit.apply();
    }

    public static void afterLoadSounds(Context context) {
        loadChatWav(context);
        setupAudioStreamNative(2, 44100);
    }

    /* ================ sf2音源部分 ================ */
    private static Long sf2SynthPtr;
    private static boolean enableSf2Synth;

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
        String filePath = getCopyFile(context, sf2File);
        if (!enableSf2Synth) {
            sf2SynthPtr = malloc();
            open(sf2SynthPtr);
            loadFont(sf2SynthPtr, filePath);
            enableSf2Synth = true;
        }
    }

    public static void unloadSf2Sound() {
        if (enableSf2Synth) {
            unloadFont(sf2SynthPtr);
            close(sf2SynthPtr);
            free(sf2SynthPtr);
            sf2SynthPtr = null;
            enableSf2Synth = false;
        }
    }

    private static native long malloc();

    private static native void free(long instance);

    private static native void open(long instance);

    private static native void close(long instance);

    private static native void loadFont(long instance, String path);

    private static native void unloadFont(long instance);

    private static native void noteOn(long instance, int channel, int note, int velocity);

    private static native void setReverbValue(long instance, int reverbValue);

    private static native void setDelayValue(int delayValue);
}
