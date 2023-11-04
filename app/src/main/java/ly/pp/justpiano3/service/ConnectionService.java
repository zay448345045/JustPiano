package ly.pp.justpiano3.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.protobuf.Descriptors;
import com.google.protobuf.MessageLite;

import java.util.UUID;
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
import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.OLBaseActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.handler.ProtobufEncryptionHandler;
import ly.pp.justpiano3.task.ReceiveTask;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.NettyUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.ReceiveTasks;
import protobuf.dto.OnlineBaseDTO;
import protobuf.dto.OnlineDeviceDTO;
import protobuf.dto.OnlineHeartBeatDTO;
import protobuf.dto.OnlineLoginDTO;
import protobuf.vo.OnlineBaseVO;

public class ConnectionService extends Service implements Runnable {

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
    public static final Long AUTO_RECONNECT_INTERVAL_TIME = 2000L;
    private final JPBinder jpBinder = new JPBinder(this);
    private String onlineSessionId;
    /**
     * 断线时的时间戳，用于断线重连判断，如果非空，表示目前没有掉线，断线自动重连成功后会置空
     */
    private Long autoReconnectTime;
    private int autoReconnectCount;
    private JPApplication jpapplication;
    private NettyUtil mNetty;

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
        // 关闭连接
        if (mNetty != null) {
            mNetty.close();
        }
    }

    public final void writeData(int type, MessageLite message) {
        OnlineBaseDTO.Builder builder = OnlineBaseDTO.newBuilder();
        Descriptors.FieldDescriptor fieldDescriptor = builder.getDescriptorForType().findFieldByNumber(type);
        builder.setField(fieldDescriptor, message);
        if (mNetty != null && mNetty.isConnected()) {
            if (type == OnlineProtocolType.LOGIN || autoReconnectTime == null) {
                OnlineUtil.setMsgTypeByChannel(mNetty.getChannelFuture().channel(), type);
                mNetty.sendMessage(builder);
                if (autoReconnectTime != null) {
                    handleOnTopBaseActivity(olBaseActivity -> {
                        if (autoReconnectTime != null) {
                            outLineAndDialogWithAutoReconnect();
                        }
                    }, AUTO_RECONNECT_INTERVAL_TIME);
                }
                Log.i(getClass().getSimpleName(), mNetty.getChannelFuture().channel().localAddress().toString()
                        + " autoReconnect! writeData autoReconnect:"
                        + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime) + " " + type);
            }
        } else {
            outLineAndDialogWithAutoReconnect();
        }
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
        mNetty = new NettyUtil(new ChannelInitializer<SocketChannel>() {
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
                        .addLast(new ProtobufEncryptionHandler(EncryptUtil.getServerPublicKey(), EncryptUtil.getDeviceKeyPair().getPrivate()))
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
                                Log.i(getClass().getSimpleName(), mNetty.getChannelFuture().channel().localAddress().toString()
                                        + " autoReconnect! channelRead0 autoReconnect:"
                                        + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime)
                                        + " " + msg.getResponseCase().getNumber());
                                autoReconnectTime = null;
                                autoReconnectCount = 0;
                                handleOnTopBaseActivity(olBaseActivity -> {
                                    olBaseActivity.jpprogressBar.setCancelable(true);
                                    olBaseActivity.jpprogressBar.setText("");
                                    if (olBaseActivity.jpprogressBar.isShowing()) {
                                        olBaseActivity.jpprogressBar.dismiss();
                                    }
                                }, 0L);
                                ReceiveTask receiveTask = ReceiveTasks.receiveTaskMap.get(msg.getResponseCase().getNumber());
                                if (receiveTask != null) {
                                    receiveTask.run(msg, JPStack.top(), Message.obtain());
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                cause.printStackTrace();
                                ctx.close();
                                outLineAndDialogWithAutoReconnect();
                            }

                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
                                if (obj instanceof IdleStateEvent) {
                                    IdleStateEvent event = (IdleStateEvent) obj;
                                    if (autoReconnectTime == null && ctx.channel().isActive() && IdleState.WRITER_IDLE.equals(event.state())) {
                                        writeData(OnlineProtocolType.HEART_BEAT, OnlineHeartBeatDTO.getDefaultInstance());
                                    }
                                }
                            }
                        });
            }
        });

        mNetty.setOnConnectListener(new NettyUtil.OnConnectListener() {
            @Override
            public void onSuccess() {
                if (autoReconnectTime == null) {
                    onlineSessionId = UUID.randomUUID().toString().replace("-", "");
                }
                OnlineLoginDTO.Builder builder = OnlineLoginDTO.newBuilder();
                builder.setAccount(jpapplication.getAccountName());
                builder.setPassword(jpapplication.getPassword());
                builder.setVersionCode(BuildConfig.VERSION_NAME);
                builder.setPackageName(getPackageName());
                builder.setOnlineSessionId(onlineSessionId);
                builder.setAutoReconnect(autoReconnectTime != null);
                builder.setPublicKey(EncryptUtil.generatePublicKeyString(EncryptUtil.getDeviceKeyPair().getPublic()));
                // 设备信息
                OnlineDeviceDTO.Builder deviceInfoBuilder = OnlineDeviceDTO.newBuilder();
                deviceInfoBuilder.setAndroidId(DeviceUtil.getAndroidId(jpapplication));
                deviceInfoBuilder.setVersion(DeviceUtil.getAndroidVersion());
                deviceInfoBuilder.setModel(DeviceUtil.getDeviceBrandAndModel());
                builder.setDeviceInfo(deviceInfoBuilder);
                writeData(OnlineProtocolType.LOGIN, builder.build());
            }

            @Override
            public void onFailed() {
                // 连接失败
                outLineAndDialogWithAutoReconnect();
            }

            @Override
            public void onError(Exception e) {
                // 连接异常
                e.printStackTrace();
                outLineAndDialogWithAutoReconnect();
            }
        });

        mNetty.setOnSendMessageListener(new NettyUtil.OnSendMessageListener() {
            @Override
            public void onSendMessage(Object msg, boolean success) {
                // 发送消息的回调
                if (!success) {
                    Log.e("autoReconnect! anetty", msg.toString() + JPStack.top());
                    outLineAndDialogWithAutoReconnect();
                }
            }

            @Override
            public void onException(Throwable e) {
                // 异常
                e.printStackTrace();
                outLineAndDialogWithAutoReconnect();
            }
        });
    }

    @Override
    public void run() {
        // 更新客户端公钥
        EncryptUtil.updateDeviceKeyPair();
        initNetty();
        mNetty.connect(OnlineUtil.server, ONLINE_PORT);
    }

    private void outLineAndDialogWithAutoReconnect() {
        Log.i(getClass().getSimpleName(), " autoReconnect! autoReconnect:"
                + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime));
        // 如果不是断线自动重连状态，先进行断线自动重连
        if (autoReconnectTime == null || System.currentTimeMillis() - autoReconnectTime < AUTO_RECONNECT_MAX_WAIT_TIME) {
            // 初始化断线重连变量
            if (autoReconnectTime == null) {
                autoReconnectTime = System.currentTimeMillis();
                autoReconnectCount = 0;
                handleOnTopBaseActivity(olBaseActivity -> {
                    if (!olBaseActivity.jpprogressBar.isShowing()) {
                        olBaseActivity.jpprogressBar.setText("断线重连中...等待连接剩余秒数：" + Math.max(0,
                                (AUTO_RECONNECT_MAX_WAIT_TIME - System.currentTimeMillis() + autoReconnectTime) / 1000));
                        olBaseActivity.jpprogressBar.setCancelable(false);
                        olBaseActivity.jpprogressBar.show();
                    }
                }, 0L);
            } else {
                handleOnTopBaseActivity(olBaseActivity -> {
                    if (olBaseActivity.jpprogressBar.isShowing() && autoReconnectTime != null) {
                        olBaseActivity.jpprogressBar.setText("断线重连中...等待连接剩余秒数：" + Math.max(0,
                                (AUTO_RECONNECT_MAX_WAIT_TIME - System.currentTimeMillis() + autoReconnectTime) / 1000));
                    }
                }, 0L);
            }
            // 每隔一小段时间尝试连接一次
            if (System.currentTimeMillis() - autoReconnectTime >= autoReconnectCount * AUTO_RECONNECT_INTERVAL_TIME) {
                outLine();
                handleOnTopBaseActivity(olBaseActivity -> {
                    if (olBaseActivity.jpprogressBar.isShowing()) {
                        olBaseActivity.jpprogressBar.setText("断线重连中...等待连接剩余秒数：" + Math.max(0,
                                (AUTO_RECONNECT_MAX_WAIT_TIME - System.currentTimeMillis() + autoReconnectTime) / 1000));
                        mNetty.connect(OnlineUtil.server, ONLINE_PORT);
                        Log.i(getClass().getSimpleName(), " autoReconnect! do autoReconnect:"
                                + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime));
                    }
                }, AUTO_RECONNECT_INTERVAL_TIME);
                autoReconnectCount = (int) ((System.currentTimeMillis() - autoReconnectTime) / AUTO_RECONNECT_INTERVAL_TIME) + 1;
            }
        } else {
            Log.i(getClass().getSimpleName(), mNetty.getChannelFuture().channel().localAddress().toString()
                    + " autoReconnect! fail autoReconnect:"
                    + (autoReconnectTime == null ? "null" : System.currentTimeMillis() - autoReconnectTime) + JPStack.top());
            outLine();
            Activity topActivity = JPStack.top();
            if (topActivity instanceof OLBaseActivity) {
                OLBaseActivity olBaseActivity = (OLBaseActivity) topActivity;
                Message message = Message.obtain(olBaseActivity.olBaseActivityHandler);
                message.what = 0;
                olBaseActivity.olBaseActivityHandler.handleMessage(message);
            }
        }
    }

    /**
     * 安卓低版本无法使用Consumer，暂时使用私有接口，配合下面方法入参使用
     */
    private interface OLBaseActivityRunner {
        void run(OLBaseActivity olBaseActivity);
    }

    private void handleOnTopBaseActivity(OLBaseActivityRunner consumer, long delayMillis) {
        Activity topActivity = JPStack.top();
        if (topActivity instanceof OLBaseActivity) {
            OLBaseActivity olBaseActivity = (OLBaseActivity) topActivity;
            olBaseActivity.olBaseActivityHandler.postDelayed(() -> consumer.run(olBaseActivity), delayMillis);
        }
    }
}
