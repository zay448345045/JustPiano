package ly.pp.justpiano3;

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

public final class PlayFinishTask extends AsyncTask<String, Void, String> {
    private final WeakReference<PlayFinish> playFinish;

    PlayFinishTask(PlayFinish playFinish) {
        this.playFinish = new WeakReference<>(playFinish);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!playFinish.get().jpapplication.getAccountName().isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + playFinish.get().jpapplication.getServer() + ":8910/JustPianoServer/server/ScoreUpload");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", playFinish.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("songID", playFinish.get().songID));
            arrayList.add(new BasicNameValuePair("userName", playFinish.get().jpapplication.getAccountName()));
            arrayList.add(new BasicNameValuePair("scoreArray", playFinish.get().scoreArray));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            defaultHttpClient.getParams().setParameter("http.connection.timeout", 10000);
            defaultHttpClient.getParams().setParameter("http.socket.timeout", 10000);
            try {
                HttpResponse execute = defaultHttpClient.execute(httpPost);
                if (execute.getStatusLine().getStatusCode() == 200) {
                    str = EntityUtils.toString(execute.getEntity());
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        return str;
    }

    @Override
    protected final void onPostExecute(String str) {
        switch (str) {
            case "0":
                playFinish.get().jpprogressBar.dismiss();
                break;
            case "1":
                playFinish.get().jpprogressBar.dismiss();
                Toast.makeText(playFinish.get(), "连接出错!", Toast.LENGTH_SHORT).show();
                break;
            case "2":
                playFinish.get().jpprogressBar.dismiss();
                Toast.makeText(playFinish.get(), "该版本无法上传成绩，请更新版本!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected final void onPreExecute() {
        playFinish.get().jpprogressBar.setMessage("正在上传您的成绩,请稍后...");
        playFinish.get().jpprogressBar.setCancelable(true);
        playFinish.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        playFinish.get().jpprogressBar.show();
    }
}
