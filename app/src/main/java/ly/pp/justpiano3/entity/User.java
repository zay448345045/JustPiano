package ly.pp.justpiano3.entity;

import java.io.Serializable;

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

    public final String getPlayerName() {
        return playerName;
    }

    public final void setPlayerName(String str) {
        playerName = str;
    }

    public final String getSex() {
        return sex;
    }

    public final String getStatus() {
        return status;
    }

    public final void setStatus(String str) {
        status = str;
    }

    public final void setOpenPosition() {
        ishost = "OPEN";
    }

    public final String getIsHost() {
        return ishost;
    }

    public final byte getPosition() {
        return position;
    }

    public final String getScore() {
        return String.valueOf(score);
    }

    public final void setScore(int i) {
        score = i;
    }

    public final int getKuang() {
        return kuang;
    }

    public final int getLevel() {
        return level;
    }

    public final int getTrousers() {
        return trousers;
    }

    public final int getJacket() {
        return jacket;
    }

    public final int getHair() {
        return hair;
    }

    public int getEye() {
        return eye;
    }

    public void setEye(int eye) {
        this.eye = eye;
    }

    public final int getCLevel() {
        return clevel;
    }

    public final int getHand() {
        return hand;
    }

    public final int getCpKind() {
        return cpKind;
    }

    public final int getShoes() {
        return shoes;
    }

    public final int getCombo() {
        return combo;
    }

    public final void setCombo(int i) {
        combo = i;
    }

    public final String getFamilyID() {
        return familyID;
    }

    public final void setFamilyID(String familyID) {
        this.familyID = familyID;
    }
}
