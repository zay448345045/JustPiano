package ly.pp.justpiano3.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.util.Consumer;

import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLMainMode;
import ly.pp.justpiano3.service.ConnectionService;

/**
 * @author as
 */
public class OnlineUtil {

    static {
        EncryptUtil.updateDeviceKeyPair();
    }

    /**
     * 官网地址
     */
    public static final String WEBSITE_URL = "justpiano.fun";

    /**
     * 官网地址
     */
    public static final String INSIDE_WEBSITE_URL = "i.justpiano.fun";

    /**
     * 对战服务器地址
     */
    public static final String ONLINE_SERVER_URL = "server.justpiano.fun";

    /**
     * 测试对战服务器地址
     */
    public static final String TEST_ONLINE_SERVER_URL = "test.justpiano.fun";

    /**
     * 当前选择的服务器
     */
    public static String server = ONLINE_SERVER_URL;

    /**
     * 对战服务端口
     */
    public static final Integer ONLINE_PORT = 8908;

    /**
     * 断线重连最长等待时间（毫秒）
     */
    public static final Long AUTO_RECONNECT_MAX_WAIT_TIME = 30000L;

    /**
     * 断线重连期间每次重连的时间间隔（毫秒）
     */
    public static final Long AUTO_RECONNECT_INTERVAL_TIME = 1000L;

    public static String onlineSessionId = UUID.randomUUID().toString().replace("-", "");
    /**
     * 断线时的时间戳，用于断线重连判断，如果非空，表示目前没有掉线，断线自动重连成功后会置空
     */
    private static Long autoReconnectTime;
    private static int autoReconnectCount;

    private static ConnectionService connectionService;

    private static boolean bindService;

    private static final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connectionService = ((ConnectionService.JPBinder) service).getConnectionService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connectionService = null;
        }
    };

    /**
     * channel绑定的消息类型属性key
     */
    private static final AttributeKey<Integer> MSG_TYPE_ATTRIBUTE_KEY = AttributeKey.valueOf("MSG_TYPE");

    /**
     * 通过客户端channel设置消息类型
     *
     * @param channel 客户端channel
     * @param msgType 消息类型
     */
    public static void setMsgTypeByChannel(Channel channel, Integer msgType) {
        channel.attr(MSG_TYPE_ATTRIBUTE_KEY).set(msgType);
    }

    /**
     * 通过客户端channel获取消息类型
     *
     * @param channel 客户端channel
     * @return 消息类型
     */
    public static Integer getMsgTypeByChannel(Channel channel) {
        return channel.attr(MSG_TYPE_ATTRIBUTE_KEY).get();
    }

    public static void outlineConnectionService(Context context) {
        try {
            if (connectionService != null) {
                connectionService.outLine();
            }
            if (bindService) {
                context.unbindService(serviceConnection);
                bindService = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onlineConnectionService(Context context) {
        try {
            if (bindService) {
                context.unbindService(serviceConnection);
            }
            bindService = context.bindService(new Intent(context, ConnectionService.class),
                    serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConnectionService getConnectionService() {
        return connectionService;
    }

    public static void cancelAutoReconnect() {
        if (OnlineUtil.autoReconnectTime != null) {
            Log.i(OnlineUtil.class.getSimpleName(), " autoReconnect! channelRead0 autoReconnect:"
                    + (OnlineUtil.autoReconnectTime == null ? "null" : System.currentTimeMillis() - OnlineUtil.autoReconnectTime));
            OnlineUtil.autoReconnectTime = null;
            OnlineUtil.autoReconnectCount = 0;
            OnlineUtil.handleOnTopBaseActivity(olBaseActivity -> {
                olBaseActivity.jpProgressBar.setCancelable(true);
                olBaseActivity.jpProgressBar.setText("");
                if (olBaseActivity.jpProgressBar.isShowing()) {
                    olBaseActivity.jpProgressBar.dismiss();
                }
            }, 0L);
        }
    }

    public static boolean autoReconnecting() {
        return autoReconnectTime != null;
    }

    public static void outLineAndDialogWithAutoReconnect(Context context) {
        // 在进入对战页面时掉线，直接强制不重连
        if (JPStack.top() instanceof OLMainMode) {
            outLineAndDialog(context);
            return;
        }
        Log.i(OnlineUtil.class.getSimpleName(), " autoReconnect! autoReconnect:"
                + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime));
        // 如果不是断线自动重连状态，先进行断线自动重连
        if (autoReconnectTime == null || System.currentTimeMillis() - autoReconnectTime < AUTO_RECONNECT_MAX_WAIT_TIME) {
            // 初始化断线重连变量
            if (autoReconnectTime == null) {
                autoReconnectTime = System.currentTimeMillis();
                autoReconnectCount = 0;
                handleOnTopBaseActivity(olBaseActivity -> {
                    if (!olBaseActivity.jpProgressBar.isShowing()) {
                        updateProgressBarText(olBaseActivity);
                        olBaseActivity.jpProgressBar.setCancelable(false);
                        olBaseActivity.jpProgressBar.show();
                    }
                }, 0L);
            } else {
                handleOnTopBaseActivity(olBaseActivity -> {
                    if (olBaseActivity.jpProgressBar.isShowing() && autoReconnectTime != null) {
                        updateProgressBarText(olBaseActivity);
                    }
                }, 0L);
            }
            // 每隔一小段时间尝试连接一次
            if (System.currentTimeMillis() - autoReconnectTime >= autoReconnectCount * AUTO_RECONNECT_INTERVAL_TIME) {
                outlineConnectionService(context);
                handleOnTopBaseActivity(olBaseActivity -> {
                    if (olBaseActivity.jpProgressBar.isShowing()) {
                        updateProgressBarText(olBaseActivity);
                        onlineConnectionService(context);
                        Log.i(OnlineUtil.class.getSimpleName(), " autoReconnect! do autoReconnect:"
                                + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime));
                    }
                }, AUTO_RECONNECT_INTERVAL_TIME);
                autoReconnectCount = (int) ((System.currentTimeMillis() - autoReconnectTime) / AUTO_RECONNECT_INTERVAL_TIME) + 1;
            }
        } else {
            outLineAndDialog(context);
        }
    }

    private static void updateProgressBarText(OLBaseActivity olBaseActivity) {
        olBaseActivity.jpProgressBar.setText("连接中...等待剩余秒数：" + Math.max(0,
                (AUTO_RECONNECT_MAX_WAIT_TIME - System.currentTimeMillis() + autoReconnectTime) / 1000) + "点击取消");
        olBaseActivity.jpProgressBar.setClickableLink("点击取消", () -> {
            olBaseActivity.progressBarDismissAndReInit();
            OLBaseActivity.returnMainMode(olBaseActivity);
        });
    }

    public static void outLineAndDialog(Context context) {
        Log.i(OnlineUtil.class.getSimpleName(), " autoReconnect! fail autoReconnect:"
                + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime) + JPStack.top());
        outlineConnectionService(context);
        Activity topActivity = JPStack.top();
        if (topActivity instanceof OLBaseActivity olBaseActivity) {
            Message message = Message.obtain(olBaseActivity.olBaseActivityHandler);
            message.what = 0;
            olBaseActivity.olBaseActivityHandler.handleMessage(message);
        }
    }

    public static void handleOnTopBaseActivity(Consumer<OLBaseActivity> consumer, long delayMillis) {
        if (JPStack.top() instanceof OLBaseActivity olBaseActivity) {
            olBaseActivity.olBaseActivityHandler.postDelayed(() -> consumer.accept(olBaseActivity), delayMillis);
        }
    }
}
