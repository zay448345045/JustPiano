package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.task.RegisterTask;
import ly.pp.justpiano3.view.JPProgressBar;

public final class Register extends BaseActivity implements OnClickListener {
    public String sex;
    public String account = "";
    public String kitiName = "";
    public String password;
    public JPProgressBar jpprogressBar;
    private RadioButton sexF;
    private RadioButton sexM;
    private TextView accountTextView;
    private TextView password1TextView;
    private TextView password2TextView;
    private TextView kitiNameTextView;
    private Button backButton;
    private Button registerButton;

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("no_auto", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == backButton) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("no_auto", true);
            startActivity(intent);
            finish();
        } else if (view == registerButton) {
            account = accountTextView.getText().toString();
            password = password1TextView.getText().toString();
            String confirmPassword = password2TextView.getText().toString();
            kitiName = kitiNameTextView.getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(kitiName) || TextUtils.isEmpty(sex)) {
                Toast.makeText(this, "请将资料填写完整!", Toast.LENGTH_SHORT).show();
            } else if (account.length() < 3 || account.length() > 12) {
                Toast.makeText(this, "账号应在3到12个字符之间", Toast.LENGTH_SHORT).show();
            } else if (kitiName.length() < 2 || kitiName.length() > 8) {
                Toast.makeText(this, "昵称应在2到8个字符之间", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 5) {
                Toast.makeText(this, "密码应大于5个字符", Toast.LENGTH_SHORT).show();
            } else if (password.equals(confirmPassword)) {
                if (!TextUtils.isEmpty(account.replaceAll("[a-z]*[A-Z]*\\d*_*", ""))) {
                    Toast.makeText(this, "账号只能由字母，数字和下划线组成!", Toast.LENGTH_SHORT).show();
                } else if (Pattern.compile("['@{}/\"\t]").matcher(kitiName).find()) {
                    Toast.makeText(this, "昵称请不要使用['@{}/\"]这些字符", Toast.LENGTH_SHORT).show();
                } else {
                    new RegisterTask(this).execute();
                }
            } else {
                Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        accountTextView = findViewById(R.id.rg_name);
        kitiNameTextView = findViewById(R.id.rg_kiti);
        password1TextView = findViewById(R.id.rg_psw_1);
        password2TextView = findViewById(R.id.rg_psw_2);
        sexF = findViewById(R.id.f_radio);
        sexM = findViewById(R.id.m_radio);
        ((RadioGroup) (findViewById(R.id.rg_sex))).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == sexF.getId()) {
                sex = "f";
            } else if (checkedId == sexM.getId()) {
                sex = "m";
            }
        });
        backButton = findViewById(R.id.rg_back);
        backButton.setOnClickListener(this);
        registerButton = findViewById(R.id.rg_register);
        registerButton.setOnClickListener(this);
        jpprogressBar = new JPProgressBar(this);
    }
}
