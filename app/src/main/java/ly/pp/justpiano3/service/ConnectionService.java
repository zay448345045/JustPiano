package ly.pp.justpiano3.service;

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
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.Receive;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.handler.ProtobufEncryptionHandler;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import protobuf.dto.OnlineBaseDTO;
import protobuf.dto.OnlineDeviceDTO;
import protobuf.dto.OnlineHeartBeatDTO;
import protobuf.dto.OnlineLoginDTO;
import protobuf.vo.OnlineBaseVO;

import java.util.concurrent.TimeUnit;

public class ConnectionService extends Service implements Runnable {

    /**
     * 对战服务端口
     */
    public static final Integer ONLINE_PORT = 8908;

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

    public final void outLine() {
        if (mNetty != null) {
            mNetty.disconnect();
        }
    }

    public final void writeData(int type, MessageLite message) {
        OnlineBaseDTO.Builder builder = OnlineBaseDTO.newBuilder();
        Descriptors.FieldDescriptor fieldDescriptor = builder.getDescriptorForType().findFieldByNumber(type);
        builder.setField(fieldDescriptor, message);
        if (mNetty != null && mNetty.isConnected()) {
            OnlineUtil.setMsgTypeByChannel(mNetty.getChannelFuture().channel(), type);
            mNetty.sendMessage(builder);
        } else {
            outLineAndDialog();
        }
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
            protected void initChannel(SocketChannel ch) throws Exception {
                // 建立管道
                ChannelPipeline channelPipeline = ch.pipeline();
                // 添加相关编码器，解码器，处理器等
                channelPipeline
                        // 入站处理器执行顺序是从上往下看，出站处理器执行顺序是从下往上
                        // 处理器从收到数据包到发送数据包的顺序，看注释的数字序号
                        // 1.解析服务端在包头添加的数据包长度（防止粘包）
                        .addLast(new ProtobufVarint32FrameDecoder())
                        // 8.在包头添加数据包的长度（防止粘包）
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        // 2.下面解析器内部的channelRead方法进行数据包解密，使用客户端生成的私钥进行解密
                        // 7.下面解析器内部的write方法进行数据包加密，使用服务端生成的公钥进行加密
                        .addLast(new ProtobufEncryptionHandler(jpapplication.getServerPublicKey(), jpapplication.getDeviceKeyPair().getPrivate()))
                        // 3.将数据包内容（字节数组）反序列化为protobuf VO对象
                        .addLast(new ProtobufDecoder(OnlineBaseVO.getDefaultInstance()))
                        // 6.将protobuf DTO对象序列化为数据包字节数组
                        .addLast(new ProtobufEncoder())
                        // 5.心跳包发送，参数writerIdleTime指空闲多少秒之后发一个心跳包给服务器
                        .addLast(new IdleStateHandler(0, 7, 0, TimeUnit.SECONDS))
                        // 4.protobuf对象进行java代码处理，在channelRead0方法中
                        .addLast(new SimpleChannelInboundHandler<OnlineBaseVO>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, OnlineBaseVO msg) throws Exception {
                                Receive.receive(msg.getResponseCase().getNumber(), msg);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                cause.printStackTrace();
                                ctx.close();
                                outLineAndDialog();
                            }

                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
                                if (obj instanceof IdleStateEvent) {
                                    IdleStateEvent event = (IdleStateEvent) obj;
                                    if (IdleState.WRITER_IDLE.equals(event.state())) {
                                        writeData(OnlineProtocolType.HEART_BEAT, OnlineHeartBeatDTO.getDefaultInstance());
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
                builder.setVersionCode(DeviceUtil.getVersionName(jpapplication));
                builder.setPackageName(getPackageName());
                builder.setPublicKey(EncryptUtil.generatePublicKeyString(jpapplication.getDeviceKeyPair().getPublic()));
                // 设备信息
                OnlineDeviceDTO.Builder deviceInfoBuilder = OnlineDeviceDTO.newBuilder();
                deviceInfoBuilder.setAndroidId(DeviceUtil.getAndroidId(jpapplication.getApplicationContext()));
                deviceInfoBuilder.setVersion(DeviceUtil.getAndroidVersion());
                deviceInfoBuilder.setModel(DeviceUtil.getDeviceBrandAndModel());
                builder.setDeviceInfo(deviceInfoBuilder);
                writeData(OnlineProtocolType.LOGIN, builder.build());
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
        // 更新客户端公钥
        jpapplication.updateDeviceKeyPair();
        initNetty();
        mNetty.connect(jpapplication.getServer(), ONLINE_PORT);
    }

    private void outLineAndDialog() {
        outLine();
        if (JPStack.top() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) JPStack.top();
            Message message = Message.obtain(baseActivity.baseActivityHandler);
            message.what = 0;
            baseActivity.baseActivityHandler.handleMessage(message);
        }
    }
}
