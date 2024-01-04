package ly.pp.justpiano3.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.SearchSongs;
import ly.pp.justpiano3.adapter.SearchPeopleAdapter;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class SearchSongsTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<SearchSongs> searchSongs;

    public SearchSongsTask(SearchSongs searchSongs) {
        this.searchSongs = new WeakReference<>(searchSongs);
    }

    @Override
    protected String doInBackground(Void... params) {
        if (!searchSongs.get().keywords.isEmpty()) {
            String function;
            String head;
            if (searchSongs.get().headType == 6) {
                function = "GetTopListByKeywords";
                head = "6";
            } else {
                function = "GetListByKeywords";
                head = "0";
            }
            return OkHttpUtil.sendPostRequest(function, new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("head", head)
                    .add("keywords", searchSongs.get().keywords)
                    .add("user", OLBaseActivity.getAccountName())
                    .build());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String str) {
        if (str.length() > 3) {
            if (searchSongs.get().headType < 2) {
                searchSongs.get().bindAdapter(str, searchSongs.get().songsListView);
                searchSongs.get().songsListView.setCacheColorHint(Color.TRANSPARENT);
            } else if (searchSongs.get().headType == 6) {
                try {
                    if (searchSongs.get().songsListView != null) {
                        searchSongs.get().songsListView.setAdapter(new SearchPeopleAdapter(
                                searchSongs.get(), searchSongs.get().userListHandle(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")))));
                        searchSongs.get().songsListView.setCacheColorHint(Color.TRANSPARENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            searchSongs.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            searchSongs.get().jpprogressBar.cancel();
            Toast.makeText(searchSongs.get(), "没有找到与[" + searchSongs.get().keywords + "]相关的信息", Toast.LENGTH_SHORT).show();
        } else {
            searchSongs.get().jpprogressBar.cancel();
            Toast.makeText(searchSongs.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        searchSongs.get().jpprogressBar.setCancelable(true);
        searchSongs.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        searchSongs.get().jpprogressBar.show();
    }
}
