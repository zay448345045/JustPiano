package ly.pp.justpiano3;

import android.os.Handler;
import android.os.Looper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SoundDownloadTask {
    private final WeakReference<SoundDownload> soundDownload;
    private Future<?> future;

    SoundDownloadTask(SoundDownload soundDownload) {
        this.soundDownload = new WeakReference<>(soundDownload);
    }

    public void execute(String... strArr) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        onPreExecute();

        future = executor.submit(() -> {
            final String result = doInBackground(strArr);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(result));
        });
    }

    private String doInBackground(String... strArr) {
        try {
            soundDownload.get().getLocalSoundList();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 连接超时
                    .readTimeout(10, TimeUnit.SECONDS)  // 读取超时
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + soundDownload.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetSoundList")
                    .post(RequestBody.create("", null))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                return "err001";
            }
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "err001";
        }
    }

    private void onPostExecute(String str) {
        soundDownload.get().gridView.setAdapter(new SoundDownloadAdapter(soundDownload.get(), new JSONArray()));
        try {
            soundDownload.get().gridView.setAdapter(new SoundDownloadAdapter(soundDownload.get(), new JSONArray(GZIP.ZIPTo(new JSONObject(str).getString("L")))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        soundDownload.get().jpProgressBar.dismiss();
    }

    private void onPreExecute() {
        soundDownload.get().jpProgressBar.setCancelable(true);
        soundDownload.get().jpProgressBar.setOnCancelListener(dialog -> cancel());
        soundDownload.get().jpProgressBar.show();
    }

    private void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }
}

