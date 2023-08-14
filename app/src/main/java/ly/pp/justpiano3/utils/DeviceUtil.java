package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import io.netty.util.internal.StringUtil;

/**
 * DeviceUtils
 * 设备工具
 *
 * @author Jhpz
 * @since create(2023 / 7 / 26)
 **/
public class DeviceUtil {
    /**
     * 获取设备的AndroidId
     *
     * @param context 上下文
     * @return AndroidId
     */
    public static String getAndroidId(Context context) {
        String androidId;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            androidId = "Exception: " + e.getMessage();
        }
        return androidId;
    }

    /**
     * 获取设备的Android版本号
     *
     * @return Android版本号
     */
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE + "." + Build.VERSION.INCREMENTAL;
    }

    /**
     * 获取设备的制造商和型号
     *
     * @return 设备制造商和型号
     */
    public static String getDeviceBrandAndModel() {
        return Build.MANUFACTURER + "." + Build.MODEL;
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo == null ? StringUtil.EMPTY_STRING : packageInfo.versionName;
    }

    /**
     * 获取app版本code
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo == null ? 0 : packageInfo.versionCode;
    }

    /**
     * 获取app package信息
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
