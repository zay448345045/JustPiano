package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public final class UsersInfoTask3 extends AsyncTask<String, Void, String> {
    private final WeakReference<UsersInfo> userInfo;

    UsersInfoTask3(UsersInfo usersInfo) {
        userInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected final String doInBackground(String... strArr) {
        return UsersInfo.m3931b(strArr[0], strArr[1], strArr[2]);
    }

    @Override
    protected final void onPostExecute(String str) {
        if (str.equals("0")) {
            userInfo.get().jpprogressBar.cancel();
            Toast.makeText(userInfo.get(), "修改成功!", Toast.LENGTH_LONG).show();
            return;
        }
        userInfo.get().jpprogressBar.cancel();
        Toast.makeText(userInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        userInfo.get().jpprogressBar.setCancelable(true);
        userInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        userInfo.get().jpprogressBar.show();
    }
}
