package ly.pp.justpiano3;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import static ly.pp.justpiano3.PlaySongs.GROUP;

final class PlayNote {
    boolean hideNote;
    boolean unPassed;
    int noteValue;
    int trackValue;
    int volumeValue;
    Bitmap noteImage = null;
    int noteXPosition;
    int posiAdd15AddAnim;
    int noteDiv12;
    int handValue;
    private int playNote;
    private PlayView playView;
    private JPApplication jpapplication;
    private float halfWidth;
    private boolean newNote = true;
    private int posiAdd15;

    PlayNote(JPApplication jPApplication, PlayView playView, int i, int i2, int f, int f2, int i3, boolean z, int i4) {
        handValue = i4;
        jpapplication = jPApplication;
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
        int width = jpapplication.getWidthPixels() / 16;
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

    final void mo3105a(Canvas canvas) {  //欣赏模式
        posiAdd15AddAnim = posiAdd15 + jpapplication.getAnimPosition();
        if (posiAdd15AddAnim >= jpapplication.getHalfHeightSub20()) {
            if (newNote) {
                jpapplication.drawFire(playView, canvas, noteValue % 12);
                if (playView.offset % GROUP == 0 && jpapplication.getOpenChord()) {
                    jpapplication.playMusic();
                    if (playView.offset + (GROUP << 1) < playView.arrayLength) {
                        String[] commands = jpapplication.makeFFmpegCmd(playView.tickGroupArray,
                                playView.trackXArray, playView.noteArray, playView.volumeArray,
                                playView.offset + (GROUP << 1),
                                Math.min(playView.arrayLength, playView.offset + 3 * GROUP),
                                (jpapplication.mpIndex + 1) % 3);
                        jpapplication.ffmpegPlayTask(commands, (jpapplication.mpIndex + 1) % 3);
                    }
                }
                playView.offset++;
                newNote = false;
            }
        }
        if (canvas != null && trackValue == handValue) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
    }

    final float getHalfWidth() {
        return halfWidth;
    }

    final float mo3107b(Canvas canvas) {    //联网模式
        posiAdd15AddAnim = posiAdd15 + jpapplication.getAnimPosition();
        if (posiAdd15AddAnim >= jpapplication.getHalfHeightSub20()) {
            if (newNote && hideNote) {
                playNote = jpapplication.playSound(noteValue, volumeValue);
                newNote = false;
                return posiAdd15AddAnim;
            } else if (((double) posiAdd15AddAnim) >= jpapplication.getWhiteKeyHeightAdd90()) {
                playView.jpapplication.stopSongs(playNote);
                return posiAdd15AddAnim;
            }
        }
        if (!(canvas == null || trackValue != handValue || hideNote)) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }

    final float mo3108c(Canvas canvas) {
        posiAdd15AddAnim = posiAdd15 + jpapplication.getAnimPosition();
        if (posiAdd15AddAnim >= jpapplication.getHalfHeightSub20()) {
            if (jpapplication.getAutoPlay()) {
                if (newNote && ((trackValue != handValue || hideNote) && jpapplication.getOpenChord())) {
                    playNote = jpapplication.playSound(noteValue, volumeValue);
                    newNote = false;
                    return posiAdd15AddAnim;
                }
            } else if (newNote && trackValue != handValue && jpapplication.getOpenChord()) {
                playNote = jpapplication.playSound(noteValue, (volumeValue * jpapplication.getChordVolume()) / 100.0f);
                newNote = false;
                return posiAdd15AddAnim;
            }
            if (((double) posiAdd15AddAnim) >= jpapplication.getWhiteKeyHeightAdd90()) {
                playView.jpapplication.stopSongs(playNote);
            }
        }
        if (jpapplication.getAutoPlay()) {
            if (canvas != null && trackValue == handValue && !hideNote) {
                canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
            }
        } else if (canvas != null && trackValue == handValue) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }

    final float compatibleMode(Canvas canvas) {
        posiAdd15AddAnim = posiAdd15 + jpapplication.getAnimPosition();
        if (posiAdd15AddAnim >= jpapplication.getHalfHeightSub20()) {
            if (newNote) {
                if (playView.offset % GROUP == 0 && jpapplication.getOpenChord()) {
                    jpapplication.playMusic();
                    if (playView.offset + (GROUP << 1) < playView.arrayLength) {
                        String[] commands = jpapplication.makeFFmpegCmd(playView.tickGroupArray,
                                playView.trackXArray, playView.noteArray, playView.volumeArray,
                                playView.offset + (GROUP << 1),
                                Math.min(playView.arrayLength, playView.offset + 3 * GROUP),
                                (jpapplication.mpIndex + 1) % 3);
                        jpapplication.ffmpegPlayTask(commands, (jpapplication.mpIndex + 1) % 3);
                    }
                }
                playView.offset++;
                newNote = false;
                return posiAdd15AddAnim;
            }
        }
        if (jpapplication.getAutoPlay()) {
            if (canvas != null && trackValue == handValue && !hideNote) {
                canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
            }
        } else if (canvas != null && trackValue == handValue) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }

    final void noCompatibleMode(Canvas canvas) {  //欣赏模式且没开兼容模式
        posiAdd15AddAnim = posiAdd15 + jpapplication.getAnimPosition();
        if (posiAdd15AddAnim >= jpapplication.getHalfHeightSub20()) {
            float s = (volumeValue * jpapplication.getChordVolume()) / 100.0f;
            if (newNote && jpapplication.getOpenChord()) {
                if (trackValue == handValue) {
                    jpapplication.drawFire(playView, canvas, noteValue % 12);
                }
                playNote = jpapplication.playSound(noteValue, s);
                newNote = false;
            }
            if (((double) posiAdd15AddAnim) >= jpapplication.getWhiteKeyHeightAdd90()) {
                jpapplication.stopSongs(playNote);
            }
        }
        if (canvas != null && trackValue == handValue) {
            canvas.drawBitmap(noteImage, noteXPosition, posiAdd15AddAnim, null);
        }
    }
}
