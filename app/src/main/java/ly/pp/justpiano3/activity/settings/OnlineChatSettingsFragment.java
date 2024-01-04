package ly.pp.justpiano3.activity.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.SettingsUtil;

public class OnlineChatSettingsFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_online_chat, rootKey);
        SettingsUtil.registerFilePickerPreference(this, "chats_sound_file", false,
                "默认音效", GlobalSetting.getChatsSoundFile(), uriInfo -> {
                    if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".wav") && !uriInfo.getDisplayName().endsWith(".mp3"))) {
                        Toast.makeText(getActivity(), "请选择合法的wav或mp3格式音频文件", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                });
        SettingsUtil.registerFilePickerPreference(this, "chats_save_path", true,
                "默认存储位置(SD卡/Android/data/ly.pp.justpiano3/files/Chats)",
                GlobalSetting.getChatsSavePath(), uriInfo -> true);
    }
}