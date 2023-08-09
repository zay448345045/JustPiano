package ly.pp.justpiano3.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
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
        List<String> dataNameList = new ArrayList<>(dataNameValueMap.keySet());
        if (TextUtils.isEmpty(defaultName) || !dataNameList.contains(defaultName)) {
            dataListIterator = dataNameList.listIterator();
        } else {
            dataListIterator = dataNameList.listIterator(dataNameList.indexOf(defaultName));
        }
        return this;
    }

    public DataSelectView setDefaultValue(String defaultValue) {
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

        // 切换上一个值按钮
        if (preButton == null) {
            preButton = new ImageView(context);
        }
        preButton.setImageResource(R.drawable.back_arrow);
        preButton.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
        preButton.setOnClickListener(v -> dataEditText.setText(getPreviousDataValue()));
        addView(preButton, new LayoutParams(0, getMeasuredHeight(), 1));

        // 切换下一个值按钮
        if (nextButton == null) {
            nextButton = new ImageView(context);
        }
        nextButton.setImageResource(R.drawable.for_arrow);
        nextButton.setBackgroundColor(ContextCompat.getColor(context, R.color.translent));
        nextButton.setOnClickListener(v -> dataEditText.setText(getNextDataValue()));
        addView(nextButton, new LayoutParams(0, getMeasuredHeight(), 1));

        // 文本框
        if (dataEditText == null) {
            dataEditText = new EditText(context);
        }
        dataEditText.setFocusable(dataEditable);
        dataEditText.setFocusableInTouchMode(dataEditable);
        dataEditText.setClickable(dataEditable);
        dataEditText.setGravity(Gravity.CENTER);
        dataEditText.setTextSize(dataTextSize);
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
                Log.i("afterTextChanged", "afterTextChanged: " + editable.toString());
                // 文字改变后的操作
                for (DataChangeListener dataChangeListener : dataChangeListenerList) {
                    dataChangeListener.onDataChange(editable.toString(), dataNameValueMap.containsKey(editable.toString())
                            ? dataNameValueMap.get(editable.toString()) : editable.toString());
                }
            }
        });
        addView(dataEditText, new LayoutParams(0, getMeasuredHeight(), 2));
    }

    private String getPreviousDataValue() {
        if (dataListIterator.hasPrevious()) {
            return dataListIterator.previous();
        } else if (dataListIterator.hasNext()) {
            String current = dataListIterator.next();
            dataListIterator.previous();
            return current;
        } else {
            return StringUtil.EMPTY_STRING;
        }
    }

    private String getNextDataValue() {
        if (dataListIterator.hasNext()) {
            return dataListIterator.next();
        } else if (dataListIterator.hasPrevious()) {
            String current = dataListIterator.previous();
            dataListIterator.next();
            return current;
        } else {
            return StringUtil.EMPTY_STRING;
        }
    }

    public String getCurrentDataName() {
        return dataEditText.toString();
    }

    public String getCurrentDataValue() {
        return dataNameValueMap.containsKey(dataEditText.toString()) ? dataNameValueMap.get(dataEditText.toString()) : dataEditText.toString();
    }
}
