package ly.pp.justpiano3.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import ly.pp.justpiano3.utils.EncryptUtil;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ProtobufEncryptionHandler extends ChannelDuplexHandler {

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
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            // 解密消息内容
            byte[] decryptedBytes = EncryptUtil.cipherHandle(Cipher.DECRYPT_MODE, RSADecryptCipher, flipBytes(bytes));
            // 将解密后的消息内容传递给下一个处理器
            ctx.fireChannelRead(Unpooled.wrappedBuffer(decryptedBytes));
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            // 读取消息内容
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            // RSA加密原始密钥
            byte[] encryptedBytes = EncryptUtil.cipherHandle(Cipher.ENCRYPT_MODE, RSAEncryptCipher, bytes);
            // 创建消息缓冲区
            ByteBuf encryptedByteBuf = Unpooled.buffer(encryptedBytes.length);
            // 写入消息内容
            encryptedByteBuf.writeBytes(flipBytes(encryptedBytes));
            // 发送加密后的消息
            ctx.write(encryptedByteBuf, promise);
        } else {
            ctx.write(msg, promise);
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
