package ly.pp.justpiano3;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public final class SkinDownloadTask extends AsyncTask<String, Void, String> {
    private final WeakReference<SkinDownload> skinDownload;

    SkinDownloadTask(SkinDownload skinDownload) {
        this.skinDownload = new WeakReference<>(skinDownload);
    }

    @Override
    protected String doInBackground(String... strArr) {
        try {
            skinDownload.get().getLocalSkinList();
            HttpPost httpPost = new HttpPost("http://111.67.204.158:8910/JustPianoServer/server/GetSkinList");
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
        skinDownload.get().gridView.setAdapter(new SkinDownloadAdapter(skinDownload.get(), new JSONArray()));
        try {
            skinDownload.get().gridView.setAdapter(new SkinDownloadAdapter(skinDownload.get(), new JSONArray(GZIP.ZIPTo(new JSONObject(str).getString("L")))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        skinDownload.get().jpProgressBar.dismiss();
    }

    @Override
    protected final void onPreExecute() {
        skinDownload.get().jpProgressBar.setCancelable(true);
        skinDownload.get().jpProgressBar.setOnCancelListener(dialog -> cancel(true));
        skinDownload.get().jpProgressBar.show();
    }
}
