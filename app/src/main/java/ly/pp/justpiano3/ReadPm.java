package ly.pp.justpiano3;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

final class ReadPm {
    private Context context;
    private String filepath;
    private InputStream inputstream;
    private byte[] noteArray;
    private byte[] tickArray;
    private byte[] trackArray;
    private byte[] volumeArray;
    private int pm_2;
    private float leftNandu = 0;
    private int songTime = 0;
    private String songName = "";
    private BufferedReader bufferedReader;
    private InputStreamReader inputstreamreader;
    private int songNameLength = 0;
    private float nandu = 0;

    ReadPm(Context ct, String str) {
        context = ct;
        filepath = str;
        m4095i();
    }

    ReadPm(byte[] bArr) {
        if (bArr == null) {
            return;
        }
        int i = 0;
        try {
            inputstream = new ByteArrayInputStream(bArr);
            int pmSize = inputstream.available();
            inputstreamreader = new InputStreamReader(inputstream);
            bufferedReader = new BufferedReader(inputstreamreader);
            songName = bufferedReader.readLine();
            songName = songName.trim();
            songNameLength = songName.getBytes().length;
            int pmDataGroupSize = (pmSize - (songNameLength + 4)) / 4;
            byte[] pmData = new byte[pmSize];
            inputstream.reset();
            inputstream.read(pmData);
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
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
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
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
                    inputstream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
                    inputstream = null;
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    private void m4095i() {
        int i = 0;
        try {
            inputstream = context.getResources().getAssets().open(filepath);
            int pmSize = inputstream.available();
            inputstreamreader = new InputStreamReader(inputstream);
            bufferedReader = new BufferedReader(inputstreamreader);
            songName = bufferedReader.readLine();
            songNameLength = songName.getBytes().length;
            int pmDataGroupSize = (pmSize - (songNameLength + 4)) / 4;
            byte[] pmData = new byte[pmSize];
            inputstream.reset();
            inputstream.read(pmData);
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
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
                    inputstream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e2) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
                    inputstream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    final void mo3452a(Context context, String str) {
        try {
            inputstream = context.getResources().getAssets().open(str);
            int pmSize = inputstream.available();
            inputstreamreader = new InputStreamReader(inputstream);
            bufferedReader = new BufferedReader(inputstreamreader);
            songName = bufferedReader.readLine();
            songNameLength = songName.getBytes().length;
            byte[] pmData = new byte[pmSize];
            inputstream.reset();
            inputstream.read(pmData);
            leftNandu = pmData[songNameLength + 1] / 10f;
            for (int i = songNameLength + 2; i < pmSize; i += 4) {
                songTime += pmData[i];
            }
            songTime /= 200;
            nandu = (float) pmData[pmSize - 1] / 10;
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
                    inputstream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e2) {
            if (inputstream != null) {
                try {
                    inputstream.close();
                    inputstreamreader.close();
                    bufferedReader.close();
                    bufferedReader = null;
                    inputstreamreader = null;
                    inputstream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    final byte[] getNoteArray() {
        return noteArray;
    }

    final byte[] getTickArray() {
        return tickArray;
    }

    final byte[] getTrackArray() {
        return trackArray;
    }

    final byte[] getVolumeArray() {
        return volumeArray;
    }

    final int getPm_2() {
        return pm_2;
    }

    final float getLeftNandu() {
        return leftNandu;
    }

    final float getNandu() {
        return nandu;
    }

    final String getSongName() {
        return songName;
    }

    final int getSongTime() {
        return songTime;
    }
}
