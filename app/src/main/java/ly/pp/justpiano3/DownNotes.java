package ly.pp.justpiano3;

public final class DownNotes extends Thread {
    private final JPApplication jpapplication;
    private final int sleepTime;
    private boolean isStop;
    private final PianoPlay pianoPlay;

    DownNotes(JPApplication jPApplication, int downSpeed, PianoPlay pianoPlay) {
        jpapplication = jPApplication;
        sleepTime = downSpeed * jpapplication.getAnimFrame();
        this.pianoPlay = pianoPlay;
    }

    @Override
    public void run() {
        while (pianoPlay.isPlayingStart) {
            while (pianoPlay.playView.startFirstNoteTouching) {
                try {
                    if (jpapplication.getGameMode() != 2 || pianoPlay.playView.isTouchRightNote) {
                        jpapplication.downNote();
                    }
                    DownNotes.sleep(sleepTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}