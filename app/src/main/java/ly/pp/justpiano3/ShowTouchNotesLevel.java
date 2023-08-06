package ly.pp.justpiano3;

import android.graphics.Bitmap;

public final class ShowTouchNotesLevel {
    int screenWidth;
    int screenHeight;
    int comboNum;
    PlayView playView;
    Bitmap levelImage;

    public ShowTouchNotesLevel(int i, PlayView playView, int i2, int i3, int i4) {
        screenWidth = i3 / 2;
        screenHeight = i4 / 4;
        this.playView = playView;
        comboNum = i2;
        switch (i) {
            case -1:
                levelImage = playView.missImage;
                return;
            case 1:
                levelImage = playView.perfectImage;
                return;
            case 2:
                levelImage = playView.coolImage;
                return;
            case 3:
                levelImage = playView.greatImage;
                return;
            case 5:
                levelImage = playView.badImage;
                return;
            default:
        }
    }
}
