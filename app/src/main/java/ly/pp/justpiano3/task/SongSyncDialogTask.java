package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.activity.OLMainMode;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public final class SongSyncDialogTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMainMode> olMainMode;
    private String maxSongId = "";

    public SongSyncDialogTask(OLMainMode olMainMode, String maxSongId) {
        this.olMainMode = new WeakReference<>(olMainMode);
        this.maxSongId = maxSongId;
    }

    @Override
    protected String doInBackground(String... objects) {
        // 创建请求参数
        FormBody formBody = new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("maxSongId", String.valueOf(maxSongId))
                .build();
        // 创建请求对象
        Request request = new Request.Builder()
                .url("http://" + olMainMode.get().jpapplication.getServer() + ":8910/JustPianoServer/server/SongSyncDialog")
                .post(formBody)
                .build();
        try {
            // 发送请求并获取响应
            Response response = OkHttpUtil.client().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        try {
            int i;
            String message = null;
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getInt("C") > 0) {
                message = "您有《" + jsonObject.getString("F") + "》等" + jsonObject.getInt("C") + "首在线曲库最新曲谱需同步至本地曲库，同步后方可进入对战。是否现在同步曲谱?";
                i = 1;
                olMainMode.get().jpprogressBar.dismiss();
            } else {
                olMainMode.get().loginOnline();
                i = 0;
            }
            JPDialog jpdialog = new JPDialog(olMainMode.get());
            jpdialog.setTitle("在线曲库同步");
            jpdialog.setMessage(message);
            jpdialog.setFirstButton("开始同步", (dialog, which) -> {
                dialog.dismiss();
                new SongSyncTask(olMainMode.get(), maxSongId).execute();
            });
            jpdialog.setSecondButton("取消", new DialogDismissClick());
            if (i != 0) {
                jpdialog.showDialog();
            }
        } catch (Exception e) {
            Toast.makeText(olMainMode.get(), "无法检查曲库同步，请尝试重新登录", Toast.LENGTH_SHORT).show();
            olMainMode.get().jpprogressBar.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        olMainMode.get().jpprogressBar.setCancelable(true);
        olMainMode.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMainMode.get().jpprogressBar.show();
    }
}
