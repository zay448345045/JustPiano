package ly.pp.justpiano3;

import java.util.List;

public final class ShowScoreAndLevels extends Thread {
    int levelScore;
    int comboScore;
    private List<ShowTouchNotesLevel> f5843a;
    private PianoPlay pianoPlay;

    ShowScoreAndLevels(List<ShowTouchNotesLevel> arrayList, PianoPlay pianoPlay) {
        f5843a = arrayList;
        levelScore = 0;
        comboScore = 0;
        this.pianoPlay = pianoPlay;
    }

    final void computeScore(ShowTouchNotesLevel showTouchNotesLevel, int i, int i2) {
        if (f5843a.size() > 1) {
            f5843a.remove(0);
        }
        f5843a.add(0, showTouchNotesLevel);
        if (i2 <= 0) {
            levelScore += i;
        } else if (i2 <= 11) {
            comboScore = (comboScore + i2) - 1;
            levelScore = ((levelScore + i) + i2) - 1;
        } else {
            comboScore += 10;
            levelScore = (levelScore + i) + 10;
        }
        if (levelScore < 0) {
            levelScore = 0;
        }
    }

    @Override
    public final void run() {
        while (pianoPlay.isPlayingStart) {
            try {
                for (ShowTouchNotesLevel aF5843a : f5843a) {
                    aF5843a.screenHeight -= 10;
                }
                ShowScoreAndLevels.sleep(60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
