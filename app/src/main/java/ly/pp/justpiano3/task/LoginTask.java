package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.LoginActivity;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import okhttp3.FormBody;

public final class LoginTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<LoginActivity> activity;
    private String message = "";
    private String title = "";

    public LoginTask(LoginActivity loginActivity) {
        this.activity = new WeakReference<>(loginActivity);
    }

    @Override
    protected String doInBackground(Void... v) {
        LoginActivity loginActivity = activity.get();
        loginActivity.accountX = loginActivity.accountTextView.getText().toString();
        loginActivity.password = loginActivity.passwordTextView.getText().toString();
        if (!loginActivity.accountX.isEmpty() && !loginActivity.password.isEmpty()) {
            return OkHttpUtil.sendPostRequest("LoginServlet", new FormBody.Builder()
                    .add("versionName", "4.3")
                    .add("packageNames", loginActivity.getPackageName())
                    .add("versionCode", String.valueOf(41))
                    .add("username", loginActivity.accountX)
                    .add("password", loginActivity.password)
                    .add("local", BuildConfig.VERSION_NAME)
                    .build());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        LoginActivity loginActivity = activity.get();
        int i = 3;
        String newVersion = null;
        try {
            JSONObject jSONObject = new JSONObject(result);
            // 记录服务端返回的会话公钥
            String serverPublicKey = jSONObject.getString("publicKey");
            EncryptUtil.setServerPublicKey(serverPublicKey);
            try {
                newVersion = jSONObject.getString("version");
            } catch (JSONException ignored) {

            }
            i = jSONObject.getInt("is");
            try {
                message = jSONObject.getString("msg");
                loginActivity.kitiName = jSONObject.getString("ukn");
                title = jSONObject.getString("title");
                OLBaseActivity.loginResultTitle = jSONObject.getString("T1");
                OLBaseActivity.loginResultMessage = jSONObject.getString("M1");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (loginActivity.jpProgressBar.isShowing()) {
            loginActivity.jpProgressBar.dismiss();
        }
        switch (i) {
            case 0, 4, 5 -> loginActivity.loginSuccess(i, message, title);
            case 1, 2 -> {
                if (!TextUtils.isEmpty(newVersion)) {
                    loginActivity.addVersionUpdateDialog(message, newVersion);
                } else {
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(loginActivity);
                    jpDialogBuilder.setTitle("提示");
                    jpDialogBuilder.setMessage(message);
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                    jpDialogBuilder.buildAndShowDialog();
                }
            }
            case 3 -> {
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(loginActivity);
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("网络错误!");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.buildAndShowDialog();
            }
            default -> {
            }
        }
    }

    @Override
    protected void onPreExecute() {
        LoginActivity loginActivity = activity.get();
        loginActivity.jpProgressBar.setCancelable(true);
        loginActivity.jpProgressBar.setOnCancelListener(dialog -> cancel(true));
        loginActivity.jpProgressBar.show();
    }
}
