package ly.pp.justpiano3.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import androidx.annotation.NonNull;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;

public class SettingsMode extends PreferenceActivity {
    private JPApplication jpApplication;

    @Override
    public void onBackPressed() {
        jpApplication.getSetting().loadSettings(this, false);
        finish();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpApplication = (JPApplication) getApplication();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
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
        }
    }
}
