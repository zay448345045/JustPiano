package ly.pp.justpiano3;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class SearchSongsPlayTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<SearchSongs> searchSongs;
    private byte[] songBytes = null;
    private String str = "";

    SearchSongsPlayTask(SearchSongs searchSongs) {
        this.searchSongs = new WeakReference<>(searchSongs);
    }

    @Override
    protected Void doInBackground(Void... v) {
        if (!searchSongs.get().songID.isEmpty()) {
            do {
                HttpResponse execute;
                HttpPost httpPost = new HttpPost("http://" + searchSongs.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong");
                List<BasicNameValuePair> arrayList = new ArrayList<>();
                arrayList.add(new BasicNameValuePair("version", searchSongs.get().jpapplication.getVersion()));
                arrayList.add(new BasicNameValuePair("songID", searchSongs.get().songID));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    execute = new DefaultHttpClient().execute(httpPost);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    execute = null;
                }
                if (execute.getStatusLine().getStatusCode() == 200) {
                    try {
                        str = EntityUtils.toString(execute.getEntity());
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
            } while (!str.endsWith("\"}"));
            songBytes = GZIP.ZIPToArray(str.substring(0, str.length() - 2));
        }
        return null;
    }

    @Override
    protected final void onCancelled() {
    }

    @Override
    protected final void onPostExecute(Void v) {
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
    }

    @Override
    protected final void onPreExecute() {
        searchSongs.get().jpprogressBar.setMessage("正在载入曲谱,请稍后...");
        searchSongs.get().jpprogressBar.setCancelable(true);
        searchSongs.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        searchSongs.get().jpprogressBar.show();
    }
}
