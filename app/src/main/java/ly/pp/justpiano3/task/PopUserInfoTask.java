package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.PopUserInfo;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class PopUserInfoTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<PopUserInfo> popUserInfo;

    public PopUserInfoTask(PopUserInfo popUserInfo) {
        this.popUserInfo = new WeakReference<>(popUserInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        if (!TextUtils.isEmpty(popUserInfo.get().userName)) {
            return OkHttpUtil.sendPostRequest("GetUserInfo", new FormBody.Builder()
                    .add("head", String.valueOf(popUserInfo.get().headType))
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("keywords", popUserInfo.get().keywords)
                    .add("userName", popUserInfo.get().userName)
                    .build());
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (popUserInfo.get().headType != 1) {
            popUserInfo.get().jpprogressBar.cancel();
            Toast.makeText(popUserInfo.get(), "发送成功!", Toast.LENGTH_SHORT).show();
        } else if (result.length() > 3) {
            popUserInfo.get().showUserInfo(result);
            popUserInfo.get().jpprogressBar.cancel();
        } else if (result.equals("[]")) {
            popUserInfo.get().jpprogressBar.cancel();
            Toast.makeText(popUserInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            popUserInfo.get().jpprogressBar.cancel();
            Toast.makeText(popUserInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        popUserInfo.get().jpprogressBar.setCancelable(true);
        popUserInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        popUserInfo.get().jpprogressBar.show();
    }
}
