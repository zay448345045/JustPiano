package ly.pp.justpiano3;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import protobuf.dto.OnlinePlaySongDTO;

public final class PlaySongs {
    public JPApplication jpapplication;
    boolean isPlayingSongs;
    private final MelodySelect melodyselect;
    private final OLPlayRoom olPlayRoom;
    private final int position;
    private int pm_2;
    private final byte[] tickArray;
    private final byte[] noteArray;
    private final byte[] volumeArray;

    PlaySongs(JPApplication jPApplication, String str, MelodySelect melodySelect, OLPlayRoom olPlayRoom, int i, int diao) {
        jpapplication = jPApplication;
        melodyselect = melodySelect;
        position = i;
        ReadPm readpm = new ReadPm(jPApplication, str);
        noteArray = readpm.getNoteArray();
        volumeArray = readpm.getVolumeArray();
        tickArray = readpm.getTickArray();
        final int arrayLength = noteArray.length;
        pm_2 = (readpm.getPm_2());
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
                        jpapplication.playSound(noteArray[j], volumeArray[j]);
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
            switch (jpapplication.getPlaySongsMode()) {
                case 1:
                    OnlinePlaySongDTO.Builder builder = OnlinePlaySongDTO.newBuilder();
                    builder.setTune(olPlayRoom.getdiao());
                    builder.setSongPath(jpapplication.getNowSongsName());
                    olPlayRoom.sendMsg(15, builder.build());
                    Message obtainMessage1 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                    obtainMessage1.what = 12;
                    olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage1);
                    return;
                case 2:
                    String str0 = "";
                    if (!olPlayRoom.online_1.isEmpty()) {
                        str0 = " AND " + olPlayRoom.online_1;
                    }
                    if (olPlayRoom.sqlitedatabase != null) {
                        Cursor query = olPlayRoom.sqlitedatabase.query("jp_data", Consts.sqlColumns, "diff >= " + 0 + " AND diff < " + 20 + str0, null, null, null, null);
                        str0 = query.moveToPosition((int) (Math.random() * ((double) query.getCount()))) ? query.getString(query.getColumnIndex("path")) : "";
                        String str1 = str0.substring(6, str0.length() - 3);
                        jpapplication.setNowSongsName(str1);
                        OnlinePlaySongDTO.Builder builder1 = OnlinePlaySongDTO.newBuilder();
                        builder1.setTune(olPlayRoom.getdiao());
                        builder1.setSongPath(str1);
                        olPlayRoom.sendMsg(15, builder1.build());
                        Message obtainMessage2 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage2.what = 12;
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage2);
                    }
                    return;
                case 3:
                    str0 = "";
                    if (!olPlayRoom.online_1.isEmpty()) {
                        str0 = " AND " + olPlayRoom.online_1;
                    }
                    if (olPlayRoom.sqlitedatabase != null) {
                        Cursor query = olPlayRoom.sqlitedatabase.query("jp_data", Consts.sqlColumns, "isfavo > 0" + str0, null, null, null, null);
                        str0 = query.moveToPosition((int) (Math.random() * ((double) query.getCount()))) ? query.getString(query.getColumnIndex("path")) : "";
                        if (str0.isEmpty()) {
                            return;
                        }
                        String str1 = str0.substring(6, str0.length() - 3);
                        jpapplication.setNowSongsName(str1);
                        OnlinePlaySongDTO.Builder builder1 = OnlinePlaySongDTO.newBuilder();
                        builder1.setTune(olPlayRoom.getdiao());
                        builder1.setSongPath(str1);
                        olPlayRoom.sendMsg(15, builder1.build());
                        Message obtainMessage2 = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage2.what = 12;
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage2);
                    }
            }
        }
    }
}
