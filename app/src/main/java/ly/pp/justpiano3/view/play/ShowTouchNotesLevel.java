package ly.pp.justpiano3.view.play;

import android.graphics.Bitmap;

import ly.pp.justpiano3.view.PlayView;

public final class ShowTouchNotesLevel {
    public int width;
    public int height;
    public int comboNum;
    public PlayView playView;
    public Bitmap levelImage;

    public ShowTouchNotesLevel(int i, PlayView playView, int comboNum, int screenWidth, int screenHeight) {
        width = screenWidth / 2;
        height = screenHeight / 4;
        this.playView = playView;
        this.comboNum = comboNum;
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
