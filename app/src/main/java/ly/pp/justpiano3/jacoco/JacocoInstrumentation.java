package ly.pp.justpiano3.jacoco;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * 使用adb shell命令 am instrument ${pkgName}/${className} 的方式启动，
 * 凡是以这种方式启动的app操作，都会统计其代码覆盖率
 */
public class JacocoInstrumentation extends Instrumentation implements FinishListener {
    public static String TAG = "JacocoInstrumentation:";
    private final Bundle mResults = new Bundle();
    private Intent mIntent;
    public static JacocoInstrumentation instance;


    public JacocoInstrumentation() {
        instance = this;
    }

    @Override
    public void onCreate(Bundle arguments) {
        Log.d(TAG, "onCreate(" + arguments + ")");
        super.onCreate(arguments);
        mIntent = new Intent(getTargetContext(), InstrumentedActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start();
    }

    @Override
    public void onStart() {
        super.onStart();
        Looper.prepare();
        InstrumentedActivity activity = (InstrumentedActivity) startActivitySync(mIntent);
        activity.setFinishListener(this);
    }

    /**
     * 接收到JacocoStopBroacast的时候生成ec文件
     */
    public void stop() {
        JacocoUtils.generateEcFile(false);
        finish(Activity.RESULT_OK, mResults);
    }

    /**
     * 当主Activity销毁的时候，生成ec文件
     */
    @Override
    public void onActivityFinished() {
        JacocoUtils.generateEcFile(false);
        finish(Activity.RESULT_OK, mResults);
    }
}