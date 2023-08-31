package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import ly.pp.justpiano3.R;

import java.lang.reflect.Field;

/**
 * 框框颜色处理工具
 */
public class ColorUtil {

    public static final int[] filledKuang = new int[]{R.drawable.filled_msg, R.drawable.filled_v1, R.drawable.filled_v2, R.drawable.filled_v3, 
            R.drawable.filled_v4, R.drawable.filled_v5, R.drawable.filled_v6, R.drawable.filled_v7, 
            R.drawable.filled_v8, R.drawable.filled_v9, R.drawable.filled_v10, R.drawable.filled_v11, 
            R.drawable.filled_v12, R.drawable.filled_v13, R.drawable.filled_v14, R.drawable.filled_v15, 
            R.drawable.filled_v16, R.drawable.filled_v17, R.drawable.filled_v18, R.drawable.filled_v19, 
            R.drawable.filled_v20, R.drawable.filled_v21, R.drawable.filled_v22, R.drawable.filled_v23, 
            R.drawable.filled_v24, R.drawable.filled_v25, R.drawable.filled_v26, R.drawable.filled_v27,
    };

    public static final int[] kuang = new int[]{R.drawable.title_bar, R.drawable.v1_name, R.drawable.v2_name, R.drawable.v3_name, 
            R.drawable.v4_name, R.drawable.v5_name, R.drawable.v6_name, R.drawable.v7_name, 
            R.drawable.v8_name, R.drawable.v9_name, R.drawable.v10_name, R.drawable.v11_name, 
            R.drawable.v12_name, R.drawable.v13_name, R.drawable.v14_name, R.drawable.v15_name, 
            R.drawable.v16_name, R.drawable.v17_name, R.drawable.v18_name, R.drawable.v19_name, 
            R.drawable.v20_name, R.drawable.v21_name, R.drawable.v22_name, R.drawable.v23_name, 
            R.drawable.v24_name, R.drawable.v25_name, R.drawable.v26_name, R.drawable.v27_name,
    };

    /**
     * 通过框框数组索引取得框框颜色
     */
    public static Integer getKuangColorByKuangIndex(Context context, int index) {
        if (index < 0 || index >= ColorUtil.kuang.length) {
            return null;
        }
        return getKuangColorByDrawable(context, ColorUtil.filledKuang[index]);
    }

    public static Integer getKuangColorByDrawable(Context context, int drawable) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getResources().getDrawable(drawable);
        Drawable.ConstantState gradientState = gradientDrawable.getConstantState();
        // 反射获取xml stroke标签中的color属性值
        try {
            Field strokeColorField = gradientState.getClass().getDeclaredField("mStrokeColors");
            strokeColorField.setAccessible(true);
            ColorStateList colorStateList = (ColorStateList) strokeColorField.get(gradientState);
            if (colorStateList != null) {
                return colorStateList.getDefaultColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
