package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.local.PlayFinish;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class PlayFinishTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<PlayFinish> playFinish;

    public PlayFinishTask(PlayFinish playFinish) {
        this.playFinish = new WeakReference<>(playFinish);
    }

    @Override
    protected String doInBackground(Void... v) {
        if (!OLBaseActivity.getAccountName().isEmpty()) {
            return OkHttpUtil.sendPostRequest("ScoreUpload", new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("songID", playFinish.get().songId)
                    .add("userName", OLBaseActivity.getAccountName())
                    .add("scoreArray", playFinish.get().scoreArray)
                    .build());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String str) {
        switch (str) {
            case "0", "1" -> playFinish.get().jpprogressBar.dismiss();
            case "2" -> {
                playFinish.get().jpprogressBar.dismiss();
                Toast.makeText(playFinish.get(), "该版本无法上传成绩，请更新版本!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        playFinish.get().jpprogressBar.setCancelable(true);
        playFinish.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        playFinish.get().jpprogressBar.show();
    }
}
