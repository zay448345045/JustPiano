package ly.pp.justpiano3.view.preference;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import ly.pp.justpiano3.utils.FilePickerUtil;

public class FilePickerPreference extends Preference {
    private Activity activity;

    public FilePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        if (activity != null) {
            FilePickerUtil.openFileManager(activity, false, getKey());
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void persistFilePath(String value) {
        setSummary(value);
        persistString(value);
    }
}
