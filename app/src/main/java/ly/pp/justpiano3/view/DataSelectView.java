package ly.pp.justpiano3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.R;

/**
 * 数据选择器
 */
public class DataSelectView extends LinearLayout {

    /**
     * 默认文字大小
     */
    public static final int DEFAULT_TEXT_SIZE = 28;

    /**
     * 数据值列表，key：数据展示名称，value：数据对应的值
     * key和value的值可相同
     * 如果支持手动修改文本框输入数据展示名称（即配置dataEditable = true），则监听器传出的数据展示名称和对应的值一定相同
     */
    private Map<String, String> dataNameValueMap;

    /**
     * 数据迭代器
     */
    private ListIterator<String> dataListIterator;

    /**
     * 选择上一个数据 按钮
     */
    private ImageView preButton;

    /**
     * 选择下一个数据 按钮
     */
    private ImageView nextButton;

    /**
     * 默认展示名称
     */
    private String defaultName;

    /**
     * 值是否只能为数字
     */
    private boolean dataOnlyNumber;

    /**
     * 选项展示
     */
    private EditText dataEditText;

    /**
     * 文本框是否可编辑
     */
    private boolean dataEditable;

    /**
     * 文字展示大小
     */
    private int dataTextSize;

    /**
     * 文字展示颜色
     */
    private int dataTextColor;

    /**
     * 文本框背景
     */
    private int dataTextBackground;

    /**
     * 数据变化 监听器
     */
    public interface DataChangeListener {

        /**
         * 数值改变时调用
         */
        void onDataChange(DataSelectView view, String name, String value);
    }

    /**
     * 触发调整到上一个数据 监听器
     */
    public interface DataChangePreListener {
        /**
         * 数值改变时调用
         */
        void onDataChangePre(DataSelectView view, String name, String value);
    }

    /**
     * 触发调整到下一个数据 监听器
     */
    public interface DataChangeNextListener {
        /**
         * 数值改变时调用
         */
        void onDataChangeNext(DataSelectView view, String name, String value);
    }

    /**
     * 值改变时的监听器
     */
    private DataChangeListener dataChangeListener;

    /**
     * 添加监听器
     */
    public void setDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public DataSelectView setDataChangePreListener(DataChangePreListener dataChangePreListener) {
        if (preButton != null) {
            preButton.setOnClickListener(v -> dataChangePreListener.onDataChangePre(this, getCurrentDataName(), getDataValue()));
        }
        return this;
    }

    public DataSelectView setDataChangeNextListener(DataChangeNextListener dataChangeNextListener) {
        if (nextButton != null) {
            nextButton.setOnClickListener(v -> dataChangeNextListener.onDataChangeNext(this, getCurrentDataName(), getDataValue()));
        }
        return this;
    }

    /**
     * 设置数据
     */
    public DataSelectView setDataNameAndValue(Map<String, String> dataNameValueMap) {
        return setDataNameAndValue(dataNameValueMap, null);
    }

    public DataSelectView setCurrentDataName(List<String> dataNameList) {
        return setCurrentDataName(dataNameList, null);
    }

    public String getCurrentDataName() {
        return dataEditText.getText().toString();
    }

    public String getDataValue() {
        if (dataNameValueMap == null) {
            dataNameValueMap = Collections.emptyMap();
        }
        return dataNameValueMap.containsKey(getCurrentDataName()) ? dataNameValueMap.get(getCurrentDataName()) : getCurrentDataName();
    }

    public DataSelectView setDataNameAndValue(Map<String, String> dataNameValueMap, String defaultName) {
        this.dataNameValueMap = dataNameValueMap;
        return setDefaultName(defaultName);
    }

    public DataSelectView setCurrentDataName(List<String> dataNameList, String defaultName) {
        Map<String, String> dataNameValueMap = new LinkedHashMap<>();
        for (String name : dataNameList) {
            dataNameValueMap.put(name, name);
        }
        return setDataNameAndValue(dataNameValueMap, defaultName);
    }

