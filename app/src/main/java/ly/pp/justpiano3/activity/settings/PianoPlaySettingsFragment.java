package ly.pp.justpiano3.activity.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ly.pp.justpiano3.R;

public class PianoPlaySettingsFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_piano_play, rootKey);
    }
}