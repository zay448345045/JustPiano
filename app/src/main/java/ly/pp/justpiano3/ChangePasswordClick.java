package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

final class ChangePasswordClick implements OnClickListener {
    private final UsersInfo userInfo;
    private final TextView f5998b;
    private final TextView f5999c;
    private final TextView f6000d;
    private final CheckBox f6001e;
    private final CheckBox f6002f;

    ChangePasswordClick(UsersInfo usersInfo, TextView textView, TextView textView2, TextView textView3, CheckBox checkBox, CheckBox checkBox2) {
        userInfo = usersInfo;
        f5998b = textView;
        f5999c = textView2;
        f6000d = textView3;
        f6001e = checkBox;
        f6002f = checkBox2;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f5998b.getText());
        String valueOf2 = String.valueOf(f5999c.getText());
        String valueOf3 = String.valueOf(f6000d.getText());
        if (valueOf.isEmpty() || valueOf2.isEmpty() || valueOf3.isEmpty()) {
            Toast.makeText(userInfo, "请填写完全部的密码框!", Toast.LENGTH_SHORT).show();
        } else if (valueOf2.length() <= 5 || valueOf3.length() <= 5 || valueOf2.length() > 20 || valueOf3.length() > 20) {
            Toast.makeText(userInfo, "新密码长度应在5-20个字符", Toast.LENGTH_SHORT).show();
        } else if (valueOf2.equals(valueOf3)) {
            userInfo.f5058b = f6001e.isChecked();
            userInfo.f5059c = f6002f.isChecked();
            new UsersInfoTask2(userInfo).execute("P", valueOf, valueOf3);
            dialogInterface.dismiss();
        } else {
            Toast.makeText(userInfo, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
        }
    }
}
