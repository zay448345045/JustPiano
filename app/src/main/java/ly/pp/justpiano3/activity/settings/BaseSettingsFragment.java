package ly.pp.justpiano3.activity.settings;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

import ly.pp.justpiano3.R;
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
        PreferenceDialogFragmentCompat dialogFragmentCompat = null;
        if (preference instanceof SkinListPreference skinListPreference) {
            dialogFragmentCompat = skinListPreference.newDialog();
        } else if (preference instanceof SoundListPreference soundListPreference) {
            dialogFragmentCompat = soundListPreference.newDialog();
        } else if (preference instanceof MidiDeviceListPreference midiDeviceListPreference
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialogFragmentCompat = midiDeviceListPreference.newDialog();
        } else if (preference instanceof SeekBarPreference seekBarPreference) {
            dialogFragmentCompat = seekBarPreference.newDialog();
        }
        if (dialogFragmentCompat != null) {
            dialogFragmentCompat.setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingDialogTheme);
            dialogFragmentCompat.setTargetFragment(this, 0);
            dialogFragmentCompat.show(getParentFragmentManager(), "PreferenceDialogFragmentCompat");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}