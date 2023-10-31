package ly.pp.justpiano3.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class SettingsMode extends PreferenceActivity {

    public static final int SETTING_MODE_CODE = 122;

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
        String data = getIntent().getDataString();
        if (data == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        } else {
            switch (data) {
                case "settings_piano_play":
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new PianoPlaySettingsFragment()).commit();
                    break;
                case "settings_play_note":
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new PlayNoteSettingsFragment()).commit();
                    break;
                case "settings_waterfall":
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new WaterfallSettingsFragment()).commit();
                    break;
                case "settings_sound":
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new SoundSettingsFragment()).commit();
                    break;
                case "settings_keyboard":
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new KeyboardSettingsFragment()).commit();
                    break;
                case "settings_online_chat":
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new OnlineChatSettingsFragment()).commit();
                    break;
                default:
                    break;
            }
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
        }
    }

    public static class SoundSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_sound);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                Preference midiDevicePedalPreference = findPreference("midi_device_pedal_delay");
                if (midiDevicePedalPreference != null) {
                    midiDevicePedalPreference.setEnabled(true);
                }
            }
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

            final SwitchPreference switchPreference = (SwitchPreference) findPreference("chats_time_show");
            final ListPreference listPreference = (ListPreference) findPreference("chats_time_show_modes");

            // 根据SwitchPreference的初始状态来设置ListPreference的可用性
            listPreference.setEnabled(switchPreference.isChecked());

            // 为SwitchPreference设置监听器
            switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // newValue为SwitchPreference即将设置的新值
                boolean isEnabled = (Boolean) newValue;
                listPreference.setEnabled(isEnabled);
                return true; // 返回true以更新SwitchPreference的状态
            });
        }
    }
}
