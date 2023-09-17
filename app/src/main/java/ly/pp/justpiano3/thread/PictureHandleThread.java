package ly.pp.justpiano3.thread;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import ly.pp.justpiano3.JPApplication;

public final class PictureHandleThread extends Thread {
    private final PictureHandle pictureHandle;
    private final JPApplication jpApplication;
    private final Map<String, ImageView> map = new LinkedHashMap<>();
    private boolean f5147c;

    public PictureHandleThread(JPApplication jpApplication, PictureHandle pictureHandle, ImageView imageView, String str) {
        this.jpApplication = jpApplication;
        this.pictureHandle = pictureHandle;
        map.put(str, imageView);
    }

    public void mo3067a() {
        pictureHandle.f5083a = false;
        map.clear();
        if (f5147c) {
            synchronized (this) {
                notify();
            }
        }
    }

    public void mo3068a(ImageView imageView, String str) {
        map.remove(imageView);
        if (str.endsWith(pictureHandle.JPG_SUFFIX)) {
            map.put(str, imageView);
            if (f5147c) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    @Override
    public void run() {
        while (map.size() > 0 && pictureHandle.f5083a) {
            f5147c = false;
            String str = map.keySet().iterator().next();
            ImageView imageView = map.remove(str);
            if (imageView.getTag().equals(str)) {
                Bitmap a = pictureHandle.m3938b(jpApplication, str);
                pictureHandle.map.put(str, new SoftReference<>(a));
                if (str == imageView.getTag() && a != null) {
                    pictureHandle.handler.post(() -> imageView.setImageBitmap(a));
                }
            }
            if (map.isEmpty() && pictureHandle.f5083a) {
                try {
                    f5147c = true;
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
