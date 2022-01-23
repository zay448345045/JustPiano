package ly.pp.justpiano3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Login extends BaseActivity implements OnClickListener {
    public JPApplication jpapplication;
    public String password;
    String kitiName = "";
    String accountX = "";
    String sex = "";
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

    public final void loginSuccess(int i, String str, String str2) {
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
                jpDialog.setTitle(str2).setMessage(str).setFirstButton("知道了", new LoginSuccessClick(this)).showDialog();
                return;
            case 5:
                jpDialog.setTitle(str2).setMessage(str).setFirstButton("确定", new DialogDismissClick()).showDialog();
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
                    if (!string.isEmpty()) {
                        JSONObject jSONObject = new JSONObject(string);
                        Iterator keys = jSONObject.keys();
                        while (keys.hasNext()) {
                            string = (String) keys.next();
                            arrayList.add(string);
                        }
                        View inflate = getLayoutInflater().inflate(R.layout.account_list, findViewById(R.id.dialog));
                        ListView listView = inflate.findViewById(R.id.account_list);
                        JPDialog.JDialog b = new JPDialog(this).setTitle("切换账号").loadInflate(inflate).setFirstButton("取消", new DialogDismissClick()).createJDialog();
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
        jpapplication.setServer(s.getString("ip", "120.25.100.169"));
        sharedPreferences = getSharedPreferences("account_list", MODE_PRIVATE);
        setPackageAndVersion();
        JPStack.create();
        JPStack.clear();
        layoutInflater = LayoutInflater.from(this);
        setContentView(R.layout.login);
        Bundle extras = getIntent().getExtras();
        boolean noAuto = extras.getBoolean("no_auto");
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
        account = extras.getString("name");
        password = extras.getString("password");
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
}
