package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import ly.pp.justpiano3.constant.Consts;

import java.lang.reflect.Field;

public class ColorUtil {

    /**
     * 通过框框数组索引取得框框颜色
     */
    public static int getKuangColorByKuangIndex(Context context, int index) {
        if (index < 0 || index >= Consts.kuang.length) {
            return -1;
        }
        return getKuangColorByDrawable(context, Consts.kuang[index]);
    }

    private static int getKuangColorByDrawable(Context context, int drawable) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getResources().getDrawable(drawable);
        // 获取solid中的color值
        int color = 0xffffffff;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ColorStateList colorStateList = gradientDrawable.getColor();
            color = colorStateList.getDefaultColor();
        } else {
            Field field;
            try {
                field = gradientDrawable.getClass().getDeclaredField("mGradientState");
                field.setAccessible(true);
                Object gradientState = field.get(gradientDrawable);
                Field solidColorField = gradientState.getClass().getDeclaredField("mSolidColor");
                solidColorField.setAccessible(true);
                color = solidColorField.getInt(gradientState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return color;
    }
}
