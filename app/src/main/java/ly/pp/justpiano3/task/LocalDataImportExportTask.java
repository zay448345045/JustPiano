package ly.pp.justpiano3.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.activity.MelodySelect;
import ly.pp.justpiano3.database.entity.Song;
import ly.pp.justpiano3.entity.LocalSongData;
import ly.pp.justpiano3.utils.StreamUtil;

public final class LocalDataImportExportTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<Activity> weakReference;
    private final int type;

    public LocalDataImportExportTask(Activity weakReference, int type) {
        this.weakReference = new WeakReference<>(weakReference);
        this.type = type;
    }

    @Override
    protected String doInBackground(Void... v) {
        String result;
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/local_data.db");
        if (type == 2) {
            if (file.exists()) {
                try {
                    List<LocalSongData> list = StreamUtil.readObjectForList(file);
                    if (list == null) {
                        throw new RuntimeException();
                    }
                    int count = JPApplication.getSongDatabase().songDao().updateSongsInfoByPaths(list);
                    result = "导入成功，更新" + count + "首曲谱数据";
                } catch (Exception e) {
                    result = "导入失败，请确保授予了设备的文件访问权限-" + e.getMessage();
                }
            } else {
                result = "文件不存在，请确认SD卡\\JustPiano\\local_data.db存在";
            }
        } else {
            try {
                List<LocalSongData> list = new ArrayList<>();
                List<Song> allSongs = JPApplication.getSongDatabase().songDao().getAllSongs();
                for (Song song : allSongs) {
                    list.add(new LocalSongData(song.getFilePath(), song.isFavorite(), song.getRightHandHighScore(), song.getLeftHandHighScore()));
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (!StreamUtil.writeObject(list, file)) {
                    file.delete();
                    throw new Exception();
                }
                result = "曲库数据导出成功，文件已保存";
            } catch (Exception e) {
                e.printStackTrace();
                result = "导出失败 " + e.getMessage();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String str) {
        if (weakReference.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) weakReference.get();
            melodySelect.jpProgressBar.dismiss();
            if (!StringUtil.isNullOrEmpty(str)) {
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
            if (type == 2) {
                Toast.makeText(melodySelect, "导入开始，请耐心等候...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
