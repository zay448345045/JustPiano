package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;

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

public final class SearchSongsTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<SearchSongs> searchSongs;

    SearchSongsTask(SearchSongs searchSongs) {
        this.searchSongs = new WeakReference<>(searchSongs);
    }

    @Override
    protected String doInBackground(Void... params) {
        String s = "";
        if (!searchSongs.get().f4948c.isEmpty()) {
            do {
                String str;
                String str2;
                if (searchSongs.get().f4958m == 6) {
                    str = "GetTopListByKeywords";
                    str2 = "6";
                } else {
                    str = "GetListByKeywords";
                    str2 = "0";
                }
                HttpPost httpPost = new HttpPost("http://" + searchSongs.get().jpapplication.getServer() + ":8910/JustPianoServer/server/" + str);
                List<BasicNameValuePair> arrayList = new ArrayList<>();
                arrayList.add(new BasicNameValuePair("version", searchSongs.get().jpapplication.getVersion()));
                arrayList.add(new BasicNameValuePair("head", str2));
                arrayList.add(new BasicNameValuePair("keywords", searchSongs.get().f4948c));
                arrayList.add(new BasicNameValuePair("user", searchSongs.get().jpapplication.getAccountName()));
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
                        s = EntityUtils.toString(execute.getEntity());
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            } while (!s.endsWith("\"}"));
        }
        return s;
    }

    @Override
    protected final void onCancelled() {
    }

    @Override
    protected final void onPostExecute(String str) {
        if (str.length() > 3) {
            if (searchSongs.get().f4958m < 2) {
                searchSongs.get().mo2963a(str, searchSongs.get().songsListView);
                searchSongs.get().songsListView.setCacheColorHint(0x00000000);
            } else if (searchSongs.get().f4958m == 6) {
                try {
                    if (searchSongs.get().songsListView != null) {
                        searchSongs.get().songsListView.setAdapter(new SearchPeopleAdapter(searchSongs.get(), searchSongs.get().m3841b(GZIP.ZIPTo(new JSONObject(str).getString("L")))));
                        searchSongs.get().songsListView.setCacheColorHint(0x00000000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            searchSongs.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            searchSongs.get().jpprogressBar.cancel();
            Toast.makeText(searchSongs.get(), "没有找到与[" + searchSongs.get().f4948c + "]相关的信息", Toast.LENGTH_SHORT).show();
        } else {
            searchSongs.get().jpprogressBar.cancel();
            Toast.makeText(searchSongs.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected final void onPreExecute() {
        searchSongs.get().jpprogressBar.setMessage("正在搜索曲库,请稍后...");
        searchSongs.get().jpprogressBar.setCancelable(true);
        searchSongs.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        searchSongs.get().jpprogressBar.show();
    }

    @Override
    protected final void onProgressUpdate(Void... params) {
    }
}
