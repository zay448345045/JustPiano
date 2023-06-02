package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.Toast;


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ShowTopInfoTask extends AsyncTask<String, Void, String> {
    private final WeakReference<ShowTopInfo> showTopInfo;

    ShowTopInfoTask(ShowTopInfo showTopInfo) {
        this.showTopInfo = new WeakReference<>(showTopInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!showTopInfo.get().f4988d.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + showTopInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetTopListByKeywords");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("head", String.valueOf(showTopInfo.get().head)));
            arrayList.add(new BasicNameValuePair("version", showTopInfo.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("keywords", showTopInfo.get().f4988d));
            arrayList.add(new BasicNameValuePair("page", String.valueOf(showTopInfo.get().f4996l)));
            arrayList.add(new BasicNameValuePair("P", showTopInfo.get().f4999o));
            arrayList.add(new BasicNameValuePair("K", JPApplication.kitiName));
            arrayList.add(new BasicNameValuePair("N", showTopInfo.get().jpapplication.getAccountName()));
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
            try {
                showTopInfo.get().f4985a = showTopInfo.get().m3877a(GZIP.ZIPTo(new JSONObject(str).getString("L")));
                ListAdapter topUserAdapter = new TopUserAdapter(showTopInfo.get(), showTopInfo.get().f4987c, showTopInfo.get().f4985a);
                if (showTopInfo.get().f4989e != null) {
                    showTopInfo.get().f4989e.setAdapter(topUserAdapter);
                }
                showTopInfo.get().f4989e.setCacheColorHint(0x0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            showTopInfo.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            showTopInfo.get().jpprogressBar.cancel();
            Toast.makeText(showTopInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            showTopInfo.get().jpprogressBar.cancel();
            Toast.makeText(showTopInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected final void onPreExecute() {
        showTopInfo.get().jpprogressBar.setMessage("正在查询,请稍后...");
        showTopInfo.get().jpprogressBar.setCancelable(true);
        showTopInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showTopInfo.get().jpprogressBar.show();
    }
}
