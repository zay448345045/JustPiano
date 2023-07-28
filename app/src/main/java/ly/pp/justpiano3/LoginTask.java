package ly.pp.justpiano3;

import android.os.AsyncTask;
import ly.pp.justpiano3.utils.EncryptUtil;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

public final class LoginTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Login> activity;
    private String f5139a = "";
    private String message = "";
    private String title = "";

    LoginTask(Login login) {
        this.activity = new WeakReference<>(login);
    }

    public static void main(String[] args) {
        String ip = "server.justpiano.fun";
        // 创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 创建HttpUrl.Builder对象，用于添加查询参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + ip + ":8910/JustPianoServer/server/LoginServlet").newBuilder();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("versionName", "4.6");
        formBuilder.add("packageNames", "");
        formBuilder.add("versionCode", "4.6");
        formBuilder.add("username", "testbot");
        formBuilder.add("password", "testbot");
        formBuilder.add("local", "4.6");
        RequestBody requestBody = formBuilder.build();
        // 创建Request对象，用于发送请求
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(requestBody)
                .build();
        try {
            // 同步执行请求，获取Response对象
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String string = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... objects) {
        String f5140b = "";
        Login login = activity.get();
        login.accountX = login.accountTextView.getText().toString();
        login.password = login.passwordTextView.getText().toString();
        if (!login.accountX.isEmpty() && !login.password.isEmpty()) {
            String ip = login.jpapplication.getServer();
            // 创建OkHttpClient对象
            OkHttpClient client = new OkHttpClient();
            // 创建HttpUrl.Builder对象，用于添加查询参数
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://" + ip + ":8910/JustPianoServer/server/LoginServlet").newBuilder();
            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("versionName", login.versionStr);
            formBuilder.add("packageNames", login.packageName);
            formBuilder.add("versionCode", String.valueOf(login.versionNum));
            formBuilder.add("username", login.accountX);
            formBuilder.add("password", login.password);
            formBuilder.add("local", login.jpapplication.getVersion());
            // 创建Request对象，用于发送请求
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .post(formBuilder.build())
                    .build();
            try {
                // 同步执行请求，获取Response对象
                Response response = client.newCall(request).execute();
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
        Login login = activity.get();
        int i = 3;
        try {
            JSONObject jSONObject = new JSONObject(f5139a);
            // 记录服务端返回的会话公钥
            String serverPublicKey = jSONObject.getString("publicKey");
            login.jpapplication.setServerPublicKey(serverPublicKey);
            i = jSONObject.getInt("is");
            try {
                message = jSONObject.getString("msg");
                login.kitiName = jSONObject.getString("ukn");
                title = jSONObject.getString("title");
                login.jpapplication.f4073g = jSONObject.getString("T1");
                login.jpapplication.f4074h = jSONObject.getString("M1");
            } catch (JSONException e1) {
                e1.printStackTrace();
                login.jpprogressBar.dismiss();
            }
        } catch (JSONException ignored) {
        }
        login.jpprogressBar.dismiss();
        switch (i) {
            case 0:
            case 4:
            case 5:
                login.loginSuccess(i, message, title);
                return;
            case 1:
            case 2:
                login.addDialog("提示", "确定", message);
                return;
            case 3:
                login.addDialog("提示", "确定", "网络错误!");
                return;
            default:
        }
    }

    @Override
    protected final void onPreExecute() {
        Login login = activity.get();
        login.jpprogressBar.setCancelable(true);
        login.jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        login.jpprogressBar.show();
    }
}
