package ly.pp.justpiano3.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.task.FeedbackTask;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.WindowUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

public class MainMode extends BaseActivity implements OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 120;
    private boolean pressAgain;

    @Override
    public void onBackPressed() {
        if (pressAgain) {
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
                intent.setClass(this, ChatFiles.class);
                startActivity(intent);
                return;
            case R.id.settings:
                intent.setClass(this, SettingsMode.class);
                startActivityForResult(intent, SettingsMode.SETTING_MODE_CODE);
                return;
            case R.id.feed_back:
                View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.text_1);
                if (JPApplication.kitiName != null) {
                    textView.setText(JPApplication.kitiName);
                }
                TextView textViewTitle = inflate.findViewById(R.id.title_1);
                TextView messageView = inflate.findViewById(R.id.message_view);
                inflate.findViewById(R.id.message_view).setVisibility(View.VISIBLE);
                messageView.setText("问题将直接反馈至开发者，感谢您的支持和宝贵意见（若昵称填写准确，您可能会收到私信回复问题处理结果）");
                textViewTitle.setText("昵称:");
                TextView textView2 = inflate.findViewById(R.id.text_2);
                TextView textView2Title = inflate.findViewById(R.id.title_2);
                textView2Title.setText("内容:");
                JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                jpDialogBuilder.setTitle("反馈");
                jpDialogBuilder.loadInflate(inflate);
                jpDialogBuilder.setFirstButton("提交", (dialog, which) -> {
                    String userName = textView.getText().toString();
                    String message = textView2.getText().toString();
                    if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(message)) {
                        Toast.makeText(this, "昵称及内容不可为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.dismiss();
                    new FeedbackTask(this, userName, DeviceUtil.getAppAndDeviceInfo() + '\n' + message).execute();
                });
                jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                jpDialogBuilder.buildAndShowDialog();
                return;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsMode.SETTING_MODE_CODE) {
            ImageLoadUtil.setBackground(this);
            if (GlobalSetting.INSTANCE.getAllFullScreenShow()) {
                WindowUtil.fullScreenHandle(getWindow());
            } else {
                WindowUtil.exitFullScreenHandle(getWindow());
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    createDirectories();
                } else {
                    Toast.makeText(this, "您未授予文件访问权限，无法使用切换皮肤音源、录音、聊天记录存储等功能", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalSetting.INSTANCE.loadSettings(this, false);
        pressAgain = false;
        setContentView(R.layout.main_mode);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean newHelp = sharedPreferences.getBoolean("new_help", true);
        if (newHelp) {
            findViewById(R.id.new_help).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.local_game).setOnClickListener(this);
        findViewById(R.id.online_game).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        findViewById(R.id.skins).setOnClickListener(this);
        findViewById(R.id.sounds).setOnClickListener(this);
        findViewById(R.id.about_game).setOnClickListener(this);
        findViewById(R.id.chat_files).setOnClickListener(this);
        findViewById(R.id.piano_help).setOnClickListener(this);
        findViewById(R.id.listen).setOnClickListener(this);
        findViewById(R.id.feed_back).setOnClickListener(this);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                } catch (Exception e) {
                    Toast.makeText(this, "引导授予文件访问权限失败，请在手动授予app的文件存储权限", Toast.LENGTH_SHORT).show();
                }
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
    }

    private void createDirectories() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
