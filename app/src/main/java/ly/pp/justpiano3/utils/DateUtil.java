package ly.pp.justpiano3.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DateUtil
 * 时间工具
 *
 * @author Jhpz
 * @since create(2023 / 8 / 6)
 **/
public class DateUtil {
    /**
     * 模板 默认
     */
    public static final String TEMPLATE_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 模板 默认+中文
     */
    public static final String TEMPLATE_DEFAULT_CHINESE = "yyyy年MM月dd日HH点mm分ss秒";


    /**
     * 现在
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 格式化时间（默认格式）
     * yyyy-MM-dd HH:mm:ss
     *
     * @return 指定时间到默认格式
     */
    public static String format(Date date) {
        return new SimpleDateFormat(TEMPLATE_DEFAULT, Locale.CHINESE).format(date);
    }

    /**
     * 格式化时间
     *
     * @param template DateUtil.TEMPLATE*
     * @return 指定时间到指定格式
     */
    public static String format(Date date, String template) {
        return new SimpleDateFormat(template, Locale.CHINESE).format(date);
    }

    /**
     * 时间解析
     *
     * @param date_str 时间文本
     * @param template 解析模板
     * @return 解析后的时间
     */
    public static Date parse(String date_str, String template) throws ParseException {
        return new SimpleDateFormat(template, Locale.CHINESE).parse(date_str);
    }

    /**
     * 时间解析 异常情况返回当前时间
     *
     * @param date_str 时间文本
     * @param template 解析模板
     * @return 解析后的时间
     */
    public static Date parseDontThrow(String date_str, String template) {
        try {
            return parse(date_str, template);
        } catch (Exception e) {
            return now();
        }
    }

}
