package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import ly.pp.justpiano3.activity.LoginActivity;
import ly.pp.justpiano3.activity.OLMainMode;

public final class LoginSuccessClick implements OnClickListener {
    private final LoginActivity loginActivity;

    public LoginSuccessClick(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent();
        intent.setClass(loginActivity, OLMainMode.class);
        Toast.makeText(loginActivity, "登陆成功!欢迎回来:" + loginActivity.kitiName + "!", Toast.LENGTH_SHORT).show();
        loginActivity.startActivity(intent);
        dialogInterface.dismiss();
        loginActivity.finish();
    }
}
