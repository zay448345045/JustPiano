package ly.pp.justpiano3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.R;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 数据选择器
 */
public class GoldConvertView extends LinearLayout {

    /**
     * 货币实际值
     */
    private BigDecimal actualValue;

    /**
     * 货币默认值
     */
    private BigDecimal defaultActualValue;

    /**
     * 货币数值选择器
     */
    private Pair<DataSelectView, DataSelectView> goldSelectViewPair;

    /**
     * 货币数值转换规则
     */
    private Pair<GoldValueConvertRule, GoldValueConvertRule> goldValueConvertRulePair;

    /**
     * 转换器左侧文字
     */
    private Pair<String, String> goldConvertText;

    /**
     * 转换器文本框背景
     */
    private int textBackground;

    /**
     * 监听开启开关，防止互相监听导致死循环
     */
    private Pair<AtomicBoolean, AtomicBoolean> listenerEnable;

    /**
     * 是否为根据按钮修改值
     */
    private Pair<AtomicBoolean, AtomicBoolean> valueChangeByButton;

    /**
     * 操纵货币数值选择范围的按钮时，改变的货币数量
     */
    private BigDecimal stepChangeValue;

    /**
     * 货币实际值与货币展示值之间的转换规则
     */
    public interface GoldValueConvertRule {

        /**
         * 转换规则：输入实际值，输出展示值
         */
        BigDecimal convertToShow(BigDecimal actualValue);

        /**
         * 转换规则：输入展示值，输出实际值
         */
        BigDecimal convertToActual(BigDecimal showValue);
    }

    public static final GoldValueConvertRule DEFAULT_GOLD_VALUE_CONVERT_RULE_INSTANCE = new GoldValueConvertRule() {
        @Override
        public BigDecimal convertToShow(BigDecimal actualValue) {
            return actualValue;
        }

        @Override
        public BigDecimal convertToActual(BigDecimal showValue) {
            return showValue;
        }
    };

    public GoldConvertView setStepChangeValue(float stepChangeValue) {
        this.stepChangeValue = new BigDecimal(stepChangeValue);
        return this;
    }

    public GoldConvertView setGoldConvertText(String text1, String text2) {
        this.goldConvertText = Pair.create(text1, text2);
//        ff
        return this;
    }

    public BigDecimal getActualValue() {
        if (actualValue == null) {
            actualValue = defaultActualValue;
        }
        return actualValue;
    }

    public GoldConvertView setGoldValueConvertRule(GoldValueConvertRule goldValueConvertRule1, GoldValueConvertRule goldValueConvertRule2) {
        goldValueConvertRulePair = Pair.create(goldValueConvertRule1, goldValueConvertRule2);

        // 配置货币转换监听
        goldSelectViewPair.first.setDataChangeListener((view, name, value) -> {
            // 防止两个文本框互相监听导致死循环，需要原子锁来控制
            if (listenerEnable.first.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
                return;
            }
            listenerEnable.second.compareAndSet(Boolean.TRUE, Boolean.FALSE);

            // 如果是通过按下按钮改变的文字值，则直接取按下按钮后更新好的实际值
            // 如果是通过输入文本改变的文字值，则需要根据文本内容重新计算实际值
            if (valueChangeByButton.first.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {
                goldSelectViewPair.second.setCurrentDataName(goldValueConvertRulePair.second.convertToShow(GoldConvertView.this.actualValue).toString());
            } else {
                BigDecimal actualValue = goldValueConvertRulePair.first.convertToActual(StringUtil.isNullOrEmpty(name) ? defaultActualValue : new BigDecimal(name));
                goldSelectViewPair.second.setCurrentDataName(goldValueConvertRulePair.second.convertToShow(actualValue).toString());
                this.actualValue = actualValue;
            }
        });
        goldSelectViewPair.first.setDataChangePreListener((view, name, value) -> {
            valueChangeByButton.first.compareAndSet(Boolean.FALSE, Boolean.TRUE);
            view.setCurrentDataName(goldValueConvertRule1.convertToShow(stepMinusActualValue()).toString());
        });
        goldSelectViewPair.first.setDataChangeNextListener((view, name, value) -> {
            valueChangeByButton.first.compareAndSet(Boolean.FALSE, Boolean.TRUE);
            view.setCurrentDataName(goldValueConvertRule1.convertToShow(stepPlusActualValue()).toString());
        });

        goldSelectViewPair.second.setDataChangeListener((view, name, value) -> {
            // 防止两个文本框互相监听导致死循环，需要原子锁来控制
            if (listenerEnable.second.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
                return;
            }
            listenerEnable.first.compareAndSet(Boolean.TRUE, Boolean.FALSE);

            // 如果是通过按下按钮改变的文字值，则直接取按下按钮后更新好的实际值
            // 如果是通过输入文本改变的文字值，则需要根据文本内容重新计算实际值
            if (valueChangeByButton.second.compareAndSet(Boolean.TRUE, Boolean.FALSE)) {
                goldSelectViewPair.first.setCurrentDataName(goldValueConvertRulePair.first.convertToShow(GoldConvertView.this.actualValue).toString());
            } else {
                BigDecimal actualValue = goldValueConvertRulePair.second.convertToActual(StringUtil.isNullOrEmpty(name) ? defaultActualValue : new BigDecimal(name));
                goldSelectViewPair.first.setCurrentDataName(goldValueConvertRulePair.first.convertToShow(actualValue).toString());
                this.actualValue = actualValue;
            }
        });
        goldSelectViewPair.second.setDataChangePreListener((view, name, value) -> {
            valueChangeByButton.second.compareAndSet(Boolean.FALSE, Boolean.TRUE);
            view.setCurrentDataName(goldValueConvertRule2.convertToShow(stepMinusActualValue()).toString());
        });
        goldSelectViewPair.second.setDataChangeNextListener((view, name, value) -> {
            valueChangeByButton.second.compareAndSet(Boolean.FALSE, Boolean.TRUE);
            view.setCurrentDataName(goldValueConvertRule2.convertToShow(stepPlusActualValue()).toString());
        });
        return this;
    }

    private BigDecimal stepPlusActualValue() {
        if (stepChangeValue == null) {
            stepChangeValue = new BigDecimal("1");
        }
        if (actualValue == null) {
            actualValue = defaultActualValue;
        }
        BigDecimal result = actualValue.add(stepChangeValue);
        this.actualValue = result;
        return result;
    }

    private BigDecimal stepMinusActualValue() {
        if (stepChangeValue == null) {
            stepChangeValue = new BigDecimal("1");
        }
        if (actualValue == null) {
            actualValue = defaultActualValue;
        }
        BigDecimal subtract = actualValue.subtract(stepChangeValue);
        BigDecimal result = subtract.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract;
        this.actualValue = result;
        return result;
    }

    public GoldConvertView setDefaultActualValue(float defaultActualValue) {
        this.defaultActualValue = new BigDecimal(defaultActualValue);
        return this;
    }

    public GoldConvertView setTextBackground(int textBackground) {
        this.textBackground = textBackground;
        if (goldSelectViewPair != null) {
            goldSelectViewPair.first.setDataTextBackground(textBackground);
            goldSelectViewPair.second.setDataTextBackground(textBackground);
        }
        return this;
    }

    public GoldConvertView(Context context) {
        this(context, null);
    }

    public GoldConvertView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoldConvertView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleCustomAttrs(context, attrs);
        initView(context);
    }

