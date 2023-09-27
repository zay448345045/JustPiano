package ly.pp.justpiano3.task;

import android.app.Activity;
import android.os.Message;

import protobuf.vo.OnlineBaseVO;

/**
 * 接收到请求时执行
 */
@FunctionalInterface
public interface ReceiveTask {

    /**
     * 执行任务
     *
     * @param receivedMessage 接收的消息体
     * @param topActivity     顶层activity
     * @param message         android message
     */
    void run(OnlineBaseVO receivedMessage, Activity topActivity, Message message) throws Exception;
}
