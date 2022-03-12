package ly.pp.justpiano3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.protobuf.Descriptors;
import com.google.protobuf.MessageLite;
import com.king.anetty.ANetty;
import com.king.anetty.Netty;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import ly.pp.justpiano3.protobuf.dto.OnlineBaseDTO;
import ly.pp.justpiano3.protobuf.dto.OnlineHeartBeatDTO;
import ly.pp.justpiano3.protobuf.dto.OnlineLoginDTO;
import ly.pp.justpiano3.protobuf.vo.OnlineBaseVO;

public class ConnectionService extends Service implements Runnable {

    private final JPBinder jpBinder = new JPBinder(this);
    private ly.pp.justpiano3.JPApplication jpapplication;
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

    public final void writeData(int type, MessageLite message) {
        OnlineBaseDTO.Builder builder = OnlineBaseDTO.newBuilder();
        Descriptors.FieldDescriptor fieldDescriptor = builder.getDescriptorForType().findFieldByNumber(type);
        builder.setField(fieldDescriptor, message);
        if (mNetty.isConnected()) {
            mNetty.sendMessage(builder);
        } else {
            outLineAndDialog();
        }
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
        jpapplication = (ly.pp.justpiano3.JPApplication) getApplication();
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
                        .addLast(new ProtobufVarint32FrameDecoder())
                        .addLast(new ProtobufDecoder(OnlineBaseVO.getDefaultInstance()))
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        .addLast(new ProtobufEncoder())
                        .addLast(new IdleStateHandler(0, 7, 0, TimeUnit.SECONDS))
                        .addLast(new SimpleChannelInboundHandler<OnlineBaseVO>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, OnlineBaseVO msg) throws Exception {
                                int messageType = msg.getResponseCase().getNumber();
                                ly.pp.justpiano3.Receive.receive(messageType, msg);
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
                                        writeData(41, OnlineHeartBeatDTO.getDefaultInstance());
                                    }
                                }
                            }
                        });
            }
        }, false);
        mNetty.setOnConnectListener(new Netty.OnConnectListener() {

            @Override
            public void onSuccess() {
                OnlineLoginDTO.Builder builder = OnlineLoginDTO.newBuilder();
                builder.setAccount(jpapplication.getAccountName());
                builder.setPassword(jpapplication.getPassword());
                builder.setVersionCode("20220218");
                builder.setPackageName(getPackageName());
                writeData(10, builder.build());
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
        if (ly.pp.justpiano3.JPStack.top() instanceof ly.pp.justpiano3.BaseActivity) {
            ly.pp.justpiano3.BaseActivity baseActivity = (ly.pp.justpiano3.BaseActivity) ly.pp.justpiano3.JPStack.top();
            Message message = new Message();
            message.what = 0;
            assert baseActivity != null;
            baseActivity.baseActivityHandler.handleMessage(message);
        }
    }
}
