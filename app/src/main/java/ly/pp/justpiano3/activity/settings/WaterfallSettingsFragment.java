package ly.pp.justpiano3.activity.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.SettingsUtil;

public class WaterfallSettingsFragment extends BaseSettingsFragment {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_waterfall, rootKey);
        SettingsUtil.registerFilePickerPreference(this, "waterfall_background_pic", false,
                "默认背景图", GlobalSetting.getWaterfallBackgroundPic(), uriInfo -> {
                    if (uriInfo.getDisplayName() == null || (!uriInfo.getDisplayName().endsWith(".jpg")
                            && !uriInfo.getDisplayName().endsWith(".jpeg") && !uriInfo.getDisplayName().endsWith(".png"))) {
                        Toast.makeText(getActivity(), "请选择合法的jpg或png格式图片文件", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                });
    }
}