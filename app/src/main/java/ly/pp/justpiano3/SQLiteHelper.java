package ly.pp.justpiano3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public final class SQLiteHelper extends SQLiteOpenHelper {

    /**
     * 4.6版本的数据库版本号为44
     */
    public static final int DATABASE_VERSION = 44;

    SQLiteHelper(Context context, String str) {
        super(context, str, null, DATABASE_VERSION);
    }

    @Override
    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS jp_data (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,item TEXT NOT NULL,path TEXT NOT NULL,isnew INTEGER,ishot INTEGER,isfavo INTEGER,player TEXT,score INTEGER,date LONG,count INTEGER,diff FLOAT DEFAULT 0,online INTEGER,Ldiff FLOAT DEFAULT 0,length INTEGER DEFAULT 0,Lscore INTEGER);");
    }

    @Override
    public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        //4.32版本极品钢琴对应的数据库版本为38
        if (sQLiteDatabase.getVersion() < 38) {
            sQLiteDatabase.execSQL("DELETE FROM sqlite_sequence;");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS jp_data;");
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS jp_data (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,item TEXT NOT NULL,path TEXT NOT NULL,isnew INTEGER,ishot INTEGER,isfavo INTEGER,player TEXT,score INTEGER,date LONG,count INTEGER,diff FLOAT DEFAULT 0,online INTEGER,Ldiff FLOAT DEFAULT 0,length INTEGER DEFAULT 0,Lscore INTEGER);");
        }
        // 重点：想在启动入口更新数据库，一定要触发下面的updateSQL为true
        if (sQLiteDatabase.getVersion() >= 38 && sQLiteDatabase.getVersion() < DATABASE_VERSION) {
            JustPiano.updateSQL = true;
        }
        if (sQLiteDatabase.getVersion() < 40) {
            JPApplication.initSettings();
            File file = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds");
            if (file.isDirectory()) {
                File[] files = file.listFiles(pathname -> {
                    String filename = pathname.getName().toLowerCase();
                    return filename.endsWith(".ss");
                });
                if (files != null) {
                    for (File fileTemp : files) {
                        if (fileTemp.exists()) {
                            fileTemp.delete();
                        }
                    }
                }
            }
        }
        // Math.max用来防止版本回退出错
        sQLiteDatabase.setVersion(Math.max(sQLiteDatabase.getVersion(), DATABASE_VERSION));
    }
}
