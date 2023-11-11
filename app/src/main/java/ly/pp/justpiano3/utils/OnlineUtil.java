package ly.pp.justpiano3.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.service.ConnectionService;

/**
 * @author as
 */
public class OnlineUtil {

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

    public static void outlineConnectionService(JPApplication jpApplication) {
        try {
            if (connectionService != null) {
                connectionService.outLine();
            }
            if (bindService) {
                jpApplication.unbindService(serviceConnection);
                bindService = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onlineConnectionService(JPApplication jpApplication) {
        try {
            if (bindService) {
                jpApplication.unbindService(serviceConnection);
            }
            bindService = jpApplication.bindService(new Intent(jpApplication, ConnectionService.class),
                    serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
