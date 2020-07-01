package ly.pp.justpiano3;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class OLMainModeTask extends AsyncTask<String, String, String> {
    private final WeakReference<OLMainMode> olMainMode;

    OLMainModeTask(OLMainMode oLMainMode) {
        olMainMode = new WeakReference<>(oLMainMode);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String string = "";
        if (!olMainMode.get().f4293s.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + olMainMode.get().jpapplication.getServer() + ":8910/JustPianoServer/server/UserKuang");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", strArr[0]));
            arrayList.add(new BasicNameValuePair("type", strArr[1]));
            arrayList.add(new BasicNameValuePair("userName", strArr[2]));
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
                    string = EntityUtils.toString(execute.getEntity());
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        return string;
    }

    @Override
    protected final void onCancelled() {
    }

    @Override
    protected final void onPostExecute(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("T");
            String string2 = jSONObject.getString("M");
            jSONObject.getInt("S");
            olMainMode.get().mo2803a(string, string2, jSONObject.getInt("I"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        olMainMode.get().jpprogressBar.dismiss();
    }

    @Override
    protected final void onPreExecute() {
        olMainMode.get().jpprogressBar.setCancelable(true);
        olMainMode.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMainMode.get().jpprogressBar.show();
    }
}
