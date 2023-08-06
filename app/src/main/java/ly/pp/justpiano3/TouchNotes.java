package ly.pp.justpiano3;

import android.content.pm.PackageManager;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.HashMap;
import java.util.Map;

public final class TouchNotes implements OnTouchListener {
    private final PlayView playView;
    private final Map<Integer, Integer> mFingerMap = new HashMap<>();

    public TouchNotes(PlayView playView) {
        this.playView = playView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (playView.pianoPlay.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (playView.pianoPlay.jpapplication.midiOutputPort != null && playView.pianoPlay.midiFramer == null) {
                    playView.pianoPlay.midiFramer = new MidiFramer(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] data, int offset, int count, long timestamp) {
                            playView.pianoPlay.midiConnectHandle(data);
                        }
                    });
                    playView.pianoPlay.jpapplication.midiOutputPort.connect(playView.pianoPlay.midiFramer);
                }
                playView.pianoPlay.jpapplication.addMidiConnectionListener(playView.pianoPlay);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (playView.startFirstNoteTouching) {
            int action = event.getActionMasked();
            // Track individual fingers.
            int pointerIndex = event.getActionIndex();
            int id = event.getPointerId(pointerIndex);
            // Get the pointer's current position
            float x = event.getX(pointerIndex);
            float y = event.getY(pointerIndex);
            // Some devices can return negative x or y, which can cause an array exception.
            x = Math.max(x, 0.0f);
            y = Math.max(y, 0.0f);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    onFingerDown(id, x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        id = event.getPointerId(i);
                        x = event.getX(i);
                        y = event.getY(i);
                        x = Math.max(x, 0.0f);
                        y = Math.max(y, 0.0f);
                        onFingerMove(id, x, y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    onFingerUp(id, x, y);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onAllFingersUp();
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void onFingerDown(int id, float x, float y) {
        if (playView.currentPlayNote != null) {
            playView.posiAdd15AddAnim = playView.currentPlayNote.posiAdd15AddAnim;
        }
        int touchNoteNum = playView.eventPositionToTouchNoteNum(x, y);
        fireKeyDown(touchNoteNum);
        mFingerMap.put(id, touchNoteNum);
    }

    private void onFingerMove(int id, float x, float y) {
        if (playView.currentPlayNote != null) {
            playView.posiAdd15AddAnim = playView.currentPlayNote.posiAdd15AddAnim;
        }
        Integer previousTouchNoteNum = mFingerMap.get(id);
        if (previousTouchNoteNum != null) {
            int touchNoteNum = playView.eventPositionToTouchNoteNum(x, y);
            // Did we change to a new key.
            if (touchNoteNum >= 0 && touchNoteNum != previousTouchNoteNum) {
                fireKeyDown(touchNoteNum);
                fireKeyUp(previousTouchNoteNum);
                mFingerMap.put(id, touchNoteNum);
            }
        }
    }

    private void onFingerUp(int id, float x, float y) {
        Integer previousPitch = mFingerMap.get(id);
        if (previousPitch != null) {
            fireKeyUp(previousPitch);
            mFingerMap.remove(id);
        } else {
            int touchNoteNum = playView.eventPositionToTouchNoteNum(x, y);
            fireKeyUp(touchNoteNum);
        }
    }

    private void onAllFingersUp() {
        // Turn off all notes.
        for (Integer pitch : mFingerMap.values()) {
            fireKeyUp(pitch);
        }
        mFingerMap.clear();
    }

    public void fireKeyDown(int touchNoteNum) {
        if (touchNoteNum != -1 && !playView.pianoPlay.keyboardview.touchNoteSet.containsKey(touchNoteNum)) {
            playView.pianoPlay.keyboardview.touchNoteSet.put(touchNoteNum, 0);
            playView.judgeAndPlaySound(touchNoteNum);
            playView.pianoPlay.updateKeyboardPrefer();
        }
    }

    public void fireKeyUp(int touchNoteNum) {
        playView.pianoPlay.keyboardview.touchNoteSet.remove(touchNoteNum);
        playView.pianoPlay.updateKeyboardPrefer();
    }
}
