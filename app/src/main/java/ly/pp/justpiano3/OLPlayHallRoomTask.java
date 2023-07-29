package ly.pp.justpiano3;

import android.os.AsyncTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

final class OLPlayHallRoomTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLPlayHallRoom> olPlayHallRoom;

    OLPlayHallRoomTask(OLPlayHallRoom olPlayHallRoom) {
        this.olPlayHallRoom = new WeakReference<>(olPlayHallRoom);
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        // 创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("head", "2")
                .add("version", olPlayHallRoom.get().jpApplication.getVersion())
                .add("keywords", strArr[0])
                .add("userName", strArr[1])
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + olPlayHallRoom.get().jpApplication.getServer() + ":8910/JustPianoServer/server/GetUserInfo")
                .post(formBody)
                .build();
        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected final void onPostExecute(String str) {
        olPlayHallRoom.get().jpprogressBar.cancel();
    }

    @Override
    protected void onPreExecute() {
        olPlayHallRoom.get().jpprogressBar.setCancelable(true);
        olPlayHallRoom.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olPlayHallRoom.get().jpprogressBar.show();
    }
}
