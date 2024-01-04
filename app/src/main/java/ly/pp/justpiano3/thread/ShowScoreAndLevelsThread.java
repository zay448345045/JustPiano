package ly.pp.justpiano3.thread;

import java.util.List;

import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.view.play.ShowTouchNotesLevel;

public final class ShowScoreAndLevelsThread extends Thread {
    public int levelScore;
    public int comboScore;
    private final List<ShowTouchNotesLevel> showTouchNotesLevelList;
    private final PianoPlay pianoPlay;

    public ShowScoreAndLevelsThread(List<ShowTouchNotesLevel> showTouchNotesLevelList, PianoPlay pianoPlay) {
        this.showTouchNotesLevelList = showTouchNotesLevelList;
        this.pianoPlay = pianoPlay;
    }

    public void computeScore(ShowTouchNotesLevel showTouchNotesLevel, int i, int i2) {
        if (showTouchNotesLevelList.size() > 1) {
            showTouchNotesLevelList.remove(0);
        }
        showTouchNotesLevelList.add(0, showTouchNotesLevel);
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
    public void run() {
        while (pianoPlay.isPlayingStart) {
            try {
                for (ShowTouchNotesLevel showTouchNotesLevel : showTouchNotesLevelList) {
                    showTouchNotesLevel.height -= 10;
                }
                ShowScoreAndLevelsThread.sleep(60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
