package ly.pp.justpiano3.activity.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.SoundEngineUtil;

public final class SoundSettingsFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_sound, rootKey);
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
                GlobalSetting.setForceEnableSustainPedal((Boolean) newValue);
                return true;
            });
        }
    }
}