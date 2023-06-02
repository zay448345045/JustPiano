package ly.pp.justpiano3;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class RegisterTask extends AsyncTask<Void, Integer, String> {
    private final WeakReference<Register> register;

    RegisterTask(Register register) {
        this.register = new WeakReference<>(register);
    }

    @Override
    protected String doInBackground(Void... v) {
        String response = "";
        HttpPost httpPost = new HttpPost("http://" + register.get().jpapplication.getServer() + ":8910/JustPianoServer/server/RegistServlet");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("username", register.get().account));
        arrayList.add(new BasicNameValuePair("password", register.get().password));
        arrayList.add(new BasicNameValuePair("userkitiname", register.get().kitiName));
        arrayList.add(new BasicNameValuePair("sex", register.get().sex));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            defaultHttpClient.getParams().setParameter("http.connection.timeout", 20000);
            defaultHttpClient.getParams().setParameter("http.socket.timeout", 20000);
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() == 200) {
                int read = new DataInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(execute.getEntity()))).read();
                response = String.valueOf(read);
                return response;
            }
        } catch (Exception e) {
            response = "3";
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected final void onPostExecute(String str) {
        register.get().jpprogressBar.dismiss();
        switch (Integer.parseInt(str)) {
            case 0:
                Toast.makeText(register.get(), "注册成功!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("name", register.get().account);
                intent.putExtra("password", register.get().password);
                intent.putExtra("no_auto", true);
                intent.setClass(register.get(), Login.class);
                register.get().startActivity(intent);
                register.get().finish();
                break;
            case 1:
                Toast.makeText(register.get(), "注册失败,账号已存在!", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(register.get(), "注册失败,填写的信息中含有系统无法识别的特殊字符!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(register.get(), "注册失败,网络错误!", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(register.get(), "注册失败,昵称已存在!", Toast.LENGTH_SHORT).show();
                break;
        }
        register.get().jpprogressBar.dismiss();
    }

    @Override
    protected final void onPreExecute() {
        register.get().jpprogressBar.setMessage("正在连接....");
        register.get().jpprogressBar.show();
    }

    @Override
    protected final void onProgressUpdate(Integer... numArr) {
        register.get().jpprogressBar.setMessage("正在连接...." + numArr[0].toString());
        register.get().jpprogressBar.setProgress(numArr[0]);
    }
}
