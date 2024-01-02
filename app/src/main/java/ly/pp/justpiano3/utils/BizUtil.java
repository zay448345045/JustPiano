package ly.pp.justpiano3.utils;

public final class BizUtil {

    /**
     * 根据等级返回对应升级所需所有经验
     */
    public static int getTargetExp(int lv) {
        return (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
    }
}
