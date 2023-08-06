package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import java.util.regex.Pattern;

public class Register extends Activity implements OnClickListener {
    String sex;
    String account;
    String kitiName;
    String password;
    JPProgressBar jpprogressBar;
    RadioButton sexF;
    RadioButton sexM;
    JPApplication jpapplication;
    private TextView accountTextView;
    private TextView password1TextView;
    private TextView password2TextView;
    private TextView kitiNameTextView;
    private Button backButton;
    private Button registerButton;

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        Intent intent = new Intent();
        intent.putExtra("no_auto", true);
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == backButton) {
            Intent intent = new Intent();
            intent.putExtra("no_auto", true);
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (view == registerButton) {
            account = accountTextView.getText().toString();
            password = password1TextView.getText().toString();
            String f4931e = password2TextView.getText().toString();
            kitiName = kitiNameTextView.getText().toString();
            if (account.isEmpty() || password.isEmpty() || f4931e.isEmpty() || kitiName.isEmpty() || sex.isEmpty()) {
                Toast.makeText(this, "请将资料填写完整!", Toast.LENGTH_SHORT).show();
            } else if (account.length() < 3 || account.length() > 12) {
                Toast.makeText(this, "账号应在3到12个字符之间", Toast.LENGTH_SHORT).show();
            } else if (kitiName.length() < 2 || kitiName.length() > 8) {
                Toast.makeText(this, "昵称应在2到8个字符之间", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 5) {
                Toast.makeText(this, "密码应大于5个字符", Toast.LENGTH_SHORT).show();
            } else if (password.equals(f4931e)) {
                if ((account.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0 ? 1 : 0) == 0) {
                    Toast.makeText(this, "账号只能由字母，数字和下划线组成!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((Pattern.compile("['@{}/\"\t]").matcher(kitiName).find() ? 1 : 0) != 0) {
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.register);
        jpapplication = (JPApplication) getApplication();
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        sex = "";
        account = "";
        kitiName = "";
        accountTextView = findViewById(R.id.rg_name);
        kitiNameTextView = findViewById(R.id.rg_kiti);
        password1TextView = findViewById(R.id.rg_psw_1);
        password2TextView = findViewById(R.id.rg_psw_2);
        sexF = findViewById(R.id.f_radio);
        sexM = findViewById(R.id.m_radio);
        RadioGroup f4937k = findViewById(R.id.rg_sex);
        f4937k.setOnCheckedChangeListener((group, checkedId) -> {
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
