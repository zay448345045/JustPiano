package ly.pp.justpiano3.task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ly.pp.justpiano3.activity.SkinDownload;
import ly.pp.justpiano3.adapter.SkinDownloadAdapter;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public final class SkinDownloadTask {
    private final WeakReference<SkinDownload> skinDownload;
    private final ExecutorService executorService;
    private Future<String> future;

    public SkinDownloadTask(SkinDownload skinDownload) {
        this.skinDownload = new WeakReference<>(skinDownload);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void execute() {
        future = executorService.submit(() -> {
            try {
                String url = "http://" + OnlineUtil.server + ":8910/JustPianoServer/server/GetSkinList";

                Request request = new Request.Builder()
                        .url(url)
                        .post(new FormBody.Builder().build())
                        .build();

                try (Response response = OkHttpUtil.client().newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        return response.body().string();
                    }
                }
                return "err001";
            } catch (Exception e) {
                e.printStackTrace();
                return "err001";
            }
        });

        // Handle the result in onPostExecute
        handleResult();
    }

    public void cancel() {
        if (future != null) {
            future.cancel(true);
        }
    }

    private void handleResult() {
        skinDownload.get().runOnUiThread(() -> {
            skinDownload.get().gridView.setAdapter(new SkinDownloadAdapter(skinDownload.get(), new JSONArray()));
            try {
                String str = future.get();
                skinDownload.get().gridView.setAdapter(new SkinDownloadAdapter(skinDownload.get(), new JSONArray(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            skinDownload.get().jpProgressBar.dismiss();
        });
    }
}
