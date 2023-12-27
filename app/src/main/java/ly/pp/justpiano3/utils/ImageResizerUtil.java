package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;

/**
 * Create by SunnyDay on 2019/04/18
 * 图片压缩工具类，用于完成图片的缩放处理，可以避免oom。
 * <p>
 * 本类主要提供两种类型的位图压缩，资源id类型、文件类型。
 */
public class ImageResizerUtil {
    private static final String TAG = "ImageResizer";
    private final Context context;

    /**
     * 构造方法 用于初始化上下文
     */
    public ImageResizerUtil(Context context) {
        this.context = context;
    }

    /**
     * @param resourceID 资源id
     * @param resources  资源对象
     * @function 缩放资源id类型的位图
     */
    public Bitmap decodeSampledBitmapFromResource(Resources resources, int resourceID) {
        // 不真正解码位图，只获得位图的一些信息(inJustDecodeBounds = true时)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resourceID, options);
        int weight = options.outWidth;
        int height = options.outHeight;
        // 计算缩放比（手动指定比较快捷，我们这里根据手机尺寸计算,具体实现自己而定）
        options.inSampleSize = caculateScale(weight, height);

        options.inJustDecodeBounds = false;// 真正解码  按照缩放比
        return BitmapFactory.decodeResource(resources, resourceID, options);
    }

    /**
     * @param fd
     * @function 缩放资源文件类型的位图
     * <p>
     * 和decodeSampledBitmapFromResource方法功能类似 注释可以参考这个方法
     */
    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        int weight = options.outWidth;
        int height = options.outHeight;
        options.inSampleSize = caculateScale(weight, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);

    }
//------------------------------工具类--------------------------------------------------------

    /**
     * @param weight 图片宽
     * @param height 图片高
     * @return 缩放比例
     * @function 计算缩放比例（根据手机尺寸）
     */
    private int caculateScale(int weight, int height) {
        // 获得手机宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int phoneWidth = point.x;
        int phoneHeight = point.y;
        int scale = 1;//默认缩放比为1

        int scaleX = weight / phoneWidth;
        int scaleY = height / phoneHeight;
        //图片的宽高比手机尺寸大时在缩放

        if (scaleX >= scaleY && scaleX > scale) {
            scale = scaleX;
        }
        if (scaleY > scaleX && scaleY > scale) {
            scale = scaleY;
        }
        Log.i(TAG, "缩放比例为： " + scale);
        return scale;
    }

    public static byte[] compressScale(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 32f;
        int be = 1;// be=1表示不缩放
        if (w > h && w > hh) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / hh);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    private static byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        return baos.toByteArray();
    }
}
