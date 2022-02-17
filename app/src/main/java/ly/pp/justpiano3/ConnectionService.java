package ly.pp.justpiano3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ConnectionService extends Service implements Runnable {
    private SocketChannel socketChannel = null;
    private ByteBuffer writeBuffer = ByteBuffer.allocateDirect(50 * 1024);
    private Selector selector = null;
    private SelectionKey selectionKey = null;
    private final JPBinder jpBinder = new JPBinder(this);
    private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(50 * 1024);
    private final ByteBuffer cacheBuffer = ByteBuffer.allocateDirect(100 * 1024);
    private int bodyLen = -1;
    private boolean cache = false;  // 是否有缓存
    private boolean online = true;
    private JPApplication jpapplication;

    // 字符数组转整数
    private static int byteArrayToInt(byte[] bytes) {
        if (bytes[0] != 94) {
            return -1;
        }
        int value = 0;
        for (int i = 1; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }

    final void outLine() {
        try {
            online = false;
            if (socketChannel != null) {
                if (selectionKey != null) {
                    selectionKey.cancel();
                }
                if (socketChannel.isConnected()) {
                    socketChannel.finishConnect();
                }
                if (socketChannel.isOpen()) {
                    socketChannel.close();
                }
                if (selector.isOpen()) {
                    selector.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeData(byte b, byte b2, byte b3, String str, byte[] bArr) {
        switch (b) {
            case (byte) 2:    //查看详细资料
            case (byte) 7:    //进入房间
            case (byte) 16:    //挑战模式
            case (byte) 18:   //家族系列
            case (byte) 19:    //加载大厅房间
            case (byte) 26:    //商店
            case (byte) 28:    //刚进入对战加载大厅和人物数据
            case (byte) 29:    //进入大厅
            case (byte) 31:    //解除搭档
            case (byte) 32:    //弹奏左上角显示
            case (byte) 33:    //保存服装
            case (byte) 34:    //查看好友或搭档
            case (byte) 35:    //发送私信
            case (byte) 36:    //大厅查看用户列表
            case (byte) 37:    //找Ta及显示对话框
            case (byte) 38:    //每日奖励
            case (byte) 39:    // 键盘模式传输弹奏音符
            case (byte) 40:    //等级考试
            case (byte) 41:    //疑似心跳包的东西
            case (byte) 43:    //显示房间内成员信息
            case (byte) 44:    //改变左右手
            case (byte) 45:    //送祝福
                writeBuffer = JsonHandle.m3945a(b, b2, b3, str);
                break;
            case (byte) 3:    //开始弹奏
            case (byte) 4:    //准备和取消准备
            case (byte) 5:    //弹奏成绩
            case (byte) 12:    //大厅聊天
            case (byte) 13:    //房间聊天
            case (byte) 14:    //更换房名密码
            case (byte) 15:    //播放曲谱
                writeBuffer = JsonHandle.m3952b(b, b2, b3, str);
                break;
            case (byte) 6:    //创建房间
                writeBuffer = JsonHandle.m3945a(b, b3, b2, str);
                break;
            case (byte) 8:    //退出房间
            case (byte) 21:    //加载房间内人物数据
            case (byte) 23:    //联网对战载入用户
            case (byte) 24:    //开始弹奏
            case (byte) 30:    //退出大厅
                writeBuffer = JsonHandle.m3945a(b, b2, b3, "");
                break;
            case (byte) 9:    //被踢出房间
            case (byte) 25:    //弹奏逐个音符状态的数据包上传
            case (byte) 42:    //打开/关闭空位
                writeBuffer = JsonHandle.m3946a(b, b2, b3, bArr);
                break;
            case (byte) 10:    //登录进入对战
                writeBuffer = ByteBuffer.wrap(new byte[]{b, b2, b3});
                break;
        }
        if (writeBuffer != null) {
            writeBuffer.flip();
            if (selectionKey != null && selectionKey.isValid()) {
                selectionKey.attach(writeBuffer);
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                selectionKey.selector().wakeup();
            }
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
        ThreadPoolUtils.execute(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        outLine();
    }

    @Override
    public void run() {
        try {
            InetSocketAddress socketAddr = new InetSocketAddress(jpapplication.getServer(), 8908);
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(socketAddr);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            while (online) {
                if (selector.select() != 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    for (SelectionKey selectionKey : selectedKeys) {
                        if (selectionKey.isConnectable() && socketChannel.finishConnect()) {
                            writeBuffer.clear();
                            try {
                                String str = "20220218";
                                writeBuffer = JsonHandle.sendLogin(jpapplication.getAccountName(), JPApplication.kitiName, getPackageName(), str, jpapplication.getPassword());
                                writeBuffer.flip();
                                selectionKey.attach(writeBuffer);
                                selectionKey.interestOps(SelectionKey.OP_WRITE);
                                ThreadPoolUtils.execute(() -> {
                                    try {
                                        while (online) {
                                            Thread.sleep(6000);
                                            writeData((byte) 41, (byte) 0, (byte) 0, "", null);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectionKey.isValid() && selectionKey.isReadable()) {
                            readBuffer.clear();
                            int read = socketChannel.read(readBuffer);
                            if (read > 0) {
                                readBuffer.flip();
                                while (readBuffer.hasRemaining()) {
                                    if (bodyLen == -1) {  // 还没有读出包头，先读出包头
                                        if (readBuffer.remaining() >= 4) {  // 可以读出包头
                                            byte[] headByte = new byte[4];
                                            readBuffer.get(headByte);
                                            bodyLen = byteArrayToInt(headByte);
                                            if (bodyLen == -1) {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    } else {  // 已经读出包头
                                        int count = new String(readBuffer.array()).trim().getBytes().length;
                                        count = Math.min(count, read);
                                        count = cache ? count : count - 4;
                                        if (count >= bodyLen) {  // 大于等于一个包，否则缓存
                                            String str;
                                            if (cache) {
                                                cache = false;
                                                byte[] temp = new byte[count];
                                                readBuffer.get(temp, 0, count);
                                                cacheBuffer.put(temp);
                                                cacheBuffer.flip();
                                                byte[] bodyByte = new byte[cacheBuffer.remaining()];
                                                cacheBuffer.get(bodyByte);
                                                str = new String(bodyByte, StandardCharsets.UTF_8);
                                                cacheBuffer.clear();
                                            } else {
                                                byte[] bodyByte = new byte[bodyLen];
                                                readBuffer.get(bodyByte, 0, bodyLen);
                                                str = new String(bodyByte, StandardCharsets.UTF_8);
                                            }
                                            bodyLen = -1;
                                            if (str.isEmpty()) {
                                                selectedKeys.remove(selectionKey);
                                                outLine();
                                                return;
                                            }
                                            Receive.receive(str);  // 传回数据
                                        } else {
                                            cache = true;
                                            byte[] temp = new byte[count];
                                            readBuffer.get(temp, 0, count);
                                            cacheBuffer.put(temp);
                                            bodyLen -= count;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (selectionKey.isValid() && selectionKey.isWritable()) {
                            this.selectionKey = selectionKey;
                            writeBuffer.clear();
                            writeBuffer = (ByteBuffer) selectionKey.attachment();
                            if (writeBuffer != null) {
                                selectionKey.attach(null);
                                socketChannel.write(writeBuffer);
                                writeBuffer.flip();
                            }
                            selectionKey.interestOps(SelectionKey.OP_READ);
                        }
                        selectedKeys.remove(selectionKey);
                    }
                }
            }
        } catch (Exception e2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
            e2.printStackTrace();
            outLine();
            JPStack.create();
            if (JPStack.top() instanceof BaseActivity) {
                JPStack.create();
                BaseActivity baseActivity = (BaseActivity) JPStack.top();
                Message message = new Message();
                message.what = 0;
                baseActivity.baseActivityHandler.handleMessage(message);
            }
        }
    }
}
