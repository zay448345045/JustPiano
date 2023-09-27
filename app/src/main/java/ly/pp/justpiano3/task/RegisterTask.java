package ly.pp.justpiano3.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.LoginActivity;
import ly.pp.justpiano3.activity.Register;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class RegisterTask extends AsyncTask<Void, Integer, String> {
    private final WeakReference<Register> register;

    public RegisterTask(Register register) {
        this.register = new WeakReference<>(register);
    }

    @Override
    protected String doInBackground(Void... v) {
        String response = "";
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(OnlineUtil.server)
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
            Response execute = OkHttpUtil.client().newCall(request).execute();
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
    protected void onPostExecute(String str) {
        register.get().jpprogressBar.dismiss();
        switch (Integer.parseInt(str)) {
            case 0:
                Toast.makeText(register.get(), "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("name", register.get().account);
                intent.putExtra("password", register.get().password);
                intent.putExtra("no_auto", true);
                intent.setClass(register.get(), LoginActivity.class);
                register.get().startActivity(intent);
                register.get().finish();
                break;
            case 1:
                Toast.makeText(register.get(), "注册失败，账号已存在", Toast.LENGTH_SHORT).show();
                break;
            case -1:
            case 2:
                Toast.makeText(register.get(), "注册失败，填写的信息中含有系统无法识别的特殊字符", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(register.get(), "注册失败，网络错误", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(register.get(), "注册失败，昵称已存在", Toast.LENGTH_SHORT).show();
                break;
        }
        register.get().jpprogressBar.dismiss();
    }

    @Override
    protected void onPreExecute() {
        register.get().jpprogressBar.show();
    }
}
