package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.GameModeEnum;
import ly.pp.justpiano3.handler.android.OLMainModeHandler;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.task.SongSyncDialogTask;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;

import java.util.List;

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
                JPDialog jpdialog = new JPDialog(this);
                jpdialog.setTitle("提示");
                jpdialog.setMessage("官网访问方式：在浏览器中输入网址" + Consts.INSIDE_WEBSITE_URL + "\n" +
                        "官网功能包括最新极品钢琴软件下载、通知公告、曲谱上传、皮肤音源上传、族徽上传、问题反馈等");
                jpdialog.setFirstButton("访问官网", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("https://" + Consts.INSIDE_WEBSITE_URL));
                    startActivity(intent1);
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick()).showDialog();
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
        jpprogressBar = new JPProgressBar(this, jpapplication);
        GlobalSetting.INSTANCE.loadSettings(this, true);
        setContentView(R.layout.olmainmode);
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        JPApplication jPApplication = jpapplication;
        jPApplication.setGameMode(GameModeEnum.NORMAL.getCode());
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
        if (jpapplication.f4073g != null && jpapplication.f4074h != null && !jpapplication.f4073g.isEmpty() && !jpapplication.f4074h.isEmpty()) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle(jpapplication.f4073g);
            jpdialog.setMessage(jpapplication.f4074h);
            jpdialog.setFirstButton("确定", (dialog, which) -> {
                jpapplication.f4074h = "";
                jpapplication.f4073g = "";
                dialog.dismiss();
            }).showDialog();
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
        jpapplication.setBindService(jpapplication.bindService(new Intent(this, ConnectionService.class), jpapplication.getServiceConnection(), Context.BIND_AUTO_CREATE));
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
