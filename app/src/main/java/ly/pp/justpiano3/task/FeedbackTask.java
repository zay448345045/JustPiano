package ly.pp.justpiano3.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Objects;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

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
        if (message.length() >= 4000) {
            return "invalid";
        }
        return OkHttpUtil.sendPostRequest("Feedback", new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("userName", userName)
                .add("message", message)
                .build());
    }

    @Override
    protected void onPostExecute(String responseStr) {
        if (weakReference.get() instanceof Activity activity) {
            if (Objects.equals("invalid", responseStr)) {
                activity.runOnUiThread(() -> Toast.makeText(activity,
                        "反馈内容过长，无法提交", Toast.LENGTH_SHORT).show());
            } else {
                activity.runOnUiThread(() -> Toast.makeText(activity,
                        TextUtils.isEmpty(responseStr) ? "反馈提交出错" : "反馈提交成功", Toast.LENGTH_SHORT).show());
            }
        }
    }
}
