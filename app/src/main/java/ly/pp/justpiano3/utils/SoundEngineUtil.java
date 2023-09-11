package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.preference.PreferenceManager;
import javazoom.jl.converter.Converter;
import ly.pp.justpiano3.entity.GlobalSetting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    private static native void trigger(int index, int volume);

    public static native void setRecord(boolean record);

    public static native void setRecordFilePath(String recordFilePath);

    public static int playSound(byte pitch, byte volume) {
        if (pitch >= 24 && pitch <= 108 && volume > 3) {
            trigger(108 - pitch, volume);
            return pitch;
        }
        return 0;
    }

    public static void stopPlaySound(int i) {
        // nothing
    }

    public static void playChatSound() {
        trigger(85, 127);
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
            if (listFiles != null && listFiles.length > 0) {
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
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_list", "original");
        edit.apply();
    }

    public static void afterLoadSounds(Context context) {
        loadChatWav(context);
        setupAudioStreamNative(2, 44100);
    }
}
