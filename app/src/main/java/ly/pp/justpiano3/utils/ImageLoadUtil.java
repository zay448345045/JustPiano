package ly.pp.justpiano3.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.User;

public class ImageLoadUtil {

    public static final Map<String, Bitmap> familyBitmapCacheMap = new HashMap<>(128);
    public static final Map<String, Bitmap> dressBitmapCacheMap = new HashMap<>(256);

    private static final StringBuilder stringBuilderCache = new StringBuilder();

    public static void init(Context context) {
        try {
            if (!dressBitmapCacheMap.containsKey("mod/_none.webp")) {
                dressBitmapCacheMap.put("mod/_none.webp", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.webp")));
            }
            if (!dressBitmapCacheMap.containsKey("mod/_close.webp")) {
                dressBitmapCacheMap.put("mod/_close.webp", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_close.webp")));
            }
            if (!dressBitmapCacheMap.containsKey("mod/f_m0.webp")) {
                dressBitmapCacheMap.put("mod/f_m0.webp", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/f_m0.webp")));
            }
            if (!dressBitmapCacheMap.containsKey("mod/m_m0.webp")) {
                dressBitmapCacheMap.put("mod/m_m0.webp", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/m_m0.webp")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFamilyImageBitmap(Context context, String familyId, ImageView familyImageView) {
        if (familyId != null && !familyId.equals("0")) {
            if (familyBitmapCacheMap.containsKey(familyId)) {
                Bitmap bitmap = familyBitmapCacheMap.get(familyId);
                if (bitmap == null) {
                    familyImageView.setImageResource(R.drawable.family);
                } else {
                    familyImageView.setImageBitmap(bitmap);
                }
            } else {
                File file = new File(context.getFilesDir(), familyId + ".webp");
                if (file.exists()) {
                    try (InputStream inputStream = new FileInputStream(file)) {
                        int length = (int) file.length();
                        byte[] pic = new byte[length];
                        inputStream.read(pic);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                        familyBitmapCacheMap.put(familyId, bitmap);
                        familyImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    familyBitmapCacheMap.put(familyId, null);
                    familyImageView.setImageResource(R.drawable.family);
                }
            }
        }
    }

    /**
     * 根据用户信息设置服装图片
     */
    public static void setUserDressImageBitmap(Context context, User user, ImageView bodyImageView, ImageView trousersImageView, ImageView jacketImageView,
                                               ImageView hairImageView, ImageView eyeImageView, ImageView shoesImageView) {
        setUserDressImageBitmap(context, user.getSex(), user.getTrousers(), user.getJacket(), user.getHair(), user.getEye(), user.getShoes(),
                bodyImageView, trousersImageView, jacketImageView, hairImageView, eyeImageView, shoesImageView);
    }

    public static void setUserDressImageBitmap(Context context, String gender, int trousers, int jacket, int hair, int eye, int shoes,
                                               ImageView bodyImageView, ImageView trousersImageView, ImageView jacketImageView,
                                               ImageView hairImageView, ImageView eyeImageView, ImageView shoesImageView) {
        Bitmap noneModBitmap = dressBitmapCacheMap.get("mod/_none.webp");
        bodyImageView.setImageBitmap(dressBitmapCacheMap.get(gender.equals("f") ? "mod/f_m0.webp" : "mod/m_m0.webp"));

        try {
            if (trousers <= 0) {
                trousersImageView.setImageBitmap(noneModBitmap);
            } else {
                stringBuilderCache.setLength(0);
                stringBuilderCache.append("mod/").append(gender).append('_').append('t').append(trousers - 1).append(".webp");
                String trousersString = stringBuilderCache.toString();
                if (!dressBitmapCacheMap.containsKey(trousersString)) {
                    dressBitmapCacheMap.put(trousersString, BitmapFactory.decodeStream(context.getResources().getAssets().open(trousersString)));
                }
                trousersImageView.setImageBitmap(dressBitmapCacheMap.get(trousersString));
            }

            if (jacket <= 0) {
                jacketImageView.setImageBitmap(noneModBitmap);
            } else {
                stringBuilderCache.setLength(0);
                stringBuilderCache.append("mod/").append(gender).append('_').append('j').append(jacket - 1).append(".webp");
                String jacketString = stringBuilderCache.toString();
                if (!dressBitmapCacheMap.containsKey(jacketString)) {
                    dressBitmapCacheMap.put(jacketString, BitmapFactory.decodeStream(context.getResources().getAssets().open(jacketString)));
                }
                jacketImageView.setImageBitmap(dressBitmapCacheMap.get(jacketString));
            }

            if (hair <= 0) {
                hairImageView.setImageBitmap(noneModBitmap);
            } else {
                stringBuilderCache.setLength(0);
                stringBuilderCache.append("mod/").append(gender).append('_').append('h').append(hair - 1).append(".webp");
                String hairString = stringBuilderCache.toString();
                if (!dressBitmapCacheMap.containsKey(hairString)) {
                    dressBitmapCacheMap.put(hairString, BitmapFactory.decodeStream(context.getResources().getAssets().open(hairString)));
                }
                hairImageView.setImageBitmap(dressBitmapCacheMap.get(hairString));
            }

            if (eye <= 0) {
                eyeImageView.setImageBitmap(noneModBitmap);
            } else {
                stringBuilderCache.setLength(0);
                stringBuilderCache.append("mod/").append(gender).append('_').append('e').append(eye - 1).append(".webp");
                String eyeString = stringBuilderCache.toString();
                if (!dressBitmapCacheMap.containsKey(eyeString)) {
                    dressBitmapCacheMap.put(eyeString, BitmapFactory.decodeStream(context.getResources().getAssets().open(eyeString)));
                }
                eyeImageView.setImageBitmap(dressBitmapCacheMap.get(eyeString));
            }

            if (shoes <= 0) {
                shoesImageView.setImageBitmap(noneModBitmap);
            } else {
                stringBuilderCache.setLength(0);
                stringBuilderCache.append("mod/").append(gender).append('_').append('s').append(shoes - 1).append(".webp");
                String shoesString = stringBuilderCache.toString();
                if (!dressBitmapCacheMap.containsKey(shoesString)) {
                    dressBitmapCacheMap.put(shoesString, BitmapFactory.decodeStream(context.getResources().getAssets().open(shoesString)));
                }
                shoesImageView.setImageBitmap(dressBitmapCacheMap.get(shoesString));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadSkinImage(Context context, String str) {
        Bitmap bitmap = null;
        if (!PreferenceManager.getDefaultSharedPreferences(context).getString("skin_select", "original").equals("original")) {
            try {
                bitmap = BitmapFactory.decodeFile(context.getFilesDir() + "/Skins/" + str + ".png");
            } catch (Exception e) {
                try {
                    return BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/" + str + ".jpg"));
                } catch (Exception e1) {
                    try {
                        return BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/" + str + ".webp"));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        if (bitmap != null) {
            return bitmap;
        }
        try {
            return BitmapFactory.decodeStream(context.getResources().getAssets().open("drawable/" + str + ".webp"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap loadFileImage(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        return loadFileImage(new File(filePath));
    }

    public static Bitmap loadFileImage(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        Bitmap bitmap = null;
        try (InputStream fis = context.getContentResolver().openInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap loadFileImage(File file) {
        Bitmap bitmap = null;
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static void setBackground(Activity activity) {
        if (activity == null || activity.getWindow() == null) {
            return;
        }
        Bitmap backgroundBitmap = null;
        if (!TextUtils.isEmpty(GlobalSetting.INSTANCE.getBackgroundPic())) {
            backgroundBitmap = loadFileImage(activity, Uri.parse(GlobalSetting.INSTANCE.getBackgroundPic()));
        }
        if (backgroundBitmap != null) {
            activity.getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), backgroundBitmap));
        } else if (!PreferenceManager.getDefaultSharedPreferences(activity).getString("skin_select", "original").equals("original")) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(activity.getFilesDir() + "/Skins/ground.jpg");
            } catch (Exception ignored) {
            }
            if (bitmap == null) {
                try {
                    bitmap = BitmapFactory.decodeFile(activity.getFilesDir() + "/Skins/ground.png");
                } catch (Exception ignored) {
                }
                if (bitmap == null) {
                    try {
                        bitmap = BitmapFactory.decodeFile(activity.getFilesDir() + "/Skins/ground.webp");
                    } catch (Exception ignored) {
                    }
                }
            }
            if (bitmap != null) {
                activity.getWindow().setBackgroundDrawable(new BitmapDrawable(activity.getResources(), bitmap));
            }
        } else {
            activity.getWindow().setBackgroundDrawableResource(R.drawable.ground);
        }
    }
}