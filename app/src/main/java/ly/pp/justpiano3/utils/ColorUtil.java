package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;

import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.Consts;

/**
 * 框框颜色处理工具
 */
public final class ColorUtil {

    public static final int[] filledUserColor = new int[]{R.drawable.filled_msg, R.drawable.filled_v1, R.drawable.filled_v2, R.drawable.filled_v3,
            R.drawable.filled_v4, R.drawable.filled_v5, R.drawable.filled_v6, R.drawable.filled_v7,
            R.drawable.filled_v8, R.drawable.filled_v9, R.drawable.filled_v10, R.drawable.filled_v11,
            R.drawable.filled_v12, R.drawable.filled_v13, R.drawable.filled_v14, R.drawable.filled_v15,
            R.drawable.filled_v16, R.drawable.filled_v17, R.drawable.filled_v18, R.drawable.filled_v19,
            R.drawable.filled_v20, R.drawable.filled_v21, R.drawable.filled_v22, R.drawable.filled_v23,
            R.drawable.filled_v24, R.drawable.filled_v25, R.drawable.filled_v26, R.drawable.filled_v27,
    };

    public static final int[] userColor = new int[]{R.drawable.title_bar, R.drawable.v1_name, R.drawable.v2_name, R.drawable.v3_name,
            R.drawable.v4_name, R.drawable.v5_name, R.drawable.v6_name, R.drawable.v7_name,
            R.drawable.v8_name, R.drawable.v9_name, R.drawable.v10_name, R.drawable.v11_name,
            R.drawable.v12_name, R.drawable.v13_name, R.drawable.v14_name, R.drawable.v15_name,
            R.drawable.v16_name, R.drawable.v17_name, R.drawable._none, R.drawable.v19_name,
            R.drawable.v20_name, R.drawable.v21_name, R.drawable.v22_name, R.drawable.v23_name,
            R.drawable.v24_name, R.drawable.v25_name, R.drawable.v26_name, R.drawable.v27_name,
    };

    public static final Map<Integer, Integer> indexToColorMap = new ConcurrentHashMap<>();

    /**
     * 通过框框数组索引取得框框颜色
     */
    public static Integer getUserColorByUserColorIndex(Context context, Integer index) {
        if (index == null || index < 0 || index >= ColorUtil.userColor.length) {
            return null;
        }
        if (indexToColorMap.containsKey(index)) {
            return indexToColorMap.get(index);
        } else {
            Integer color = getUserColorByDrawable(context, ColorUtil.filledUserColor[index]);
            indexToColorMap.put(index, color);
            return color;
        }
    }

    public static Integer getUserColorByDrawable(Context context, int drawable) {
        // 使用 XmlPullParser 解析 Drawable 的 XML 内容
        XmlResourceParser parser = context.getResources().getXml(drawable);
        AttributeSet attrs = Xml.asAttributeSet(parser);

        // 寻找 stroke 标签，并获取 color 属性的值
        Integer color = null;
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("stroke")) {
                        int colorResource = attrs.getAttributeResourceValue(Consts.ANDROID_NAMESPACE, "color", 0);
                        if (colorResource > 0) {
                            color = ContextCompat.getColor(context, colorResource);
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }
}
