package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.UsersInfo;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.ref.WeakReference;

public final class UsersInfoGetTask extends AsyncTask<String, Void, String> {
    private final WeakReference<UsersInfo> userInfo;

    public UsersInfoGetTask(UsersInfo usersInfo) {
        userInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!userInfo.get().jpapplication.getAccountName().isEmpty()) {
            // 创建FormBody对象，添加请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("head", "0")
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("keywords", "0")
                    .add("userName", userInfo.get().jpapplication.getAccountName())
                    .build();
            // 创建Request对象，设置URL和请求体
            Request request = new Request.Builder()
                    .url("http://" + userInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetUserInfo")
                    .post(formBody) // 注意这里是POST方法
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
            UsersInfo.m3930a(userInfo.get(), str);
            userInfo.get().jpprogressBar.cancel();
        } else if (str.equals("{}")) {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        userInfo.get().jpprogressBar.setMessage("正在查询,请稍后...");
        userInfo.get().jpprogressBar.setCancelable(true);
        userInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        userInfo.get().jpprogressBar.show();
    }
}
