package ly.pp.justpiano3.task;

import android.content.Intent;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.activity.online.SearchSongs;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import okhttp3.FormBody;

public final class SearchSongsPlayTask {
    private final WeakReference<SearchSongs> searchSongs;
    private byte[] songBytes;
    private final Intent intent;

    public SearchSongsPlayTask(SearchSongs searchSongs, Intent intent) {
        this.searchSongs = new WeakReference<>(searchSongs);
        this.intent = intent;
    }

    public void execute() {
        ThreadPoolUtil.execute(() -> {
            if (!searchSongs.get().songId.isEmpty()) {
                songBytes = GZIPUtil.ZIPToArray(OkHttpUtil.sendPostRequest("DownloadSong", new FormBody.Builder()
                        .add("version", BuildConfig.VERSION_NAME)
                        .add("songID", searchSongs.get().songId)
                        .build()));
            }
            searchSongs.get().runOnUiThread(() -> {
                if (songBytes == null || songBytes.length <= 3) {
                    searchSongs.get().jpprogressBar.cancel();
                    Toast.makeText(searchSongs.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                OLMelodySelect.songBytes = songBytes;
                intent.putExtra("head", 1);
                intent.putExtra("songBytes", songBytes);
                intent.putExtra("songName", searchSongs.get().songName);
                intent.putExtra("songID", searchSongs.get().songId);
                intent.putExtra("topScore", searchSongs.get().topScore);
                intent.putExtra("degree", searchSongs.get().degree);
                searchSongs.get().startActivity(intent);
                searchSongs.get().jpprogressBar.cancel();
            });
        });
    }
}
