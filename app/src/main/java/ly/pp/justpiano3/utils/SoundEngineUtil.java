package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.preference.PreferenceManager;
import javazoom.jl.converter.Converter;

import java.io.*;

public class SoundEngineUtil {

    static {
        System.loadLibrary("soundengine");
    }

    public static native void setupAudioStreamNative(int var1, int var2);

    public static native void teardownAudioStreamNative();

    public static native void loadWavAssetNative(byte[] var1, int var2, float var3);

    public static native void unloadWavAssetsNative();

    private static native void trigger(int var1, int var2);

    public static native void setRecord(boolean record);

    public static native void setRecordFilePath(String recordFilePath);

    public static int playSound(int note, int volume) {
        if (note >= 24 && note <= 108 && volume > 3) {
            trigger(108 - note, volume);
            return note;
        }
        return 0;
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

    private static void loadChatWav(Context context) throws IOException {
        AssetFileDescriptor assetFD = context.getResources().getAssets().openFd("chat_b5.wav");
        FileInputStream dataStream = assetFD.createInputStream();
        int dataLen = dataStream.available();
        byte[] dataBytes = new byte[dataLen];
        dataStream.read(dataBytes, 0, dataLen);
        loadWavAssetNative(dataBytes, 0, 0);
        assetFD.close();
        dataStream.close();
    }

    /**
     * 移动文件到新文件的位置（拷贝流）
     *
     * @param src 源文件对象
     * @param des 目标文件对象
     */
    public static boolean moveFile(File src, File des) {
        if (!src.exists()) {
            return false;
        }
        if (des.exists()) {
            des.delete();
        }
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(src)); BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(des))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            src.delete();
        }
        return true;
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
        confirmLoadSounds(context);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString("sound_list", "original");
        edit.apply();
    }

    public static void confirmLoadSounds(Context context) {
        try {
            loadChatWav(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean compatibleSound = sharedPreferences.getBoolean("compatible_sound", true);
        setupAudioStreamNative(compatibleSound ? 2 : 4, 44100);
    }
}
