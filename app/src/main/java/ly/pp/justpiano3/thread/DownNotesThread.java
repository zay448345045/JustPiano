package ly.pp.justpiano3.thread;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.enums.GameModeEnum;

public final class DownNotesThread extends Thread {
    private final JPApplication jpapplication;
    private final int sleepTime;
    private final PianoPlay pianoPlay;

    public DownNotesThread(JPApplication jPApplication, int downSpeed, PianoPlay pianoPlay) {
        jpapplication = jPApplication;
        sleepTime = downSpeed * jpapplication.getSetting().getAnimFrame();
        this.pianoPlay = pianoPlay;
    }

    @Override
    public void run() {
        while (pianoPlay.isPlayingStart) {
            while (pianoPlay.playView.startFirstNoteTouching) {
                try {
                    if (jpapplication.getGameMode() != GameModeEnum.PRACTISE.getCode() || pianoPlay.playView.isTouchRightNote) {
                        jpapplication.downNote();
                    }
                    DownNotesThread.sleep(sleepTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}