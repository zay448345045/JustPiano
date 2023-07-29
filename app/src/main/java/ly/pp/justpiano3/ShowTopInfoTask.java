package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

public final class ShowTopInfoTask extends AsyncTask<String, Void, String> {
    private final WeakReference<ShowTopInfo> showTopInfo;

    ShowTopInfoTask(ShowTopInfo showTopInfo) {
        this.showTopInfo = new WeakReference<>(showTopInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!showTopInfo.get().f4988d.isEmpty()) {
            // 创建请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("head", String.valueOf(showTopInfo.get().head))
                    .add("version", showTopInfo.get().jpapplication.getVersion())
                    .add("keywords", showTopInfo.get().f4988d)
                    .add("page", String.valueOf(showTopInfo.get().f4996l))
                    .add("P", showTopInfo.get().f4999o)
                    .add("K", JPApplication.kitiName)
                    .add("N", showTopInfo.get().jpapplication.getAccountName())
                    .build();
            // 创建请求对象
            Request request = new Request.Builder()
                    .url("http://" + showTopInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetTopListByKeywords")
                    .post(formBody) // 设置请求方式为POST
                    .build();
            try {
                // 发送请求并获取响应
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) { // 判断响应是否成功
                    try {
                        str = response.body().string(); // 获取响应内容
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    @Override
    protected void onPostExecute(String str) {
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
    protected void onPreExecute() {
        showTopInfo.get().jpprogressBar.setMessage("正在查询,请稍后...");
        showTopInfo.get().jpprogressBar.setCancelable(true);
        showTopInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showTopInfo.get().jpprogressBar.show();
    }
}
