package ly.pp.justpiano3.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.media.midi.MidiDeviceInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.task.SkinListPreferenceTask;
import ly.pp.justpiano3.task.SoundListPreferenceTask;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SettingsUtil;
import ly.pp.justpiano3.view.preference.FilePickerPreference;
import ly.pp.justpiano3.view.preference.SkinListPreference;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public final class SettingsActivity extends BaseActivity implements MidiDeviceUtil.MidiDeviceListener {

    private static final PreferenceFragmentCompat settingsFragment = new SettingsFragment();

    private static final Map<String, PreferenceFragmentCompat> preferenceFragmentMap = new HashMap<>() {{
        put("settings_piano_play", new PianoPlaySettingsFragment());
        put("settings_play_note", new PlayNoteSettingsFragment());
        put("settings_waterfall", new WaterfallSettingsFragment());
        put("settings_sound", new SoundSettingsFragment());
        put("settings_keyboard", new KeyboardSettingsFragment());
        put("settings_online_chat", new OnlineChatSettingsFragment());
        put("settings_easter_egg", new EasterEggFragment());
    }};

    public final ActivityResultLauncher<Intent> skinSelectLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Preference preference = settingsFragment.findPreference("skin_select");
                if (preference instanceof SkinListPreference skinListPreference) {
                    skinListPreference.skinKey = skinListPreference.getPersistedString();
                    skinListPreference.closeDialog();
                }
                ImageLoadUtil.setBackground(this, GlobalSetting.getBackgroundPic());
            });

    public final ActivityResultLauncher<Intent> soundSelectLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Preference preference = settingsFragment.findPreference("sound_select");
                if (preference instanceof SoundListPreference soundListPreference) {
                    soundListPreference.soundKey = soundListPreference.getPersistedString();
                    soundListPreference.closeDialog();
                }
            });

    public final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                    return;
                }
                FileUtil.UriInfo uriInfo = Objects.equals(Intent.ACTION_OPEN_DOCUMENT_TREE, result.getData().getAction())
                        ? FileUtil.getFolderUriInfo(this, result.getData().getData())
                        : FileUtil.getUriInfo(this, result.getData().getData());
                if (!SettingsUtil.checkFilePickerPreferenceSelectedUri(FilePickerUtil.extra, uriInfo)) {
                    return;
                }
                try {
                    getContentResolver().takePersistableUriPermission(Objects.requireNonNull(uriInfo.getUri()),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "获取文件访问权限出错", Toast.LENGTH_SHORT).show();
                    return;
                }
                Preference preference = SettingsUtil.getFilePickerPreference(FilePickerUtil.extra);
                if (preference instanceof FilePickerPreference filePickerPreference) {
                    filePickerPreference.persist(uriInfo.getDisplayName(), uriInfo.getUri().toString());
                } else if (preference instanceof SkinListPreference skinListPreference) {
                    skinListPreference.skinKey = uriInfo.getUri().toString();
                    skinListPreference.skinFile = uriInfo.getUri();
                    new SkinListPreferenceTask(skinListPreference).execute(uriInfo.getUri().toString());
                } else if (preference instanceof SoundListPreference soundListPreference) {
                    soundListPreference.soundKey = uriInfo.getUri().toString();
                    new SoundListPreferenceTask(soundListPreference).execute(uriInfo.getUri().toString());
                }
                if (preference != null && Objects.equals(preference.getKey(), "background_pic")) {
                    ImageLoadUtil.setBackground(this, uriInfo.getUri().toString());
                }
            });

    @Override
    public void onBackPressed() {
        GlobalSetting.loadSettings(this, false);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundResource(R.color.black);
        PreferenceFragmentCompat preferenceFragmentCompat = preferenceFragmentMap.get(getIntent().getDataString());
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                preferenceFragmentCompat == null ? settingsFragment : preferenceFragmentCompat).commit();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiConnect(MidiDeviceInfo midiDeviceInfo) {
        SettingsUtil.refreshMidiDevicePreference(settingsFragment);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiDisconnect(MidiDeviceInfo midiDeviceInfo) {
        SettingsUtil.refreshMidiDevicePreference(settingsFragment);
    }
}
