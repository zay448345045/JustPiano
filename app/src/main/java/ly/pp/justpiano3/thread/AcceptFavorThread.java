package ly.pp.justpiano3.thread;

import android.text.TextUtils;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class AcceptFavorThread extends Thread {
    private final String type;
    private final String songId;
    private final String userName;

    public AcceptFavorThread(String songId, String type, String userName) {
        this.songId = songId;
        this.userName = userName;
        this.type = type;
    }

    @Override
    public void run() {
        if (!TextUtils.isEmpty(userName)) {
            OkHttpUtil.sendPostRequest("AcceptFavorIn", new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("type", type)
                    .add("songID", songId)
                    .add("user", userName)
                    .build());
        }
    }
}
