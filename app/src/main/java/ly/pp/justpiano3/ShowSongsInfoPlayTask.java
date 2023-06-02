package ly.pp.justpiano3;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;


import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ShowSongsInfoPlayTask extends AsyncTask<String, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;
    private byte[] songBytes = null;
    private String str = "";

    ShowSongsInfoPlayTask(ShowSongsInfo showSongsInfo) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        if (!showSongsInfo.get().songID.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + showSongsInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", showSongsInfo.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("songID", showSongsInfo.get().songID));
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
            songBytes = GZIP.ZIPToArray(str);
        }
        return null;
    }

    @Override
    protected final void onPostExecute(String str) {
        if (songBytes == null || songBytes.length <= 3) {
            showSongsInfo.get().jpprogressBar.dismiss();
            Toast.makeText(showSongsInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
            return;
        }
        OLMelodySelect.songBytes = songBytes;
        Intent intent = new Intent();
        intent.putExtra("head", 1);
        intent.putExtra("songBytes", songBytes);
        intent.putExtra("songName", showSongsInfo.get().songName);
        intent.putExtra("songID", showSongsInfo.get().songID);
        intent.putExtra("topScore", showSongsInfo.get().score);
        intent.putExtra("degree", showSongsInfo.get().nandu);
        intent.setClass(showSongsInfo.get(), PianoPlay.class);
        showSongsInfo.get().startActivity(intent);
        showSongsInfo.get().jpprogressBar.dismiss();
    }

    @Override
    protected final void onPreExecute() {
        showSongsInfo.get().jpprogressBar.setMessage("正在载入曲谱,请稍后...");
        showSongsInfo.get().jpprogressBar.setCancelable(true);
        showSongsInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showSongsInfo.get().jpprogressBar.show();
    }
}
