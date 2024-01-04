package ly.pp.justpiano3.utils;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.core.util.Predicate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.HashMap;
import java.util.Map;

import ly.pp.justpiano3.activity.settings.SettingsFragment;
import ly.pp.justpiano3.view.MidiDeviceListPreference;
import ly.pp.justpiano3.view.preference.FilePickerPreference;

public final class SettingsUtil {

    private static final Map<String, Pair<Preference, Predicate<FileUtil.UriInfo>>> filePickerPreferenceMap = new HashMap<>();

    /**
     * 注册filePickerPreference行为
     */
    public static void registerFilePickerPreference(PreferenceFragmentCompat preferenceFragment, String key, boolean folderPicker,
                                                    String defaultSummary, String uri, Predicate<FileUtil.UriInfo> predicate) {
        Preference preference = preferenceFragment.findPreference(key);
        if (preference != null) {
            if (TextUtils.isEmpty(uri)) {
                preference.setSummary(defaultSummary);
            } else if (preferenceFragment.getActivity() != null) {
                FileUtil.UriInfo uriInfo = folderPicker ? FileUtil.getFolderUriInfo(preferenceFragment.getActivity(), Uri.parse(uri))
                        : FileUtil.getUriInfo(preferenceFragment.getActivity(), Uri.parse(uri));
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

    public static Preference getFilePickerPreference(String key) {
        Pair<Preference, Predicate<FileUtil.UriInfo>> pairValue = filePickerPreferenceMap.get(key);
        if (pairValue == null) {
            return null;
        }
        return pairValue.first;
    }

    public static boolean checkFilePickerPreferenceSelectedUri(String key, FileUtil.UriInfo uriInfo) {
        Pair<Preference, Predicate<FileUtil.UriInfo>> pairValue = filePickerPreferenceMap.get(key);
        return uriInfo.getUri() != null && uriInfo.getDisplayName() != null && pairValue != null && pairValue.second.test(uriInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void refreshMidiDevicePreference(SettingsFragment settingsFragment) {
        Preference preference = settingsFragment.findPreference("midi_device_list");
        if (preference instanceof MidiDeviceListPreference midiDeviceListPreference) {
            midiDeviceListPreference.midiDeviceListRefresh();
        }
    }
}
