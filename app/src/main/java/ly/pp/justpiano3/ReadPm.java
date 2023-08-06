package ly.pp.justpiano3;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ReadPm {
    private Context context;
    private InputStream inputstream;
    private byte[] noteArray;
    private byte[] tickArray;
    private byte[] trackArray;
    private byte[] volumeArray;
    private int pm_2;
    private float leftNandu = 0;
    private int songTime = 0;
    private String songName = "";
    private int songNameLength = 0;
    private float nandu = 0;

    public ReadPm(Context ct, String str) {
        context = ct;
        createWithSongPath(str);
    }

    public ReadPm(byte[] bArr) {
        if (bArr == null) {
            return;
        }
        int i = 0;
        try {
            inputstream = new ByteArrayInputStream(bArr);
            int pmSize = inputstream.available();
            byte[] pmData = new byte[pmSize];
            inputstream.read(pmData);
            for (int j = 0; j < pmData.length; j++) {
                if (pmData[j] == 0x0A) {
                    songName = new String(pmData, 0, j, StandardCharsets.UTF_8);
                    songNameLength = j;
                    break;
                }
            }
            int pmDataGroupSize = (pmSize - (songNameLength + 4)) / 4;
            leftNandu = pmData[songNameLength + 1] / 10f;
            pm_2 = pmData[pmSize - 2];
            noteArray = new byte[pmDataGroupSize];
            tickArray = new byte[pmDataGroupSize];
            trackArray = new byte[pmDataGroupSize];
            volumeArray = new byte[pmDataGroupSize];
            int i3 = 0;
            int i4 = 0;
            int i2 = 0;
            for (int i5 = songNameLength + 2; i5 < pmSize - 2; i5++) {
                switch ((i5 - songNameLength) % 4) {
                    case 0:
                        noteArray[i4] = pmData[i5];
                        i4++;
                        break;
                    case 1:
                        volumeArray[i2] = pmData[i5];
                        i2++;
                        break;
                    case 2:
                        tickArray[i3] = pmData[i5];
                        i3++;
                        break;
                    case 3:
                        trackArray[i] = pmData[i5];
                        i++;
                        break;
                }
            }
            nandu = (float) pmData[pmSize - 1] / 10;
            tickArray[0] += 20;
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    private void createWithSongPath(String songPath) {
        int i = 0;
        try {
            inputstream = context.getResources().getAssets().open(songPath);
        } catch (Exception e) {
            try {
                inputstream = new FileInputStream(context.getFilesDir().getAbsolutePath() + "/Songs/" + songPath.substring(8));
            } catch (Exception fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        try {
            int pmSize = inputstream.available();
            byte[] pmData = new byte[pmSize];
            inputstream.read(pmData);
            for (int j = 0; j < pmData.length; j++) {
                if (pmData[j] == 0x0A) {
                    songName = new String(pmData, 0, j, StandardCharsets.UTF_8);
                    songNameLength = j;
                    break;
                }
            }
            int pmDataGroupSize = (pmSize - (songNameLength + 4)) / 4;
            leftNandu = pmData[songNameLength + 1] / 10f;
            pm_2 = pmData[pmSize - 2];
            noteArray = new byte[pmDataGroupSize];
            tickArray = new byte[pmDataGroupSize];
            trackArray = new byte[pmDataGroupSize];
            volumeArray = new byte[pmDataGroupSize];
            int i3 = 0;
            int i4 = 0;
            int i2 = 0;
            for (int i5 = songNameLength + 2; i5 < pmSize - 2; i5++) {
                switch ((i5 - songNameLength) % 4) {
                    case 0:
                        noteArray[i4] = pmData[i5];
                        i4++;
                        break;
                    case 1:
                        volumeArray[i2] = pmData[i5];
                        i2++;
                        break;
                    case 2:
                        tickArray[i3] = pmData[i5];
                        i3++;
                        break;
                    case 3:
                        trackArray[i] = pmData[i5];
                        i++;
                        break;
                }
            }
            nandu = (float) pmData[pmSize - 1] / 10;
            tickArray[0] += 20;
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e2) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public void loadWithSongPath(Context context, String str) {
        try {
            inputstream = context.getResources().getAssets().open(str);
        } catch (Exception e) {
            try {
                inputstream = new FileInputStream(context.getFilesDir().getAbsolutePath() + "/Songs" + str.substring(8));
            } catch (Exception fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        try {
            loadWithInputStream(inputstream);
        } catch (Throwable e2) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public void loadWithInputStream(InputStream inputstream) {
        try {
            this.inputstream = inputstream;
            int pmSize = inputstream.available();
            byte[] pmData = new byte[pmSize];
            inputstream.read(pmData);
            for (int i = 0; i < pmData.length; i++) {
                if (pmData[i] == 0x0A) {
                    songName = new String(pmData, 0, i, StandardCharsets.UTF_8);
                    songNameLength = i;
                    break;
                }
            }
            leftNandu = pmData[songNameLength + 1] / 10f;
            for (int i = songNameLength + 2; i < pmSize; i += 4) {
                songTime += pmData[i];
            }
            songTime /= 200;
            nandu = (float) pmData[pmSize - 1] / 10;
            try {
                inputstream.close();
                inputstream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Throwable e2) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public byte[] getNoteArray() {
        return noteArray;
    }

    public byte[] getTickArray() {
        return tickArray;
    }

    public byte[] getTrackArray() {
        return trackArray;
    }

    public byte[] getVolumeArray() {
        return volumeArray;
    }

    public int getPm_2() {
        return pm_2;
    }

    public float getLeftNandu() {
        return leftNandu;
    }

    public float getNandu() {
        return nandu;
    }

    public String getSongName() {
        return songName;
    }

    public int getSongTime() {
        return songTime;
    }
}
