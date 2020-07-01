package ly.pp.justpiano3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class TestSQL extends SQLiteOpenHelper {

    TestSQL(Context context, String str) {
        super(context, str, null, 39);
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
            JustPiano.updateSQL = true;
        }
        if (sQLiteDatabase.getVersion() <= 38){
            JPApplication.initSettings();
        }
        sQLiteDatabase.setVersion(39);
    }
}
