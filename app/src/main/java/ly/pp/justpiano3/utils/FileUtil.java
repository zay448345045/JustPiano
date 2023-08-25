package ly.pp.justpiano3.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil {

    /**
     * 移动文件到新文件的位置（拷贝流）
     *
     * @param src 源文件对象
     * @param des 目标文件对象
     */
    public static boolean moveFile(File src, File des) {
        if (!src.exists()) {
            return false;
        }
        if (des.exists()) {
            des.delete();
        }
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(src)); BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(des))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            src.delete();
        }
        return true;
    }
}
