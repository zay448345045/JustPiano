package ly.pp.justpiano3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import ly.pp.justpiano3.thread.PictureHandleThread;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class PictureHandle {
    public boolean f5083a = true;
    public Map<String, SoftReference<Bitmap>> map = new HashMap<>();
    public Handler handler;
    public String f5088f = ".jpg";
    private PictureHandleThread f5084b;
    private final int f5087e;

    public PictureHandle(Handler handler, int i) {
        this.handler = handler;
        f5087e = i;
    }

    public Bitmap m3938b(JPApplication jpApplication, String str) {
        if (!str.endsWith(f5088f)) {
            return null;
        }
        try {
            URL url;
            switch (f5087e) {
                case 0:
                    url = new URL("http://" + jpApplication.getServer() + ":8910/file/NailImage/" + str);
                    break;
                case 1:
                    url = new URL("http://" + jpApplication.getServer() + ":8910/file/Image/" + str);
                    break;
                case 2:
                    url = new URL("http://" + jpApplication.getServer() + ":8910/JustPianoServer/server/PicSkin" + str);
                    break;
                case 3:
                    url = new URL("http://" + jpApplication.getServer() + ":8910/JustPianoServer/server/PicSound" + str);
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

    public void mo3026a() {
        if (map != null) {
            for (Map.Entry<String, SoftReference<Bitmap>> value : map.entrySet()) {
                Bitmap bitmap = value.getValue().get();
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            map.clear();
        }
        if (f5084b != null) {
            f5084b.mo3067a();
        }
    }

    public void mo3027a(JPApplication jpApplication, ImageView imageView, Bitmap bitmap) {
        String str = (String) imageView.getTag();
        if (map.containsKey(str)) {
            Bitmap bitmap2 = map.get(str).get();
            if (bitmap2 != null) {
                imageView.setImageBitmap(bitmap2);
                return;
            } else {
                map.remove(str);
            }
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        if (f5084b == null) {
            f5084b = new PictureHandleThread(jpApplication, this, imageView, str);
            f5084b.start();
            return;
        }
        f5084b.mo3068a(imageView, str);
    }
}