    public DataSelectView setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        setCurrentDataName(defaultName);
        return this;
    }

    public DataSelectView setDefaultValue(String defaultValue) {
        if (dataNameValueMap == null) {
            dataNameValueMap = Collections.emptyMap();
        }
        for (Map.Entry<String, String> nameValueEntry : dataNameValueMap.entrySet()) {
            if (Objects.equals(nameValueEntry.getValue(), defaultValue)) {
                return setDefaultName(nameValueEntry.getKey());
            }
        }
        return this;
    }

    /**
     * 设置字体大小
     */
    public DataSelectView setDataTextSize(int dataTextSize) {
        this.dataTextSize = dataTextSize;
        if (dataEditText != null) {
            dataEditText.setTextSize(dataTextSize);
        }
        return this;
    }

    /**
     * 设置字体颜色
     */
    public DataSelectView setDataTextColor(int dataTextColor) {
        this.dataTextColor = dataTextColor;
        if (dataEditText != null) {
            dataEditText.setTextColor(dataTextColor);
        }
        return this;
    }

    /**
     * 设置文本框背景
     */
    public DataSelectView setDataTextBackground(int dataTextBackground) {
        this.dataTextBackground = dataTextBackground;
        if (dataEditText != null) {
            dataEditText.setBackgroundResource(dataTextBackground);
        }
        return this;
    }

    /**
     * 设置数据是否可编辑
     */
    public DataSelectView setDataEditable(boolean dataEditable) {
        this.dataEditable = dataEditable;
        if (dataEditText != null) {
            dataEditText.setFocusable(dataEditable);
            dataEditText.setFocusableInTouchMode(dataEditable);
            dataEditText.setClickable(dataEditable);
        }
        return this;
    }

    public DataSelectView setDataOnlyNumber(boolean dataOnlyNumber) {
        this.dataOnlyNumber = dataOnlyNumber;
        return this;
    }

    public DataSelectView setCurrentDataName(String dataName) {
        if (dataEditText != null) {
            dataEditText.setText(dataName);
        }
        if (dataNameValueMap == null) {
            dataNameValueMap = Collections.emptyMap();
        }
        List<String> dataNameList = new ArrayList<>(dataNameValueMap.keySet());
        if (TextUtils.isEmpty(dataName) || !dataNameList.contains(dataName)) {
            dataListIterator = dataNameList.listIterator();
        } else {
            dataListIterator = dataNameList.listIterator(dataNameList.indexOf(dataName));
        }
        return this;
    }

    public DataSelectView(Context context) {
        this(context, null);
    }

    public DataSelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleCustomAttrs(context, attrs);
        initView(context);
    }

    private void handleCustomAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DataSelectView);
        setDataEditable(typedArray.getBoolean(R.styleable.DataSelectView_dataEditable, Boolean.FALSE));
        setDataTextSize(typedArray.getInteger(R.styleable.DataSelectView_dataTextSize, DEFAULT_TEXT_SIZE));
        setDataTextColor(typedArray.getColor(R.styleable.DataSelectView_dataTextColor, Color.WHITE));
        setDataOnlyNumber(typedArray.getBoolean(R.styleable.DataSelectView_dataOnlyNumber, Boolean.FALSE));
        String dataDefaultName = typedArray.getString(R.styleable.DataSelectView_dataDefaultName);
        int dataTextBackgroundResId = typedArray.getResourceId(R.styleable.DataSelectView_dataTextBackground, -1);
        if (dataTextBackgroundResId != -1) {
            setDataTextBackground(dataTextBackgroundResId);
        }
        int dataValueResourceId = typedArray.getResourceId(R.styleable.DataSelectView_dataValueList, -1);
        int dataNameResourceId = typedArray.getResourceId(R.styleable.DataSelectView_dataNameList, -1);
        if (dataValueResourceId != -1 && dataNameResourceId != -1) {
            String[] dataValueArray = getResources().getStringArray(dataValueResourceId);
            String[] dataNameArray = getResources().getStringArray(dataNameResourceId);
            Map<String, String> dataValueMap = new HashMap<>();
            if (dataValueArray.length == dataNameArray.length) {
                for (int i = 0; i < dataValueArray.length; i++) {
                    dataValueMap.put(dataNameArray[i], dataValueArray[i]);
                }
            }
            setDataNameAndValue(dataValueMap, dataDefaultName);
        }
        typedArray.recycle();
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);

        // 切换上一个值按钮
        if (preButton == null) {
            preButton = new ImageView(context);
            preButton.setImageResource(R.drawable.back_arrow);
            preButton.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
            setDataChangePreListener((view, name, value) -> view.setCurrentDataName(getPreviousDataName()));
            addView(preButton, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        }

        // 文本框
        if (dataEditText == null) {
            dataEditText = new EditText(context);
            setDataEditable(dataEditable);
            dataEditText.setGravity(Gravity.CENTER);
            setDataTextSize(dataTextSize);
            setDataTextColor(dataTextColor);
            setCurrentDataName(defaultName);
            setDataTextBackground(dataTextBackground);
            dataEditText.setPadding(0, 0, 0, 0);
            dataEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 文字改变前的操作
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 文字改变中的操作
                    if (dataOnlyNumber && !TextUtils.isEmpty(s) && !TextUtils.isDigitsOnly(s)) {
                        dataEditText.setText(s.toString().replaceAll("\\D+", ""));
                        dataEditText.setSelection(dataEditText.getText().length());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // 文字改变后的操作
                    if (dataNameValueMap == null) {
                        dataNameValueMap = Collections.emptyMap();
                    }
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChange(DataSelectView.this, editable.toString(),
                                dataNameValueMap.containsKey(editable.toString()) ? dataNameValueMap.get(editable.toString()) : editable.toString());
                    }
                }
            });
            addView(dataEditText, new LayoutParams(0, LayoutParams.MATCH_PARENT, 2));
        }

        // 切换下一个值按钮
        if (nextButton == null) {
            nextButton = new ImageView(context);
            nextButton.setImageResource(R.drawable.for_arrow);
            nextButton.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
            setDataChangeNextListener((view, name, value) -> view.setCurrentDataName(getNextDataName()));
            addView(nextButton, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        }
    }

    /**
     * 根据当前迭代器获取前一个元素
     */
    private String getPreviousDataName() {
        if (dataListIterator == null) {
            dataListIterator = Collections.emptyListIterator();
        }
        if (dataListIterator.hasPrevious()) {
            return dataListIterator.previous();
        } else if (dataListIterator.hasNext()) {
            String currentData = dataListIterator.next();
            dataListIterator.previous();
            return currentData;
        } else {
            return defaultName;
        }
    }

    /**
     * 根据当前迭代器获取后一个元素
     */
    private String getNextDataName() {
        if (dataListIterator == null) {
            dataListIterator = Collections.emptyListIterator();
        }
        if (dataListIterator.hasNext()) {
            String currentData = dataListIterator.next();
            if (dataListIterator.hasNext()) {
                String nextData = dataListIterator.next();
                dataListIterator.previous();
                return nextData;
            } else {
                dataListIterator.previous();
                return currentData;
            }
        } else if (dataListIterator.hasPrevious()) {
            return dataListIterator.previous();
        } else {
            return defaultName;
        }
    }
}
