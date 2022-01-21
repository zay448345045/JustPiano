package ly.pp.justpiano3;

import org.json.JSONException;
import org.json.JSONObject;

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
    private final int clevel;
    private int hand = 0;
    private int cpKind = 0;
    private int shoes = 0;
    private String familyID = "0";

    public User(byte b, String str, JSONObject jSONObject, String str2, String str3, String str4, int i, int i2, int i3, int i4, int i5, String familyID) {
        position = b;
        playerName = str;
        sex = str2;
        status = str3;
        ishost = str4;
        setClothes(jSONObject);
        kuang = i2;
        level = i;
        clevel = i3;
        hand = i4;
        cpKind = i5;
        this.familyID = familyID;
    }

    public User(String str, JSONObject jSONObject, String str2, int i, int i2) {
        playerName = str;
        sex = str2;
        setClothes(jSONObject);
        level = i;
        clevel = i2;
    }

    private void setClothes(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                sex = jSONObject.getString("S");
                trousers = jSONObject.getInt("T");
                jacket = jSONObject.getInt("J");
                hair = jSONObject.getInt("H");
                shoes = jSONObject.getInt("O");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    final String getPlayerName() {
        return playerName;
    }

    final void setPlayerName(String str) {
        playerName = str;
    }

    final String getSex() {
        return sex;
    }

    final String getStatus() {
        return status;
    }

    public final void setStatus(String str) {
        status = str;
    }

    final void setOpenPosition() {
        ishost = "OPEN";
    }

    final String getIsHost() {
        return ishost;
    }

    final byte getPosition() {
        return position;
    }

    final String getScore() {
        return String.valueOf(score);
    }

    final void setScore(int i) {
        score = i;
    }

    final int getKuang() {
        return kuang;
    }

    final int getLevel() {
        return level;
    }

    final int getTrousers() {
        return trousers;
    }

    final int getJacket() {
        return jacket;
    }

    final int getHair() {
        return hair;
    }

    final int getCLevel() {
        return clevel;
    }

    final int getHand() {
        return hand;
    }

    final int getCpKind() {
        return cpKind;
    }

    final int getShoes() {
        return shoes;
    }

    final int getCombo() {
        return combo;
    }

    final void setCombo(int i) {
        combo = i;
    }

    final String getFamilyID() {
        return familyID;
    }

    final void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    @Override
    public String toString() {
        return "User{" +
                "playerName='" + playerName + '\'' +
                ", sex='" + sex + '\'' +
                ", status='" + status + '\'' +
                ", ishost='" + ishost + '\'' +
                ", position=" + position +
                ", score=" + score +
                ", combo=" + combo +
                ", kuang=" + kuang +
                ", level=" + level +
                ", trousers=" + trousers +
                ", jacket=" + jacket +
                ", hair=" + hair +
                ", clevel=" + clevel +
                ", hand=" + hand +
                ", cpKind=" + cpKind +
                ", shoes=" + shoes +
                ", familyID='" + familyID + '\'' +
                '}';
    }
}
