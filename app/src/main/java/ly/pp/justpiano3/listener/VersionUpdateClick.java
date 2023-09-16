package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.LoginActivity;
import ly.pp.justpiano3.thread.ThreadPoolUtil;

public final class VersionUpdateClick implements OnClickListener {

    private final LoginActivity loginActivity;

    /**
     * 需要下载的版本号
     */
    private final String version;

    public VersionUpdateClick(String version, LoginActivity loginActivity) {
        this.version = version;
        this.loginActivity = loginActivity;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        ThreadPoolUtil.execute(()-> loginActivity.downloadApk(version));
    }
}
