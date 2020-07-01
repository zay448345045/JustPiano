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

final class PopUserInfoTask extends AsyncTask<String, Void, String> {
    private final WeakReference<PopUserInfo> popUserInfo;

    PopUserInfoTask(PopUserInfo popUserInfo) {
        this.popUserInfo = new WeakReference<>(popUserInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!popUserInfo.get().f4829c.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + popUserInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/" + popUserInfo.get().f4839m);
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("head", String.valueOf(popUserInfo.get().f4828b)));
            arrayList.add(new BasicNameValuePair("version", popUserInfo.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("keywords", popUserInfo.get().f4830d));
            arrayList.add(new BasicNameValuePair("userName", popUserInfo.get().f4829c));
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
        if (popUserInfo.get().f4828b != 1) {
            popUserInfo.get().jpprogressBar.cancel();
            Toast.makeText(popUserInfo.get(), "发送成功!", Toast.LENGTH_SHORT).show();
        } else if (str.length() > 3) {
            PopUserInfo.m3823a(popUserInfo.get(), str);
            popUserInfo.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            popUserInfo.get().jpprogressBar.cancel();
            Toast.makeText(popUserInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            popUserInfo.get().jpprogressBar.cancel();
            Toast.makeText(popUserInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected final void onPreExecute() {
        popUserInfo.get().jpprogressBar.setMessage("正在查询,请稍后...");
        popUserInfo.get().jpprogressBar.setCancelable(true);
        popUserInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        popUserInfo.get().jpprogressBar.show();
    }
}
