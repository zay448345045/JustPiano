package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.midi.MidiDeviceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.WindowUtil;
import ly.pp.justpiano3.view.MidiDeviceListPreference;
import ly.pp.justpiano3.view.preference.FilePickerPreference;

public class SettingsMode extends PreferenceActivity implements MidiDeviceUtil.MidiDeviceListener {

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
        preferenceFragmentMap.put("settings_easter_egg", new EasterEggFragment());
    }

    @Override
    public void onBackPressed() {
        GlobalSetting.INSTANCE.loadSettings(this, false);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoadUtil.setBackground(this);
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.color.black);
        PreferenceFragment preferenceFragment = preferenceFragmentMap.get(getIntent().getDataString());
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                preferenceFragment == null ? new SettingsFragment() : preferenceFragment).commit();
        if (GlobalSetting.INSTANCE.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(getWindow());
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
            // 背景图设置项初始化
            Preference backgroundPicPreference = findPreference("background_pic");
            if (backgroundPicPreference != null) {
                if (GlobalSetting.INSTANCE.getBackgroundPic().isEmpty()) {
                    backgroundPicPreference.setSummary("默认背景图");
                } else {
                    FileUtil.UriInfo uriInfo = FileUtil.INSTANCE.getUriInfo(
                            getActivity(), Uri.parse(GlobalSetting.INSTANCE.getBackgroundPic()));
                    backgroundPicPreference.setSummary(
                            uriInfo.getFileName() == null ? "默认背景图" : uriInfo.getFileName());
                }
                FilePickerPreference filePickerPreference = (FilePickerPreference) (backgroundPicPreference);
                filePickerPreference.setActivity(getActivity());
                filePickerPreference.setDefaultButtonClickListener(view -> {
                    GlobalSetting.INSTANCE.setBackgroundPic("");
                    ImageLoadUtil.setBackground(SettingsFragment.this.getActivity());
                    filePickerPreference.persist("默认背景图", "");
                });
                filePickerPreferenceMap.put(backgroundPicPreference.getKey(), filePickerPreference);
            }
            // 全面屏设置项开关监听
            Preference allFullScreenShowPreference = findPreference("all_full_screen_show");
            if (allFullScreenShowPreference != null) {
                allFullScreenShowPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    if ((Boolean) newValue) {
                        WindowUtil.fullScreenHandle((SettingsFragment.this.getActivity()).getWindow());
                    } else {
                        WindowUtil.exitFullScreenHandle((SettingsFragment.this.getActivity()).getWindow());
                    }
                    return true;
                });
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
                if (GlobalSetting.INSTANCE.getWaterfallBackgroundPic().isEmpty()) {
                    waterfallBackgroundPicPreference.setSummary("默认背景图");
                } else {
                    FileUtil.UriInfo uriInfo = FileUtil.INSTANCE.getUriInfo(
                            getActivity(), Uri.parse(GlobalSetting.INSTANCE.getWaterfallBackgroundPic()));
                    waterfallBackgroundPicPreference.setSummary(
                            uriInfo.getFileName() == null ? "默认背景图" : uriInfo.getFileName());
                }
                FilePickerPreference filePickerPreference = (FilePickerPreference) (waterfallBackgroundPicPreference);
                filePickerPreference.setActivity(getActivity());
                filePickerPreference.setDefaultButtonClickListener(view -> filePickerPreference.persist("默认背景图", ""));
                filePickerPreferenceMap.put(waterfallBackgroundPicPreference.getKey(), filePickerPreference);
            }
        }
    }

    public static class SoundSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_sound);
            Preference soundDelayPreference = findPreference("sound_delay");
            if (soundDelayPreference != null) {
                soundDelayPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    SoundEngineUtil.setDelayValue((Integer) newValue);
                    return true;
                });
            }
            Preference soundReverbPreference = findPreference("sound_reverb");
            if (soundReverbPreference != null) {
                soundReverbPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    SoundEngineUtil.setReverbValue((Integer) newValue);
                    return true;
                });
            }
            Preference forceEnableSustainPedalPreference = findPreference("force_enable_sustain_pedal");
            if (forceEnableSustainPedalPreference != null) {
                forceEnableSustainPedalPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    GlobalSetting.INSTANCE.setForceEnableSustainPedal((Boolean) newValue);
                    return true;
                });
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
            Preference chatSoundFilePreference = findPreference("chats_sound_file");
            if (chatSoundFilePreference != null) {
                if (GlobalSetting.INSTANCE.getChatsSoundFile().isEmpty()) {
                    chatSoundFilePreference.setSummary("默认音效");
                } else {
                    FileUtil.UriInfo uriInfo = FileUtil.INSTANCE.getUriInfo(
                            getActivity(), Uri.parse(GlobalSetting.INSTANCE.getChatsSoundFile()));
                    chatSoundFilePreference.setSummary(
                            uriInfo.getFileName() == null ? "默认音效" : uriInfo.getFileName());
                }
                FilePickerPreference filePickerPreference = (FilePickerPreference) (chatSoundFilePreference);
                filePickerPreference.setActivity(getActivity());
                filePickerPreference.setDefaultButtonClickListener(view -> filePickerPreference.persist("默认音效", ""));
                filePickerPreferenceMap.put(chatSoundFilePreference.getKey(), filePickerPreference);
            }
        }
    }

    public static class EasterEggFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_easter_egg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerUtil.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<FileUtil.UriInfo> uriInfoList = FilePickerUtil.getUriFromIntent(this, data);
            if (uriInfoList.size() != 1) {
                return;
            }
            FileUtil.UriInfo uriInfo = uriInfoList.get(0);
            FilePickerPreference filePickerPreference = filePickerPreferenceMap.get(FilePickerUtil.extra);
            if (filePickerPreference == null || uriInfo == null || uriInfo.getFileName() == null) {
                return;
            }
            // 具体的文件选择项的文件格式校验，校验通过后执行存储
            switch (filePickerPreference.getKey()) {
                case "waterfall_background_pic":
                case "background_pic":
                    if (uriInfo.getFileName() == null || (!uriInfo.getFileName().endsWith(".jpg")
                            && !uriInfo.getFileName().endsWith(".jpeg") && !uriInfo.getFileName().endsWith(".png"))) {
                        Toast.makeText(this, "请选择合法的jpg或png格式文件", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case "chats_sound_file":
                    if (uriInfo.getFileName() == null || (!uriInfo.getFileName().endsWith(".wav") && !uriInfo.getFileName().endsWith(".mp3"))) {
                        Toast.makeText(this, "请选择合法的wav或mp3格式文件", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                default:
                    break;
            }
            // 取得持久化 URI 权限
            if (uriInfo.getUri() != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uriInfo.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "读取文件出错，请确保授予了设备的文件访问权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                filePickerPreference.persist(uriInfo.getFileName(), uriInfo.getUri().toString());
                if (Objects.equals(filePickerPreference.getKey(), "background_pic")) {
                    GlobalSetting.INSTANCE.setBackgroundPic(uriInfo.getUri().toString());
                    ImageLoadUtil.setBackground(this);
                }
            }
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiConnect(MidiDeviceInfo midiDeviceInfo) {
        Preference midiDevicePreference = findPreference("midi_device_list");
        if (midiDevicePreference != null) {
            ((MidiDeviceListPreference) midiDevicePreference).midiDeviceListRefresh();
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiDisconnect(MidiDeviceInfo midiDeviceInfo) {
        Preference midiDevicePreference = findPreference("midi_device_list");
        if (midiDevicePreference != null) {
            ((MidiDeviceListPreference) midiDevicePreference).midiDeviceListRefresh();
        }
    }
}
