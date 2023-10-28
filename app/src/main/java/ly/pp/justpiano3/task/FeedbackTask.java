package ly.pp.justpiano3.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class FeedbackTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<Context> weakReference;
    private final String userName;
    private final String message;

    public FeedbackTask(Context context, String userName, String message) {
        this.weakReference = new WeakReference<>(context);
        this.userName = userName;
        this.message = message;
    }

    @Override
    protected String doInBackground(Void... v) {
        String url = "http://" + OnlineUtil.server + ":8910/JustPianoServer/server/Feedback";
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("version", BuildConfig.VERSION_NAME);
        formBuilder.add("userName", userName);
        formBuilder.add("message", message);
        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = OkHttpUtil.client().newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String responseStr) {
        if (weakReference.get() instanceof Activity) {
            ((Activity) weakReference.get()).runOnUiThread(() -> Toast.makeText(weakReference.get(),
                    StringUtil.isNullOrEmpty(responseStr) ? "反馈提交出错" : "反馈提交成功", Toast.LENGTH_SHORT).show());
        }
    }
}
