package ly.pp.justpiano3.jacoco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 接收到广播后生成ec文件
 */

public class JacocoStopBroacast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("cxq", "JacocoStopBroacast_onReceive: ");
        JacocoInstrumentation.instance.stop();
    }
}
