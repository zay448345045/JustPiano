package ly.pp.justpiano3.view.play;

import android.graphics.Bitmap;

import ly.pp.justpiano3.view.PlayView;

public final class ShowTouchNotesLevel {
    public int screenWidth;
    public int screenHeight;
    public int comboNum;
    public PlayView playView;
    public Bitmap levelImage;

    public ShowTouchNotesLevel(int i, PlayView playView, int i2, int i3, int i4) {
        screenWidth = i3 / 2;
        screenHeight = i4 / 4;
        this.playView = playView;
        comboNum = i2;
        switch (i) {
            case -1 -> levelImage = playView.missImage;
            case 1 -> levelImage = playView.perfectImage;
            case 2 -> levelImage = playView.coolImage;
            case 3 -> levelImage = playView.greatImage;
            case 5 -> levelImage = playView.badImage;
            default -> {
            }
        }
    }
}
