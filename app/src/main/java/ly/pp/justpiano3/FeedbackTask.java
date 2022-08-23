package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;

import io.netty.util.internal.StringUtil;
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

public final class FeedbackTask extends AsyncTask<String, Void, String> {
    private final WeakReference<MainMode> mainMode;
    private final String userName;
    private final String message;

    FeedbackTask(MainMode mainMode, String userName, String message) {
        this.mainMode = new WeakReference<>(mainMode);
        this.userName = userName;
        this.message = message;
    }

    @Override
    protected String doInBackground(String... objects) {
        HttpPost httpPost = new HttpPost("http://" + mainMode.get().jpApplication.getServer() + ":8910/JustPianoServer/server/Feedback");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("version", mainMode.get().jpApplication.getVersion()));
        arrayList.add(new BasicNameValuePair("userName", userName));
        arrayList.add(new BasicNameValuePair("message", message));
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
                return EntityUtils.toString(execute.getEntity());
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    @Override
    protected final void onPostExecute(String str) {
        Toast.makeText(mainMode.get(), StringUtil.isNullOrEmpty(str) ? "反馈提交出错" : "反馈提交成功", Toast.LENGTH_SHORT).show();
    }
}
