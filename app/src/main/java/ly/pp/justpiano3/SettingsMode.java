package ly.pp.justpiano3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

public class SettingsMode extends PreferenceActivity {
    private JPApplication jpApplication;

    @Override
    public void onBackPressed() {
        jpApplication.loadSettings(0);
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
            if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("skin_list", "original").equals("original")) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(getActivity().getDir("Skin", Context.MODE_PRIVATE) + "/ground.jpg");
                } catch (Exception ignored) {
                }
                if (bitmap == null) {
                    try {
                        bitmap = BitmapFactory.decodeFile(getActivity().getDir("Skin", Context.MODE_PRIVATE) + "/ground.png");
                    } catch (Exception ignored) {
                    }
                }
                if (bitmap != null) {
                    getActivity().getWindow().getDecorView().setBackground(new BitmapDrawable(getResources(), bitmap));
                }
            }
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
