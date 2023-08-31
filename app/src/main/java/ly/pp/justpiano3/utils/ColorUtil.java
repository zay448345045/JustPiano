package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import ly.pp.justpiano3.constant.Consts;

import java.lang.reflect.Field;

public class ColorUtil {

    /**
     * 通过框框数组索引取得框框颜色
     */
    public static Integer getKuangColorByKuangIndex(Context context, int index) {
        if (index < 0 || index >= Consts.kuang.length) {
            return null;
        }
        return getKuangColorByDrawable(context, Consts.filledKuang[index]);
    }

    private static Integer getKuangColorByDrawable(Context context, int drawable) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getResources().getDrawable(drawable);
        // 反射获取xml stroke标签中的color属性值
        Integer color = null;
        Field field;
        try {
            field = gradientDrawable.getClass().getDeclaredField("mGradientState");
            field.setAccessible(true);
            Object gradientState = field.get(gradientDrawable);
            if (gradientState != null) {
                Field strokeColorField = gradientState.getClass().getDeclaredField("mStrokeColors");
                strokeColorField.setAccessible(true);
                ColorStateList colorStateList = (ColorStateList) strokeColorField.get(gradientState);
                if (colorStateList != null) {
                    color = colorStateList.getDefaultColor();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }
}
