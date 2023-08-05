package ly.pp.justpiano3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import androidx.core.content.FileProvider;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.listener.VersionUpdateClick;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends BaseActivity implements OnClickListener {
    public JPApplication jpapplication;
    public String password;
    String kitiName = "";
    String accountX = "";
    TextView accountTextView;
    TextView passwordTextView;
    JPProgressBar jpprogressBar;
    SharedPreferences sharedPreferences;
    private JPDialog jpDialog = null;
    private LayoutInflater layoutInflater;
    private CheckBox rememAccount;
    private CheckBox rememPassword;
    private CheckBox autoLogin;
    private String account;

    public final void loginSuccess(int i, String message, String title) {
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        String string = sharedPreferences.getString("accountList", "");
        try {
            Editor edit = sharedPreferences.edit();
            JSONObject jSONObject;
            if (string.isEmpty()) {
                jSONObject = new JSONObject();
            } else {
                jSONObject = new JSONObject(string);
            }
            if (rememAccount.isChecked() && rememPassword.isChecked()) {
                jSONObject.put(accountX, password);
            } else {
                jSONObject.remove(accountX);
            }
            edit.putString("accountList", jSONObject.toString());
            edit.putBoolean("remem_account", rememAccount.isChecked());
            edit.putBoolean("remem_password", rememPassword.isChecked());
            edit.putBoolean("auto_login", autoLogin.isChecked());
            edit.putString("current_account", accountX);
            edit.putString("current_password", password);
            edit.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jpapplication.setKitiName(kitiName);
        jpapplication.setAccountName(accountX);
        jpapplication.setPassword(password);
        switch (i) {
            case 0:
                Toast.makeText(this, "登陆成功!欢迎回来:" + kitiName + "!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
                return;
            case 4:
                jpDialog.setTitle(title).setMessage(message).setFirstButton("知道了", new LoginSuccessClick(this)).showDialog();
                return;
            case 5:
                jpDialog.setTitle(title).setMessage(message).setFirstButton("确定", new DialogDismissClick()).showDialog();
                return;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        if (rememAccount.isChecked() && rememPassword.isChecked()) {
            Editor edit = sharedPreferences.edit();
            edit.putBoolean("remem_account", true);
            edit.putBoolean("remem_password", true);
            edit.putBoolean("auto_login", autoLogin.isChecked());
            edit.putString("current_account", accountTextView.getText().toString());
            edit.putString("current_password", passwordTextView.getText().toString());
            edit.apply();
        }
        Intent intent = new Intent();
        intent.setClass(this, MainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ol_login:
                account = accountTextView.getText().toString();
                password = passwordTextView.getText().toString();
                if (account.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LoginTask(this).execute();
                return;
            case R.id.ol_register:
                Intent intent = new Intent();
                intent.setClass(this, Register.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_change_account:
                String string = sharedPreferences.getString("accountList", "");
                List<String> arrayList = new ArrayList<>();
                try {
                    if (!StringUtil.isNullOrEmpty(string)) {
                        JSONObject jSONObject = new JSONObject(string);
                        Iterator<String> keys = jSONObject.keys();
                        while (keys.hasNext()) {
                            string = keys.next();
                            arrayList.add(string);
                        }
                        View inflate = getLayoutInflater().inflate(R.layout.account_list, findViewById(R.id.dialog));
                        ListView listView = inflate.findViewById(R.id.account_list);
                        JPDialog.JDialog b = new JPDialog(this).setTitle("切换账号").loadInflate(inflate)
                                .setFirstButton("取消", new DialogDismissClick()).createJDialog();
                        listView.setAdapter(new ChangeAccountAdapter(arrayList, layoutInflater, this, b, jSONObject));
                        b.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            case R.id.ol_change_server:
                Editor edit = sharedPreferences.edit();
                edit.putBoolean("remem_account", true);
                edit.putBoolean("remem_password", true);
                edit.putBoolean("auto_login", autoLogin.isChecked());
                edit.putString("current_account", accountTextView.getText().toString());
                edit.putString("current_password", passwordTextView.getText().toString());
                edit.apply();
                intent = new Intent();
                intent.setClass(this, ChangeServer.class);
                startActivity(intent);
                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this);
        jpapplication.setServer(s.getString("ip", JPApplication.ONLINE_SERVER_URL));
        sharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        setPackageAndVersion();
        JPStack.clear();
        layoutInflater = LayoutInflater.from(this);
        setContentView(R.layout.login);
        Bundle extras = getIntent().getExtras();
        boolean noAuto = extras == null || extras.getBoolean("no_auto");
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        Button f4189o = findViewById(R.id.ol_login);
        f4189o.setOnClickListener(this);
        Button f4192r = findViewById(R.id.ol_change_server);
        f4192r.setOnClickListener(this);
        Button f4190p = findViewById(R.id.ol_register);
        f4190p.setOnClickListener(this);
        Button f4191q = findViewById(R.id.ol_change_account);
        f4191q.setOnClickListener(this);
        accountTextView = findViewById(R.id.username);
        passwordTextView = findViewById(R.id.password);
        rememAccount = findViewById(R.id.chec_name);
        rememAccount.setChecked(sharedPreferences.getBoolean("remem_account", false));
        autoLogin = findViewById(R.id.chec_autologin);
        autoLogin.setChecked(sharedPreferences.getBoolean("auto_login", false));
        if (noAuto) {
            autoLogin.setChecked(false);
        }
        rememPassword = findViewById(R.id.chec_psw);
        rememPassword.setChecked(sharedPreferences.getBoolean("remem_password", false));
        if (rememAccount.isChecked()) {
            accountTextView.setText(sharedPreferences.getString("current_account", ""));
        }
        if (rememPassword.isChecked()) {
            passwordTextView.setText(sharedPreferences.getString("current_password", ""));
        }
        jpprogressBar = new JPProgressBar(this);
        jpDialog = new JPDialog(this);
        account = extras == null ? null : extras.getString("name");
        password = extras == null ? null : extras.getString("password");
        if (account != null && !account.isEmpty() && password != null && !password.isEmpty()) {
            accountTextView.setText(account);
            passwordTextView.setText(password);
            rememAccount.setChecked(true);
            rememPassword.setChecked(true);
        }
        if (autoLogin.isChecked()) {
            account = accountTextView.getText().toString();
            password = passwordTextView.getText().toString();
            if (account.isEmpty() || password.isEmpty()) {
                addDialog("提示", "确定", "用户名或密码不能为空");
                return;
            }
            new LoginTask(this).execute();
        }
    }

    final void addVersionUpdateDialog(String str3, String newVersion) {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle("版本更新");
        jpdialog.setMessage(str3);
        jpdialog.setFirstButton("下载更新", new VersionUpdateClick(newVersion, this));
        jpdialog.setSecondButton("取消", new DialogDismissClick());
        jpdialog.showDialog();
    }


    private String getApkUrlByVersion(String version) {
        return "https://" + JPApplication.RESOURCE_WEBSITE_URL + "/res/" + getApkFileName(version);
    }

    private String getApkFileName(String version) {
        return "justpiano_" + version.replace(".", "") + ".apk";
    }

    public void downloadApk(String version) {
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/" + getApkFileName(version));
        if (file.exists()) {
            file.delete();
        }
        runOnUiThread(() -> {
            jpprogressBar.setCancelable(false);
            jpprogressBar.setText(version + "版本准备开始下载，请稍候");
            jpprogressBar.show();
            Toast.makeText(LoginActivity.this, version + "版本开始下载", Toast.LENGTH_SHORT).show();
        });

        OkHttpClient client = new OkHttpClient();
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(getApkUrlByVersion(version)).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> {
                    jpprogressBar.dismiss();
                    Toast.makeText(LoginActivity.this, version + "版本下载失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) { // 请求成功
                long length = response.body().contentLength();
                // 下面从返回的输入流中读取字节数据并保存为本地文件
                try (InputStream is = response.body().byteStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buf = new byte[100 * 1024];
                    int sum = 0, len;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / length * 100);
                        String detail = String.format("下载进度：%.2fM / %.2fM（%d%%）", sum / 1048576f, length / 1048576f, progress);
                        // 回到主线程操纵界面
                        runOnUiThread(() -> {
                            jpprogressBar.setText(detail);
                        });
                    }
                    installApk(LoginActivity.this, file);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(() -> {
                        jpprogressBar.dismiss();
                    });
                }
            }
        });
    }

    /**
     * 启动安装apk
     *
     * @param context
     * @param appFile
     */
    private void installApk(Context context, File appFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。
            // 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 确保authority 与AndroidManifest.xml中android:authorities="包名.fileProvider"所有字符一致
            fileUri = FileProvider.getUriForFile(context, getPackageName() + ".fileProvider", appFile);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
