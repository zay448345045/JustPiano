package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.activity.LoginActivity;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public final class LoginTask extends AsyncTask<String, Void, String> {
    private final WeakReference<LoginActivity> activity;
    private String f5139a = "";
    private String message = "";
    private String title = "";

    public LoginTask(LoginActivity loginActivity) {
        this.activity = new WeakReference<>(loginActivity);
    }

    @Override
    protected String doInBackground(String... objects) {
        String f5140b = "";
        LoginActivity loginActivity = activity.get();
        loginActivity.accountX = loginActivity.accountTextView.getText().toString();
        loginActivity.password = loginActivity.passwordTextView.getText().toString();
        if (!loginActivity.accountX.isEmpty() && !loginActivity.password.isEmpty()) {
            String ip = loginActivity.jpapplication.getServer();
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + ip + ":8910/JustPianoServer/server/LoginServlet").newBuilder();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("versionName", "4.3");
            formBuilder.add("packageNames", loginActivity.getPackageName());
            formBuilder.add("versionCode", String.valueOf(41));
            formBuilder.add("username", loginActivity.accountX);
            formBuilder.add("password", loginActivity.password);
            formBuilder.add("local", loginActivity.jpapplication.getVersion());
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
                        f5139a = line;
                    }
                    response.body().close();
                    return f5140b;
                }
            } catch (Exception e) {
                f5140b = "";
                e.printStackTrace();
            }
        }
        return f5140b;
    }

    @Override
    protected void onPostExecute(String str) {
        LoginActivity loginActivity = activity.get();
        int i = 3;
        String newVersion = null;
        try {
            JSONObject jSONObject = new JSONObject(f5139a);
            // 记录服务端返回的会话公钥
            String serverPublicKey = jSONObject.getString("publicKey");
            loginActivity.jpapplication.setServerPublicKey(serverPublicKey);
            try {
                newVersion = jSONObject.getString("version");
            } catch (JSONException ignored) {
            }
            i = jSONObject.getInt("is");
            try {
                message = jSONObject.getString("msg");
                loginActivity.kitiName = jSONObject.getString("ukn");
                title = jSONObject.getString("title");
                loginActivity.jpapplication.f4073g = jSONObject.getString("T1");
                loginActivity.jpapplication.f4074h = jSONObject.getString("M1");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (JSONException ignored) {
        }
        loginActivity.jpprogressBar.dismiss();
        switch (i) {
            case 0:
            case 4:
            case 5:
                loginActivity.loginSuccess(i, message, title);
                return;
            case 1:
            case 2:
                if (!StringUtil.isNullOrEmpty(newVersion)) {
                    loginActivity.addVersionUpdateDialog(message, newVersion);
                } else {
                    loginActivity.addDialog("提示", "确定", message);
                }
                return;
            case 3:
                loginActivity.addDialog("提示", "确定", "网络错误!");
                return;
            default:
        }
    }

    @Override
    protected void onPreExecute() {
        LoginActivity loginActivity = activity.get();
        loginActivity.jpprogressBar.setCancelable(true);
        loginActivity.jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        loginActivity.jpprogressBar.show();
    }
}
