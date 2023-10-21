package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.handler.android.OLMainModeHandler;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.task.SongSyncDialogTask;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public class OLMainMode extends OLBaseActivity implements OnClickListener {
    final OLMainMode context = this;
    public JPApplication jpapplication;
    public OLMainModeHandler olMainModeHandler = new OLMainModeHandler(this);

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null) {
            jpprogressBar.dismiss();
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
        switch (view.getId()) {
            case R.id.ol_web_b:
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                jpDialogBuilder.setTitle("提示");
                jpDialogBuilder.setMessage("官网访问方式：在浏览器中输入网址" + OnlineUtil.INSIDE_WEBSITE_URL + "\n" +
                        "官网功能包括最新极品钢琴软件下载、通知公告、曲谱上传、皮肤音源上传、族徽上传、问题反馈等");
                jpDialogBuilder.setFirstButton("访问官网", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://" + OnlineUtil.INSIDE_WEBSITE_URL)));
                });
                jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
                return;
            case R.id.ol_songs_b:
                intent.setClass(this, OLSongsPage.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_playhall_b:
                if (jpapplication.getAccountName().isEmpty()) {
                    Toast.makeText(context, "您已经掉线请返回重新登陆!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String maxSongIdFromDatabase = getMaxSongIdFromDatabase();
                new SongSyncDialogTask(this, maxSongIdFromDatabase).execute();
                return;
            case R.id.ol_top_b:
                intent.setClass(this, OLTopUser.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_users_b:
                intent.putExtra("head", 0);
                intent.setClass(this, UsersInfo.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_chatblack_b:
                intent.setClass(this, UserListPage.class);
                startActivity(intent);
                finish();
                return;
            case R.id.ol_finduser_b:
                intent.putExtra("head", 6);
                intent.setClass(this, SearchSongs.class);
                startActivity(intent);
                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jpapplication = (JPApplication) getApplication();
        jpprogressBar = new JPProgressBar(this);
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.ol_main_mode);
        ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
        GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
        Button topButton = findViewById(R.id.ol_top_b);
        topButton.setOnClickListener(this);
        Button userButton = findViewById(R.id.ol_users_b);
        userButton.setOnClickListener(this);
        Button hallButton = findViewById(R.id.ol_playhall_b);
        hallButton.setOnClickListener(this);
        Button songButton = findViewById(R.id.ol_songs_b);
        songButton.setOnClickListener(this);
        Button webButton = findViewById(R.id.ol_web_b);
        webButton.setOnClickListener(this);
        webButton.setVisibility(View.VISIBLE);
        Button chatBlackButton = findViewById(R.id.ol_chatblack_b);
        chatBlackButton.setOnClickListener(this);
        Button findUserButton = findViewById(R.id.ol_finduser_b);
        findUserButton.setOnClickListener(this);
        try {
            if (jpapplication.getConnectionService() != null) {
                jpapplication.getConnectionService().outLine();
            }
            if (jpapplication.isBindService()) {
                jpapplication.unbindService(jpapplication.getServiceConnection());
                jpapplication.setBindService(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPStack.push(this);
        if (jpapplication.loginResultTitle != null && jpapplication.loginResultMessage != null
                && !jpapplication.loginResultTitle.isEmpty() && !jpapplication.loginResultMessage.isEmpty()) {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle(jpapplication.loginResultTitle);
            jpDialogBuilder.setMessage(jpapplication.loginResultMessage);
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                jpapplication.loginResultMessage = "";
                jpapplication.loginResultTitle = "";
                dialog.dismiss();
            }).buildAndShowDialog();
        }
    }

    @Override
    protected void onDestroy() {
        JPStack.pop(this);
        super.onDestroy();
    }

    public void loginOnline() {
        jpprogressBar.show();
        if (jpapplication.isBindService()) {
            try {
                jpapplication.unbindService(jpapplication.getServiceConnection());
                jpapplication.setBindService(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        jpapplication.setBindService(jpapplication.bindService(new Intent(this, ConnectionService.class),
                jpapplication.getServiceConnection(), Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT));
    }

    public static String getMaxSongIdFromDatabase() {
        List<Song> allSongs = JPApplication.getSongDatabase().songDao().getAllSongs();
        int maxSongId = 0;
        for (Song song : allSongs) {
            if (song.getFilePath().length() > 8 && song.getFilePath().charAt(7) == '/') {
                maxSongId = Math.max(maxSongId, Integer.parseInt(song.getFilePath().substring(9, 15)));
            }
        }
        return String.valueOf(maxSongId);
    }
}
