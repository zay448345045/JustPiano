package ly.pp.justpiano3.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 分享Util
 */
public class ShareUtil {

    private static final String JPG_SUFFIX = ".jpg";

    public static void share(Activity activity) {
        View rootView = activity.getWindow().getDecorView().getRootView();
        // 这是在API小于26的情况下的备用方案
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);
        String fileName = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒", Locale.CHINESE).format(new Date(System.currentTimeMillis())) + JPG_SUFFIX;
        saveBitmapToJPG(bitmap, fileName);
        shareImage(activity, fileName);
    }

    private static void saveBitmapToJPG(Bitmap bitmap, String filename) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/JustPiano/share/"); // 替换为你自己的目录
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void shareImage(Activity activity, String filename) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/JustPiano/share/", filename); // 同上
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(intent, "😘分享给小伙伴们吧~"));
    }
}
