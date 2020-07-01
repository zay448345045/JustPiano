package ly.pp.justpiano3;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public final class SoundDownloadCntPre extends Thread {
    private final SoundDownload soundDownload;
    private String fileName;

    SoundDownloadCntPre(SoundDownload soundDownload, String str) {
        this.soundDownload = soundDownload;
        fileName = str;
    }

    @Override
    public final void run() {
        try {
            HttpPost httpPost = new HttpPost("http://111.67.204.158:8910/JustPianoServer/server/DownloadSound");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", soundDownload.jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("path", fileName));
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            defaultHttpClient.getParams().setParameter("http.connection.timeout", 10000);
            defaultHttpClient.getParams().setParameter("http.socket.timeout", 10000);
            defaultHttpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
