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
public class Sf2SynthUtil {

    static {
        System.loadLibrary("fluidsynth");
    }

    private final int THREAD_MAX = 10;
    private Thread[] sf2SynthStreamPlayThreads;

    private int index;
    private long[] sf2SynthPtrArray;
    public boolean isStreamPlaying = false;
    private final String filePath;

    public Sf2SynthUtil(Context context, String filePath) {
        this.filePath = getCopyFile(context, filePath);
    }

    private String getCopyFile(Context context, String fileName) {
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

    public void startSynth() {
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

    public boolean isInit() {
        for (long synthNum : sf2SynthPtrArray) {
            if (synthNum != 0) {
                return true;
            }
        }
        return false;
    }

    private class Sf2SynthStreamPlayThread extends Thread {

        private final int synthNum;

        private Sf2SynthStreamPlayThread(int synthNum) {
            this.synthNum = synthNum;
        }

        @Override
        public void run() {
            super.run();
            int minBufferSize = AudioTrack.getMinBufferSize(44100 / 2,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100 / 2,
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

    private int getSynthNumNoteOn() {
        index = (index + 1) % THREAD_MAX;
        return index;
    }

    public void sendNoteOn(int channel, int key, int velocity) {
        if (isInit()) {
            this.noteOn(sf2SynthPtrArray[getSynthNumNoteOn()], channel, key, velocity);
        }
    }

    public void sendControlChange(int channel, int controller, int value) {
        if (isInit()) {
            for (int i = 0; i < THREAD_MAX; i++) {
                controlChange(sf2SynthPtrArray[i], channel, controller, value);
            }
        }
    }

    public void sendProgramChange(int channel, int value) {
        if (isInit()) {
            for (int i = 0; i < THREAD_MAX; i++) {
                programChange(sf2SynthPtrArray[i], channel, value);
            }
        }
    }

    private native long malloc();

    private native void free(long instance);

    private native void open(long instance);

    private native void close(long instance);

    private native void loadFont(long instance, String path);

    private native void unloadFont(long instance);

    private native void systemReset(long instance);

    private synchronized native void noteOn(long instance, int channel, int note, int velocity);

    private synchronized native void noteOff(long instance, int channel, int note, int velocity);

    private native void controlChange(long instance, int channel, int control, int value);

    private native void programChange(long instance, int channel, int program);

    private native void pitchBend(long instance, int channel, int value);

    public native void fillBuffer(long instance, short[] buffer, int buffLen);
}
