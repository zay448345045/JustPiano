package ly.pp.justpiano3.activity.local;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.PreferenceManager;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.activity.online.LoginActivity;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.settings.SettingsActivity;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.task.FeedbackTask;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class MainMode extends BaseActivity implements OnClickListener {
    private boolean pressAgain;

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                ImageLoadUtil.setBackground(this, GlobalSetting.getBackgroundPic());
                fullScreenHandle();
            });

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
        int id = view.getId();
        if (id == R.id.local_game) {
            intent.setClass(this, PlayModeSelect.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.online_game) {
            intent.setClass(this, LoginActivity.class);
            intent.putExtra("result", "");
            startActivity(intent);
            finish();
        } else if (id == R.id.sounds) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setClass(this, SoundDownload.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.skins) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setClass(this, SkinDownload.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.about_game) {
            intent.setClass(this, InfoShowActivity.class);
            intent.putExtra("title", R.string.update_log);
            intent.putExtra("info", R.string.about);
            startActivity(intent);
        } else if (id == R.id.listen) {
            intent.setClass(this, RecordFiles.class);
            startActivity(intent);
        } else if (id == R.id.piano_help) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean("new_help", false).apply();
            intent.setClass(this, PianoHelper.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.chat_files) {
            intent.setClass(this, ChatFiles.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            intent.setClass(this, SettingsActivity.class);
            settingsLauncher.launch(intent);
        } else if (id == R.id.feed_back) {
            View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
            TextView textView = inflate.findViewById(R.id.text_1);
            if (OLBaseActivity.kitiName != null) {
                textView.setText(OLBaseActivity.kitiName);
            }
            TextView textViewTitle = inflate.findViewById(R.id.title_1);
            TextView messageView = inflate.findViewById(R.id.message_view);
            inflate.findViewById(R.id.message_view).setVisibility(View.VISIBLE);
            messageView.setText("如果您遇到应用崩溃、隐私信息泄密等问题，或想讨论需求&优化点、提出意见、举报用户等，" +
                    "可在此提交反馈，尽可能详细地说明情况，感谢您的支持和建议（若昵称填写准确，您可能会收到客服的私信回复）");
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
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalSetting.loadSettings(this, false);
        pressAgain = false;
        setContentView(R.layout.main_mode);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean newHelp = sharedPreferences.getBoolean("new_help", true);
        if (newHelp) {
            findViewById(R.id.new_help).setVisibility(View.VISIBLE);
        }
//        newVersionFirstTimeDialogShowHandle(sharedPreferences);
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
    }

    private void newVersionFirstTimeDialogShowHandle(SharedPreferences sharedPreferences) {
        int newVersionFirstTime = sharedPreferences.getInt("new_version_first_time", 0);
        if (newVersionFirstTime < BuildConfig.VERSION_CODE) {
            sharedPreferences.edit().putInt("new_version_first_time", BuildConfig.VERSION_CODE).apply();
            new JPDialogBuilder(this).setCheckMessageUrl(true)
                    .setTitle(BuildConfig.VERSION_NAME + "版本存储位置变更")
                    .setMessage("为响应APP合规要求，保护用户隐私，4.9版本已移除SD卡完全访问权限，原JustPiano目录默认不会再进行读取。" +
                            "用户可在设置中查看/设定存储位置及选择文件\n除非用户手动同意，否则APP技术上无法做到，也不会私自访问您的敏感信息")
                    .setFirstButton("确定", ((dialog, which) -> dialog.dismiss()))
                    .buildAndShowDialog();
        }
    }
}
