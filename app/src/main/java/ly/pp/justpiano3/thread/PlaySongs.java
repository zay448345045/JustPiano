package ly.pp.justpiano3.thread;

import android.os.Bundle;
import android.os.Message;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.play.PmFileParser;
import protobuf.dto.OnlinePlaySongDTO;

import java.util.List;
import java.util.Objects;

public final class PlaySongs {

    /**
     * 目前正在播放的歌曲路径
     */
    private static String songFilePath;

    /**
     * 播放模式
     */
    private static PlaySongsModeEnum playSongsMode = PlaySongsModeEnum.ONCE;
    private boolean isPlayingSongs;
    private final MelodySelect melodySelect;
    private final OLPlayRoom olPlayRoom;
    private int pm_2;
    private final byte[] tickArray;
    private final byte[] noteArray;
    private final byte[] volumeArray;

    public static String getSongFilePath() {
        return songFilePath;
    }

    public static void setSongFilePath(String songFilePath) {
        PlaySongs.songFilePath = songFilePath;
    }

    public static PlaySongsModeEnum getPlaySongsMode() {
        return playSongsMode;
    }

    public static void setPlaySongsMode(PlaySongsModeEnum playSongsMode) {
        PlaySongs.playSongsMode = playSongsMode;
    }

    public boolean isPlayingSongs() {
        return isPlayingSongs;
    }

    public void setPlayingSongs(boolean playingSongs) {
        isPlayingSongs = playingSongs;
    }

    public MelodySelect getMelodySelect() {
        return melodySelect;
    }

    public OLPlayRoom getOlPlayRoom() {
        return olPlayRoom;
    }

    public int getPm_2() {
        return pm_2;
    }

    public void setPm_2(int pm_2) {
        this.pm_2 = pm_2;
    }

    public byte[] getTickArray() {
        return tickArray;
    }

    public byte[] getNoteArray() {
        return noteArray;
    }

    public byte[] getVolumeArray() {
        return volumeArray;
    }

    public PlaySongs(JPApplication jPApplication, String str, MelodySelect melodySelect, OLPlayRoom olPlayRoom, int diao) {
        songFilePath = str;
        this.melodySelect = melodySelect;
        PmFileParser pmFileParser = new PmFileParser(jPApplication, str);
        noteArray = pmFileParser.getNoteArray();
        volumeArray = pmFileParser.getVolumeArray();
        tickArray = pmFileParser.getTickArray();
        final int arrayLength = noteArray.length;
        pm_2 = (pmFileParser.getPm_2());
        if (pm_2 <= 0) {
            pm_2 += 256;
        }
        isPlayingSongs = true;
        this.olPlayRoom = olPlayRoom;
        if (diao != 0) {
            for (int k = 0; k < arrayLength; k++) {
                noteArray[k] += diao;
            }
        }
        new Thread(() -> {
            int j = 0;
            while (j >= 0) {
                try {
                    if (j < arrayLength) {
                        if (!isPlayingSongs) {
                            break;
                        }
                        long intervalTime = (long) tickArray[j] * pm_2;
                        if (intervalTime > 0) {
                            try {
                                Thread.sleep(intervalTime);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        SoundEngineUtil.playSound(noteArray[j], volumeArray[j]);
                        j++;
                    } else {
                        setNestSong();
                        break;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
    }

    private void setNestSong() {
        if (melodySelect != null && melodySelect.getIsFollowPlay() && melodySelect.handler != null) {
            Message message = Message.obtain(melodySelect.handler);
            Bundle bundle = new Bundle();
            message.what = 4;
            message.setData(bundle);
            melodySelect.handler.sendMessage(message);
        }
        if (olPlayRoom != null) {
            switch (playSongsMode) {
                case RECYCLE:
                    OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
                    builder.setTune(olPlayRoom.getdiao());
                    builder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
                    olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
                    Message obtainMessage1 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                    obtainMessage1.what = 12;
                    olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage1);
                    return;
                case RANDOM:
                    List<Song> songs = JPApplication.getSongDatabase().songDao().getSongByRightHandDegreeWithRandom(0, 10);
                    songFilePath = songs.isEmpty() ? "" : songs.get(0).getFilePath();
                    if (songFilePath.isEmpty()) {
                        return;
                    }
                    OnlinePlaySongDTO.Builder builder1 = OnlinePlaySongDTO.newBuilder();
                    builder1.setTune(olPlayRoom.getdiao());
                    builder1.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
                    olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder1.build());
                    Message obtainMessage2 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                    obtainMessage2.what = 12;
                    olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage2);
                    return;
                case FAVOR_RANDOM:
                    List<Song> songInFavoriteWithRandom = JPApplication.getSongDatabase().songDao().getSongInFavoriteWithRandom();
                    songFilePath = songInFavoriteWithRandom.isEmpty() ? "" : songInFavoriteWithRandom.get(0).getFilePath();
                    if (songFilePath.isEmpty()) {
                        return;
                    }
                    OnlinePlaySongDTO.Builder songBuilder = OnlinePlaySongDTO.newBuilder();
                    songBuilder.setTune(olPlayRoom.getdiao());
                    songBuilder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
                    olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, songBuilder.build());
                    Message obtainMessage3 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                    obtainMessage3.what = 12;
                    olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage3);
                    return;
                case FAVOR:
                    List<Song> favoriteSongList = JPApplication.getSongDatabase().songDao().getFavoriteSongs();
                    for (int i = 0; i < favoriteSongList.size(); i++) {
                        if (Objects.equals(favoriteSongList.get(i).getFilePath(), PlaySongs.songFilePath)) {
                            songFilePath = favoriteSongList.get(i == favoriteSongList.size() - 1 ? 0 : i + 1).getFilePath();
                            break;
                        }
                    }
                    if (songFilePath.isEmpty()) {
                        return;
                    }
                    OnlinePlaySongDTO.Builder playSongBuilder = OnlinePlaySongDTO.newBuilder();
                    playSongBuilder.setTune(olPlayRoom.getdiao());
                    playSongBuilder.setSongPath(songFilePath.substring(6, songFilePath.length() - 3));
                    olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, playSongBuilder.build());
                    Message obtainMessage = olPlayRoom.olPlayRoomHandler.obtainMessage();
                    obtainMessage.what = 12;
                    olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
                    return;
                default:
            }
        }
    }
}
