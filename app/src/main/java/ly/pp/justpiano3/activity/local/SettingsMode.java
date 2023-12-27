package ly.pp.justpiano3.activity.local;

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
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.core.util.Predicate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.task.SkinListPreferenceTask;
import ly.pp.justpiano3.task.SoundListPreferenceTask;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.WindowUtil;
import ly.pp.justpiano3.view.MidiDeviceListPreference;
import ly.pp.justpiano3.view.preference.FilePickerPreference;
import ly.pp.justpiano3.view.preference.SkinListPreference;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public final class SettingsMode extends PreferenceActivity implements MidiDeviceUtil.MidiDeviceListener {

    public static final int SETTING_MODE_CODE = 122;

    private static final Map<String, PreferenceFragment> preferenceFragmentMap = new HashMap<>();
    private static final Map<String, Pair<Preference, Predicate<FileUtil.UriInfo>>> filePickerPreferenceMap = new HashMap<>();

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
            Preference skinPreference = findPreference("skin_select");
            if (skinPreference != null) {
                skinPreference.setSummary(GlobalSetting.INSTANCE.getSkin());
            }
            Preference soundPreference = findPreference("sound_select");
            if (soundPreference != null) {
                soundPreference.setSummary(GlobalSetting.INSTANCE.getSound());
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
            registerFilePickerPreference(this, "background_pic", false,
                    "默认背景图", GlobalSetting.INSTANCE.getBackgroundPic(), uriInfo -> {
                        if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".jpg")
                                && !uriInfo.getDisplayName().endsWith(".jpeg") && !uriInfo.getDisplayName().endsWith(".png"))) {
                            Toast.makeText(getActivity(), "请选择合法的jpg或png格式图片文件", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    });
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
            // 皮肤、音源选择器初始化
            registerFilePickerPreference(this, "skin_select", false,
                    "默认皮肤", GlobalSetting.INSTANCE.getSkin(), uriInfo -> {
                        if (uriInfo.getDisplayName() == null || !uriInfo.getDisplayName().endsWith(".ps")) {
                            Toast.makeText(getActivity(), "请选择合法的ps格式皮肤文件", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    });
            registerFilePickerPreference(this, "sound_select", false,
                    "默认音源", GlobalSetting.INSTANCE.getSound(), uriInfo -> {
                        if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".ss")
                                && !uriInfo.getDisplayName().endsWith(".sf2"))) {
                            Toast.makeText(getActivity(), "请选择合法的ss或sf2格式音源文件", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    });
            registerFilePickerPreference(this, "records_save_path", true,
                    "默认存储位置(SD卡/Android/data/ly.pp.justpiano3/files/Records)",
                    GlobalSetting.INSTANCE.getRecordsSavePath(), uriInfo -> true);
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
            registerFilePickerPreference(this, "waterfall_background_pic", false,
                    "默认背景图", GlobalSetting.INSTANCE.getWaterfallBackgroundPic(), uriInfo -> {
                        if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".jpg")
                                && !uriInfo.getDisplayName().endsWith(".jpeg") && !uriInfo.getDisplayName().endsWith(".png"))) {
                            Toast.makeText(getActivity(), "请选择合法的jpg或png格式图片文件", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    });
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
            registerFilePickerPreference(this, "chats_sound_file", false,
                    "默认音效", GlobalSetting.INSTANCE.getChatsSoundFile(), uriInfo -> {
                        if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".wav") && !uriInfo.getDisplayName().endsWith(".mp3"))) {
                            Toast.makeText(getActivity(), "请选择合法的wav或mp3格式音频文件", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    });
            registerFilePickerPreference(this, "chats_save_path", true,
                    "默认存储位置(SD卡/Android/data/ly.pp.justpiano3/files/Chats)",
                    GlobalSetting.INSTANCE.getChatsSavePath(), uriInfo -> true);
        }
    }

    public static class EasterEggFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_easter_egg);
        }
    }

    /**
     * 注册filePickerPreference行为
     */
    private static void registerFilePickerPreference(PreferenceFragment preferenceFragment, String key, boolean folderPicker,
                                                     String defaultSummary, String uri, Predicate<FileUtil.UriInfo> predicate) {
        Preference preference = preferenceFragment.findPreference(key);
        if (preference != null) {
            if (TextUtils.isEmpty(uri)) {
                preference.setSummary(defaultSummary);
            } else {
                FileUtil.UriInfo uriInfo = folderPicker ? FileUtil.INSTANCE.getFolderUriInfo(preferenceFragment.getActivity(), Uri.parse(uri))
                        : FileUtil.INSTANCE.getUriInfo(preferenceFragment.getActivity(), Uri.parse(uri));
                preference.setSummary(TextUtils.isEmpty(uriInfo.getDisplayName()) ? defaultSummary : uriInfo.getDisplayName());
            }
            if (preference instanceof FilePickerPreference filePickerPreference) {
                filePickerPreference.setActivity(preferenceFragment.getActivity());
                filePickerPreference.setFolderPicker(folderPicker);
                filePickerPreference.setDefaultButtonClickListener(view -> filePickerPreference.persist(defaultSummary, ""));
            }
            filePickerPreferenceMap.put(preference.getKey(), Pair.create(preference, predicate));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == FilePickerUtil.PICK_FOLDER_REQUEST_CODE || requestCode == FilePickerUtil.PICK_FILE_REQUEST_CODE)
                && resultCode == Activity.RESULT_OK) {
            FileUtil.UriInfo uriInfo = requestCode == FilePickerUtil.PICK_FOLDER_REQUEST_CODE
                    ? FileUtil.INSTANCE.getFolderUriInfo(this, data.getData())
                    : FileUtil.INSTANCE.getUriInfo(this, data.getData());
            Pair<Preference, Predicate<FileUtil.UriInfo>> value = filePickerPreferenceMap.get(FilePickerUtil.extra);
            if (value == null || uriInfo.getUri() == null || uriInfo.getDisplayName() == null || !value.second.test(uriInfo)) {
                return;
            }
            try {
                getContentResolver().takePersistableUriPermission(uriInfo.getUri(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "获取文件访问权限出错", Toast.LENGTH_SHORT).show();
                return;
            }
            if (value.first instanceof FilePickerPreference) {
                ((FilePickerPreference) value.first).persist(uriInfo.getDisplayName(), uriInfo.getUri().toString());
            } else if (value.first instanceof SkinListPreference) {
                ((SkinListPreference) value.first).skinKey = uriInfo.getUri().toString();
                ((SkinListPreference) value.first).skinFile = uriInfo.getUri();
                new SkinListPreferenceTask(((SkinListPreference) value.first)).execute(uriInfo.getUri().toString());
            } else if (value.first instanceof SoundListPreference) {
                ((SoundListPreference) value.first).soundKey = uriInfo.getUri().toString();
                new SoundListPreferenceTask(((SoundListPreference) value.first)).execute(uriInfo.getUri().toString());
            }
            if (Objects.equals(value.first.getKey(), "background_pic")) {
                GlobalSetting.INSTANCE.setBackgroundPic(uriInfo.getUri().toString());
                ImageLoadUtil.setBackground(this);
            }
        } else if (requestCode == SkinDownload.SKIN_DOWNLOAD_REQUEST_CODE) {
            Pair<Preference, Predicate<FileUtil.UriInfo>> value = filePickerPreferenceMap.get("skin_select");
            if (value != null && value.first instanceof SkinListPreference) {
                ((SkinListPreference) value.first).skinKey = ((SkinListPreference) value.first).getPersistedString();
                ((SkinListPreference) value.first).closeDialog();
            }
            ImageLoadUtil.setBackground(this);
        } else if (requestCode == SoundDownload.SOUND_DOWNLOAD_REQUEST_CODE) {
            Pair<Preference, Predicate<FileUtil.UriInfo>> value = filePickerPreferenceMap.get("sound_select");
            if (value != null && value.first instanceof SoundListPreference) {
                ((SoundListPreference) value.first).soundKey = ((SoundListPreference) value.first).getPersistedString();
                ((SoundListPreference) value.first).closeDialog();
            }
            ImageLoadUtil.setBackground(this);
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
