package ly.pp.justpiano3;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

final class OLPlayHallRoomTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLPlayHallRoom> olPlayHallRoom;

    OLPlayHallRoomTask(OLPlayHallRoom olPlayHallRoom) {
        this.olPlayHallRoom = new WeakReference<>(olPlayHallRoom);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        HttpPost httpPost = new HttpPost("http://" + olPlayHallRoom.get().jpApplication.getServer() + ":8910/JustPianoServer/server/GetUserInfo");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("head", "2"));
        arrayList.add(new BasicNameValuePair("version", olPlayHallRoom.get().jpApplication.getVersion()));
        arrayList.add(new BasicNameValuePair("keywords", strArr[0]));
        arrayList.add(new BasicNameValuePair("userName", strArr[1]));
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
        } catch (ClientProtocolException ignored) {
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return str;
    }

    @Override
    protected final void onPostExecute(String str) {
        olPlayHallRoom.get().jpprogressBar.cancel();
    }

    @Override
    protected final void onPreExecute() {
        olPlayHallRoom.get().jpprogressBar.setCancelable(true);
        olPlayHallRoom.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olPlayHallRoom.get().jpprogressBar.show();
    }
}
