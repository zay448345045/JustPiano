package ly.pp.justpiano3.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.google.protobuf.Descriptors;
import com.google.protobuf.MessageLite;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
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
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.handler.ProtobufEncryptionHandler;
import ly.pp.justpiano3.task.ReceiveTask;
import ly.pp.justpiano3.utils.DeviceUtil;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.NettyUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.utils.ReceiveTasks;
import ly.pp.justpiano3.utils.WakeLockUtil;
import protobuf.dto.OnlineBaseDTO;
import protobuf.dto.OnlineDeviceDTO;
import protobuf.dto.OnlineHeartBeatDTO;
import protobuf.dto.OnlineLoginDTO;
import protobuf.vo.OnlineBaseVO;

public final class ConnectionService extends Service {
    public static final String CHANNEL_ID = "1";
    public static final String CHANNEL_NAME = "联网模式";
    public static final String NOTIFY_CONTENT_TITLE = "JustPiano keep alive";
    public static final String NOTIFY_CONTENT_TEXT = "联网模式已连接...";
    public static final int NOTIFICATION_ID = 1;
    private final JPBinder jpBinder = new JPBinder(this);
    private NettyUtil nettyUtil;

    public static class JPBinder extends Binder {
        private final ConnectionService connectionService;

        JPBinder(ConnectionService connectionService) {
            this.connectionService = connectionService;
        }

        public ConnectionService getConnectionService() {
            return connectionService;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return jpBinder;
    }

    public void writeData(int type, MessageLite message) {
        Log.i("autoReconnect", "writeData: " + nettyUtil + " " + type);
        if (nettyUtil != null && nettyUtil.isConnected()) {
            OnlineBaseDTO.Builder builder = OnlineBaseDTO.newBuilder();
            Descriptors.FieldDescriptor fieldDescriptor = builder.getDescriptorForType().findFieldByNumber(type);
            builder.setField(fieldDescriptor, message);
            if (type == OnlineProtocolType.LOGIN || !OnlineUtil.autoReconnecting()) {
                OnlineUtil.setMsgTypeByChannel(nettyUtil.getChannelFuture().channel(), type);
                nettyUtil.sendMessage(builder);
            }
        } else {
            OnlineUtil.outLineAndDialogWithAutoReconnect(getApplicationContext());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        foregroundServiceHandle();
        initNetty();
        if (GlobalSetting.getWakeLock()) {
            WakeLockUtil.acquireWakeLock(this, "JustPiano:JPWakeLock");
        }
    }

    private void foregroundServiceHandle() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(serviceChannel);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ConnectionService.class));
            } else {
                startService(new Intent(this, ConnectionService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(NOTIFY_CONTENT_TITLE)
                    .setContentText(NOTIFY_CONTENT_TEXT)
                    .setSmallIcon(R.drawable.icon);
            Activity topActivity = JPStack.top();
            if (topActivity != null) {
                Intent targetIntent = new Intent(this, topActivity.getClass());
                targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                notificationBuilder.setContentIntent(pendingIntent);
            }
            Notification notification = notificationBuilder.build();
            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
            }
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        outLine();
        if (GlobalSetting.getWakeLock()) {
            WakeLockUtil.releaseWakeLock();
        }
        super.onDestroy();
    }

    /**
     * 初始化Netty
     */
    public void initNetty() {
        nettyUtil = new NettyUtil(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NonNull SocketChannel socketChannel) throws Exception {
                // 添加相关编码器，解码器，处理器等
                socketChannel.pipeline()
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
                        .addLast(new IdleStateHandler(0, 6, 0, TimeUnit.SECONDS))
                        // 4.protobuf对象进行java代码处理，在channelRead0方法中
                        .addLast(new SimpleChannelInboundHandler<OnlineBaseVO>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, OnlineBaseVO msg) throws Exception {
                                OnlineUtil.cancelAutoReconnect();
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
                                OnlineUtil.outLineAndDialogWithAutoReconnect(getApplicationContext());
                            }

                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
                                if (obj instanceof IdleStateEvent event) {
                                    if (!OnlineUtil.autoReconnecting() && ctx.channel().isActive() && IdleState.WRITER_IDLE.equals(event.state())) {
                                        writeData(OnlineProtocolType.HEART_BEAT, OnlineHeartBeatDTO.getDefaultInstance());
                                    }
                                } else {
                                    super.userEventTriggered(ctx, obj);
                                }
                            }
                        });
            }
        });
        nettyUtil.setOnConnectListener(new NettyUtil.OnConnectListener() {
            @Override
            public void onSuccess() {
                if (!OnlineUtil.autoReconnecting()) {
                    OnlineUtil.onlineSessionId = UUID.randomUUID().toString().replace("-", "");
                }
                OnlineLoginDTO.Builder builder = OnlineLoginDTO.newBuilder();
                builder.setAccount(OLBaseActivity.getAccountName());
                builder.setPassword(OLBaseActivity.getPassword());
                builder.setVersionCode(BuildConfig.VERSION_NAME);
                builder.setPackageName(getApplicationContext().getPackageName());
                builder.setOnlineSessionId(OnlineUtil.onlineSessionId);
                builder.setAutoReconnect(OnlineUtil.autoReconnecting());
                builder.setPublicKey(EncryptUtil.generatePublicKeyString(EncryptUtil.getDeviceKeyPair().getPublic()));
                // 设备信息
                OnlineDeviceDTO.Builder deviceInfoBuilder = OnlineDeviceDTO.newBuilder();
                deviceInfoBuilder.setAndroidId(DeviceUtil.getAndroidId(getApplicationContext()));
                deviceInfoBuilder.setVersion(DeviceUtil.getAndroidVersion());
                deviceInfoBuilder.setModel(DeviceUtil.getDeviceBrandAndModel());
                builder.setDeviceInfo(deviceInfoBuilder);
                writeData(OnlineProtocolType.LOGIN, builder.build());
            }

            @Override
            public void onFailed() {
                // 连接失败
                OnlineUtil.outLineAndDialogWithAutoReconnect(getApplicationContext());
            }

            @Override
            public void onError(Exception e) {
                // 连接异常
                e.printStackTrace();
                OnlineUtil.outLineAndDialogWithAutoReconnect(getApplicationContext());
            }
        });
        nettyUtil.setOnSendMessageListener(new NettyUtil.OnSendMessageListener() {
            @Override
            public void onSendMessage(Object msg, boolean success) {
                // 发送消息的回调
                if (!success) {
                    Log.e(getClass().getSimpleName(), "autoReconnect! onSendMessage" + msg.toString());
                    OnlineUtil.outLineAndDialogWithAutoReconnect(getApplicationContext());
                }
            }

            @Override
            public void onException(Throwable e) {
                // 异常
                e.printStackTrace();
                OnlineUtil.outLineAndDialogWithAutoReconnect(getApplicationContext());
            }
        });
        nettyUtil.connect(OnlineUtil.server, OnlineUtil.ONLINE_PORT);
    }

    public void outLine() {
        Log.i("autoReconnect", "outLine: " + nettyUtil);
        // 关闭连接
        if (nettyUtil != null) {
            nettyUtil.close();
            nettyUtil.destroy();
            nettyUtil = null;
        }
        try {
            stopForeground(true);
            stopSelf();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        getApplication().onTerminate();
    }
}
