package ly.pp.justpiano3;

import android.content.Intent;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class SearchSongsPlayTask {
    private final WeakReference<SearchSongs> searchSongs;
    private byte[] songBytes = null;
    private String str = "";
    private ExecutorService executorService;
    private Future<Void> future;

    SearchSongsPlayTask(SearchSongs searchSongs) {
        this.searchSongs = new WeakReference<>(searchSongs);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void execute() {
        future = executorService.submit(() -> {
            if (!searchSongs.get().songID.isEmpty()) {
                String url = "http://" + searchSongs.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong";

                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("version", searchSongs.get().jpapplication.getVersion());
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
                songBytes = GZIP.ZIPToArray(str);
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
                Toast.makeText(searchSongs.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
                return;
            }
            OLMelodySelect.songBytes = songBytes;
            Intent intent = new Intent();
            intent.putExtra("head", 1);
            intent.putExtra("songBytes", songBytes);
            intent.putExtra("songName", searchSongs.get().f4949d);
            intent.putExtra("songID", searchSongs.get().songID);
            intent.putExtra("topScore", searchSongs.get().f4954i);
            intent.putExtra("degree", searchSongs.get().f4953h);
            intent.setClass(searchSongs.get(), PianoPlay.class);
            searchSongs.get().startActivity(intent);
            searchSongs.get().jpprogressBar.cancel();
        });
    }
}
