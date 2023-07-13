package ly.pp.justpiano3;

import android.os.AsyncTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

final class FriendMailPageTask2 extends AsyncTask<String, Void, String> {
    private final WeakReference<FriendMailPage> friendMailPage;

    FriendMailPageTask2(FriendMailPage friendMailPage) {
        this.friendMailPage = new WeakReference<>(friendMailPage);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        if (strArr[0].isEmpty()) {
            return str;
        }
        // 创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("head", "2")
                .add("version", friendMailPage.get().jpApplication.getVersion())
                .add("keywords", strArr[0])
                .add("userName", "")
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + friendMailPage.get().jpApplication.getServer() + ":8910/JustPianoServer/server/" + strArr[1])
                .post(formBody)
                .build();
        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : str;
        } catch (IOException e) {
            e.printStackTrace();
            return str;
        }
    }


    @Override
    protected final void onPostExecute(String str) {
        friendMailPage.get().f4023e.cancel();
    }

    @Override
    protected final void onPreExecute() {
        friendMailPage.get().f4023e.setCancelable(true);
        friendMailPage.get().f4023e.setOnCancelListener(dialog -> cancel(true));
        friendMailPage.get().f4023e.show();
    }
}
