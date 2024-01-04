package ly.pp.justpiano3.activity.settings;

import static android.content.Context.POWER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SettingsUtil;
import ly.pp.justpiano3.utils.WakeLockUtil;
import ly.pp.justpiano3.utils.WindowUtil;
import ly.pp.justpiano3.view.MidiDeviceListPreference;
import ly.pp.justpiano3.view.preference.SkinListPreference;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public class SettingsFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        Preference versionPreference = findPreference("app_version");
        if (versionPreference != null) {
            versionPreference.setSummary(BuildConfig.VERSION_NAME + '-' + BuildConfig.BUILD_TIME + '-' + BuildConfig.BUILD_TYPE);
        }
        Preference skinPreference = findPreference("skin_select");
        if (skinPreference instanceof SkinListPreference) {
            skinPreference.setSummary(GlobalSetting.getSkin());
        }
        Preference soundPreference = findPreference("sound_select");
        if (soundPreference instanceof SoundListPreference) {
            soundPreference.setSummary(GlobalSetting.getSound());
        }
        // 检测是否支持midi功能，支持midi功能时，midi设备相关的设置才允许点击
        if (MidiDeviceUtil.isSupportMidiDevice(getActivity())) {
            Preference midiDevicePreference = findPreference("midi_device_list");
            if (midiDevicePreference instanceof MidiDeviceListPreference) {
                midiDevicePreference.setSummary("启用/禁用MIDI设备");
                midiDevicePreference.setEnabled(true);
            }
        }
        // 背景图设置项初始化
        SettingsUtil.registerFilePickerPreference(this, "background_pic", false,
                "默认背景图", GlobalSetting.getBackgroundPic(), uriInfo -> {
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
                if (getActivity() != null) {
                    if ((Boolean) newValue) {
                        WindowUtil.fullScreenHandle(getActivity().getWindow());
                    } else {
                        WindowUtil.exitFullScreenHandle(getActivity().getWindow());
                    }
                }
                return true;
            });
        }
        // 皮肤、音源选择器初始化
        SettingsUtil.registerFilePickerPreference(this, "skin_select", false,
                "默认皮肤", GlobalSetting.getSkin(), uriInfo -> {
                    if (uriInfo.getDisplayName() == null || !uriInfo.getDisplayName().endsWith(".ps")) {
                        Toast.makeText(getActivity(), "请选择合法的ps格式皮肤文件", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                });
        SettingsUtil.registerFilePickerPreference(this, "sound_select", false,
                "默认音源", GlobalSetting.getSound(), uriInfo -> {
                    if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".ss")
                            && !uriInfo.getDisplayName().endsWith(".sf2"))) {
                        Toast.makeText(getActivity(), "请选择合法的ss或sf2格式音源文件", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                });
        SettingsUtil.registerFilePickerPreference(this, "records_save_path", true,
                "默认存储位置(SD卡/Android/data/ly.pp.justpiano3/files/Records)",
                GlobalSetting.getRecordsSavePath(), uriInfo -> true);
        // 保持后台网络连接点击事件处理
        Preference appInfoPreference = findPreference("background_keep_alive");
        if (appInfoPreference != null) {
            appInfoPreference.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                    getActivity().startActivity(intent);
                }
                return true;
            });
        }
        // 电池优化白名单检查
        batteryKeepAliveHandle();
        // 唤醒锁处理
        Preference wakeLockPreference = findPreference("wake_lock");
        if (wakeLockPreference instanceof SwitchPreference wakeLockSwitchPreference) {
            wakeLockSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!(Boolean) newValue) {
                    WakeLockUtil.acquireWakeLock(getActivity(), "JustPiano:JPWakeLock");
                } else {
                    WakeLockUtil.releaseWakeLock();
                }
                return true;
            });
        }
    }

    @SuppressLint("BatteryLife")
    private void batteryKeepAliveHandle() {
        Preference batteryPreference = findPreference("battery_keep_alive");
        if (batteryPreference instanceof SwitchPreference batteryKeepAlivePreference && getActivity() != null) {
            PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && powerManager != null) {
                batteryKeepAlivePreference.setEnabled(true);
                batteryKeepAlivePreference.setChecked(powerManager.isIgnoringBatteryOptimizations(getActivity().getPackageName()));
                batteryKeepAlivePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    Intent intent = new Intent();
                    if ((Boolean) newValue) {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    } else {
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    }
                    getActivity().startActivity(intent);
                    return true;
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        batteryKeepAliveHandle();
    }
}
