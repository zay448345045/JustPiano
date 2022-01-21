package ly.pp.justpiano3;

final class Room {

    private byte roomID;
    private String roonName;
    private int fcount;
    private int mcount;
    private final int isPlaying;
    private final int[] people;
    private boolean peopleFull;
    private int isPassword;
    private int roomKuang;
    private int roomMode;

    Room(byte b, String str, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        roomID = (byte) 0;
        roonName = "";
        fcount = 0;
        mcount = 0;
        people = new int[6];
        peopleFull = false;
        int totalPeople;
        isPassword = 0;
        roomKuang = 0;
        roomMode = 0;
        roomID = b;
        roonName = str;
        mcount = i2;
        fcount = i;
        isPlaying = i3;
        isPassword = i4;
        roomMode = i7;
        totalPeople = (i + i2) + i6;
        if (totalPeople == 6) {
            peopleFull = true;
        }
        for (int i8 = 0; i8 < i; i8++) {
            people[i8] = 1;
        }
        while (i < totalPeople - i6) {
            people[i] = 0;
            i++;
        }
        for (int i8 = 6 - i6; i8 < 6; i8++) {
            people[i8] = 3;
        }
        if (!peopleFull) {
            for (int i8 = totalPeople - i6; i8 < 6 - i6; i8++) {
                people[i8] = 2;
            }
        }
        roomKuang = i5;
    }

    final byte getRoomID() {
        return roomID;
    }

    final String getRoomName() {
        return roonName;
    }

    final int getFCount() {
        return fcount;
    }

    final int getMCount() {
        return mcount;
    }

    final int getIsPlaying() {
        return isPlaying;
    }

    final int[] getPeople() {
        return people;
    }

    final boolean getPeopleFull() {
        return peopleFull;
    }

    final int getIsPassword() {
        return isPassword;
    }

    final int getRoomKuang() {
        return roomKuang;
    }

    final int getRoomMode() {
        return roomMode;
    }
}
