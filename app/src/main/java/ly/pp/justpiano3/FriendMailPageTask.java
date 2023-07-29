package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.ListView;
import okhttp3.*;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public final class FriendMailPageTask extends AsyncTask<String, Void, String> {
    private final WeakReference<FriendMailPage> friendMailPage;

    FriendMailPageTask(FriendMailPage friendMailPage) {
        this.friendMailPage = new WeakReference<>(friendMailPage);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        if (friendMailPage.get().f4024f.isEmpty()) {
            return str;
        }
        // 创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 创建HttpUrl.Builder对象，用于添加查询参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + friendMailPage.get().jpApplication.getServer() + ":8910/JustPianoServer/server/" + strArr[1]).newBuilder();
        // 创建FormBody.Builder对象，用于添加查询参数
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("version", friendMailPage.get().jpApplication.getVersion());
        formBuilder.add("T", strArr[0]);
        formBuilder.add("U", friendMailPage.get().jpApplication.getAccountName());
        // 创建Request对象，用于发送请求
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(formBuilder.build())
                .build();
        try {
            // 同步执行请求，获取Response对象
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String entityUtils = response.body().string();
                str = GZIP.ZIPTo(new JSONObject(entityUtils).getString("L"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected final void onPostExecute(String str) {
        ListView b = friendMailPage.get().f4019a;
        FriendMailPage.m3506a(friendMailPage.get(), b, str);
        friendMailPage.get().f4023e.cancel();
    }

    @Override
    protected void onPreExecute() {
        friendMailPage.get().f4023e.setOnCancelListener(dialog -> cancel(true));
        friendMailPage.get().f4023e.show();
    }
}
