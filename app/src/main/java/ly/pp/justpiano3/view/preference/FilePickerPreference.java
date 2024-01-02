package ly.pp.justpiano3.view.preference;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import ly.pp.justpiano3.activity.local.SettingsActivity;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public final class FilePickerPreference extends Preference {
    private static final int BUTTON_ID = View.generateViewId();
    private Activity activity;
    /**
     * 是否是目录选择，默认false
     */
    private boolean folderPicker;

    /**
     * 恢复默认按钮
     */
    private final Button defaultButton;

    public FilePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultButton = new Button(context);
        defaultButton.setFocusable(false);
        defaultButton.setText("恢复默认");
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (preferenceViewHolder.itemView.findViewById(BUTTON_ID) != null) {
            return;
        }
        // 检查按钮是否已经添加到一个父视图，如果是，先从父视图移除
        if (defaultButton.getParent() != null) {
            ((ViewGroup) defaultButton.getParent()).removeView(defaultButton);
        }
        LinearLayout layout = (LinearLayout) preferenceViewHolder.itemView;
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        if (defaultButton.getParent() == null) {
            defaultButton.setId(BUTTON_ID);
            layout.addView(defaultButton, params);
        }
    }

    public void setActivity(ComponentActivity activity) {
        this.activity = activity;
    }

    public void setFolderPicker(boolean folderPicker) {
        this.folderPicker = folderPicker;
    }

    public void persist(String showName, String value) {
        setSummary(showName);
        persistString(value);
        ImageLoadUtil.setBackground(activity);
    }

    public void setDefaultButtonClickListener(View.OnClickListener onClickListener) {
        defaultButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (activity != null) {
            if (folderPicker) {
                FilePickerUtil.openFolderPicker(getKey(), ((SettingsActivity) activity).filePickerLauncher);
            } else {
                FilePickerUtil.openFilePicker(activity, false, getKey(), ((SettingsActivity) activity).filePickerLauncher);
            }
        }
    }
}
