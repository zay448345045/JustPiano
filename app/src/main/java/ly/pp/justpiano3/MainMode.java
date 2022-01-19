package ly.pp.justpiano3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainMode extends Activity implements OnClickListener {
    private boolean pressAgain;
    private JPProgressBar jpprogressBar;

    @Override
    public void onBackPressed() {
        jpprogressBar.dismiss();
        if (pressAgain) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            Toast.makeText(this, "再按一次返回键将退出极品钢琴.", Toast.LENGTH_SHORT).show();
        }
        pressAgain = true;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.local_game:
                intent.setClass(this, PlayModeSelect.class);
                startActivity(intent);
                finish();
                return;
            case R.id.online_game:
                intent.setClass(this, Login.class);
                intent.putExtra("result", "");
                startActivity(intent);
                finish();
                return;
            case R.id.sounds:
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setClass(this, SoundDownload.class);
                startActivity(intent);
                finish();
                return;
            case R.id.skins:
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setClass(this, SkinDownload.class);
                startActivity(intent);
                finish();
                return;
            case R.id.about_game:
                intent.setClass(this, About.class);
                startActivityForResult(intent, 0);
                return;
            case R.id.listen:
                intent = new Intent();
                intent.setClass(this, RecordFiles.class);
                startActivity(intent);
                finish();
                return;
            case R.id.piano_help:
                intent.setClass(this, PianoHelper.class);
                startActivity(intent);
                finish();
                return;
            case R.id.chat_files:
                intent = new Intent();
                intent.setClass(this, ChatFiles.class);
                startActivity(intent);
                finish();
                return;
            case R.id.settings:
                intent.setClass(this, SettingsMode.class);
                startActivity(intent);
                return;
            case R.id.feed_back:
                JPDialog jpdialog = new JPDialog(this);
                jpdialog.setTitle("反馈");
                jpdialog.setMessage("反馈问题请使用极品钢琴账号登录官网，打开官网问题反馈页面进行意见反馈");
                jpdialog.setFirstButton("访问官网", (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse("https://i.justpiano.fun"));
                    startActivity(intent1);
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.showDialog();
                return;
            default:
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        JPApplication jpApplication = (JPApplication) getApplication();
        jpApplication.loadSettings(0);
        pressAgain = false;
        setContentView(R.layout.main_mode);
        jpApplication.setBackGround(this, "ground", findViewById(R.id.layout));
        TextView f4202b = findViewById(R.id.local_game);
        f4202b.setOnClickListener(this);
        TextView f4203c = findViewById(R.id.online_game);
        f4203c.setOnClickListener(this);
        TextView f4204d = findViewById(R.id.settings);
        f4204d.setOnClickListener(this);
        TextView f4210j = findViewById(R.id.skins);
        f4210j.setOnClickListener(this);
        TextView f4212l = findViewById(R.id.sounds);
        f4212l.setOnClickListener(this);
        TextView f4205e = findViewById(R.id.about_game);
        f4205e.setOnClickListener(this);
        TextView f4206f = findViewById(R.id.chat_files);
        f4206f.setOnClickListener(this);
        TextView f4208h = findViewById(R.id.piano_help);
        f4208h.setOnClickListener(this);
        TextView f4211k = (Button) findViewById(R.id.listen);
        f4211k.setOnClickListener(this);
        f4211k = (Button) findViewById(R.id.feed_back);
        f4211k.setOnClickListener(this);
        jpprogressBar = new JPProgressBar(this);
        if (jpApplication.title != null && jpApplication.f4072f != null && !jpApplication.title.isEmpty() && !jpApplication.f4072f.isEmpty()) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setTitle(jpApplication.title);
            jpdialog.setMessage(jpApplication.f4072f);
            jpdialog.setFirstButton("确定", (dialog, which) -> {
                jpApplication.f4072f = "";
                jpApplication.title = "";
                dialog.dismiss();
            });
            jpdialog.showDialog();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.KILL_BACKGROUND_PROCESSES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.KILL_BACKGROUND_PROCESSES}, 1);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            }
        }
    }
}
