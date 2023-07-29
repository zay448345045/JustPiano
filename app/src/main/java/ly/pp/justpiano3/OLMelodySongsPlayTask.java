package ly.pp.justpiano3;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.ref.WeakReference;

public final class OLMelodySongsPlayTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMelodySelect> olMelodySelect;
    private String str = "";

    OLMelodySongsPlayTask(OLMelodySelect oLMelodySelect) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
    }

    @Override
    protected final void onPostExecute(String str) {
        if (OLMelodySelect.songBytes == null || OLMelodySelect.songBytes.length <= 3) {
            olMelodySelect.get().jpprogressBar.cancel();
            Toast.makeText(olMelodySelect.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("head", 1);
        intent.putExtra("songBytes", OLMelodySelect.songBytes);
        intent.putExtra("songName", olMelodySelect.get().songName);
        intent.putExtra("songID", OLMelodySelect.songID);
        intent.putExtra("topScore", olMelodySelect.get().topScore);
        intent.putExtra("degree", olMelodySelect.get().degree);
        intent.setClass(olMelodySelect.get(), PianoPlay.class);
        olMelodySelect.get().startActivity(intent);
        olMelodySelect.get().jpprogressBar.cancel();
    }

    @Override
    protected String doInBackground(String... objects) {
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("version", olMelodySelect.get().jpapplication.getVersion())
                .add("songID", OLMelodySelect.songID)
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + olMelodySelect.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong")
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
        OLMelodySelect.songBytes = GZIP.ZIPToArray(str);
        return null;
    }

    @Override
    protected void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setMessage("正在载入曲谱,请稍后...");
        olMelodySelect.get().jpprogressBar.setCancelable(true);
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
