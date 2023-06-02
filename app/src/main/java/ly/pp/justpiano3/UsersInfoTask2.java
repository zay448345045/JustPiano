package ly.pp.justpiano3;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;


import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class UsersInfoTask2 extends AsyncTask<String, Void, String> {
    private final WeakReference<UsersInfo> usersInfo;
    private String f6010b = "";

    UsersInfoTask2(UsersInfo usersInfo) {
        this.usersInfo = new WeakReference<>(usersInfo);
    }

    @Override
    protected final void onPostExecute(String str) {
        switch (str) {
            case "0":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "资料修改成功!", Toast.LENGTH_LONG).show();
                break;
            case "4":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "密码中含有无法识别的特殊符号!", Toast.LENGTH_SHORT).show();
                break;
            case "5":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "原密码有错!请再试一遍", Toast.LENGTH_SHORT).show();
                break;
            case "6":
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "密码修改成功!", Toast.LENGTH_LONG).show();
                Editor edit = JPApplication.sharedpreferences.edit();
                if (usersInfo.get().f5059c) {
                    edit.putString("name", usersInfo.get().jpapplication.getAccountName());
                    edit.putString("password", f6010b);
                    edit.putBoolean("chec_psw", usersInfo.get().f5059c);
                    edit.putBoolean("chec_autologin", usersInfo.get().f5058b);
                } else {
                    edit.putString("password", "");
                    edit.putBoolean("chec_psw", usersInfo.get().f5059c);
                    edit.putBoolean("chec_autologin", usersInfo.get().f5059c);
                }
                edit.apply();
                break;
            default:
                usersInfo.get().jpprogressBar.cancel();
                Toast.makeText(usersInfo.get(), "连接有错!请再试一遍", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected String doInBackground(String... strArr) {
        String str = "";
        HttpPost httpPost = new HttpPost("http://" + usersInfo.get().jpapplication.getServer() + ":8910/JustPianoServer/server/ChangeUserMsg");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("head", "0"));
        arrayList.add(new BasicNameValuePair("version", usersInfo.get().jpapplication.getVersion()));
        arrayList.add(new BasicNameValuePair("keywords", strArr[0]));
        arrayList.add(new BasicNameValuePair("userName", usersInfo.get().jpapplication.getAccountName()));
        if (strArr[1] == null || strArr[2] == null) {
            arrayList.add(new BasicNameValuePair("msg", usersInfo.get().pSign));
            arrayList.add(new BasicNameValuePair("age", String.valueOf(usersInfo.get().age)));
        } else {
            arrayList.add(new BasicNameValuePair("oldPass", strArr[1]));
            arrayList.add(new BasicNameValuePair("newPass", strArr[2]));
            f6010b = strArr[2];
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        defaultHttpClient.getParams().setParameter("http.connection.timeout", 10000);
        defaultHttpClient.getParams().setParameter("http.socket.timeout", 10000);
        try {
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() == 200) {
                str = EntityUtils.toString(execute.getEntity());
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return str;
    }

    @Override
    protected final void onPreExecute() {
        usersInfo.get().jpprogressBar.setCancelable(true);
        usersInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        usersInfo.get().jpprogressBar.show();
    }
}
