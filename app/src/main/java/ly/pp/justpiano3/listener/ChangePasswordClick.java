package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import ly.pp.justpiano3.task.UsersInfoTask2;
import ly.pp.justpiano3.activity.UsersInfo;

public final class ChangePasswordClick implements OnClickListener {
    private final UsersInfo userInfo;
    private final TextView originalPasswordTextView;
    private final TextView newPasswordTextView;
    private final TextView confirmPasswordTextView;
    private final CheckBox autoLoginCheckBox;
    private final CheckBox remNewPasswordCheckBox;

    public ChangePasswordClick(UsersInfo userInfo, TextView originalPasswordTextView, TextView newPasswordTextView,
                        TextView confirmPasswordTextView, CheckBox autoLoginCheckBox, CheckBox remNewPasswordCheckBox) {
        this.userInfo = userInfo;
        this.originalPasswordTextView = originalPasswordTextView;
        this.newPasswordTextView = newPasswordTextView;
        this.confirmPasswordTextView = confirmPasswordTextView;
        this.autoLoginCheckBox = autoLoginCheckBox;
        this.remNewPasswordCheckBox = remNewPasswordCheckBox;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String originalPassword = String.valueOf(originalPasswordTextView.getText());
        String newPassword = String.valueOf(newPasswordTextView.getText());
        String confirmPassword = String.valueOf(confirmPasswordTextView.getText());
        if (originalPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(userInfo, "请填写完全部的密码框!", Toast.LENGTH_SHORT).show();
        } else if (newPassword.length() <= 5 || confirmPassword.length() <= 5 || newPassword.length() > 20 || confirmPassword.length() > 20) {
            Toast.makeText(userInfo, "新密码长度应在5-20个字符", Toast.LENGTH_SHORT).show();
        } else if (newPassword.equals(confirmPassword)) {
            userInfo.autoLogin = autoLoginCheckBox.isChecked();
            userInfo.rememberNewPassword = remNewPasswordCheckBox.isChecked();
            new UsersInfoTask2(userInfo).execute("P", originalPassword, confirmPassword);
            dialogInterface.dismiss();
        } else {
            Toast.makeText(userInfo, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
        }
    }
}
