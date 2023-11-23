package ly.pp.justpiano3.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.ShowSongsInfo;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public final class ShowSongsInfoTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;

    public ShowSongsInfoTask(ShowSongsInfo showSongsInfo) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        String str = "";
        if (!showSongsInfo.get().keywords.isEmpty()) {
            // 创建一个FormBody对象，用于存放请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("head", showSongsInfo.get().head)
                    .add("keywords", showSongsInfo.get().keywords)
                    .add("user", showSongsInfo.get().jpapplication.getAccountName())
                    .add("page", String.valueOf(showSongsInfo.get().page))
                    .build();
            // 创建一个Request对象，设置请求URL和请求体
            Request request = new Request.Builder()
                    .url("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/GetListByKeywords")
                    .post(formBody)
                    .build();
            try {
                // 发送请求并获取响应
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) {
                    str = response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
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
