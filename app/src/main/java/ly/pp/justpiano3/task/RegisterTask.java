package ly.pp.justpiano3.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.online.LoginActivity;
import ly.pp.justpiano3.activity.online.Register;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class RegisterTask extends AsyncTask<Void, Integer, String> {
    private final WeakReference<Register> register;

    public RegisterTask(Register register) {
        this.register = new WeakReference<>(register);
    }

    @Override
    protected String doInBackground(Void... v) {
        String result = OkHttpUtil.sendPostRequest("RegistServlet", new FormBody.Builder()
                .add("username", register.get().account)
                .add("password", register.get().password)
                .add("userkitiname", register.get().kitiName)
                .add("sex", register.get().sex)
                .build());
        return result.length() > 0 ? String.valueOf(result.charAt(0)) : "3";
    }

    @Override
    protected void onPostExecute(String str) {
        register.get().jpprogressBar.dismiss();
        switch (Integer.parseInt(str)) {
            case 0 -> {
                Toast.makeText(register.get(), "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(register.get(), LoginActivity.class);
                intent.putExtra("name", register.get().account);
                intent.putExtra("password", register.get().password);
                intent.putExtra("no_auto", true);
                register.get().startActivity(intent);
                register.get().finish();
            }
            case 1 ->
                    Toast.makeText(register.get(), "注册失败，账号已存在", Toast.LENGTH_SHORT).show();
            case -1, 2 ->
                    Toast.makeText(register.get(), "注册失败，填写的信息中含有系统无法识别的特殊字符", Toast.LENGTH_SHORT).show();
            case 3 ->
                    Toast.makeText(register.get(), "注册失败，网络错误", Toast.LENGTH_SHORT).show();
            case 4 ->
                    Toast.makeText(register.get(), "注册失败，昵称已存在", Toast.LENGTH_SHORT).show();
        }
        register.get().jpprogressBar.dismiss();
    }

    @Override
    protected void onPreExecute() {
        register.get().jpprogressBar.show();
    }
}
