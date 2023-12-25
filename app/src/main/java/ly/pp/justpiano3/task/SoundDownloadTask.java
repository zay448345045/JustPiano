package ly.pp.justpiano3.task;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.adapter.SoundDownloadAdapter;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class SoundDownloadTask {
    private final WeakReference<SoundDownload> soundDownload;
    private Future<?> future;

    public SoundDownloadTask(SoundDownload soundDownload) {
        this.soundDownload = new WeakReference<>(soundDownload);
    }

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        onPreExecute();
        future = executor.submit(() -> {
            final String result = doInBackground();

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(result));
        });
    }

    private String doInBackground() {
        try {
            Request request = new Request.Builder()
                    .url("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/GetSoundList")
                    .post(RequestBody.create("", null))
                    .build();
            Response response = OkHttpUtil.client().newCall(request).execute();
            if (response.code() != 200) {
                return "";
            }
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void onPostExecute(String str) {
        soundDownload.get().gridView.setAdapter(new SoundDownloadAdapter(soundDownload.get(), new JSONArray()));
        try {
            soundDownload.get().gridView.setAdapter(new SoundDownloadAdapter(soundDownload.get(), new JSONArray(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")))));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            soundDownload.get().jpProgressBar.dismiss();
        }
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

