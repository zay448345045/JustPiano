package ly.pp.justpiano3.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String playerName = "";
    private String sex = "";
    private String status = "";
    private String ishost = "";
    private byte position = (byte) 0;
    private int score = 0;
    private int combo;
    private int kuang;
    private final int level;
    private int trousers;
    private int jacket;
    private int hair;
    private int eye;
    private final int clevel;
    private int hand = 0;
    private int cpKind = 0;
    private int shoes;
    private String familyID = "0";

    public User(byte b, String str, int hair, int eye, int jacket, int trousers, int shoes, String str2, String str3, String str4, int i, int i2, int i3, int i4, int i5, String familyID) {
        position = b;
        playerName = str;
        sex = str2;
        status = str3;
        ishost = str4;
        setClothes(hair, eye, jacket, trousers, shoes);
        kuang = i2;
        level = i;
        clevel = i3;
        hand = i4;
        cpKind = i5;
        this.familyID = familyID;
    }

    public User(String str, int hair, int eye, int jacket, int trousers, int shoes, String str2, int i, int i2) {
        playerName = str;
        sex = str2;
        setClothes(hair, eye, jacket, trousers, shoes);
        level = i;
        clevel = i2;
    }

    private void setClothes(int hair, int eye, int jacket, int trousers, int shoes) {
        this.hair = hair;
        this.eye = eye;
        this.jacket = jacket;
        this.trousers = trousers;
        this.shoes = shoes;
    }

    public final void setOpenPosition() {
        ishost = "OPEN";
    }
}
