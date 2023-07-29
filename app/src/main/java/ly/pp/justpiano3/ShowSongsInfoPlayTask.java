package ly.pp.justpiano3;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.ref.WeakReference;

public final class ShowSongsInfoPlayTask extends AsyncTask<String, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;
    private byte[] songBytes = null;
    private String str = "";

    ShowSongsInfoPlayTask(ShowSongsInfo showSongsInfo) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
    }

    @Override
    protected String doInBackground(String... objects) {
        if (!showSongsInfo.get().songID.isEmpty()) {
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + showSongsInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong").newBuilder();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("version", showSongsInfo.get().jpapplication.getVersion());
            builder.add("songID", showSongsInfo.get().songID);
            // 创建Request对象，用于发送请求
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .post(builder.build())
                    .build();
            try {
                // 同步执行请求，获取Response对象
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) {
                    str = response.body().string();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            songBytes = GZIP.ZIPToArray(str);
        }
        return null;
    }


    @Override
    protected void onPostExecute(String str) {
        if (songBytes == null || songBytes.length <= 3) {
            showSongsInfo.get().jpprogressBar.dismiss();
            Toast.makeText(showSongsInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
            return;
        }
        OLMelodySelect.songBytes = songBytes;
        Intent intent = new Intent();
        intent.putExtra("head", 1);
        intent.putExtra("songBytes", songBytes);
        intent.putExtra("songName", showSongsInfo.get().songName);
        intent.putExtra("songID", showSongsInfo.get().songID);
        intent.putExtra("topScore", showSongsInfo.get().score);
        intent.putExtra("degree", showSongsInfo.get().nandu);
        intent.setClass(showSongsInfo.get(), PianoPlay.class);
        showSongsInfo.get().startActivity(intent);
        showSongsInfo.get().jpprogressBar.dismiss();
    }

    @Override
    protected void onPreExecute() {
        showSongsInfo.get().jpprogressBar.setMessage("此版本仅做学习研究使用,如发现曲谱皮肤等对您构成侵权,请联系开发者修改或删除,正在载入曲谱,请稍后...");
        showSongsInfo.get().jpprogressBar.setCancelable(true);
        showSongsInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showSongsInfo.get().jpprogressBar.show();
    }
}
