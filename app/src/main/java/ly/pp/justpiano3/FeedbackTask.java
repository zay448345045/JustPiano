package ly.pp.justpiano3;

import android.widget.Toast;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class FeedbackTask {
    private final WeakReference<MainMode> mainMode;
    private final String userName;
    private final String message;
    private final ExecutorService executorService;
    private Future<String> future;

    FeedbackTask(MainMode mainMode, String userName, String message) {
        this.mainMode = new WeakReference<>(mainMode);
        this.userName = userName;
        this.message = message;
        executorService = Executors.newSingleThreadExecutor();
    }

    public void execute(String... objects) {
        String url = "http://" + mainMode.get().jpApplication.getServer() + ":8910/JustPianoServer/server/Feedback";


        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("version", mainMode.get().jpApplication.getVersion());
        formBuilder.add("userName", userName);
        formBuilder.add("message", message);
        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        future = executorService.submit(() -> {
            try (Response response = OkHttpUtil.client().newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        handleResult();
    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }

    private void handleResult() {
        executorService.execute(() -> {
            try {
                final String str = future.get();
                mainMode.get().runOnUiThread(() -> {
                    Toast.makeText(mainMode.get(), StringUtil.isNullOrEmpty(str) ? "反馈提交出错" : "反馈提交成功", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
