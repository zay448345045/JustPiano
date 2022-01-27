package ly.pp.justpiano3;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class LoginTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Login> activity;
    private String f5139a = "";
    private String message = "";
    private String title = "";

    LoginTask(Login login) {
        this.activity = new WeakReference<>(login);
    }

    @Override
    protected String doInBackground(String... objects) {
        String f5140b = "";
        Login login = activity.get();
        login.accountX = login.accountTextView.getText().toString();
        login.password = login.passwordTextView.getText().toString();
        if (!login.accountX.isEmpty() && !login.password.isEmpty()) {
            String ip = login.jpapplication.getServer();
            HttpPost httpPost = new HttpPost("http://" + ip + ":8910/JustPianoServer/server/LoginServlet");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("versionName", login.versionStr));
            arrayList.add(new BasicNameValuePair("packageNames", login.packageName));
            arrayList.add(new BasicNameValuePair("versionCode", String.valueOf(login.versionNum)));
            arrayList.add(new BasicNameValuePair("username", login.accountX));
            arrayList.add(new BasicNameValuePair("password", login.password));
            arrayList.add(new BasicNameValuePair("local", login.jpapplication.getVersion()));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
                DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                defaultHttpClient.getParams().setParameter("http.connection.timeout", 20000);
                defaultHttpClient.getParams().setParameter("http.socket.timeout", 20000);
                HttpResponse execute = defaultHttpClient.execute(httpPost);
                if (execute.getStatusLine().getStatusCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getEntity().getContent(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        f5139a = line;
                    }
                    execute.getEntity().consumeContent();
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
    protected final void onPostExecute(String str) {
        Login login = activity.get();
        int i = 3;
        try {
            JSONObject jSONObject = new JSONObject(f5139a);
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
