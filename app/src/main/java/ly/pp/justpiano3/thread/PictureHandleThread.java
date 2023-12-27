package ly.pp.justpiano3.thread;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PictureHandleThread extends Thread {
    private final PictureHandle pictureHandle;
    private final Map<String, ImageView> imageCacheMap = new LinkedHashMap<>();
    private boolean handled;

    public PictureHandleThread(PictureHandle pictureHandle, ImageView imageView, String str) {
        this.pictureHandle = pictureHandle;
        imageCacheMap.put(str, imageView);
    }

    public void release() {
        pictureHandle.running = false;
        imageCacheMap.clear();
        if (handled) {
            synchronized (this) {
                notify();
            }
        }
    }

    public void removeCache(ImageView imageView, String str) {
        imageCacheMap.remove(imageView);
        if (str.endsWith(PictureHandle.JPG_SUFFIX)) {
            imageCacheMap.put(str, imageView);
            if (handled) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    @Override
    public void run() {
        while (imageCacheMap.size() > 0 && pictureHandle.running) {
            handled = false;
            String key = imageCacheMap.keySet().iterator().next();
            ImageView imageView = imageCacheMap.remove(key);
            if (imageView.getTag().equals(key)) {
                Bitmap bitmap = pictureHandle.getBitmapFromRemote(key);
                pictureHandle.bitmapMap.put(key, new SoftReference<>(bitmap));
                if (key == imageView.getTag() && bitmap != null) {
                    pictureHandle.handler.post(() -> imageView.setImageBitmap(bitmap));
                }
            }
            if (imageCacheMap.isEmpty() && pictureHandle.running) {
                try {
                    handled = true;
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
