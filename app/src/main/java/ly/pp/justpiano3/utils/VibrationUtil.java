package ly.pp.justpiano3.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationUtil {

    /**
     * 震动一次，持续时间由参数指定
     */
    public static void vibrateOnce(Context context, long durationInMillis) {
        if (durationInMillis <= 0L) {
            return;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationInMillis, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(durationInMillis);
            }
        }
    }
}
