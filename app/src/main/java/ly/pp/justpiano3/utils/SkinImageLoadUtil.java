package ly.pp.justpiano3.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import ly.pp.justpiano3.R;

import java.io.IOException;

public class SkinImageLoadUtil {

    public static Bitmap loadImage(Context context, String str) {
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_list", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
            } catch (Exception e) {
                try {
                    return BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/" + str + ".png"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            return BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/" + str + ".png"));
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static void setBackGround(Context context, String str, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_list", "original").equals("original")) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".jpg");
            } catch (Exception ignored) {
            }
            if (bitmap == null) {
                try {
                    bitmap = BitmapFactory.decodeFile(context.getDir("Skin", Context.MODE_PRIVATE) + "/" + str + ".png");
                } catch (Exception ignored) {
                }
            }
            if (bitmap != null) {
                viewGroup.setBackground(new BitmapDrawable(context.getResources(), bitmap));
            }
        } else {
            viewGroup.setBackgroundResource(R.drawable.ground);
        }
    }
}