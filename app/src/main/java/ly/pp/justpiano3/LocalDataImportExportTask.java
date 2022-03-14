package ly.pp.justpiano3;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class LocalDataImportExportTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activity;
    private final int type;

    LocalDataImportExportTask(Activity activity, int type) {
        this.activity = new WeakReference<>(activity);
        this.type = type;
    }

    @Override
    protected String doInBackground(String... objects) {
        MelodySelect melodySelect = (MelodySelect) activity.get();
        String result;
        File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/local_data.db");
        if (type == 2) {
            if (file.exists()) {
                try {
                    List<LocalSongData> list = StreamUtils.readObjectForList(file);
                    if (list == null) {
                        throw new Exception();
                    }
                    int count = 0;
                    SQLiteDatabase writableDatabase = melodySelect.testSQL.getWritableDatabase();
                    writableDatabase.beginTransaction();
                    for (LocalSongData localSongData : list) {
                        ContentValues contentvalues = new ContentValues();
                        contentvalues.put("isfavo", localSongData.getIsfavo());
                        contentvalues.put("score", localSongData.getScore());
                        contentvalues.put("Lscore", localSongData.getlScore());
                        writableDatabase.update("jp_data", contentvalues, "path = '" + localSongData.getPath() + "'", null);
                        count++;
                    }
                    writableDatabase.setTransactionSuccessful();
                    writableDatabase.endTransaction();
                    result = "导入成功，更新" + count + "首曲谱数据";
                } catch (Exception e) {
                    result = "导入失败 " + e.getMessage();
                }
            } else {
                result = "文件不存在，请确认SD卡\\JustPiano\\local_data.db存在";
            }
        } else {
            try {
                List<LocalSongData> list = new ArrayList<>();
                SQLiteDatabase writableDatabase = melodySelect.testSQL.getWritableDatabase();
                Cursor query = writableDatabase.query("jp_data", new String[]{"path", "isfavo", "score", "Lscore"}, null, null, null, null, null);
                while (query.moveToNext()) {
                    String path = query.getString(query.getColumnIndex("path"));
                    int isfavo = query.getInt(query.getColumnIndex("isfavo"));
                    int score = query.getInt(query.getColumnIndex("score"));
                    int lScore = query.getInt(query.getColumnIndex("Lscore"));
                    list.add(new LocalSongData(path, isfavo, score, lScore));
                }
                query.close();
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (!StreamUtils.writeObject(list, file)) {
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
    protected final void onPostExecute(String str) {
        if (activity.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) activity.get();
            melodySelect.jpprogressBar.dismiss();
            if (str != null && !str.isEmpty()) {
                Toast.makeText(melodySelect, str, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected final void onPreExecute() {
        if (activity.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) activity.get();
            melodySelect.jpprogressBar.setCancelable(false);
            melodySelect.jpprogressBar.show();
        }
    }
}
