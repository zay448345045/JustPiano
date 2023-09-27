package ly.pp.justpiano3.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.OLMelodySelect;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public final class OLMelodySongsPlayTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMelodySelect> olMelodySelect;
    private Intent intent;
    private String str = "";

    public OLMelodySongsPlayTask(OLMelodySelect oLMelodySelect, Intent intent) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
        this.intent = intent;
    }

    @Override
    protected void onPostExecute(String str) {
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
    protected String doInBackground(String... objects) {
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("songID", OLMelodySelect.songID)
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/DownloadSong")
                .post(formBody)
                .build();
        try {
            // 发送请求并获取响应
            Response response = OkHttpUtil.client().newCall(request).execute();
            if (response.isSuccessful()) {
                str = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        OLMelodySelect.songBytes = GZIPUtil.ZIPToArray(str);
        return null;
    }

    @Override
    protected void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setCancelable(true);
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
