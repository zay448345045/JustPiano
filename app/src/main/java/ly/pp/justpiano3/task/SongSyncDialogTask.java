package ly.pp.justpiano3.task;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLMainMode;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import okhttp3.FormBody;

public final class SongSyncDialogTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMainMode> olMainMode;

    public SongSyncDialogTask(OLMainMode olMainMode) {
        this.olMainMode = new WeakReference<>(olMainMode);
    }

    @Override
    protected String doInBackground(String... objects) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(olMainMode.get());
        long lastSongModifiedTime = sharedPreferences.getLong("song_sync_time", PmSongUtil.SONG_SYNC_DEFAULT_TIME);
        return OkHttpUtil.sendPostRequest("SyncSongDialog", new FormBody.Builder()
                .add("version", BuildConfig.VERSION_NAME)
                .add("lastSongModifiedTime", String.valueOf(lastSongModifiedTime))
                .build());
    }

    @Override
    protected void onPostExecute(String str) {
        try {
            int i;
            String message = null;
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getInt("C") > 0) {
                message = "您有《" + jsonObject.getString("F") + "》等" + jsonObject.getInt("C") + "首在线曲库曲谱改动需同步至本地曲库，同步后方可进入对战。是否现在同步曲谱?";
                i = 1;
                olMainMode.get().jpProgressBar.dismiss();
            } else {
                olMainMode.get().jpProgressBar.show();
                OnlineUtil.cancelAutoReconnect();
                OnlineUtil.onlineConnectionService(olMainMode.get().getApplication());
                i = 0;
            }
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olMainMode.get());
            jpDialogBuilder.setTitle("在线曲库同步");
            jpDialogBuilder.setMessage(message);
            jpDialogBuilder.setFirstButton("开始同步", (dialog, which) -> {
                dialog.dismiss();
                new SongSyncTask(olMainMode.get()).execute();
            });
            jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
            if (i != 0) {
                jpDialogBuilder.buildAndShowDialog();
            }
        } catch (Exception e) {
            Toast.makeText(olMainMode.get(), "无法检查曲库同步，请尝试重新登录", Toast.LENGTH_SHORT).show();
            olMainMode.get().jpProgressBar.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        olMainMode.get().jpProgressBar.setCancelable(true);
        olMainMode.get().jpProgressBar.setOnCancelListener(dialog -> cancel(true));
        olMainMode.get().jpProgressBar.show();
    }
}
