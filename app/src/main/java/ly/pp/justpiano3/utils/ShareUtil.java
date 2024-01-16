package ly.pp.justpiano3.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 分享Util
 */
public final class ShareUtil {

    private static final String JPG_SUFFIX = ".jpg";

    public static void share(Activity activity) {
        View rootView = activity.getWindow().getDecorView().getRootView();
        // 这是在API小于26的情况下的备用方案
        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);
        String fileName = DateUtil.format(DateUtil.now(), DateUtil.TEMPLATE_DEFAULT_CHINESE) + JPG_SUFFIX;
        saveBitmapToJPG(activity, bitmap, fileName);
        shareImage(activity, fileName);
    }

    private static void saveBitmapToJPG(Context context, Bitmap bitmap, String filename) {
        File dir = new File(context.getCacheDir(), "Share");
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
        File dir = new File(activity.getCacheDir(), "Share");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileProvider", file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(intent, "😘分享给小伙伴们吧~"));
        }
    }
}
