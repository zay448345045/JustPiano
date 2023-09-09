package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.PopUserInfo;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.ref.WeakReference;

public final class PopUserInfoTask extends AsyncTask<String, Void, String> {
    private final WeakReference<PopUserInfo> popUserInfo;

    public PopUserInfoTask(PopUserInfo popUserInfo) {
        this.popUserInfo = new WeakReference<>(popUserInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!popUserInfo.get().kitiName.isEmpty()) {
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + popUserInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/" + popUserInfo.get().f4839m).newBuilder();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("head", String.valueOf(popUserInfo.get().headType));
            formBuilder.add("version", BuildConfig.VERSION_NAME);
            formBuilder.add("keywords", popUserInfo.get().f4830d);
            formBuilder.add("userName", popUserInfo.get().kitiName);
            // 创建Request对象，用于发送请求
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .post(formBuilder.build())
                    .build();
            try {
                // 同步执行请求，获取Response对象
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
        if (popUserInfo.get().headType != 1) {
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
            Toast.makeText(popUserInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        popUserInfo.get().jpprogressBar.setMessage("正在查询,请稍后...");
        popUserInfo.get().jpprogressBar.setCancelable(true);
        popUserInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        popUserInfo.get().jpprogressBar.show();
    }
}
