package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.local.MelodySelect;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.thread.SongPlay;

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
        SongPlay.INSTANCE.stopPlay();
        Intent intent = new Intent(melodySelect, PianoPlay.class);
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
        if (melodySelect.isPractise.isChecked()) {
            GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.PRACTISE);
        } else {
            GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
        }
        intent.putExtra(str, i);
        melodySelect.startActivity(intent);
    }
}
