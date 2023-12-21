package ly.pp.justpiano3.task;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.LocalSongData;
import ly.pp.justpiano3.utils.StreamUtil;

public final class LocalDataImportExportTask extends AsyncTask<Void, Void, String> {
    public static final String EXPORT_FILE_NAME = "just_piano_local_data.db";
    private final WeakReference<Activity> weakReference;
    private final Uri uri;
    private final boolean export;

    public LocalDataImportExportTask(Activity weakReference, Uri uri, boolean export) {
        this.weakReference = new WeakReference<>(weakReference);
        this.uri = uri;
        this.export = export;
    }

    @Override
    protected String doInBackground(Void... v) {
        String result;
        if (export) {
            try {
                DocumentFile directory = DocumentFile.fromTreeUri(weakReference.get(), uri);
                if (directory == null || !directory.exists()) {
                    throw new RuntimeException("文件夹不存在");
                }
                // 在目录中查找现有文件
                DocumentFile documentFile = directory.findFile(EXPORT_FILE_NAME);
                // 如果文件不存在，尝试创建新文件
                if (documentFile == null || !documentFile.exists()) {
                    documentFile = directory.createFile("*/*", EXPORT_FILE_NAME);
                }
                // 如果文件创建成功，或已经存在，则获取并返回输出流
                if (documentFile == null || !documentFile.exists()) {
                    throw new RuntimeException("文件创建失败");
                }
                List<LocalSongData> list = new ArrayList<>();
                List<Song> allSongs = JPApplication.getSongDatabase().songDao().getAllSongs();
                for (Song song : allSongs) {
                    list.add(new LocalSongData(song.getFilePath(), song.isFavorite(), song.getRightHandHighScore(), song.getLeftHandHighScore()));
                }
                if (!StreamUtil.writeObject(list, weakReference.get(), documentFile.getUri())) {
                    throw new Exception();
                }
                result = "曲库数据导出成功，文件已保存";
            } catch (Exception e) {
                e.printStackTrace();
                result = "导出失败 " + e.getMessage();
            }
        } else {
            try {
                List<LocalSongData> list = StreamUtil.readObjectForList(weakReference.get(), uri);
                if (list == null) {
                    throw new RuntimeException();
                }
                int count = JPApplication.getSongDatabase().songDao().updateSongsInfoByPaths(list);
                result = "导入成功，更新" + count + "首曲谱数据";
            } catch (Exception e) {
                result = "导入失败，请确保授予了设备的文件访问权限-" + e.getMessage();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String str) {
        if (weakReference.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) weakReference.get();
            melodySelect.jpProgressBar.dismiss();
            if (!TextUtils.isEmpty(str)) {
                Toast.makeText(melodySelect, str, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        if (weakReference.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) weakReference.get();
            melodySelect.jpProgressBar.setCancelable(false);
            melodySelect.jpProgressBar.show();
            if (!export) {
                Toast.makeText(melodySelect, "开始导入，请耐心等候...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
