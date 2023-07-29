package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.ref.WeakReference;

public final class ShowSongsInfoTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;

    ShowSongsInfoTask(ShowSongsInfo showSongsInfo) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        String str = "";
        if (!showSongsInfo.get().keywords.isEmpty()) {
            // 创建一个FormBody对象，用于存放请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("version", showSongsInfo.get().jpapplication.getVersion())
                    .add("head", showSongsInfo.get().head)
                    .add("keywords", showSongsInfo.get().keywords)
                    .add("user", showSongsInfo.get().jpapplication.getAccountName())
                    .add("page", String.valueOf(showSongsInfo.get().page))
                    .build();
            // 创建一个Request对象，设置请求URL和请求体
            Request request = new Request.Builder()
                    .url("http://" + showSongsInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetListByKeywords")
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
