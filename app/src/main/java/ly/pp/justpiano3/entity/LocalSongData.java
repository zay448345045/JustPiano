package ly.pp.justpiano3.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(path);
        oos.writeObject(isfavo);
        oos.writeObject(score);
        oos.writeObject(lScore);
        oos.writeObject(score * lScore ^ 2134);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        path = (String) ois.readObject();
        isfavo = (int) ois.readObject();
        score = (int) ois.readObject();
        lScore = (int) ois.readObject();
        int sign = (int) ois.readObject();
        if (sign != (score * lScore ^ 2134)) {
            throw new IOException("Don't modify data");
        }
    }
}