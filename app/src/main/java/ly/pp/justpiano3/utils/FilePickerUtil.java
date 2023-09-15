package ly.pp.justpiano3.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilePickerUtil {

    public static final int PICK_FILE_REQUEST_CODE = 110;

    public static void openFileManager(Activity activity, boolean allowMultipleFiles) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleFiles);
        activity.startActivityForResult(Intent.createChooser(intent, "选择文件"), PICK_FILE_REQUEST_CODE);
    }

    public static List<File> getFilesFromIntent(Activity activity, Intent intent) {
        List<File> files = new ArrayList<>();
        if (intent != null) {
            List<Uri> uris = getFileUrisFromIntent(intent);
            for (Uri uri : uris) {
                File file = getFileFromUri(activity, uri);
                if (file != null) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private static List<Uri> getFileUrisFromIntent(Intent intent) {
        List<Uri> uris = new ArrayList<>();
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            uris.add(uri);
        } else if (intent != null && intent.getClipData() != null) {
            int count = intent.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri uri = intent.getClipData().getItemAt(i).getUri();
                uris.add(uri);
            }
        }
        return uris;
    }

    private static File getFileFromUri(Activity activity, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath == null ? null : new File(filePath);
    }
}
