package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class FriendMailPageTask extends AsyncTask<String, Void, String> {
    private final WeakReference<FriendMailPage> friendMailPage;

    FriendMailPageTask(FriendMailPage friendMailPage) {
        this.friendMailPage = new WeakReference<>(friendMailPage);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        if (friendMailPage.get().f4024f.isEmpty()) {
            return str;
        }
        HttpPost httpPost = new HttpPost("http://" + friendMailPage.get().jpApplication.getServer() + ":8910/JustPianoServer/server/" + strArr[1]);
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("version", friendMailPage.get().jpApplication.getVersion()));
        arrayList.add(new BasicNameValuePair("T", strArr[0]));
        arrayList.add(new BasicNameValuePair("U", friendMailPage.get().jpApplication.getAccountName()));
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
                String entityUtils = EntityUtils.toString(execute.getEntity());
                str = GZIP.ZIPTo(new JSONObject(entityUtils).getString("L"));
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return str;
    }

    @Override
    protected final void onPostExecute(String str) {
        ListView b = friendMailPage.get().f4019a;
        FriendMailPage.m3506a(friendMailPage.get(), b, str);
        friendMailPage.get().f4023e.cancel();
    }

    @Override
    protected final void onPreExecute() {
        friendMailPage.get().f4023e.setOnCancelListener(dialog -> cancel(true));
        friendMailPage.get().f4023e.show();
    }
}
