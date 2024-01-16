package ly.pp.justpiano3.activity.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ly.pp.justpiano3.R;

public final class EasterEggFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_easter_egg, rootKey);
    }
}