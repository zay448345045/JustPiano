package ly.pp.justpiano3;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Create by SunnyDay on 2019/04/18
 */
public class LoaderResult {
    public Bitmap bitmap;
    ImageView imageView;
    String uri;

    public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
        this.imageView = imageView;
        this.uri = uri;
        this.bitmap = bitmap;
    }
}
