package ly.pp.justpiano3.handler;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.utils.EncryptUtil;
import ly.pp.justpiano3.utils.OnlineUtil;

public class ProtobufEncryptionHandler extends ChannelDuplexHandler {

    /**
     * 指定不加密的消息类型，必须和服务端设置的一致，非必要不修改
     */
    private static final List<Integer> NOT_ENCRYPT_MSG_TYPE = Arrays.asList(OnlineProtocolType.KEYBOARD, OnlineProtocolType.HEART_BEAT);

    /**
     * RSA加密器
     * <p>
     * 客户端：采用服务端的公钥加密一个常量字符串
     * 服务端：采用客户端的公钥加密
     */
    private final Cipher RSAEncryptCipher;

    /**
     * RSA解密器
     * <p>
     * 客户端：采用客户端的私钥解密
     * 服务端：采用服务端的私钥解密
     */
    private final Cipher RSADecryptCipher;

    public ProtobufEncryptionHandler(PublicKey publicKey, PrivateKey privateKey) throws Exception {
        RSAEncryptCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        RSAEncryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        RSADecryptCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        RSADecryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            // 读取数据是否加密的标记
            if (byteBuf.readBoolean()) {
                // 数据形式为加密，需进行解密操作
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                // 解密消息内容
                byte[] decryptedBytes = EncryptUtil.cipherHandle(Cipher.DECRYPT_MODE, RSADecryptCipher, flipBytes(bytes));
                // 将解密后的消息内容传递给下一个处理器
                ctx.fireChannelRead(Unpooled.wrappedBuffer(decryptedBytes));
            } else {
                ctx.fireChannelRead(byteBuf);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            // 使用CompositeByteBuf进行组装，减少内存拷贝，提升性能
            CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer(2);
            // 判断消息是否需要加密
            if (NOT_ENCRYPT_MSG_TYPE.contains(OnlineUtil.getMsgTypeByChannel(ctx.channel()))) {
                // 无需加密：在ByteBuf数据开头加入false标记，然后发送
                compositeByteBuf.addComponent(true, Unpooled.buffer(1).writeBoolean(false));
                compositeByteBuf.addComponent(true, Unpooled.wrappedBuffer(byteBuf));
            } else {
                // 读取消息内容
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(byteBuf.readerIndex(), bytes);
                // 执行RSA加密
                byte[] encryptedBytes = EncryptUtil.cipherHandle(Cipher.ENCRYPT_MODE, RSAEncryptCipher, bytes);
                // 在数据开头加入true标记表示加密，组装后执行发送
                compositeByteBuf.addComponent(true, Unpooled.buffer(1).writeBoolean(true));
                compositeByteBuf.addComponent(true, Unpooled.wrappedBuffer(flipBytes(encryptedBytes)));
            }
            ctx.writeAndFlush(compositeByteBuf, promise);
        } else {
            ctx.writeAndFlush(msg, promise);
        }
    }

    /**
     * 数组翻转
     *
     * @param bytes
     * @return
     */
    private byte[] flipBytes(byte[] bytes) {
        if (bytes == null) {
            return bytes;
        }
        int i = 0;
        int j = bytes.length - 1;
        byte tmp;
        while (j > i) {
            tmp = bytes[j];
            bytes[j] = bytes[i];
            bytes[i] = tmp;
            j--;
            i++;
        }
        return bytes;
    }
}
