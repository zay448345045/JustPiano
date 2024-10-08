package ly.pp.justpiano3.activity.online;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.activity.local.MainMode;
import ly.pp.justpiano3.adapter.ChangeAccountAdapter;
import ly.pp.justpiano3.task.LoginTask;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import okhttp3.Request;
import okhttp3.Response;

public final class LoginActivity extends BaseActivity implements OnClickListener {
    public String password;
    public String kitiName = "";
    public String accountX = "";
    public TextView accountTextView;
    public TextView passwordTextView;
    public JPProgressBar jpProgressBar;
    public SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private CheckBox changeServerCheckBox;
    private CheckBox rememAccount;
    private CheckBox rememPassword;
    private CheckBox autoLogin;
    private String account;
    private EditText debugIpEditText;

    public void loginSuccess(int i, String message, String title, String newVersion) {
        if (!TextUtils.isEmpty(newVersion)) {
            addVersionUpdateDialog(i, message, newVersion, false);
        } else {
            doLoginSuccess(i, message, title);
        }
    }

    private void doLoginSuccess(int i, String message, String title) {
        Intent intent = new Intent(this, OLMainMode.class);
        String accountList = sharedPreferences.getString("accountList", "");
        try {
            Editor edit = sharedPreferences.edit();
            JSONObject jSONObject;
            if (accountList.isEmpty()) {
                jSONObject = new JSONObject();
            } else {
                jSONObject = new JSONObject(accountList);
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
            if (BuildConfig.DEBUG && debugIpEditText != null) {
                edit.putString("debug_server_ip", debugIpEditText.getText().toString());
            }
            edit.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OLBaseActivity.setKitiName(kitiName);
        OLBaseActivity.setAccountName(accountX);
        OLBaseActivity.setPassword(password);
        switch (i) {
            case 0 -> {
                if (Objects.equals(OnlineUtil.server, OnlineUtil.ONLINE_SERVER_URL)) {
                    Toast.makeText(this, "登录成功!欢迎回来:" + kitiName + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "登录成功!欢迎回来:" + kitiName + "!" + "当前登录:测试服", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                finish();
            }
            case 4 -> new JPDialogBuilder(this).setWidth(400).setTitle(title).setMessage(message)
                    .setCheckMessageUrl(true).setFirstButton("知道了", (dialog, i1) -> {
                dialog.dismiss();
                if (Objects.equals(OnlineUtil.server, OnlineUtil.ONLINE_SERVER_URL)) {
                    Toast.makeText(this, "登录成功!欢迎回来:" + kitiName + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "登录成功!欢迎回来:" + kitiName + "!" + "当前登录:测试服", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                finish();
            }).buildAndShowDialog();
            case 5 -> new JPDialogBuilder(this).setTitle(title).setMessage(message).setCheckMessageUrl(true)
                    .setFirstButton("确定", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
            default -> {
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (jpProgressBar.isShowing()) {
            jpProgressBar.dismiss();
        }
        if (rememAccount.isChecked() && rememPassword.isChecked()) {
            Editor edit = sharedPreferences.edit();
            edit.putBoolean("remem_account", true);
            edit.putBoolean("remem_password", true);
            edit.putBoolean("auto_login", autoLogin.isChecked());
            edit.putString("current_account", accountTextView.getText().toString());
            edit.putString("current_password", passwordTextView.getText().toString());
            edit.apply();
        }
        startActivity(new Intent(this, MainMode.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ol_login) {
            account = accountTextView.getText().toString();
            password = passwordTextView.getText().toString();
            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            OnlineUtil.server = changeServerCheckBox.isChecked() ? OnlineUtil.TEST_ONLINE_SERVER_URL : OnlineUtil.ONLINE_SERVER_URL;
            if (BuildConfig.DEBUG && debugIpEditText != null && !debugIpEditText.getText().toString().isEmpty()) {
                OnlineUtil.server = debugIpEditText.getText().toString();
            }
            new LoginTask(this).execute();
        } else if (id == R.id.ol_register) {
            startActivity(new Intent(this, Register.class));
            finish();
        } else if (id == R.id.ol_change_account) {
            String string = sharedPreferences.getString("accountList", "");
            List<String> arrayList = new ArrayList<>();
            try {
                if (!TextUtils.isEmpty(string)) {
                    JSONObject jSONObject = new JSONObject(string);
                    Iterator<String> keys = jSONObject.keys();
                    while (keys.hasNext()) {
                        string = keys.next();
                        arrayList.add(string);
                    }
                    View inflate = getLayoutInflater().inflate(R.layout.account_list, findViewById(R.id.dialog));
                    ListView listView = inflate.findViewById(R.id.account_list);
                    JPDialogBuilder.JPDialog b = new JPDialogBuilder(this).setTitle("切换账号").loadInflate(inflate)
                            .setFirstButton("取消", (dialog, which) -> dialog.dismiss()).createJPDialog();
                    listView.setAdapter(new ChangeAccountAdapter(arrayList, layoutInflater, this, b, jSONObject));
                    b.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.clear();
        sharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        layoutInflater = LayoutInflater.from(this);
        setContentView(R.layout.login);
        findViewById(R.id.ol_login).setOnClickListener(this);
        changeServerCheckBox = findViewById(R.id.ol_change_server);
        changeServerCheckBox.setChecked(OnlineUtil.server.equals(OnlineUtil.TEST_ONLINE_SERVER_URL));
        findViewById(R.id.ol_register).setOnClickListener(this);
        findViewById(R.id.ol_change_account).setOnClickListener(this);
        ((TextView) (findViewById(R.id.app_version))).setText(BuildConfig.VERSION_NAME);
        accountTextView = findViewById(R.id.username);
        passwordTextView = findViewById(R.id.password);
        rememAccount = findViewById(R.id.chec_name);
        rememAccount.setChecked(sharedPreferences.getBoolean("remem_account", false));
        autoLogin = findViewById(R.id.chec_autologin);
        autoLogin.setChecked(sharedPreferences.getBoolean("auto_login", false));
        Bundle extras = getIntent().getExtras();
        boolean noAuto = extras == null || extras.getBoolean("no_auto");
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
        jpProgressBar = new JPProgressBar(this);
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
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("用户名或密码不能为空");
                jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.buildAndShowDialog();
                return;
            }
            new LoginTask(this).execute();
        }
        if (BuildConfig.DEBUG) {
            debugIpEditText = findViewById(R.id.ol_debug_server_ip);
            debugIpEditText.setVisibility(View.VISIBLE);
            debugIpEditText.setText(sharedPreferences.getString("debug_server_ip", ""));
        }
    }

    public void addVersionUpdateDialog(int i, String updateMessage, String newVersion, boolean forceUpdate) {
        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
        jpDialogBuilder.setTitle("版本更新");
        jpDialogBuilder.setMessage(updateMessage);
        jpDialogBuilder.setFirstButton("下载更新", (dialog, which) -> {
            dialog.dismiss();
            ThreadPoolUtil.execute(() -> downloadApk(newVersion));
        });
        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> {
            dialog.dismiss();
            if (!forceUpdate) {
                doLoginSuccess(i, "", "");
            }
        });
        jpDialogBuilder.buildAndShowDialog();
    }

    private String getApkUrlByVersion(String version) {
        return "https://" + OnlineUtil.INSIDE_WEBSITE_URL + "/res/" + getApkFileName(version);
    }

    private String getApkFileName(String version) {
        return "justpiano_" + version.replace(".", "") + ".apk";
    }

    public void downloadApk(String version) {
        File file = new File(getFilesDir(), getApkFileName(version));
        if (file.exists()) {
            file.delete();
        }
        runOnUiThread(() -> {
            jpProgressBar.setCancelable(false);
            jpProgressBar.setText(version + "版本准备开始下载，请稍候");
            jpProgressBar.show();
            Toast.makeText(this, version + "版本开始下载", Toast.LENGTH_SHORT).show();
        });
        Request request = new Request.Builder().url(getApkUrlByVersion(version)).build();
        try (Response response = OkHttpUtil.client().newCall(request).execute()) {
            if (response.isSuccessful()) {
                getApkResponseAndInstall(file, response);
            } else {
                apkDownloadFallHandle(version);
            }
        } catch (Exception e) {
            e.printStackTrace();
            apkDownloadFallHandle(version);
        }
    }

    private void apkDownloadFallHandle(String version) {
        // 回到主线程操纵界面
        runOnUiThread(() -> {
            if (jpProgressBar.isShowing()) {
                jpProgressBar.dismiss();
            }
            Toast.makeText(this, version + "版本下载失败", Toast.LENGTH_SHORT).show();
        });
    }

    private void getApkResponseAndInstall(File file, Response response) {
        long start = System.currentTimeMillis();
        long length = response.body().contentLength();
        // 下面从返回的输入流中读取字节数据并保存为本地文件
        try (InputStream inputStream = response.body().byteStream();
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buf = new byte[100 * 1024];
            int sum = 0, len;
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / length * 100);
                String detail = String.format(Locale.getDefault(), "下载进度：%.2fM / %.2fM（%d%%）", sum / 1048576f, length / 1048576f, progress);
                // 回到主线程操纵界面
                if (System.currentTimeMillis() - start > 200) {
                    start = System.currentTimeMillis();
                    runOnUiThread(() -> jpProgressBar.setText(detail));
                }
            }
            installApk(this, file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runOnUiThread(() -> {
                if (jpProgressBar.isShowing()) {
                    jpProgressBar.dismiss();
                }
            });
        }
    }

    /**
     * 启动安装apk
     */
    private void installApk(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 区别于FLAG_GRANT_READ_URI_PERMISSION和FLAG_GRANT_WRITE_URI_PERMISSION
            // URI权限会持久存在即使重启，直到明确的用revokeUriPermission(Uri, int)撤销。
            // 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, getPackageName() + ".fileProvider", apkFile);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
