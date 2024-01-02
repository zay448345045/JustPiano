package ly.pp.justpiano3.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class OLMelodySongsPlayTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<OLMelodySelect> olMelodySelect;
    private final Intent intent;

    public OLMelodySongsPlayTask(OLMelodySelect oLMelodySelect, Intent intent) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
        this.intent = intent;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (OLMelodySelect.songBytes == null || OLMelodySelect.songBytes.length <= 3) {
            olMelodySelect.get().jpprogressBar.cancel();
            Toast.makeText(olMelodySelect.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("head", 1);
        intent.putExtra("songBytes", OLMelodySelect.songBytes);
        intent.putExtra("songName", olMelodySelect.get().songName);
        intent.putExtra("songID", OLMelodySelect.songID);
        intent.putExtra("topScore", olMelodySelect.get().topScore);
        intent.putExtra("degree", olMelodySelect.get().degree);
        olMelodySelect.get().startActivity(intent);
        olMelodySelect.get().jpprogressBar.cancel();
    }

    @Override
    protected Void doInBackground(Void... v) {
        OLMelodySelect.songBytes = GZIPUtil.ZIPToArray(OkHttpUtil.sendPostRequest("DownloadSong", new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("songID", OLMelodySelect.songID)
                .build()));
        return null;
    }

    @Override
    protected void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setCancelable(true);
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
