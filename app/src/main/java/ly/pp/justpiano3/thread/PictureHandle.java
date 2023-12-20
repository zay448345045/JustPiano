package ly.pp.justpiano3.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import ly.pp.justpiano3.utils.OnlineUtil;

public final class PictureHandle {
    public boolean running = true;
    public Map<String, SoftReference<Bitmap>> bitmapMap = new HashMap<>();
    public Handler handler;
    public static final String JPG_SUFFIX = ".jpg";
    private PictureHandleThread pictureHandleThread;
    private final int picType;

    public PictureHandle(Handler handler, int i) {
        this.handler = handler;
        picType = i;
    }

    public Bitmap getBitmapFromRemote(String str) {
        if (!str.endsWith(JPG_SUFFIX)) {
            return null;
        }
        try {
            URL url;
            switch (picType) {
                case 0:
                    url = new URL("http://" + OnlineUtil.server + ":8910/file/NailImage/" + str);
                    break;
                case 1:
                    url = new URL("http://" + OnlineUtil.server + ":8910/file/Image/" + str);
                    break;
                case 2:
                    url = new URL("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/PicSkin" + str);
                    break;
                case 3:
                    url = new URL("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/PicSound" + str);
                    break;
                default:
                    url = null;
                    break;
            }
            if (url == null) {
                return null;
            }
            Bitmap decodeStream;
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            if (inputStream != null) {
                decodeStream = BitmapFactory.decodeStream(inputStream);
                return decodeStream;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        if (bitmapMap != null) {
            for (Map.Entry<String, SoftReference<Bitmap>> value : bitmapMap.entrySet()) {
                Bitmap bitmap = value.getValue().get();
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            bitmapMap.clear();
        }
        if (pictureHandleThread != null) {
            pictureHandleThread.release();
        }
    }

    public void setBitmap(ImageView imageView, Bitmap bitmap) {
        String str = (String) imageView.getTag();
        if (bitmapMap.containsKey(str)) {
            Bitmap bitmap2 = bitmapMap.get(str).get();
            if (bitmap2 != null) {
                imageView.setImageBitmap(bitmap2);
                return;
            } else {
                bitmapMap.remove(str);
            }
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        if (pictureHandleThread == null) {
            pictureHandleThread = new PictureHandleThread(this, imageView, str);
            pictureHandleThread.start();
            return;
        }
        pictureHandleThread.removeCache(imageView, str);
    }
}
