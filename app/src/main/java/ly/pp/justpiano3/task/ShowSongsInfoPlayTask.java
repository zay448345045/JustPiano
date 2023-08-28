package ly.pp.justpiano3.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.activity.OLMelodySelect;
import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.activity.ShowSongsInfo;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.ref.WeakReference;

public final class ShowSongsInfoPlayTask extends AsyncTask<String, Void, String> {
    private final WeakReference<ShowSongsInfo> showSongsInfo;
    private final Intent intent;
    private byte[] songBytes = null;
    private String str = "";

    public ShowSongsInfoPlayTask(ShowSongsInfo showSongsInfo, Intent intent) {
        this.showSongsInfo = new WeakReference<>(showSongsInfo);
        this.intent = intent;
    }

    @Override
    protected String doInBackground(String... objects) {
        if (!showSongsInfo.get().songID.isEmpty()) {
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + showSongsInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/DownloadSong").newBuilder();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("version", DeviceUtil.getAppVersionName(showSongsInfo.get()));
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
            songBytes = GZIPUtil.ZIPToArray(str);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
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
        intent.putExtra("degree", showSongsInfo.get().nandu);
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
