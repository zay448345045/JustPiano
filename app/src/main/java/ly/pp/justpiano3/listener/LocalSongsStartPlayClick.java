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
        SongPlay.stopPlay();
        Intent intent = new Intent(melodySelect, PianoPlay.class);
        intent.putExtra("head", 0);
        intent.putExtra("path", path);
        intent.putExtra("name", name);
        intent.putExtra("rightHandDegree", rightHandDegree);
        intent.putExtra("leftHandDegree", leftHandDegree);
        intent.putExtra("songsTime", songsTime);
        intent.putExtra("score", score);
        intent.putExtra("isOpenRecord", melodySelect.isRecord.isChecked());
        GlobalSetting.setLocalPlayMode(melodySelect.isPractise.isChecked()
                ? LocalPlayModeEnum.PRACTISE : LocalPlayModeEnum.NORMAL);
        intent.putExtra("hand", melodySelect.isLeftHand.isChecked() ? 1 : 0);
        melodySelect.startActivity(intent);
    }
}
