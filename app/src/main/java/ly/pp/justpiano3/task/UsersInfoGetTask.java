package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.UsersInfo;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class UsersInfoGetTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<UsersInfo> userInfo;

    public UsersInfoGetTask(UsersInfo usersInfo) {
        userInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        if (!OLBaseActivity.getAccountName().isEmpty()) {
            return OkHttpUtil.sendPostRequest("GetUserInfo", new FormBody.Builder()
                    .add("head", "0")
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("keywords", "0")
                    .add("userName", OLBaseActivity.getAccountName())
                    .build());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String str) {
        if (str.length() > 3) {
            UsersInfo.updateUserInfo(userInfo.get(), str);
            userInfo.get().jpprogressBar.cancel();
        } else if (str.equals("{}")) {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        userInfo.get().jpprogressBar.setCancelable(true);
        userInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        userInfo.get().jpprogressBar.show();
    }
}
