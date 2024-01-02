package ly.pp.justpiano3.task;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ly.pp.justpiano3.activity.local.SoundDownload;
import ly.pp.justpiano3.adapter.SoundDownloadAdapter;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

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
            final String result = OkHttpUtil.sendPostRequest("GetSoundList", new FormBody.Builder().build());
            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(result));
        });
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

