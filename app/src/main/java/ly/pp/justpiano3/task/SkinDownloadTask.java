package ly.pp.justpiano3.task;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ly.pp.justpiano3.activity.local.SkinDownload;
import ly.pp.justpiano3.adapter.SkinDownloadAdapter;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class SkinDownloadTask {
    private final WeakReference<SkinDownload> skinDownload;
    private Future<?> future;

    public SkinDownloadTask(SkinDownload skinDownload) {
        this.skinDownload = new WeakReference<>(skinDownload);
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
                    .url("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/GetSkinList")
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
        skinDownload.get().gridView.setAdapter(new SkinDownloadAdapter(skinDownload.get(), new JSONArray()));
        try {
            skinDownload.get().gridView.setAdapter(new SkinDownloadAdapter(skinDownload.get(), new JSONArray(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")))));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            skinDownload.get().jpProgressBar.dismiss();
        }
    }

    private void onPreExecute() {
        skinDownload.get().jpProgressBar.setCancelable(true);
        skinDownload.get().jpProgressBar.setOnCancelListener(dialog -> cancel());
        skinDownload.get().jpProgressBar.show();
    }

    private void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }
}
