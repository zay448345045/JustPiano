package ly.pp.justpiano3.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.activity.online.ShowSongsInfo;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class ShowSongsInfoPlayTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;
    private final Intent intent;
    private byte[] songBytes = null;

    public ShowSongsInfoPlayTask(ShowSongsInfo showSongsInfo, Intent intent) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
        this.intent = intent;
    }

    @Override
    protected Void doInBackground(Void... v) {
        if (!showSongsInfo.get().songID.isEmpty()) {
            songBytes = GZIPUtil.ZIPToArray(OkHttpUtil.sendPostRequest("DownloadSong", new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("songID", showSongsInfo.get().songID)
                    .build()));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (songBytes == null || songBytes.length <= 3) {
            showSongsInfo.get().jpprogressBar.dismiss();
            Toast.makeText(showSongsInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        OLMelodySelect.songBytes = songBytes;
        intent.putExtra("head", 1);
        intent.putExtra("songBytes", songBytes);
        intent.putExtra("songName", showSongsInfo.get().songName);
        intent.putExtra("songID", showSongsInfo.get().songID);
        intent.putExtra("topScore", showSongsInfo.get().score);
        intent.putExtra("degree", showSongsInfo.get().degree);
        showSongsInfo.get().startActivity(intent);
        showSongsInfo.get().jpprogressBar.dismiss();
    }

    @Override
    protected void onPreExecute() {
        showSongsInfo.get().jpprogressBar.setCancelable(true);
        showSongsInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showSongsInfo.get().jpprogressBar.show();
    }
}
