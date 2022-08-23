package ly.pp.justpiano3;

import android.app.Activity;

import io.netty.util.internal.StringUtil;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public final class AcceptFavorThread extends Thread {
    private final String type;
    private final String songID;
    private final String userName;
    private final JPApplication jpapplication;

    AcceptFavorThread(Activity activity, String l, String str, String str2) {
        jpapplication = (JPApplication) activity.getApplication();
        songID = l;
        userName = str2;
        type = str;
    }

    @Override
    public void run() {
        if (!StringUtil.isNullOrEmpty(userName)) {
            HttpPost httpPost = new HttpPost("http://" + jpapplication.getServer() + ":8910/JustPianoServer/server/AcceptFavorIn");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("type", type));
            arrayList.add(new BasicNameValuePair("songID", songID));
            arrayList.add(new BasicNameValuePair("user", userName));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                new DefaultHttpClient().execute(httpPost);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
