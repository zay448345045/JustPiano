package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public final class SearchSongsTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<SearchSongs> searchSongs;

    SearchSongsTask(SearchSongs searchSongs) {
        this.searchSongs = new WeakReference<>(searchSongs);
    }

    @Override
    protected String doInBackground(Void... params) {
        String s = "";
        if (!searchSongs.get().f4948c.isEmpty()) {
            String str;
            String str2;
            if (searchSongs.get().headType == 6) {
                str = "GetTopListByKeywords";
                str2 = "6";
            } else {
                str = "GetListByKeywords";
                str2 = "0";
            }
            // 创建FormBody对象，添加请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("version", searchSongs.get().jpapplication.getVersion())
                    .add("head", str2)
                    .add("keywords", searchSongs.get().f4948c)
                    .add("user", searchSongs.get().jpapplication.getAccountName())
                    .build();
            // 创建Request对象，设置URL和请求体
            Request request = new Request.Builder()
                    .url("http://" + searchSongs.get().jpapplication.getServer() + ":8910/JustPianoServer/server/" + str)
                    .post(formBody) // 注意这里是POST方法
                    .build();
            try {
                // 发送请求并获取响应
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) {
                    s = response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    @Override
    protected final void onCancelled() {
    }

    @Override
    protected final void onPostExecute(String str) {
        if (str.length() > 3) {
            if (searchSongs.get().headType < 2) {
                searchSongs.get().mo2963a(str, searchSongs.get().songsListView);
                searchSongs.get().songsListView.setCacheColorHint(0x00000000);
            } else if (searchSongs.get().headType == 6) {
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
    protected void onPreExecute() {
        searchSongs.get().jpprogressBar.setMessage("正在搜索曲库,请稍后...");
        searchSongs.get().jpprogressBar.setCancelable(true);
        searchSongs.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        searchSongs.get().jpprogressBar.show();
    }

    @Override
    protected final void onProgressUpdate(Void... params) {
    }
}
