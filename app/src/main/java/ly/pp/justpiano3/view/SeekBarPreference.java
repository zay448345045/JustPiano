package ly.pp.justpiano3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import ly.pp.justpiano3.R;

import java.util.Locale;

/**
 * 可拖动选择数值的Preference
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String androidNs = "http://schemas.android.com/apk/res/android";

    private SeekBar seekBar;
    private TextView valueText;
    private final Context context;
    private final String dialogMessage;
    private final String suffix;
    private final float minValue;
    private final float maxValue;
    private final boolean floatNumber;
    private final String defaultValue;
    private String value;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        dialogMessage = attrs.getAttributeValue(androidNs, "dialogMessage");
        suffix = attrs.getAttributeValue(androidNs, "text");
        defaultValue = attrs.getAttributeValue(androidNs, "defaultValue");
        // 获取自定义属性的最大和最小值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
        minValue = typedArray.getFloat(R.styleable.SeekBarPreference_minValue, 0f);
        maxValue = typedArray.getFloat(R.styleable.SeekBarPreference_maxValue, 100f);
        floatNumber = typedArray.getBoolean(R.styleable.SeekBarPreference_floatNumber, false);
        typedArray.recycle();
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        TextView splashText = new TextView(context);
        if (dialogMessage != null) {
            splashText.setText(dialogMessage);
        }
        layout.addView(splashText);

        valueText = new TextView(context);
        valueText.setGravity(Gravity.CENTER_HORIZONTAL);
        valueText.setTextSize(24);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(valueText, params);

        seekBar = new SeekBar(context);
        seekBar.setOnSeekBarChangeListener(this);
        layout.addView(seekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (shouldPersist()) {
            value = getPersistedString(defaultValue);
        }

        seekBar.setProgress((int) ((Float.parseFloat(value) - minValue) / (maxValue - minValue) * 100));
        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        seekBar.setProgress((int) ((Float.parseFloat(value) - minValue) / (maxValue - minValue) * 100));
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) {
            value = shouldPersist() ? getPersistedString(this.defaultValue) : (String) defaultValue;
        } else {
            value = (String) defaultValue;
        }
    }

    public void onProgressChanged(SeekBar seekBar, int value, boolean fromTouch) {
        float floatValue = minValue + value / 100f * (maxValue - minValue);
        String showValue = floatNumber ? String.format(Locale.getDefault(), "%.2f", floatValue) : String.valueOf(Math.round(floatValue));
        valueText.setText(suffix == null ? showValue : showValue.concat(suffix));
        if (shouldPersist()) {
            persistString(showValue);
        }
        callChangeListener(value);
    }

    public void onStartTrackingTouch(SeekBar seek) {
    }

    public void onStopTrackingTouch(SeekBar seek) {
    }
}