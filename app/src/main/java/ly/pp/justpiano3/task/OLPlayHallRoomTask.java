package ly.pp.justpiano3.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class OLPlayHallRoomTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLPlayHallRoom> olPlayHallRoom;

    public OLPlayHallRoomTask(OLPlayHallRoom olPlayHallRoom) {
        this.olPlayHallRoom = new WeakReference<>(olPlayHallRoom);
    }

    @Override
    protected String doInBackground(String... strArr) {
        return OkHttpUtil.sendPostRequest("GetUserInfo", new FormBody.Builder()
                .add("head", "2")
                .add("version", BuildConfig.VERSION_NAME)
                .add("keywords", strArr[0])
                .build());
    }

    @Override
    protected void onPostExecute(String str) {
        olPlayHallRoom.get().jpprogressBar.cancel();
    }

    @Override
    protected void onPreExecute() {
        olPlayHallRoom.get().jpprogressBar.setCancelable(true);
        olPlayHallRoom.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olPlayHallRoom.get().jpprogressBar.show();
    }
}
