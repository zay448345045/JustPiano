package ly.pp.justpiano3.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationUtil {
    public static void vibrateOnce(Context context, long durationInMillis) {
        if (durationInMillis <= 0L) {
            return;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // 检查设备是否支持震动功能
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 使用 VibrationEffect.createOneShot() 方法来创建震动效果
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(durationInMillis, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(vibrationEffect);
            } else {
                // 旧版本的设备，使用 deprecated 的 vibrate() 方法
                vibrator.vibrate(durationInMillis);
            }
        }
    }
}
