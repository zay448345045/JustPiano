package ly.pp.justpiano3;

import android.os.AsyncTask;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.ParseException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

final class FriendMailPageTask2 extends AsyncTask<String, Void, String> {
    private final WeakReference<FriendMailPage> friendMailPage;

    FriendMailPageTask2(FriendMailPage friendMailPage) {
        this.friendMailPage = new WeakReference<>(friendMailPage);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        if (strArr[0].isEmpty()) {
            return str;
        }
        HttpPost httpPost = new HttpPost("http://" + friendMailPage.get().jpApplication.getServer() + ":8910/JustPianoServer/server/" + strArr[1]);
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("head", "2"));
        arrayList.add(new BasicNameValuePair("version", friendMailPage.get().jpApplication.getVersion()));
        arrayList.add(new BasicNameValuePair("keywords", strArr[0]));
        arrayList.add(new BasicNameValuePair("userName", ""));
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
            return execute.getStatusLine().getStatusCode() == 200 ? EntityUtils.toString(execute.getEntity()) : str;
        } catch (ParseException | IOException e2) {
            e2.printStackTrace();
            return str;
        }
    }

    @Override
    protected final void onPostExecute(String str) {
        friendMailPage.get().f4023e.cancel();
    }

    @Override
    protected final void onPreExecute() {
        friendMailPage.get().f4023e.setCancelable(true);
        friendMailPage.get().f4023e.setOnCancelListener(dialog -> cancel(true));
        friendMailPage.get().f4023e.show();
    }
}
