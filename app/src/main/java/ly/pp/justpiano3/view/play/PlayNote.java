package ly.pp.justpiano3.view.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.view.PlayView;

public final class PlayNote {
    public boolean hide;
    public boolean unPassed;
    public byte pitch;
    public byte track;
    public byte volume;
    public Bitmap image = null;
    public int positionX;
    public int posiAdd15AddAnim;
    public int octave;
    public int hand;
    private final PlayView playView;
    private final float halfWidth;
    private boolean newNote = true;
    private final int posiAdd15;

    public PlayNote(PlayView playView, byte i, byte i2, byte f, int f2, int i3, boolean z, int i4) {
        hand = i4;
        this.playView = playView;
        octave = i3;
        pitch = i;
        hide = z;
        posiAdd15 = 15 + f2;
        track = i2;
        unPassed = true;
        volume = f;
        int widthPixels = playView.noteImage.getWidth();
        halfWidth = widthPixels / 2f;
        int width = playView.getWidth() / 16;
        int halfWidthPixels = widthPixels / 2;
        if (track != hand) {
            return;
        }
        if (pitch == i3 * 12 + 12) {
            image = playView.noteImage;
            positionX = width * 15 - halfWidthPixels;
            return;
        }
        switch (pitch % 12) {
            case 0:
                positionX = width - halfWidthPixels;
                image = playView.noteImage;
                return;
            case 1:
                positionX = 2 * width - halfWidthPixels;
                image = playView.blackNoteImage;
                return;
            case 2:
                positionX = 3 * width - halfWidthPixels;
                image = playView.noteImage;
                return;
            case 3:
                positionX = 4 * width - halfWidthPixels;
                image = playView.blackNoteImage;
                return;
            case 4:
                positionX = 5 * width - halfWidthPixels;
                image = playView.noteImage;
                return;
            case 5:
                positionX = 7 * width - halfWidthPixels;
                image = playView.noteImage;
                return;
            case 6:
                positionX = 8 * width - halfWidthPixels;
                image = playView.blackNoteImage;
                return;
            case 7:
                positionX = 9 * width - halfWidthPixels;
                image = playView.noteImage;
                return;
            case 8:
                positionX = 10 * width - halfWidthPixels;
                image = playView.blackNoteImage;
                return;
            case 9:
                positionX = 11 * width - halfWidthPixels;
                image = playView.noteImage;
                return;
            case 10:
                positionX = 12 * width - halfWidthPixels;
                image = playView.blackNoteImage;
                return;
            case 11:
                positionX = 13 * width - halfWidthPixels;
                image = playView.noteImage;
                return;
            default:
        }
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float onlineNotePlayHandle(Canvas canvas) {
        posiAdd15AddAnim = posiAdd15 + playView.progress;
        if (posiAdd15AddAnim >= playView.halfHeightSub20) {
            if (newNote && hide) {
                playView.pianoPlay.playNoteSoundHandle(track, pitch, (byte) (volume * GlobalSetting.INSTANCE.getChordVolume()));
                newNote = false;
                return posiAdd15AddAnim;
            } else if ((double) posiAdd15AddAnim >= playView.whiteKeyHeightAdd90) {
                return posiAdd15AddAnim;
            }
        }
        if (!(canvas == null || track != hand || hide)) {
            canvas.drawBitmap(image, positionX, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }

    public float localNotePlayHandle(Canvas canvas) {
        posiAdd15AddAnim = posiAdd15 + playView.progress;
        if (posiAdd15AddAnim >= playView.halfHeightSub20) {
            if (GlobalSetting.INSTANCE.getAutoPlay()) {
                if (newNote && ((track != hand || hide) && GlobalSetting.INSTANCE.isOpenChord())) {
                    playView.pianoPlay.playNoteSoundHandle(track, pitch, (byte) (volume * GlobalSetting.INSTANCE.getChordVolume()));
                    newNote = false;
                    return posiAdd15AddAnim;
                }
            } else if (newNote && track != hand && GlobalSetting.INSTANCE.isOpenChord()) {
                playView.pianoPlay.playNoteSoundHandle(track, pitch, (byte) (volume * GlobalSetting.INSTANCE.getChordVolume()));
                newNote = false;
                return posiAdd15AddAnim;
            }
        }
        if (GlobalSetting.INSTANCE.getAutoPlay()) {
            if (canvas != null && track == hand && !hide) {
                canvas.drawBitmap(image, positionX, posiAdd15AddAnim, null);
            }
        } else if (canvas != null && track == hand) {
            canvas.drawBitmap(image, positionX, posiAdd15AddAnim, null);
        }
        return posiAdd15AddAnim;
    }
}
