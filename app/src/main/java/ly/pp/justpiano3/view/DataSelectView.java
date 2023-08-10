package ly.pp.justpiano3.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.R;

import java.util.*;

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
     * 是否可编辑文本框
     */
    private boolean dataEditable;

    /**
     * 文字展示大小
     */
    private int dataTextSize;

    /**
     * 数据变化 监听器
     */
    public interface DataChangeListener {

        /**
         * 数值改变时调用
         */
        void onDataChange(String name, String value);
    }

    /**
     * 监听器列表
     */
    private final List<DataChangeListener> dataChangeListenerList = new ArrayList<>();

    /**
     * 添加监听器
     */
    public void addDataChangeListener(DataChangeListener dataChangeListener) {
        dataChangeListenerList.add(dataChangeListener);
    }

    /**
     * 设置数据
     * 如果数据顺序要求严格，请保证入参Map有序，如使用LinkedHashMap
     */
    public DataSelectView setDataNameAndValue(Map<String, String> dataNameValueMap) {
        return setDataNameAndValue(dataNameValueMap, null);
    }

    /**
     * 设置数据
     * 如果数据顺序要求严格，请保证入参Map有序，如使用LinkedHashMap
     */
    public DataSelectView setDataNameAndValue(Map<String, String> dataNameValueMap, String defaultName) {
        this.dataNameValueMap = dataNameValueMap;
        return setDefaultName(defaultName);
    }

    public DataSelectView setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        if (dataEditText != null) {
            dataEditText.setText(defaultName);
        }
        if (dataNameValueMap == null) {
            dataNameValueMap = Collections.emptyMap();
        }
        List<String> dataNameList = new ArrayList<>(dataNameValueMap.keySet());
        if (TextUtils.isEmpty(defaultName) || !dataNameList.contains(defaultName)) {
            dataListIterator = dataNameList.listIterator();
        } else {
            dataListIterator = dataNameList.listIterator(dataNameList.indexOf(defaultName));
        }
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
        return this;
    }

    /**
     * 设置数据是否可编辑
     */
    public DataSelectView setDataEditable(boolean dataEditable) {
        this.dataEditable = dataEditable;
        return this;
    }

    public DataSelectView setDataOnlyNumber(boolean dataOnlyNumber) {
        this.dataOnlyNumber = dataOnlyNumber;
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
        setDataOnlyNumber(typedArray.getBoolean(R.styleable.DataSelectView_dataOnlyNumber, Boolean.FALSE));
        String dataDefaultName = typedArray.getString(R.styleable.DataSelectView_dataDefaultName);
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
        // 指定图片按钮高度
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        // 切换上一个值按钮
        if (preButton == null) {
            preButton = new ImageView(context);
        }
        preButton.setImageResource(R.drawable.back_arrow);
        preButton.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
        preButton.setOnClickListener(v -> dataEditText.setText(getPreviousDataName()));
        addView(preButton, new LayoutParams(0, height, 1));

        // 文本框
        if (dataEditText == null) {
            dataEditText = new EditText(context);
        }
        dataEditText.setFocusable(dataEditable);
        dataEditText.setFocusableInTouchMode(dataEditable);
        dataEditText.setClickable(dataEditable);
        dataEditText.setGravity(Gravity.CENTER);
        dataEditText.setTextSize(dataTextSize);
        dataEditText.setText(defaultName);
        dataEditText.setPadding(0, 0, 0, 0);
        dataEditText.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
        dataEditText.setTextColor(ContextCompat.getColor(context, R.color.white1));
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
                if (dataNameValueMap == null) {
                    dataNameValueMap = Collections.emptyMap();
                }
                // 文字改变后的操作
                for (DataChangeListener dataChangeListener : dataChangeListenerList) {
                    dataChangeListener.onDataChange(editable.toString(), dataNameValueMap.containsKey(editable.toString())
                            ? dataNameValueMap.get(editable.toString()) : editable.toString());
                }
            }
        });
        addView(dataEditText, new LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        // 切换下一个值按钮
        if (nextButton == null) {
            nextButton = new ImageView(context);
        }
        nextButton.setImageResource(R.drawable.for_arrow);
        nextButton.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
        nextButton.setOnClickListener(v -> dataEditText.setText(getNextDataName()));
        addView(nextButton, new LayoutParams(0, height, 1));
    }

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
