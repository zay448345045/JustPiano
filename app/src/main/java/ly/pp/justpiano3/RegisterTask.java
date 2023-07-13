package ly.pp.justpiano3;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import okhttp3.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public final class RegisterTask extends AsyncTask<Void, Integer, String> {
    private final WeakReference<Register> register;

    RegisterTask(Register register) {
        this.register = new WeakReference<>(register);
    }

    @Override
    protected String doInBackground(Void... v) {
        String response = "";
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(register.get().jpapplication.getServer())
                .port(8910)
                .addPathSegment("JustPianoServer")
                .addPathSegment("server")
                .addPathSegment("RegistServlet")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("username", register.get().account)
                .add("password", register.get().password)
                .add("userkitiname", register.get().kitiName)
                .add("sex", register.get().sex)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response execute = client.newCall(request).execute();
            if (execute.isSuccessful()) {
                int read = new DataInputStream(new ByteArrayInputStream(execute.body().bytes())).read();
                response = String.valueOf(read);
                return response;
            }
        } catch (IOException e) {
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