    private void handleCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GoldConvertView);
        setStepChangeValue(typedArray.getFloat(R.styleable.GoldConvertView_stepChangeValue, 1));
        setDefaultActualValue(typedArray.getFloat(R.styleable.GoldConvertView_defaultValue, 0));
        setGoldConvertText(typedArray.getString(R.styleable.GoldConvertView_topGoldConvertText),
                typedArray.getString(R.styleable.GoldConvertView_bottomGoldConvertText));
        int textBackgroundResId = typedArray.getResourceId(R.styleable.GoldConvertView_textBackground, -1);
        if (textBackgroundResId != -1) {
            setTextBackground(textBackgroundResId);
        }
        typedArray.recycle();
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        // 初始化货币选择器
        if (goldSelectViewPair == null) {
            goldSelectViewPair = Pair.create(createGoldSelectView(context), createGoldSelectView(context));
            addView(createGoldSelectLayout(context, goldSelectViewPair.first, true), new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
            addView(createGoldSelectLayout(context, goldSelectViewPair.second, false), new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
        }
        if (listenerEnable == null) {
            listenerEnable = Pair.create(new AtomicBoolean(Boolean.TRUE), new AtomicBoolean(Boolean.TRUE));
        }
        if (valueChangeByButton == null) {
            valueChangeByButton = Pair.create(new AtomicBoolean(Boolean.FALSE), new AtomicBoolean(Boolean.FALSE));
        }

        // 初始化货币默认转换规则
        if (goldValueConvertRulePair == null) {
            setGoldValueConvertRule(DEFAULT_GOLD_VALUE_CONVERT_RULE_INSTANCE, DEFAULT_GOLD_VALUE_CONVERT_RULE_INSTANCE);
        }
    }

    private LinearLayout createGoldSelectLayout(Context context, DataSelectView goldSelectView, boolean isFirst) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(HORIZONTAL);
        TextView textView = new TextView(context);
        textView.setText(isFirst ? goldConvertText.first : goldConvertText.second);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        linearLayout.addView(textView, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        linearLayout.addView(goldSelectView, new LayoutParams(0, LayoutParams.MATCH_PARENT, 3));
        return linearLayout;
    }

    private DataSelectView createGoldSelectView(Context context) {
        return new DataSelectView(context).setDataTextColor(ContextCompat.getColor(context, R.color.white1)).setDataTextBackground(textBackground)
                .setDataTextSize(26).setDataEditable(true).setDataOnlyNumber(true).setDefaultName(defaultActualValue.toString());
    }
}
