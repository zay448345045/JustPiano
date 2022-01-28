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

public final class OLMelodySongsPlayTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMelodySelect> olMelodySelect;
    private String str = "";

    OLMelodySongsPlayTask(OLMelodySelect oLMelodySelect) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
    }

    @Override
    protected final void onPostExecute(String str) {
        if (OLMelodySelect.songBytes == null || OLMelodySelect.songBytes.length <= 3) {
            olMelodySelect.get().jpprogressBar.cancel();
            Toast.makeText(olMelodySelect.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("head", 1);
        intent.putExtra("songBytes", OLMelodySelect.songBytes);
        intent.putExtra("songName", olMelodySelect.get().songName);
        intent.putExtra("songID", OLMelodySelect.songID);
        intent.putExtra("topScore", olMelodySelect.get().topScore);
        intent.putExtra("degree", olMelodySelect.get().degree);
        intent.setClass(olMelodySelect.get(), PianoPlay.class);
        olMelodySelect.get().startActivity(intent);
        olMelodySelect.get().jpprogressBar.cancel();
    }

    @Override
    protected String doInBackground(String... objects) {
        if (!OLMelodySelect.songID.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + olMelodySelect.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", olMelodySelect.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("songID", OLMelodySelect.songID));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse execute = new DefaultHttpClient().execute(httpPost);
                if (execute.getStatusLine().getStatusCode() == 200) {
                    str = EntityUtils.toString(execute.getEntity());
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            OLMelodySelect.songBytes = GZIP.ZIPToArray(str);
        }
        return null;
    }

    @Override
    protected final void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setMessage("正在载入曲谱,请稍后...");
        olMelodySelect.get().jpprogressBar.setCancelable(true);
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
