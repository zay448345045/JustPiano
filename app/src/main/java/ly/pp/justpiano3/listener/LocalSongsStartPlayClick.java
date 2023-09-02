package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.database.entity.Song;

public final class LocalSongsStartPlayClick implements OnClickListener {
    private final MelodySelect melodySelect;
    private final String path;
    private final String name;
    private final double rightHandDegree;
    private final double leftHandDegree;
    private final int songsTime;
    private final int score;

    public LocalSongsStartPlayClick(MelodySelect melodySelect, Song song) {
        this.melodySelect = melodySelect;
        path = song.getFilePath();
        name = song.getName();
        rightHandDegree = song.getRightHandDegree();
        leftHandDegree = song.getLeftHandDegree();
        songsTime = song.getLength();
        score = song.getRightHandHighScore();
    }

    @Override
    public void onClick(View view) {
        int i = 0;
        melodySelect.jpapplication.stopPlaySong();
        Intent intent = new Intent();
        intent.putExtra("head", 0);
        intent.putExtra("path", path);
        intent.putExtra("name", name);
        intent.putExtra("nandu", rightHandDegree);
        intent.putExtra("leftnandu", leftHandDegree);
        intent.putExtra("songstime", songsTime);
        intent.putExtra("score", score);
        intent.putExtra("isrecord", melodySelect.isRecord.isChecked());
        String str = "hand";
        if (melodySelect.isLeftHand.isChecked()) {
            i = 1;
        }
        intent.putExtra(str, i);
        intent.setClass(melodySelect, PianoPlay.class);
        melodySelect.startActivity(intent);
    }
}
