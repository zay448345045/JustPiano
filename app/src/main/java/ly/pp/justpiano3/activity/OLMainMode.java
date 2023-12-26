package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.handler.android.OLMainModeHandler;
import ly.pp.justpiano3.task.SongSyncDialogTask;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class OLMainMode extends OLBaseActivity implements OnClickListener {
    public OLMainModeHandler olMainModeHandler = new OLMainModeHandler(this);

    @Override
    public void onBackPressed() {
        if (jpProgressBar != null) {
            jpProgressBar.dismiss();
        }
        Intent intent = new Intent();
        intent.putExtra("no_auto", true);
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int id = view.getId();
        if (id == R.id.ol_web_b) {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setCheckMessageUrl(true).setTitle("提示");
            jpDialogBuilder.setMessage("官网访问方式：在浏览器中输入网址https://" + OnlineUtil.INSIDE_WEBSITE_URL + "\n" +
                    "官网功能包括最新极品钢琴软件下载、通知公告、曲谱上传、皮肤音源上传、族徽上传、问题反馈等");
            jpDialogBuilder.setFirstButton("访问官网", (dialog, which) -> {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + OnlineUtil.INSIDE_WEBSITE_URL)));
            });
            jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        } else if (id == R.id.ol_songs_b) {
            intent.setClass(this, OLSongsPage.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_playhall_b) {
            if (OLBaseActivity.getAccountName().isEmpty()) {
                Toast.makeText(this, "您已经掉线请返回重新登陆!", Toast.LENGTH_SHORT).show();
                return;
            }
            new SongSyncDialogTask(this).execute();
        } else if (id == R.id.ol_top_b) {
            intent.setClass(this, OLTopUser.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_users_b) {
            intent.putExtra("head", 0);
            intent.setClass(this, UsersInfo.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_chatblack_b) {
            intent.setClass(this, UserListPage.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.ol_finduser_b) {
            intent.putExtra("head", 6);
            intent.setClass(this, SearchSongs.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.ol_main_mode);
        GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
        findViewById(R.id.ol_top_b).setOnClickListener(this);
        findViewById(R.id.ol_users_b).setOnClickListener(this);
        findViewById(R.id.ol_playhall_b).setOnClickListener(this);
        findViewById(R.id.ol_songs_b).setOnClickListener(this);
        findViewById(R.id.ol_web_b).setOnClickListener(this);
        findViewById(R.id.ol_chatblack_b).setOnClickListener(this);
        findViewById(R.id.ol_finduser_b).setOnClickListener(this);
        OnlineUtil.outlineConnectionService((JPApplication) getApplication());
        if (OLBaseActivity.loginResultTitle != null && OLBaseActivity.loginResultMessage != null
                && !OLBaseActivity.loginResultTitle.isEmpty() && !OLBaseActivity.loginResultMessage.isEmpty()) {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle(OLBaseActivity.loginResultTitle);
            jpDialogBuilder.setMessage(OLBaseActivity.loginResultMessage);
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                OLBaseActivity.loginResultMessage = "";
                OLBaseActivity.loginResultTitle = "";
                dialog.dismiss();
            }).buildAndShowDialog();
        }
    }
}
