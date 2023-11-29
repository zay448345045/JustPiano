package ly.pp.justpiano3.view.preference

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import ly.pp.justpiano3.R
import ly.pp.justpiano3.constant.Consts
import kotlin.math.roundToInt

/**
 * 可拖动选择数值的Preference
 */
class SeekBarPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs),
    OnSeekBarChangeListener {
    private var seekBar: SeekBar? = null
    private var valueText: TextView? = null
    private val suffix: String?
    private val minValue: Float
    private val maxValue: Float
    private val maxSteps: Int
    private val floatNumber: Boolean
    private val defaultValue: String
    private var value: String? = null
    private var message: String? = null

    init {
        suffix = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "text")
        message = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "dialogMessage")
        defaultValue = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "defaultValue")
        // 获取自定义属性的最大和最小值
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference)
        minValue = typedArray.getFloat(R.styleable.SeekBarPreference_minValue, 0f)
        maxValue = typedArray.getFloat(R.styleable.SeekBarPreference_maxValue, 100f)
        maxSteps = typedArray.getInt(R.styleable.SeekBarPreference_maxSteps, 100)
        floatNumber = typedArray.getBoolean(R.styleable.SeekBarPreference_floatNumber, false)
        typedArray.recycle()
    }

    override fun onCreateDialogView(): View {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(6, 6, 6, 60)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        if (!message.isNullOrEmpty()) {
            val messageText = TextView(context)
            messageText.text = message
            layout.addView(messageText, params)
        }
        valueText = TextView(context)
        valueText!!.gravity = Gravity.CENTER_HORIZONTAL
        valueText!!.textSize = 22f
        layout.addView(valueText, params)
        seekBar = SeekBar(context)
        seekBar!!.setOnSeekBarChangeListener(this)
        seekBar!!.max = maxSteps
        layout.addView(
            seekBar,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        )
        if (shouldPersist()) {
            value = getPersistedString(defaultValue)
        }
        valueText!!.text = if (value == defaultValue) "$value (默认)" else value
        seekBar!!.progress = ((value!!.toFloat() - minValue) / (maxValue - minValue) * 100).roundToInt()
        return layout
    }

    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        seekBar!!.progress = ((value!!.toFloat() - minValue) / (maxValue - minValue) * 100).roundToInt()
    }

    override fun onSetInitialValue(restore: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restore, defaultValue)
        value = if (restore) {
            if (shouldPersist()) getPersistedString(this.defaultValue) else defaultValue as String
        } else {
            defaultValue as String
        }
    }

    /**
     * 展示对话框，并隐藏取消和确定
     */
    override fun showDialog(state: Bundle?) {
        super.showDialog(state)
        val dialog = dialog as AlertDialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).visibility = View.GONE
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).visibility = View.GONE
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, fromTouch: Boolean) {
        val floatValue = minValue + value / 100f * (maxValue - minValue)
        val showValue = if (floatNumber) String.format("%.2f", floatValue) else floatValue.roundToInt().toString()
        val showText = if (suffix == null) showValue else showValue + suffix
        // 标记默认值
        valueText!!.text = if (showValue.toFloat() == defaultValue.toFloat()) "$showText (默认)" else showText
        if (shouldPersist()) {
            persistString(showValue)
        }
        callChangeListener(value)
    }

    override fun onStartTrackingTouch(seek: SeekBar) {}

    override fun onStopTrackingTouch(seek: SeekBar) {}
}