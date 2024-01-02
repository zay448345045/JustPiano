package ly.pp.justpiano3.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.ShowSongsInfo;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class ShowSongsInfoTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;

    public ShowSongsInfoTask(ShowSongsInfo showSongsInfo) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        if (!showSongsInfo.get().keywords.isEmpty()) {
            return OkHttpUtil.sendPostRequest("GetListByKeywords", new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("head", showSongsInfo.get().head)
                    .add("keywords", showSongsInfo.get().keywords)
                    .add("user", OLBaseActivity.getAccountName())
                    .add("page", String.valueOf(showSongsInfo.get().page))
                    .build());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String str) {
        if (str.length() > 3) {
            showSongsInfo.get().handleResultAndBindAdapter(str, showSongsInfo.get().songsListView);
            showSongsInfo.get().songsListView.setCacheColorHint(Color.TRANSPARENT);
            showSongsInfo.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            showSongsInfo.get().jpprogressBar.cancel();
            Toast.makeText(showSongsInfo.get(), "获取列表出错!", Toast.LENGTH_SHORT).show();
        } else {
            showSongsInfo.get().jpprogressBar.cancel();
            Toast.makeText(showSongsInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        showSongsInfo.get().jpprogressBar.setCancelable(true);
        showSongsInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showSongsInfo.get().jpprogressBar.show();
    }
}
