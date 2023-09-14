package ly.pp.justpiano3.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.task.FeedbackTask;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

import java.io.File;

public class MainMode extends Activity implements OnClickListener {
    private boolean pressAgain;
    public JPApplication jpApplication;
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
                intent.setClass(this, LoginActivity.class);
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
                intent.setClass(this, AboutActivity.class);
                startActivityForResult(intent, 0);
                return;
            case R.id.listen:
                intent = new Intent();
                intent.setClass(this, RecordFiles.class);
                startActivity(intent);
                return;
            case R.id.piano_help:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().putBoolean("new_help", false).apply();
                intent.setClass(this, PianoHelper.class);
                startActivity(intent);
                finish();
                return;
            case R.id.chat_files:
                intent = new Intent();
                intent.setClass(this, ChatFiles.class);
                startActivity(intent);
                return;
            case R.id.settings:
                intent.setClass(this, SettingsMode.class);
                startActivityForResult(intent, JPApplication.SETTING_MODE_CODE);
                return;
            case R.id.feed_back:
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_1);
                if (jpApplication.getKitiName() != null) {
                    textView.setText(jpApplication.getKitiName());
                }
                TextView textViewTitle = inflate.findViewById(R.id.title_1);
                TextView messageView = inflate.findViewById(R.id.message_view);
                inflate.findViewById(R.id.message_view).setVisibility(View.VISIBLE);
                messageView.setText("问题将直接反馈至开发者，感谢您的支持和宝贵意见（若昵称填写准确，您可能会收到私信回复问题处理结果）");
                textViewTitle.setText("昵称:");
                TextView textView2 = inflate.findViewById(R.id.text_2);
                TextView textView2Title = inflate.findViewById(R.id.title_2);
                textView2Title.setText("内容:");
                JPDialogBuilder jpdialog = new JPDialogBuilder(this);
                jpdialog.setTitle("反馈");
                jpdialog.loadInflate(inflate);
                jpdialog.setFirstButton("提交", (dialog, which) -> {
                    String userName = textView.getText().toString();
                    String message = textView2.getText().toString();
                    if (StringUtil.isNullOrEmpty(userName) || StringUtil.isNullOrEmpty(message)) {
                        Toast.makeText(this, "昵称及内容不可为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.dismiss();
                    new FeedbackTask(this, userName, message).execute();
                });
                jpdialog.setSecondButton("取消", new DialogDismissClick());
                jpdialog.buildAndShowDialog();
                return;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JPApplication.SETTING_MODE_CODE) {
            ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jpApplication = (JPApplication) getApplication();
        GlobalSetting.INSTANCE.loadSettings(this, false);
        pressAgain = false;
        setContentView(R.layout.main_mode);
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean newHelp = sharedPreferences.getBoolean("new_help", true);
        if (newHelp) {
            findViewById(R.id.new_help).setVisibility(View.VISIBLE);
        }
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
        TextView f4211k = findViewById(R.id.listen);
        f4211k.setOnClickListener(this);
        f4211k = findViewById(R.id.feed_back);
        f4211k.setOnClickListener(this);
        jpprogressBar = new JPProgressBar(this);
        if (jpApplication.title != null && jpApplication.f4072f != null && !jpApplication.title.isEmpty() && !jpApplication.f4072f.isEmpty()) {
            JPDialogBuilder jpdialog = new JPDialogBuilder(this);
            jpdialog.setTitle(jpApplication.title);
            jpdialog.setMessage(jpApplication.f4072f);
            jpdialog.setFirstButton("确定", (dialog, which) -> {
                jpApplication.f4072f = "";
                jpApplication.title = "";
                dialog.dismiss();
            });
            jpdialog.buildAndShowDialog();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BIND_MIDI_DEVICE_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BIND_MIDI_DEVICE_SERVICE}, 1);
            }
        }
        try {
            File file1 = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Skins");
            File file2 = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds");
            File file3 = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records");
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (!file2.exists()) {
                file2.mkdirs();
            }
            if (!file3.exists()) {
                file3.mkdirs();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }
}
