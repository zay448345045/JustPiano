package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.WindowUtil;
import ly.pp.justpiano3.view.preference.FilePickerPreference;

public class SettingsMode extends PreferenceActivity {

    public static final int SETTING_MODE_CODE = 122;

    private static final Map<String, PreferenceFragment> preferenceFragmentMap = new HashMap<>();
    private static final Map<String, FilePickerPreference> filePickerPreferenceMap = new HashMap<>();

    static {
        preferenceFragmentMap.put("settings_piano_play", new PianoPlaySettingsFragment());
        preferenceFragmentMap.put("settings_play_note", new PlayNoteSettingsFragment());
        preferenceFragmentMap.put("settings_waterfall", new WaterfallSettingsFragment());
        preferenceFragmentMap.put("settings_sound", new SoundSettingsFragment());
        preferenceFragmentMap.put("settings_keyboard", new KeyboardSettingsFragment());
        preferenceFragmentMap.put("settings_online_chat", new OnlineChatSettingsFragment());
    }

    @Override
    public void onBackPressed() {
        GlobalSetting.INSTANCE.loadSettings(this, false);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoadUtil.setBackground(this, "ground", getWindow());
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.color.black);
        PreferenceFragment preferenceFragment = preferenceFragmentMap.get(getIntent().getDataString());
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                preferenceFragment == null ? new SettingsFragment() : preferenceFragment).commit();
        if (GlobalSetting.INSTANCE.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(getWindow());
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            Preference versionPreference = findPreference("app_version");
            if (versionPreference != null) {
                versionPreference.setSummary(BuildConfig.VERSION_NAME + '-' + BuildConfig.BUILD_TIME + '-' + BuildConfig.BUILD_TYPE);
            }
            Preference skinPreference = findPreference("skin_list");
            if (skinPreference != null) {
                skinPreference.setSummary("当前皮肤：" + GlobalSetting.INSTANCE.getSkinName());
            }
            Preference soundPreference = findPreference("sound_list");
            if (soundPreference != null) {
                soundPreference.setSummary("当前音源：" + GlobalSetting.INSTANCE.getSoundName());
            }
            // 检测是否支持midi功能，支持midi功能时，midi设备相关的设置才允许点击
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                Preference midiDevicePreference = findPreference("midi_device_list");
                if (midiDevicePreference != null) {
                    midiDevicePreference.setSummary("启用/禁用MIDI设备");
                    midiDevicePreference.setEnabled(true);
                }
            }
        }
    }

    public static class PianoPlaySettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_piano_play);
        }
    }

    public static class PlayNoteSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_play_note);
        }
    }

    public static class WaterfallSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_waterfall);
            Preference waterfallBackgroundPicPreference = findPreference("waterfall_background_pic");
            if (waterfallBackgroundPicPreference != null) {
                waterfallBackgroundPicPreference.setSummary(
                        GlobalSetting.INSTANCE.getWaterfallBackgroundPic().isEmpty()
                                ? "默认背景图" : GlobalSetting.INSTANCE.getWaterfallBackgroundPic());
                FilePickerPreference filePickerPreference = (FilePickerPreference) (waterfallBackgroundPicPreference);
                filePickerPreference.setActivity(getActivity());
                filePickerPreference.setDefaultButtonClickListener(view -> {
                    filePickerPreference.persistFilePath("");
                    filePickerPreference.setSummary("默认背景图");
                });
                filePickerPreferenceMap.put(waterfallBackgroundPicPreference.getKey(), filePickerPreference);
            }
        }
    }

    public static class SoundSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_sound);
        }
    }

    public static class KeyboardSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_keyboard);
        }
    }

    public static class OnlineChatSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_online_chat);
            Preference chatSoundFilePreference = findPreference("chats_sound_file");
            if (chatSoundFilePreference != null) {
                chatSoundFilePreference.setSummary(
                        GlobalSetting.INSTANCE.getChatsSoundFile().isEmpty()
                                ? "默认音效" : GlobalSetting.INSTANCE.getChatsSoundFile());
                FilePickerPreference filePickerPreference = (FilePickerPreference) (chatSoundFilePreference);
                filePickerPreference.setActivity(getActivity());
                filePickerPreference.setDefaultButtonClickListener(view -> {
                    filePickerPreference.persistFilePath("");
                    filePickerPreference.setSummary("默认音效");
                });
                filePickerPreferenceMap.put(chatSoundFilePreference.getKey(), filePickerPreference);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerUtil.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<File> fileList = FilePickerUtil.getFilesFromIntent(this, data);
            if (fileList.size() != 1) {
                return;
            }
            File file = fileList.get(0);
            FilePickerPreference filePickerPreference = filePickerPreferenceMap.get(FilePickerUtil.extra);
            if (filePickerPreference == null || file == null) {
                return;
            }
            // 具体的文件选择项的文件格式校验，校验通过后执行存储
            switch (filePickerPreference.getKey()) {
                case "waterfall_background_pic":
                    if (!file.exists() || (!file.getName().endsWith(".jpg")
                            && !file.getName().endsWith(".jpeg") && !file.getName().endsWith(".png"))) {
                        Toast.makeText(this, "请选择合法的jpg或png格式文件", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case "chats_sound_file":
                    if (!file.exists() || (!file.getName().endsWith(".wav") && !file.getName().endsWith(".mp3"))) {
                        Toast.makeText(this, "请选择合法的wav或mp3格式文件", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                default:
                    break;
            }
            filePickerPreference.persistFilePath(file.getAbsolutePath());
        }
    }
}
