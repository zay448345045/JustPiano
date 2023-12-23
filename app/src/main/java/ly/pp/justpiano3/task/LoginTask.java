package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.LoginActivity;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public final class LoginTask extends AsyncTask<Void, Void, Void> {
    private final WeakReference<LoginActivity> activity;
    private String loginResponse = "";
    private String message = "";
    private String title = "";

    public LoginTask(LoginActivity loginActivity) {
        this.activity = new WeakReference<>(loginActivity);
    }

    @Override
    protected Void doInBackground(Void... v) {
        LoginActivity loginActivity = activity.get();
        loginActivity.accountX = loginActivity.accountTextView.getText().toString();
        loginActivity.password = loginActivity.passwordTextView.getText().toString();
        if (!loginActivity.accountX.isEmpty() && !loginActivity.password.isEmpty()) {
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/LoginServlet").newBuilder();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("versionName", "4.3");
            formBuilder.add("packageNames", loginActivity.getPackageName());
            formBuilder.add("versionCode", String.valueOf(41));
            formBuilder.add("username", loginActivity.accountX);
            formBuilder.add("password", loginActivity.password);
            formBuilder.add("local", BuildConfig.VERSION_NAME);
            // 创建Request对象，用于发送请求
            Request request = new Request.Builder().url(urlBuilder.build())
                    .post(formBuilder.build())
                    .build();
            try {
                // 同步执行请求，获取Response对象
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        loginResponse = line;
                    }
                    response.body().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        LoginActivity loginActivity = activity.get();
        int i = 3;
        String newVersion = null;
        try {
            JSONObject jSONObject = new JSONObject(loginResponse);
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
                loginActivity.jpApplication.loginResultTitle = jSONObject.getString("T1");
                loginActivity.jpApplication.loginResultMessage = jSONObject.getString("M1");
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
            case 0, 4, 5 -> {
                loginActivity.loginSuccess(i, message, title);
                return;
            }
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
                return;
            }
            case 3 -> {
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(loginActivity);
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("网络错误!");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.buildAndShowDialog();
                return;
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
