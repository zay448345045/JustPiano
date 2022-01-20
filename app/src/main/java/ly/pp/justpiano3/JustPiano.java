package ly.pp.justpiano3;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class JustPiano extends Activity implements Callback, Runnable {
    public static boolean updateSQL = false;
    Handler handler;
    private boolean f4094d;
    private boolean f4096f;
    private int songCount;
    private TestSQL testSQL;
    private SQLiteDatabase sqliteDataBase;
    private ContentValues contentvalues;
    private JustPianoView justpianoview;
    private String info = "";
    private int progress;
    private String loading = "";

    private void m3587a(SQLiteDatabase sQLiteDatabase) {
        String string;
        //键为pm文件名后缀不带分类的内容（subString文件名（1），值为整个原path，更新时候筛选搜索用
        Map<String, String> pmPathMap = new HashMap<>();
        ReadPm readpm = new ReadPm(null);
        // 下面的字段是删除标记，更新和插入的曲谱会得到更新，然后把未更新也未插入的（就是新版没有这个pm）的曲子删掉，也就是在客户端曲子删库用
        int originalPmVersion = 0;
        Cursor query = sQLiteDatabase.query("jp_data", new String[]{"path"}, null, null, null, null, "_id desc");
        while (query.moveToNext()) {
            string = query.getString(query.getColumnIndex("path"));
            originalPmVersion = query.getInt(query.getColumnIndex("count"));
            if (string.length() > 8 && string.charAt(7) == '/') {
                pmPathMap.put(string.substring(9), string);
            }
        }
        try {
            String[] list = getResources().getAssets().list("songs");
            if (list != null) {
                sQLiteDatabase.beginTransaction();
                for (int i = 0; i < list.length; i++) {
                    String[] list3 = getResources().getAssets().list("songs/" + list[i]);
                    if (list3 != null) {
                        for (String aList3 : list3) {
                            string = "songs/" + list[i] + "/" + aList3;
                            readpm.loadWithSongPath(this, string);
                            String h = readpm.getSongName();
                            if (h != null) {
                                float g = readpm.getNandu();
                                float j = readpm.getLeftNandu();
                                int l = readpm.getSongTime();
                                songCount++;
                                if (pmPathMap.containsKey(aList3.substring(1))) {
                                    info = "更新曲目:" + h + "..." + songCount + "";
                                    contentvalues.put("name", h);
                                    contentvalues.put("item", Consts.items[i + 1]);
                                    contentvalues.put("path", string);
                                    contentvalues.put("diff", g);
                                    contentvalues.put("Ldiff", j);
                                    contentvalues.put("length", l);
                                    contentvalues.put("isnew", 0);
                                    contentvalues.put("online", 1);
                                    contentvalues.put("count", originalPmVersion + 1);
                                    sQLiteDatabase.update("jp_data", contentvalues, "path = '" + pmPathMap.get(aList3.substring(1)) + "'", null);
                                    contentvalues.clear();
                                } else {
                                    info = "加入曲目" + h + "..." + songCount + "";
                                    contentvalues.put("name", h);
                                    contentvalues.put("item", Consts.items[i + 1]);
                                    contentvalues.put("path", string);
                                    contentvalues.put("isnew", 1);
                                    contentvalues.put("ishot", 0);
                                    contentvalues.put("isfavo", 0);
                                    contentvalues.put("player", "");
                                    contentvalues.put("score", 0);
                                    contentvalues.put("date", 0);
                                    contentvalues.put("count", originalPmVersion + 1);
                                    contentvalues.put("diff", g);
                                    contentvalues.put("online", 1);
                                    contentvalues.put("Ldiff", j);
                                    contentvalues.put("length", l);
                                    contentvalues.put("Lscore", 0);
                                    sQLiteDatabase.insertOrThrow("jp_data", null, contentvalues);
                                    contentvalues.clear();
                                }
                                Message obtainMessage = handler.obtainMessage();
                                obtainMessage.what = 0;
                                handler.sendMessage(obtainMessage);
                            }
                        }
                    }
                }
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                // 删除未检测到pm的曲谱
                sQLiteDatabase.delete("jp_data", "count=" + originalPmVersion, null);
                info = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        query.close();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 0:
                justpianoview.mo2761a(progress, info, loading);
                break;
            case 1:
                f4096f = true;
                if (!f4094d) {
                    Intent intent = new Intent();
                    intent.setClass(this, MainMode.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        JPApplication jpapplication = (JPApplication) getApplication();
        handler = new Handler(this);
        new Options().inPreferredConfig = Config.RGB_565;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        jpapplication.setHeightPixels(displayMetrics.heightPixels);
        jpapplication.setWidthPixels(displayMetrics.widthPixels);
        justpianoview = new JustPianoView(this, jpapplication);
        setContentView(justpianoview);
        contentvalues = new ContentValues();
        Timer timer = new Timer();
        timer.schedule(new JustPianoTimerTask(this, timer), 0, 1000);
    }

    @Override
    protected void onDestroy() {
        if (sqliteDataBase != null) {
            try {
                if (sqliteDataBase.isOpen()) {
                    sqliteDataBase.close();
                }
                sqliteDataBase = null;
                if (testSQL != null) {
                    testSQL.close();
                }
                testSQL = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        justpianoview.mo2760a();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!f4096f) {
            f4094d = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (f4094d && f4096f) {
            Intent intent = new Intent();
            intent.setClass(this, MainMode.class);
            startActivity(intent);
            finish();
        } else {
            f4094d = false;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
    }

    @Override
    public void run() {
        Message obtainMessage;
        FileOutputStream fileOutputStream;
        try {
            File file1 = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Skins");
            File file2 = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Sounds");
            if (file1.mkdirs() && file2.mkdirs()) {
                fileOutputStream = new FileOutputStream(new File(file1, "看我.txt"));
                fileOutputStream.write(".ps格式皮肤文件请直接放入此文件夹中".getBytes());
                fileOutputStream = new FileOutputStream(new File(file2, "看我.txt"));
                fileOutputStream.write(".ss格式音源文件请直接放入此文件夹中".getBytes());
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        testSQL = new TestSQL(this, "data");
        sqliteDataBase = testSQL.getWritableDatabase();
        try {
            Cursor query = sqliteDataBase.query("jp_data", null, null, null, null, null, null);
            if ((query != null && (query.getCount() == 0)) || updateSQL) {
                loading = "";
                m3587a(sqliteDataBase);
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e5) {
            System.exit(-1);
        }
        File file = new File(getFilesDir().getAbsolutePath() + "/Sounds");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(getFilesDir().getAbsolutePath() + "/Songs");
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 108; i >= 24; i--) {
            JPApplication.preloadSounds(i);
            progress++;
            loading = "载入声音资源..." + progress + "/85";
            Message obtainMessage2 = handler.obtainMessage();
            obtainMessage2.what = 0;
            handler.sendMessage(obtainMessage2);
        }
        JPApplication.confirmLoadSounds();
        loading = "载入界面资源...";
        progress = 99;
        obtainMessage = handler.obtainMessage();
        obtainMessage.what = 1;
        handler.sendMessage(obtainMessage);
        info = "载入完成.";
        progress = 100;
    }
}
