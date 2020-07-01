package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;

final class LoginSuccessClick implements OnClickListener {
    private final Login login;

    LoginSuccessClick(Login login) {
        this.login = login;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent();
        intent.setClass(login, OLMainMode.class);
        Toast.makeText(login, "登陆成功!欢迎回来:" + login.kitiName + "!", Toast.LENGTH_SHORT).show();
        login.startActivity(intent);
        dialogInterface.dismiss();
        login.finish();
    }
}
