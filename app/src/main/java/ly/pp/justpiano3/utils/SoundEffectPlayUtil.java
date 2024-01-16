package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public final class SoundEffectPlayUtil {

    /**
     * 默认聊天音效名称
     */
    private static final String CHAT_SOUND_FILE_NAME = "chat_b5.wav";
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying = false;

    public static void playSoundEffect(Context context, Uri soundUri) {
        if (isPlaying) {
            stopPlaySoundEffect();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(context, soundUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
        } catch (Exception e) {
            try {
                AssetFileDescriptor assetFD = context.getResources().getAssets().openFd(CHAT_SOUND_FILE_NAME);
                mediaPlayer.setDataSource(assetFD.getFileDescriptor(), assetFD.getStartOffset(), assetFD.getLength());
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener((mediaPlayer) -> {
                    try {
                        assetFD.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                mediaPlayer.start();
                isPlaying = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void stopPlaySoundEffect() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }
}
