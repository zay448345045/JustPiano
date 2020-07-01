package ly.pp.justpiano3;

import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final class TouchNotes implements OnTouchListener {
    private final PlayView playView;
    private int moveNoteNum = -2;
    private int tapCounts;
    private float lastY;

    TouchNotes(PlayView playView) {
        this.playView = playView;
    }

    @Override
    public final boolean onTouch(View view, MotionEvent motionEvent) {
        if (playView.startFirstNoteTouching) {
            int actionIndex = motionEvent.getActionIndex();
            float x = motionEvent.getX(actionIndex);
            float y = motionEvent.getY(actionIndex);
            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (playView.currentPlayNote != null) {
                        playView.posiAdd15AddAnim = playView.currentPlayNote.posiAdd15AddAnim;
                    }
                    int touchNoteNum = playView.eventPositionToTouchNoteNum(x, y);
                    if (y >= playView.jpapplication.getWhiteKeyHeight() && touchNoteNum != -1 && !playView.pianoPlay.keyboardview.touchNoteSet.containsKey(touchNoteNum)) {
                        playView.pianoPlay.keyboardview.touchNoteSet.put(touchNoteNum, 0);
                        playView.judgeAndPlaySound(touchNoteNum);
                        moveNoteNum = touchNoteNum;
                    }
                    if (Math.abs(y - lastY) < 2){
                        tapCounts++;
                    } else {
                        tapCounts = 0;
                    }
                    if (tapCounts > 64){
                        playView.pianoPlay.finish();
                    }
                    lastY = y;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    touchNoteNum = playView.eventPositionToTouchNoteNum(x, y);
                    playView.pianoPlay.keyboardview.touchNoteSet.remove(touchNoteNum);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (playView.currentPlayNote != null) {
                        playView.posiAdd15AddAnim = playView.currentPlayNote.posiAdd15AddAnim;
                    }
                    touchNoteNum = playView.eventPositionToTouchNoteNum(x, y);
                    if (touchNoteNum != moveNoteNum) {
                        playView.pianoPlay.keyboardview.touchNoteSet.remove(moveNoteNum);
                        if (y >= playView.jpapplication.getWhiteKeyHeight() && touchNoteNum != -1 && !playView.pianoPlay.keyboardview.touchNoteSet.containsKey(touchNoteNum)) {
                            playView.pianoPlay.keyboardview.touchNoteSet.put(touchNoteNum, 0);
                            playView.judgeAndPlaySound(touchNoteNum);
                            moveNoteNum = touchNoteNum;
                        }
                    }
                    tapCounts = 0;
                    break;
            }
        }
        if (playView.jpapplication.hasKeyboardPerfer()) {
            Message obtainMessage = playView.pianoPlay.pianoPlayHandler.obtainMessage();
            obtainMessage.what = 4;
            playView.pianoPlay.pianoPlayHandler.handleMessage(obtainMessage);
        }
        return true;
    }
}
