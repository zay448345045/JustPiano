package ly.pp.justpiano3.entity;

public final class Room {

    private static final int peopleCapacity = 6;
    private final byte roomID;
    private final String roonName;
    private final int fcount;
    private final int mcount;
    private final int isPlaying;
    private final int[] people;
    private boolean peopleFull;
    private final int isPassword;
    private final int roomKuang;
    private final int roomMode;

    public Room(byte b, String str, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        peopleFull = false;
        int notEmptyPositionNum;
        roomID = b;
        roonName = str;
        mcount = i2;
        fcount = i;
        isPlaying = i3;
        isPassword = i4;
        roomMode = i7;
        people = new int[peopleCapacity];
        notEmptyPositionNum = (i + i2) + i6;
        if (notEmptyPositionNum == peopleCapacity) {
            peopleFull = true;
        }
        for (int i8 = 0; i8 < i; i8++) {
            people[i8] = 1;
        }
        while (i < notEmptyPositionNum - i6) {
            people[i] = 0;
            i++;
        }
        for (int i8 = peopleCapacity - i6; i8 < peopleCapacity; i8++) {
            people[i8] = 3;
        }
        if (!peopleFull) {
            for (int i8 = notEmptyPositionNum - i6; i8 < peopleCapacity - i6; i8++) {
                people[i8] = 2;
            }
        }
        roomKuang = i5;
    }

    public byte getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roonName;
    }

    public int getFCount() {
        return fcount;
    }

    public int getMCount() {
        return mcount;
    }

    public int getIsPlaying() {
        return isPlaying;
    }

    public int[] getPeople() {
        return people;
    }

    public boolean getPeopleFull() {
        return peopleFull;
    }

    public int getIsPassword() {
        return isPassword;
    }

    public int getRoomKuang() {
        return roomKuang;
    }

    public int getRoomMode() {
        return roomMode;
    }
}
