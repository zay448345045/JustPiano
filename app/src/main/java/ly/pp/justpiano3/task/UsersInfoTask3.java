package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.activity.UsersInfo;

import java.lang.ref.WeakReference;

public final class UsersInfoTask3 extends AsyncTask<String, Void, String> {
    private final WeakReference<UsersInfo> userInfo;

    public UsersInfoTask3(UsersInfo usersInfo) {
        userInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected String doInBackground(String... strArr) {
        return UsersInfo.m3931b(strArr[0], strArr[1], strArr[2]);
    }

    @Override
    protected void onPostExecute(String str) {
        if (str.equals("0")) {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "修改成功!", Toast.LENGTH_LONG).show();
            return;
        }
        userInfo.get().jpprogressBar.cancel();
        Toast.makeText(userInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        userInfo.get().jpprogressBar.setCancelable(true);
        userInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        userInfo.get().jpprogressBar.show();
    }
}
