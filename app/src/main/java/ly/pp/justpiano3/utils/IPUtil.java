package ly.pp.justpiano3.utils;

import java.net.InetAddress;

/**
 * @author as
 */
public class IPUtil {

    /**
     * 验证是否为合法的ip地址
     */
    public static boolean isValidIP(String ip) {
        // 检查是否为空
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // 尝试创建InetAddress实例
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
