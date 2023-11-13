package ly.pp.justpiano3.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyUtil {

    public interface OnConnectListener {
        void onSuccess();

        void onFailed();

        void onError(Exception e);
    }

    /**
     * 发送消息监听
     */
    public interface OnSendMessageListener {
        void onSendMessage(Object msg, boolean success);

        void onException(Throwable e);
    }

    private static final int NETTY_INIT = 1;
    private static final int NETTY_CONNECT = 2;
    private static final int NETTY_SEND_MESSAGE = 3;

    private HandlerThread mHandlerThread;

    private Handler mHandler;

    private Handler mMainHandler;

    private String mHost;

    private int mPort;

    private ChannelFuture mChannelFuture;

    private Bootstrap mBootstrap;

    private final ChannelInitializer<SocketChannel> mChannelInitializer;

    private OnConnectListener mOnConnectListener;

    private OnSendMessageListener mOnSendMessageListener;

    /**
     * 构造
     *
     * @param channelInitializer {@link ChannelInitializer}
     */
    public NettyUtil(ChannelInitializer<SocketChannel> channelInitializer) {
        this.mChannelInitializer = channelInitializer;
        initHandlerThread();
        mHandler.sendEmptyMessage(NETTY_INIT);
    }

    private void initHandlerThread() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mHandlerThread = new HandlerThread(NettyUtil.class.getSimpleName());
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case NETTY_INIT:
                        handleNettyInit();
                        break;
                    case NETTY_CONNECT:
                        handleConnect();
                        break;
                    case NETTY_SEND_MESSAGE:
                        handleSendMessage(msg.obj);
                        break;
                }
            }
        };
    }

    private void handleNettyInit() {
        mBootstrap = new Bootstrap();
        mBootstrap.channel(NioSocketChannel.class);
        EventLoopGroup mGroup = new NioEventLoopGroup();
        mBootstrap.group(mGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, false)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new LoggingHandler(LogLevel.DEBUG));
        mBootstrap.handler(mChannelInitializer);
    }

    private void handleConnect() {
        try {
            mChannelFuture = mBootstrap.connect(mHost, mPort)
                    .addListener(future -> {
                        boolean isSuccess = future.isSuccess();
                        if (mOnConnectListener != null) {
                            mMainHandler.post(() -> {
                                if (isSuccess) {
                                    mOnConnectListener.onSuccess();
                                } else {
                                    mOnConnectListener.onFailed();
                                }
                            });

                        }
                    })
                    .sync();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnConnectListener != null) {
                mMainHandler.post(() -> mOnConnectListener.onError(e));
            }
        }
    }

    private void handleSendMessage(Object msg) {
        try {
            if (isOpen()) {
                mChannelFuture.channel().writeAndFlush(msg).addListener(future -> {
                    boolean isSuccess = future.isSuccess();
                    if (mOnSendMessageListener != null) {
                        mMainHandler.post(() -> mOnSendMessageListener.onSendMessage(msg, isSuccess));
                    }

                }).sync();
            } else {
                if (mOnSendMessageListener != null) {
                    mMainHandler.post(() -> mOnSendMessageListener.onSendMessage(msg, false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnSendMessageListener != null) {
                mMainHandler.post(() -> mOnSendMessageListener.onException(e));
            }
        }
    }

    public void connect(String host, int port) {
        if (isConnected()) {
            return;
        }
        this.mHost = host;
        this.mPort = port;
        mHandler.sendEmptyMessage(NETTY_CONNECT);
    }

    public void sendMessage(Object msg) {
        mHandler.obtainMessage(NETTY_SEND_MESSAGE, msg).sendToTarget();
    }

    public void setOnConnectListener(OnConnectListener listener) {
        this.mOnConnectListener = listener;
    }

    public void setOnSendMessageListener(OnSendMessageListener listener) {
        this.mOnSendMessageListener = listener;
    }

    public ChannelFuture close() {
        if (mChannelFuture == null) {
            return null;
        }
        try {
            return mChannelFuture.channel().close().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        return mChannelFuture != null && mChannelFuture.channel().isActive();
    }

    public boolean isOpen() {
        return mChannelFuture != null && mChannelFuture.channel().isOpen();
    }

    public ChannelFuture getChannelFuture() {
        return mChannelFuture;
    }
}
