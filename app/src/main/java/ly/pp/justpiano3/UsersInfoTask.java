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

public final class UsersInfoTask extends AsyncTask<String, Void, String> {
    private final WeakReference<UsersInfo> userInfo;

    UsersInfoTask(UsersInfo usersInfo) {
        userInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!userInfo.get().jpapplication.getAccountName().isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + userInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetUserInfo");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("head", "0"));
            arrayList.add(new BasicNameValuePair("version", userInfo.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("keywords", "0"));
            arrayList.add(new BasicNameValuePair("userName", userInfo.get().jpapplication.getAccountName()));
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
        if (str.length() > 3) {
            UsersInfo.m3930a(userInfo.get(), str);
            userInfo.get().jpprogressBar.cancel();
        } else if (str.equals("{}")) {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected final void onPreExecute() {
        userInfo.get().jpprogressBar.setMessage("正在查询,请稍后...");
        userInfo.get().jpprogressBar.setCancelable(true);
        userInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        userInfo.get().jpprogressBar.show();
    }
}
