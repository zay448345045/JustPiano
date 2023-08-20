package ly.pp.justpiano3.thread;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.enums.PlaySongsModeEnum;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.play.PmFileParser;
import protobuf.dto.OnlinePlaySongDTO;

import java.util.Objects;

@Data
public final class PlaySongs {
    private JPApplication jpapplication;

    /**
     * 目前正在播放的歌曲路径
     */
    private String songPath;

    /**
     * 播放模式
     */
    @Setter
    @Getter
    private static PlaySongsModeEnum playSongsMode;
    private boolean isPlayingSongs;
    private final MelodySelect melodyselect;
    private final OLPlayRoom olPlayRoom;
    private final int position;
    private int pm_2;
    private final byte[] tickArray;
    private final byte[] noteArray;
    private final byte[] volumeArray;

    public PlaySongs(JPApplication jPApplication, String str, MelodySelect melodySelect, OLPlayRoom olPlayRoom, int i, int diao) {
        jpapplication = jPApplication;
        songPath = str;
        melodyselect = melodySelect;
        position = i;
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
        if (melodyselect != null && melodyselect.getIsFollowPlay() && melodyselect.handler != null) {
            Message message = Message.obtain(melodyselect.handler);
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            message.what = 4;
            message.setData(bundle);
            melodyselect.handler.sendMessage(message);
        }
        if (olPlayRoom != null) {
            switch (playSongsMode) {
                case RECYCLE:
                    OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
                    builder.setTune(olPlayRoom.getdiao());
                    builder.setSongPath(jpapplication.getNowSongsName());
                    olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder.build());
                    Message obtainMessage1 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                    obtainMessage1.what = 12;
                    olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage1);
                    return;
                case RANDOM:
                    String str0 = "";
                    if (!olPlayRoom.online_1.isEmpty()) {
                        str0 = " AND " + olPlayRoom.online_1;
                    }
                    if (olPlayRoom.sqlitedatabase != null) {
                        Cursor query = olPlayRoom.sqlitedatabase.query("jp_data", Consts.sqlColumns, "diff >= " + 0 + " AND diff < " + 20 + str0, null, null, null, null);
                        str0 = query.moveToPosition((int) (Math.random() * ((double) query.getCount()))) ? query.getString(query.getColumnIndex("path")) : "";
                        query.close();
                        String str1 = str0.substring(6, str0.length() - 3);
                        jpapplication.setNowSongsName(str1);
                        OnlinePlaySongDTO.Builder builder1 = OnlinePlaySongDTO.newBuilder();
                        builder1.setTune(olPlayRoom.getdiao());
                        builder1.setSongPath(str1);
                        olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder1.build());
                        Message obtainMessage2 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage2.what = 12;
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage2);
                    }
                    return;
                case FAVOR_RANDOM:
                    str0 = "";
                    if (!olPlayRoom.online_1.isEmpty()) {
                        str0 = " AND " + olPlayRoom.online_1;
                    }
                    if (olPlayRoom.sqlitedatabase != null) {
                        Cursor query = olPlayRoom.sqlitedatabase.query("jp_data", Consts.sqlColumns, "isfavo > 0" + str0, null, null, null, null);
                        str0 = query.moveToPosition((int) (Math.random() * ((double) query.getCount()))) ? query.getString(query.getColumnIndex("path")) : "";
                        query.close();
                        if (str0.isEmpty()) {
                            return;
                        }
                        String str1 = str0.substring(6, str0.length() - 3);
                        jpapplication.setNowSongsName(str1);
                        OnlinePlaySongDTO.Builder builder1 = OnlinePlaySongDTO.newBuilder();
                        builder1.setTune(olPlayRoom.getdiao());
                        builder1.setSongPath(str1);
                        olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder1.build());
                        Message obtainMessage2 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage2.what = 12;
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage2);
                    }
                    return;
                case FAVOR:
                    str0 = "";
                    if (!olPlayRoom.online_1.isEmpty()) {
                        str0 = " AND " + olPlayRoom.online_1;
                    }
                    if (olPlayRoom.sqlitedatabase != null) {
                        Cursor query = olPlayRoom.sqlitedatabase.query("jp_data", Consts.sqlColumns, "isfavo > 0" + str0, null, null, null, null);
                        while (query.moveToNext()) {
                            String path = query.getString(query.getColumnIndex("path"));
                            if (Objects.equals(path, songPath)) {
                                break;
                            }
                        }
                        str0 = query.moveToNext() ? query.getString(query.getColumnIndex("path")) : "";
                        query.close();
                        if (str0.isEmpty()) {
                            return;
                        }
                        String str1 = str0.substring(6, str0.length() - 3);
                        jpapplication.setNowSongsName(str1);
                        OnlinePlaySongDTO.Builder builder1 = OnlinePlaySongDTO.newBuilder();
                        builder1.setTune(olPlayRoom.getdiao());
                        builder1.setSongPath(str1);
                        olPlayRoom.sendMsg(OnlineProtocolType.PLAY_SONG, builder1.build());
                        Message obtainMessage2 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage2.what = 12;
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage2);
                    }
                    return;
                default:
            }
        }
    }
}
