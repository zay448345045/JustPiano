package ly.pp.justpiano3.view.preference;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ly.pp.justpiano3.utils.FilePickerUtil;

public class FilePickerPreference extends Preference {
    private Activity activity;

    /**
     * 恢复默认按钮
     */
    private final Button defaultButton;

    public FilePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultButton = new Button(context);
        defaultButton.setText("恢复默认");
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        // 检查按钮是否已经添加过（有父级），防止每次调用onBindView时重复添加
        if (defaultButton.getParent() != null) {
            return;
        }
        LinearLayout layout = (LinearLayout) view;
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        layout.addView(defaultButton, params);
        layout.setOnClickListener(v -> {
            if (activity != null) {
                FilePickerUtil.openFileManager(activity, false, getKey());
            }
        });
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void persist(String showName, String value) {
        setSummary(showName);
        persistString(value);
    }

    public void setDefaultButtonClickListener(View.OnClickListener onClickListener) {
        defaultButton.setOnClickListener(onClickListener);
    }
}
