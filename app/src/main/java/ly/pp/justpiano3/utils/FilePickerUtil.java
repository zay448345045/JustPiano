package ly.pp.justpiano3.utils;

import android.content.Context;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.util.Consumer;

import java.util.ArrayList;
import java.util.List;

public final class FilePickerUtil {

    public static String extra;

    public static void openFilePicker(ComponentActivity activity, boolean allowMultipleFiles,
                                      String extra, Consumer<ActivityResult> resultHandle) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleFiles);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        sIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        Intent chooserIntent;
        if (activity.getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with Samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "选择文件");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "选择文件");
        }
        FilePickerUtil.extra = extra;
        activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                resultHandle::accept).launch(chooserIntent);
    }

    public static void openFolderPicker(ComponentActivity activity, String extra,
                                        Consumer<ActivityResult> resultHandle) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        FilePickerUtil.extra = extra;
        activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                resultHandle::accept).launch(intent);
    }

    public static List<FileUtil.UriInfo> getUriFromIntent(Context context, Intent intent) {
        List<FileUtil.UriInfo> uriInfoList = new ArrayList<>();
        if (intent != null) {
            if (intent.getData() != null) {
                uriInfoList.add(FileUtil.getUriInfo(context, intent.getData()));
            } else if (intent.getClipData() != null) {
                int count = intent.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    uriInfoList.add(FileUtil.getUriInfo(context, intent.getClipData().getItemAt(i).getUri()));
                }
            }
        }
        return uriInfoList;
    }
}
