package ly.pp.justpiano3.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 使用sf2音色库播放工具
 */
public class Sf2SynthSoundEngineUtil {

    static {
        System.loadLibrary("fluidsynth");
    }

    private static final int THREAD_MAX = 5;
    private static Thread[] sf2SynthStreamPlayThreads;

    private static int index;
    private static long[] sf2SynthPtrArray;
    public static boolean isStreamPlaying = false;
    private static String filePath;

    private static String getCopyFile(Context context, String fileName) {
        File cacheFile = new File(context.getFilesDir(), fileName);
        try {
            try (InputStream inputStream = context.getAssets().open(fileName)) {
                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

    public static void startSynth(Context context, String fileName) {
        filePath = getCopyFile(context, fileName);
        index = 0;
        sf2SynthPtrArray = new long[THREAD_MAX];
        sf2SynthStreamPlayThreads = new Thread[THREAD_MAX];
        // not running yet
        if (!isStreamPlaying) {
            isStreamPlaying = true;
            for (int i = 0; i < THREAD_MAX; i++) {
                sf2SynthStreamPlayThreads[i] = new Sf2SynthStreamPlayThread(i);
                sf2SynthStreamPlayThreads[i].start();
            }
            // need a small timeout to enable synth to start
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() {
        if (isStreamPlaying) {
            isStreamPlaying = false;
            try {
                for (int i = 0; i < THREAD_MAX; i++) {
                    sf2SynthStreamPlayThreads[i].join();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isInit() {
        for (long synthNum : sf2SynthPtrArray) {
            if (synthNum != 0) {
                return true;
            }
        }
        return false;
    }

    private static class Sf2SynthStreamPlayThread extends Thread {

        private final int synthNum;

        private Sf2SynthStreamPlayThread(int synthNum) {
            this.synthNum = synthNum;
        }

        @Override
        public void run() {
            super.run();
            int minBufferSize = AudioTrack.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
            audioTrack.play();

            int bufferSize = 2;
            short[] streamBuffer = new short[bufferSize];
            sf2SynthPtrArray[synthNum] = malloc();

            open(sf2SynthPtrArray[synthNum]);
            loadFont(sf2SynthPtrArray[synthNum], filePath);

            while (isStreamPlaying) {
                // Have fluidSynth fill our buffer...
                fillBuffer(sf2SynthPtrArray[synthNum], streamBuffer, bufferSize);
                // ... then feed that data to the AudioTrack
                audioTrack.write(streamBuffer, 0, bufferSize);
            }
            audioTrack.flush();
            audioTrack.stop();
            audioTrack.release();

            unloadFont(sf2SynthPtrArray[synthNum]);
            close(sf2SynthPtrArray[synthNum]);
        }
    }

    private static int getSynthNumNoteOn() {
        index = (index + 1) % THREAD_MAX;
        return index;
    }

    public static void sendNoteOn(int channel, int key, int velocity) {
        if (isInit()) {
            noteOn(sf2SynthPtrArray[getSynthNumNoteOn()], channel, key, velocity);
        }
    }

    public static void sendControlChange(int channel, int controller, int value) {
        if (isInit()) {
            for (int i = 0; i < THREAD_MAX; i++) {
                controlChange(sf2SynthPtrArray[i], channel, controller, value);
            }
        }
    }

    public static void sendProgramChange(int channel, int value) {
        if (isInit()) {
            for (int i = 0; i < THREAD_MAX; i++) {
                programChange(sf2SynthPtrArray[i], channel, value);
            }
        }
    }

    private static native long malloc();

    private static native void free(long instance);

    private static native void open(long instance);

    private static native void close(long instance);

    private static native void loadFont(long instance, String path);

    private static native void unloadFont(long instance);

    private static native void systemReset(long instance);

    private static native void noteOn(long instance, int channel, int note, int velocity);

    private static native void noteOff(long instance, int channel, int note, int velocity);

    private static native void controlChange(long instance, int channel, int control, int value);

    private static native void programChange(long instance, int channel, int program);

    private static native void pitchBend(long instance, int channel, int value);

    private static native void fillBuffer(long instance, short[] buffer, int buffLen);
}
