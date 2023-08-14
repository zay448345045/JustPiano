package ly.pp.justpiano3.utils;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author as
 */
public class OnlineUtil {

    /**
     * channel绑定的消息类型属性key
     */
    private static final AttributeKey<Integer> MSG_TYPE_ATTRIBUTE_KEY = AttributeKey.valueOf("MSG_TYPE");

    /**
     * 通过客户端channel设置消息类型
     *
     * @param channel    客户端channel
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
}
