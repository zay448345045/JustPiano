package ly.pp.justpiano3.view.preference

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
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
    private val critValue: Float
    private val maxSteps: Int
    private val floatNumber: Boolean
    private val defaultValue: String
    private var value: String? = null
    private var message: String? = null
    private var emptyText: TextView? = null

    init {
        suffix = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "text")
        message = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "dialogMessage")
        defaultValue = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "defaultValue")
        // 获取自定义属性的最大和最小值
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference)
        minValue = typedArray.getFloat(R.styleable.SeekBarPreference_minValue, 0f)
        maxValue = typedArray.getFloat(R.styleable.SeekBarPreference_maxValue, 100f)
        critValue = typedArray.getFloat(R.styleable.SeekBarPreference_critValue, Float.MAX_VALUE)
        maxSteps = typedArray.getInt(R.styleable.SeekBarPreference_maxSteps, 100)
        floatNumber = typedArray.getBoolean(R.styleable.SeekBarPreference_floatNumber, false)
        typedArray.recycle()
    }

    override fun onSetInitialValue(restore: Boolean, defaultValue: Any?) {
        super.onSetInitialValue(restore, defaultValue)
        value = if (restore) {
            if (shouldPersist()) getPersistedString(this.defaultValue) else defaultValue as String
        } else {
            defaultValue as String
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, fromTouch: Boolean) {
        val floatValue = minValue + value / 100f * (maxValue - minValue)
        val showValue =
            if (floatNumber) String.format("%.2f", floatValue) else floatValue.roundToInt()
                .toString()
        val showText = if (suffix == null) showValue else showValue + suffix
        // 标记默认值
        valueText!!.text =
            if (showValue.toFloat() == defaultValue.toFloat()) "$showText (默认)" else if (showValue.toFloat() >= critValue) "$showText (不建议)" else showText
        valueText!!.setTextColor(if (showValue.toFloat() >= critValue) 0xfff05189.toInt() else 0xffffffff.toInt())
        if (shouldPersist()) {
            persistString(showValue)
        }
        callChangeListener(value)
    }

    override fun onStartTrackingTouch(seek: SeekBar) {}

    override fun onStopTrackingTouch(seek: SeekBar) {}

    inner class DialogFragmentCompat : PreferenceDialogFragmentCompat() {
        fun newInstance(key: String?): DialogFragmentCompat {
            val fragment = DialogFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }

        override fun onCreateDialogView(context: Context): View {
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(6, 6, 6, 60)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            emptyText = TextView(context)
            emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
            emptyText!!.textSize = 4f
            layout.addView(emptyText, params)
            if (!TextUtils.isEmpty(message)) {
                emptyText = TextView(context)
                emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
                emptyText!!.textSize = 2f
                layout.addView(emptyText, params)
                val messageText = TextView(context)
                messageText.text = message
                layout.addView(messageText, params)
                emptyText = TextView(context)
                emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
                emptyText!!.textSize = 10f
                layout.addView(emptyText, params)
            }
            valueText = TextView(context)
            valueText!!.gravity = Gravity.CENTER_HORIZONTAL
            valueText!!.textSize = 24f
            layout.addView(valueText, params)
            emptyText = TextView(context)
            emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
            emptyText!!.textSize = 14f
            layout.addView(emptyText, params)
            seekBar = SeekBar(context)
            seekBar!!.max = maxSteps
            seekBar!!.setOnSeekBarChangeListener(this@SeekBarPreference)
            layout.addView(
                seekBar,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            if (shouldPersist()) {
                value = getPersistedString(defaultValue)
            }
            valueText!!.text = if (value == defaultValue) "$value (默认)" else value
            seekBar!!.progress =
                ((value!!.toFloat() - minValue) / (maxValue - minValue) * 100).roundToInt()
            return layout
        }

        override fun onBindDialogView(v: View) {
            super.onBindDialogView(v)
            seekBar!!.progress =
                ((value!!.toFloat() - minValue) / (maxValue - minValue) * 100).roundToInt()
        }

        override fun onDialogClosed(positiveResult: Boolean) {
        }
    }
}