package ly.pp.justpiano3.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

/**
 * 唤醒锁操作工具
 */
public final class WakeLockUtil {

    private static PowerManager.WakeLock wakeLock;

    /**
     * 获取唤醒锁
     *
     * @param context 上下文
     * @param tag     提供给WakeLock的标签，用于调试
     */
    @SuppressLint("WakelockTimeout")
    public static void acquireWakeLock(Context context, String tag) {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
        }
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    /**
     * 释放唤醒锁
     */
    public static void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
