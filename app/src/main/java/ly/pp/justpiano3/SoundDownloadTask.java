package ly.pp.justpiano3;

import android.os.AsyncTask;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public final class SoundDownloadTask extends AsyncTask<String, Void, String> {
    private final WeakReference<SoundDownload> soundDownload;

    SoundDownloadTask(SoundDownload soundDownload) {
        this.soundDownload = new WeakReference<>(soundDownload);
    }

    @Override
    protected String doInBackground(String... strArr) {
        try {
            soundDownload.get().getLocalSoundList();
            HttpPost httpPost = new HttpPost("http://" + soundDownload.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetSoundList");
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            defaultHttpClient.getParams().setParameter("http.connection.timeout", 10000);
            defaultHttpClient.getParams().setParameter("http.socket.timeout", 10000);
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() != 200) {
                return "err001";
            }
            return EntityUtils.toString(execute.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return "err001";
        }
    }

    @Override
    protected final void onPostExecute(String str) {
        soundDownload.get().gridView.setAdapter(new SoundDownloadAdapter(soundDownload.get(), new JSONArray()));
        try {
            soundDownload.get().gridView.setAdapter(new SoundDownloadAdapter(soundDownload.get(), new JSONArray(GZIP.ZIPTo(new JSONObject(str).getString("L")))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        soundDownload.get().jpProgressBar.dismiss();
    }

    @Override
    protected final void onPreExecute() {
        soundDownload.get().jpProgressBar.setCancelable(true);
        soundDownload.get().jpProgressBar.setOnCancelListener(dialog -> cancel(true));
        soundDownload.get().jpProgressBar.show();
    }
}
