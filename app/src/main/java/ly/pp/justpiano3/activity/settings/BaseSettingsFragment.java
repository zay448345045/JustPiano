package ly.pp.justpiano3.activity.settings;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ly.pp.justpiano3.view.MidiDeviceListPreference;
import ly.pp.justpiano3.view.preference.SeekBarPreference;
import ly.pp.justpiano3.view.preference.SkinListPreference;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public class BaseSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        // nothing
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof SkinListPreference skinListPreference) {
            SkinListPreference.DialogFragmentCompat dialogFragmentCompat = skinListPreference.newDialog();
            dialogFragmentCompat.setTargetFragment(this, 0);
            dialogFragmentCompat.show(getParentFragmentManager(), "SkinListPreference");
        } else if (preference instanceof SoundListPreference soundListPreference) {
            SoundListPreference.DialogFragmentCompat dialogFragmentCompat = soundListPreference.newDialog();
            dialogFragmentCompat.setTargetFragment(this, 0);
            dialogFragmentCompat.show(getParentFragmentManager(), "SoundListPreference");
        } else if (preference instanceof MidiDeviceListPreference midiDeviceListPreference
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MidiDeviceListPreference.DialogFragmentCompat dialogFragmentCompat = midiDeviceListPreference.newDialog();
            dialogFragmentCompat.setTargetFragment(this, 0);
            dialogFragmentCompat.show(getParentFragmentManager(), "MidiDeviceListPreference");
        } else if (preference instanceof SeekBarPreference seekBarPreference) {
            SeekBarPreference.DialogFragmentCompat dialogFragmentCompat = seekBarPreference.newDialog();
            dialogFragmentCompat.setTargetFragment(this, 0);
            dialogFragmentCompat.show(getParentFragmentManager(), "SeekBarPreference");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}