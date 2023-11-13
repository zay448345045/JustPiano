package ly.pp.justpiano3.task;

import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.OLMelodySelect;
import ly.pp.justpiano3.activity.SearchSongs;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class SearchSongsPlayTask {
    private final WeakReference<SearchSongs> searchSongs;
    private byte[] songBytes = null;
    private String str = "";
    private final ExecutorService executorService;
    private Future<Void> future;
    private final Intent intent;

    public SearchSongsPlayTask(SearchSongs searchSongs, Intent intent) {
        this.searchSongs = new WeakReference<>(searchSongs);
        executorService = Executors.newSingleThreadExecutor();
        this.intent = intent;
    }

    public void execute() {
        future = executorService.submit(() -> {
            if (!searchSongs.get().songID.isEmpty()) {
                String url = "http://" + OnlineUtil.server + ":8910/JustPianoServer/server/DownloadSong";

                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("version", BuildConfig.VERSION_NAME);
                formBuilder.add("songID", searchSongs.get().songID);
                RequestBody requestBody = formBuilder.build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try (Response response = OkHttpUtil.client().newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        str = response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                songBytes = GZIPUtil.ZIPToArray(str);
            }
            // Handle the result in onPostExecute
            handleResult();
            return null;
        });
    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }

    private void handleResult() {
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
            intent.putExtra("songID", searchSongs.get().songID);
            intent.putExtra("topScore", searchSongs.get().topScore);
            intent.putExtra("degree", searchSongs.get().degree);
            searchSongs.get().startActivity(intent);
            searchSongs.get().jpprogressBar.cancel();
        });
    }
}
