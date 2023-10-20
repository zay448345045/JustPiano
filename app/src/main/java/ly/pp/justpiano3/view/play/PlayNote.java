package ly.pp.justpiano3.view.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.PlayView;

public final class PlayNote {
    public boolean hideNote;
    public boolean unPassed;
    public byte noteValue;
    public int trackValue;
    public byte volumeValue;
    public Bitmap noteImage = null;
    public int noteXPosition;
    public int posiAdd15AddAnim;
    public int noteDiv12;
    public int handValue;
    private byte playNote;
    private final PlayView playView;
    private final float halfWidth;
    private boolean newNote = true;
    private final int posiAdd15;

    public PlayNote(JPApplication jPApplication, PlayView playView, byte i, int i2, byte f, int f2, int i3, boolean z, int i4) {
        handValue = i4;
        this.playView = playView;
        noteDiv12 = i3;
        noteValue = i;
        hideNote = z;
        posiAdd15 = 15 + f2;
        trackValue = i2;
        unPassed = true;
        volumeValue = f;
        int widthPixels = playView.noteImage.getWidth();
        halfWidth = widthPixels / 2f;
        int width = jPApplication.getWidthPixels() / 16;
        int f4 = widthPixels / 2;
        if (trackValue != handValue) {
            return;
        }
        if (noteValue == i3 * 12 + 12) {
            noteImage = playView.noteImage;
            noteXPosition = width * 15 - f4;
            return;
        }
        switch (noteValue % 12) {
            case 0:
                noteXPosition = width - f4;
                noteImage = playView.noteImage;
                return;
            case 1:
                noteXPosition = 2 * width - f4;
                noteImage = playView.blackNoteImage;
                return;
            case 2:
                noteXPosition = 3 * width - f4;
                noteImage = playView.noteImage;
                return;
            case 3:
                noteXPosition = 4 * width - f4;
                noteImage = playView.blackNoteImage;
                return;
            case 4:
                noteXPosition = 5 * width - f4;
                noteImage = playView.noteImage;
                return;
            case 5:
                noteXPosition = 7 * width - f4;
                noteImage = playView.noteImage;
                return;
            case 6:
                noteXPosition = 8 * width - f4;
                noteImage = playView.blackNoteImage;
                return;
            case 7:
                noteXPosition = 9 * width - f4;
                noteImage = playView.noteImage;
                return;
            case 8:
                noteXPosition = 10 * width - f4;
                noteImage = playView.blackNoteImage;
                return;
            case 9:
                noteXPosition = 11 * width - f4;
                noteImage = playView.noteImage;
                return;
            case 10:
                noteXPosition = 12 * width - f4;
                noteImage = playView.blackNoteImage;
                return;
            case 11:
                noteXPosition = 13 * width - f4;
                noteImage = playView.noteImage;
                return;
            default:
        }
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float mo3107b(Canvas canvas) {    // 联网模式
        posiAdd15AddAnim = posiAdd15 + playView.progress;
        if (posiAdd15AddAnim >= playView.halfHeightSub20) {
            if (newNote && hideNote) {
                playNote = SoundEngineUtil.playSound(noteValue, (byte) (volumeValue * GlobalSetting.INSTANCE.getChordVolume()));
                newNote = false;
                return posiAdd15AddAnim;
            } else if (((double) posiAdd15AddAnim) >= playView.whiteKeyHeightAdd90) {
                SoundEngineUtil.stopPlaySound(playNote);
                return posiAdd15AddAnim;
            }
        }
        if (!(canvas == null || trackValue != handValue || hideNote)) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }

    public float mo3108c(Canvas canvas) {
        posiAdd15AddAnim = posiAdd15 + playView.progress;
        if (posiAdd15AddAnim >= playView.halfHeightSub20) {
            if (GlobalSetting.INSTANCE.getAutoPlay()) {
                if (newNote && ((trackValue != handValue || hideNote) && GlobalSetting.INSTANCE.isOpenChord())) {
                    playNote = SoundEngineUtil.playSound(noteValue, (byte) (volumeValue * GlobalSetting.INSTANCE.getChordVolume()));
                    newNote = false;
                    return posiAdd15AddAnim;
                }
            } else if (newNote && trackValue != handValue && GlobalSetting.INSTANCE.isOpenChord()) {
                playNote = SoundEngineUtil.playSound(noteValue, (byte) (volumeValue * GlobalSetting.INSTANCE.getChordVolume()));
                newNote = false;
                return posiAdd15AddAnim;
            }
            if (((double) posiAdd15AddAnim) >= playView.whiteKeyHeightAdd90) {
                SoundEngineUtil.stopPlaySound(playNote);
            }
        }
        if (GlobalSetting.INSTANCE.getAutoPlay()) {
            if (canvas != null && trackValue == handValue && !hideNote) {
                canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
            }
        } else if (canvas != null && trackValue == handValue) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }

    public void noCompatibleMode(Canvas canvas) {  // 欣赏模式
        posiAdd15AddAnim = posiAdd15 + playView.progress;
        if (posiAdd15AddAnim >= playView.halfHeightSub20) {
            if (newNote && GlobalSetting.INSTANCE.isOpenChord()) {
                if (trackValue == handValue) {
                    playView.drawFire(canvas, noteValue % 12);
                }
                playNote = SoundEngineUtil.playSound(noteValue, volumeValue);
                newNote = false;
            }
            if (((double) posiAdd15AddAnim) >= playView.whiteKeyHeightAdd90) {
                SoundEngineUtil.stopPlaySound(playNote);
            }
        }
        if (canvas != null && trackValue == handValue) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
    }
}
