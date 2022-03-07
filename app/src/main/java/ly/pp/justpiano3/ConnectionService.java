package ly.pp.justpiano3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.king.anetty.ANetty;
import com.king.anetty.Netty;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class ConnectionService extends Service implements Runnable {

    private final JPBinder jpBinder = new JPBinder(this);
    private JPApplication jpapplication;
    private Netty mNetty;

    public static class JPBinder extends Binder {
        private final ConnectionService connectionService;

        JPBinder(ConnectionService connectionService) {
            this.connectionService = connectionService;
        }

        public ConnectionService getConnectionService() {
            return connectionService;
        }
    }

    /**
     * 整数转字节数组
     *
     * @param value 整数
     * @return 字节数组
     */
    public static byte[] intToByteArray(int value) {
        byte[] result = new byte[4];
        // 94为商议好的定界符，确认协议是否有效
        result[0] = (byte) 94;
        result[1] = (byte) ((value >> 16) & 0xFF);
        result[2] = (byte) ((value >> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }

    final void outLine() {
        mNetty.disconnect();
    }

    public final void writeData(byte b, byte b2, byte b3, String str, byte[] bArr) {
        byte[] input;
        if (str != null && !str.isEmpty()) {
            input = str.getBytes(StandardCharsets.UTF_8);
        } else if (bArr != null && bArr.length > 0) {
            input = bArr;
        } else {
            input = new byte[0];
        }
        if (mNetty.isConnected()) {
            mNetty.sendMessage(makeBytes(b, b2, b3, input));
        } else {
            outLineAndDialog();
        }
    }

    public static byte[] makeBytes(byte b, byte b2, byte b3, byte[] bArr) {
        byte[] bArr2 = new byte[]{b, b2, b3};
        byte[] obj2 = new byte[7 + bArr.length];
        byte[] bytes = intToByteArray(3 + bArr.length);
        System.arraycopy(bytes, 0, obj2, 0, 4);
        System.arraycopy(bArr2, 0, obj2, 4, 3);
        System.arraycopy(bArr, 0, obj2, 7, bArr.length);
        return obj2;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return jpBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        jpapplication = (JPApplication) getApplication();
        new Thread(this).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        outLine();
    }

    /**
     * 初始化Netty
     */
    private void initNetty() {
        mNetty = new ANetty(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                // 建立管道
                ChannelPipeline channelPipeline = ch.pipeline();
                // 添加相关编码器，解码器，处理器等
                channelPipeline
                        .addLast(new IdleStateHandler(0, 7, 0, TimeUnit.SECONDS))
                        .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, 1048576,
                                1, 3, 0, 4, true))
                        .addLast(new ByteArrayEncoder())
                        .addLast(new ByteArrayDecoder())
                        .addLast(new SimpleChannelInboundHandler<Object>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 接收到消息
                                byte[] bytes = (byte[]) msg;
                                Receive.receive(new String(bytes, StandardCharsets.UTF_8));
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                cause.printStackTrace();
                                ctx.close();
                            }

                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
                                if (obj instanceof IdleStateEvent) {
                                    IdleStateEvent event = (IdleStateEvent) obj;
                                    if (IdleState.WRITER_IDLE.equals(event.state())) {
                                        ctx.channel().writeAndFlush(new byte[]{94, 0, 0, 3, 41, 0, 0});
                                    }
                                }
                            }
                        });
            }
        }, false);
        mNetty.setOnConnectListener(new Netty.OnConnectListener() {

            @Override
            public void onSuccess() {
                // 连接成功
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("P", getPackageName());
//                    jSONObject.put("V", jpapplication.getVersionCode());
                    jSONObject.put("V", "20220218");
                    jSONObject.put("N", jpapplication.getAccountName());
                    jSONObject.put("K", JPApplication.kitiName);
                    jSONObject.put("C", jpapplication.getPassword());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                writeData((byte) 10, (byte) 0, (byte) 0, jSONObject.toString(), null);
            }

            @Override
            public void onFailed() {
                // 连接失败
                outLineAndDialog();
            }

            @Override
            public void onError(Exception e) {
                // 连接异常
                e.printStackTrace();
                outLineAndDialog();
            }
        });

        mNetty.setOnSendMessageListener(new Netty.OnSendMessageListener() {

            @Override
            public void onSendMessage(Object msg, boolean success) {
                // 发送消息的回调
                if (!success) {
                    Log.e("anetty", msg.toString());
                    outLineAndDialog();
                }
            }

            @Override
            public void onException(Throwable e) {
                // 异常
                e.printStackTrace();
                outLineAndDialog();
            }
        });
    }

    @Override
    public void run() {
        initNetty();
        mNetty.connect(jpapplication.getServer(), 8908);
    }

    private void outLineAndDialog() {
        outLine();
        if (JPStack.top() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) JPStack.top();
            Message message = new Message();
            message.what = 0;
            assert baseActivity != null;
            baseActivity.baseActivityHandler.handleMessage(message);
        }
    }
}
