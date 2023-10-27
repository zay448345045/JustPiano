package ly.pp.justpiano3.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import androidx.annotation.NonNull;

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
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        ImageLoadUtil.setBackground(this, "ground", getWindow());
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.color.black);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        }
    }
}
