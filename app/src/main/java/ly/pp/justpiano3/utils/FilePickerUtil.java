package ly.pp.justpiano3.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.netty.util.internal.StringUtil;

public class FilePickerUtil {

    public static final int PICK_FILE_REQUEST_CODE = 110;

    public static void openFileManager(Activity activity, boolean allowMultipleFiles) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleFiles);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (activity.getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with Samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }

        activity.startActivityForResult(chooserIntent, PICK_FILE_REQUEST_CODE);
    }

    public static List<File> getFilesFromIntent(Context context, Intent intent) {
        List<File> files = new ArrayList<>();
        if (intent != null) {
            List<Uri> uris = getFileUrisFromIntent(intent);
            for (Uri uri : uris) {
                String filePath = FileUtil.INSTANCE.getPathByUri(context, uri);
                if (!StringUtil.isNullOrEmpty(filePath)) {
                    files.add(new File(filePath));
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
}
