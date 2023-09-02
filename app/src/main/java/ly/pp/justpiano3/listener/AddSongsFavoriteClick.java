package ly.pp.justpiano3.listener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.database.entity.Song;

public final class AddSongsFavoriteClick implements OnClickListener {
    private final MelodySelect melodySelect;
    private final Song song;
    private final String songsPath;
    private final String songsName;

    public AddSongsFavoriteClick(MelodySelect melodySelect, Song song) {
        this.melodySelect = melodySelect;
        this.song = song;
        songsPath = song.getFilePath();
        songsName = song.getName();
    }

    public void onClick(View view) {
        try {
            JPApplication.getSongDatabase().songDao().updateFavoriteSong(songsPath, song.isFavorite() == 0 ? 1 : 0);
            Toast.makeText(melodySelect, songsName + (song.isFavorite() == 0 ? ":已移出收藏夹" : ":已加入收藏夹"), Toast.LENGTH_SHORT).show();
            switch (melodySelect.f4264t) {
                case 0:
                    melodySelect.mo2784a(melodySelect.sqlitedatabase.query("jp_data", null, "isfavo = 1", null, null, null, melodySelect.sortStr));
                    return;
                case 1:
                case 4:
                case 2:
                case 6:
                case 5:
                case 3:
                    melodySelect.mo2784a(melodySelect.sqlitedatabase.query("jp_data", null, "item=?", new String[]{melodySelect.f4263s}, null, null, melodySelect.sortStr));
                    return;
                case 7:
                    melodySelect.mo2784a(melodySelect.sqlitedatabase.query("jp_data", null, "isfavo=0", null, null, null, melodySelect.sortStr));
                    return;
                default:
            }
        } catch (Exception ignored) {
        }
    }
}
