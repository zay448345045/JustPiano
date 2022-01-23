package ly.pp.justpiano3;

import java.io.Serializable;

public class LocalSongData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String path;
    private int isfavo;
    private int score;
    private int lScore;

    public LocalSongData(String path, int isfavo, int score, int lScore) {
        this.path = path;
        this.isfavo = isfavo;
        this.score = score;
        this.lScore = lScore;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIsfavo() {
        return isfavo;
    }

    public void setIsfavo(int isfavo) {
        this.isfavo = isfavo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getlScore() {
        return lScore;
    }

    public void setlScore(int lScore) {
        this.lScore = lScore;
    }
}