package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;


import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class ShowSongsInfoTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;

    ShowSongsInfoTask(ShowSongsInfo showSongsInfo) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        String str = "";
        if (!showSongsInfo.get().keywords.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + showSongsInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetListByKeywords");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", showSongsInfo.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("head", showSongsInfo.get().head));
            arrayList.add(new BasicNameValuePair("keywords", showSongsInfo.get().keywords));
            arrayList.add(new BasicNameValuePair("user", showSongsInfo.get().jpapplication.getAccountName()));
            arrayList.add(new BasicNameValuePair("page", String.valueOf(showSongsInfo.get().page)));
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
            showSongsInfo.get().mo2977a(str, showSongsInfo.get().songsListView);
            showSongsInfo.get().songsListView.setCacheColorHint(0);
            showSongsInfo.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            showSongsInfo.get().jpprogressBar.cancel();
            Toast.makeText(showSongsInfo.get(), "获取列表出错!", Toast.LENGTH_SHORT).show();
        } else {
            showSongsInfo.get().jpprogressBar.cancel();
            Toast.makeText(showSongsInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected final void onPreExecute() {
        showSongsInfo.get().jpprogressBar.setMessage("正在搜索曲库,请稍后...");
        showSongsInfo.get().jpprogressBar.setCancelable(true);
        showSongsInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showSongsInfo.get().jpprogressBar.show();
    }
}
