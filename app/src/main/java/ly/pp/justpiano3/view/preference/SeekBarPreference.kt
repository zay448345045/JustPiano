package ly.pp.justpiano3.view.preference

import android.content.Context
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
    private var dialogFragmentCompat: DialogFragmentCompat? = null
    private var seekBar: SeekBar? = null
    private var valueText: TextView? = null
    private val suffix: String?
    private val minValue: Float
    private val maxValue: Float
    private val criticalValue: Float
    private val maxSteps: Int
    private val floatNumber: Boolean
    private val defaultValue: String
    private var value: String? = null
    private var message: String? = null
    var emptyText: TextView? = null

    init {
        suffix = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "text")
        message = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "dialogMessage")
        defaultValue = attrs.getAttributeValue(Consts.ANDROID_NAMESPACE, "defaultValue")
        // 获取自定义属性的最大和最小值
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference)
        minValue = typedArray.getFloat(R.styleable.SeekBarPreference_minValue, 0f)
        maxValue = typedArray.getFloat(R.styleable.SeekBarPreference_maxValue, 100f)
        criticalValue =
            typedArray.getFloat(R.styleable.SeekBarPreference_critValue, Float.MAX_VALUE)
        maxSteps = typedArray.getInt(R.styleable.SeekBarPreference_maxSteps, 100)
        floatNumber = typedArray.getBoolean(R.styleable.SeekBarPreference_floatNumber, false)
        typedArray.recycle()
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, fromTouch: Boolean) {
        val floatValue = minValue + value / 100f * (maxValue - minValue)
        val showValue =
            if (floatNumber) String.format("%.2f", floatValue) else floatValue.roundToInt()
                .toString()
        val showText = if (suffix == null) showValue else showValue + suffix
        // 标记默认值
        valueText!!.text =
            if (showValue.toFloat() == defaultValue.toFloat()) "$showText (默认)"
            else if (showValue.toFloat() >= criticalValue) "$showText (不建议)" else showText
        valueText!!.setTextColor(
            if (showValue.toFloat() >= criticalValue) 0xfff05189.toInt() else 0xffffffff.toInt()
        )
        if (shouldPersist()) {
            persistString(showValue)
        }
        callChangeListener(value)
    }

    override fun onStartTrackingTouch(seek: SeekBar) {}

    override fun onStopTrackingTouch(seek: SeekBar) {}

    fun newDialog(): DialogFragmentCompat {
        dialogFragmentCompat = DialogFragmentCompat(this)
        return dialogFragmentCompat as DialogFragmentCompat
    }

    class DialogFragmentCompat(private val seekbarPreference: SeekBarPreference) :
        PreferenceDialogFragmentCompat() {

        override fun onCreateDialogView(context: Context): View {
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(6, 6, 6, 60)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            seekbarPreference.emptyText = TextView(context)
            seekbarPreference.emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
            seekbarPreference.emptyText!!.textSize = 4f
            layout.addView(seekbarPreference.emptyText, params)
            if (!TextUtils.isEmpty(seekbarPreference.message)) {
                seekbarPreference.emptyText = TextView(context)
                seekbarPreference.emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
                seekbarPreference.emptyText!!.textSize = 2f
                layout.addView(seekbarPreference.emptyText, params)
                val messageText = TextView(context)
                messageText.text = seekbarPreference.message
                layout.addView(messageText, params)
                seekbarPreference.emptyText = TextView(context)
                seekbarPreference.emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
                seekbarPreference.emptyText!!.textSize = 10f
                layout.addView(seekbarPreference.emptyText, params)
            }
            seekbarPreference.valueText = TextView(context)
            seekbarPreference.valueText!!.gravity = Gravity.CENTER_HORIZONTAL
            seekbarPreference.valueText!!.textSize = 24f
            layout.addView(seekbarPreference.valueText, params)
            seekbarPreference.emptyText = TextView(context)
            seekbarPreference.emptyText!!.gravity = Gravity.CENTER_HORIZONTAL
            seekbarPreference.emptyText!!.textSize = 14f
            layout.addView(seekbarPreference.emptyText, params)
            seekbarPreference.seekBar = SeekBar(context)
            seekbarPreference.seekBar!!.max = seekbarPreference.maxSteps
            seekbarPreference.seekBar!!.setOnSeekBarChangeListener(seekbarPreference)
            layout.addView(
                seekbarPreference.seekBar,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            if (seekbarPreference.shouldPersist()) {
                seekbarPreference.value =
                    seekbarPreference.getPersistedString(seekbarPreference.defaultValue)
            }
            seekbarPreference.valueText!!.text =
                if (seekbarPreference.value == seekbarPreference.defaultValue)
                    "${seekbarPreference.value} (默认)" else seekbarPreference.value
            seekbarPreference.seekBar!!.progress =
                ((seekbarPreference.value!!.toFloat() - seekbarPreference.minValue)
                        / (seekbarPreference.maxValue - seekbarPreference.minValue) * 100).roundToInt()
            return layout
        }

        override fun onBindDialogView(v: View) {
            super.onBindDialogView(v)
            seekbarPreference.seekBar!!.progress =
                ((seekbarPreference.value!!.toFloat() - seekbarPreference.minValue)
                        / (seekbarPreference.maxValue - seekbarPreference.minValue) * 100).roundToInt()
        }

        override fun onDialogClosed(positiveResult: Boolean) {
        }
    }
}