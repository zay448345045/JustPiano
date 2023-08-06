package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.activity.PlayFinish;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.*;

import java.io.IOException;
import java.lang.ref.WeakReference;

public final class PlayFinishTask extends AsyncTask<String, Void, String> {
    private final WeakReference<PlayFinish> playFinish;

    public PlayFinishTask(PlayFinish playFinish) {
        this.playFinish = new WeakReference<>(playFinish);
    }

    @Override
    protected String doInBackground(String... objects) {
        String str = "";
        if (!playFinish.get().jpapplication.getAccountName().isEmpty()) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host(playFinish.get().jpapplication.getServer())
                    .port(8910)
                    .addPathSegment("JustPianoServer")
                    .addPathSegment("server")
                    .addPathSegment("ScoreUpload")
                    .build();
            RequestBody body = new FormBody.Builder()
                    .add("version", playFinish.get().jpapplication.getVersion())
                    .add("songID", playFinish.get().songID)
                    .add("userName", playFinish.get().jpapplication.getAccountName())
                    .add("scoreArray", playFinish.get().scoreArray)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try {
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) {
                    str = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    @Override
    protected void onPostExecute(String str) {
        switch (str) {
            case "0":
                playFinish.get().jpprogressBar.dismiss();
                break;
            case "1":
                playFinish.get().jpprogressBar.dismiss();
                Toast.makeText(playFinish.get(), "连接出错!", Toast.LENGTH_SHORT).show();
                break;
            case "2":
                playFinish.get().jpprogressBar.dismiss();
                Toast.makeText(playFinish.get(), "该版本无法上传成绩，请更新版本!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onPreExecute() {
        playFinish.get().jpprogressBar.setMessage("正在上传您的成绩,请稍后...");
        playFinish.get().jpprogressBar.setCancelable(true);
        playFinish.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        playFinish.get().jpprogressBar.show();
    }
}
