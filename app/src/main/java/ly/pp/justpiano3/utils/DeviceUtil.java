package ly.pp.justpiano3.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import ly.pp.justpiano3.BuildConfig;

/**
 * DeviceUtils
 * 设备工具
 *
 * @author Jhpz
 * @since create(2023 / 7 / 26)
 **/
public final class DeviceUtil {
    /**
     * 获取设备的AndroidId
     *
     * @param context 上下文
     * @return AndroidId
     */
    @SuppressLint("HardwareIds")
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
     * 获取app与设备信息，用于异常&反馈上报时使用
     */
    public static String getAppAndDeviceInfo() {
        return BuildConfig.VERSION_NAME + '-' + BuildConfig.BUILD_TIME + '-'
                + BuildConfig.BUILD_TYPE + ' ' + getDeviceBrandAndModel() + ' ' + getAndroidVersion();
    }

    /**
     * 是否为X86设备
     */
    public static boolean isX86() {
        for (String abi : Build.SUPPORTED_ABIS) {
            if ("x86".equalsIgnoreCase(abi) || "x86_64".equalsIgnoreCase(abi)) {
                return true;
            }
        }
        return false;
    }
}
