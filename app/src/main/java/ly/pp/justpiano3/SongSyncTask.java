package ly.pp.justpiano3;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class SongSyncTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMainMode> olMainMode;
    private String maxSongId = "";

    SongSyncTask(OLMainMode olMainMode, String maxSongId) {
        this.olMainMode = new WeakReference<>(olMainMode);
        this.maxSongId = maxSongId;
    }

    @Override
    protected String doInBackground(String... objects) {
        HttpPost httpPost = new HttpPost("http://" + olMainMode.get().jpapplication.getServer() + ":8910/JustPianoServer/server/SongSync");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("version", olMainMode.get().jpapplication.getVersion()));
        arrayList.add(new BasicNameValuePair("maxSongId", maxSongId));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        defaultHttpClient.getParams().setParameter("http.connection.timeout", 10000);
        defaultHttpClient.getParams().setParameter("http.socket.timeout", 10000);
        try {
            HttpResponse execute = defaultHttpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() == 200) {
                byte[] bytes = EntityUtils.toByteArray(execute.getEntity());
                File zipFile = new File(olMainMode.get().getFilesDir().getAbsolutePath() + "/Songs/" + System.currentTimeMillis());
                FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
                fileOutputStream.write(bytes, 0, bytes.length);
                fileOutputStream.close();
                List<File> files = GZIP.ZIPFileTo(zipFile, zipFile.getParentFile().toString());
                zipFile.delete();
                TestSQL testSQL = new TestSQL(olMainMode.get(), "data");
                SQLiteDatabase sqliteDataBase = testSQL.getWritableDatabase();
                sqliteDataBase.beginTransaction();
                for (File file : files) {
                    String item = file.getName().substring(0, 1);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ReadPm readpm = new ReadPm(null);
                    readpm.loadWithInputStream(fileInputStream);
                    ContentValues contentvalues = new ContentValues();
                    contentvalues.put("name", readpm.getSongName());
                    contentvalues.put("item", Consts.items[item.charAt(0) - 'a' + 1]);
                    contentvalues.put("path", "songs/" + item + '/' + file.getName());
                    contentvalues.put("isnew", 1);
                    contentvalues.put("ishot", 0);
                    contentvalues.put("isfavo", 0);
                    contentvalues.put("player", "");
                    contentvalues.put("score", 0);
                    contentvalues.put("date", 0);
                    contentvalues.put("count", 0);
                    contentvalues.put("diff", readpm.getNandu());
                    contentvalues.put("online", 1);
                    contentvalues.put("Ldiff", readpm.getLeftNandu());
                    contentvalues.put("length", readpm.getSongTime());
                    contentvalues.put("Lscore", 0);
                    sqliteDataBase.insertOrThrow("jp_data", null, contentvalues);
                }
                sqliteDataBase.setTransactionSuccessful();
                sqliteDataBase.endTransaction();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    @Override
    protected final void onPostExecute(String str) {
        olMainMode.get().jpprogressBar.dismiss();
        olMainMode.get().loginOnline();
    }

    @Override
    protected final void onPreExecute() {
        Toast.makeText(olMainMode.get(), "曲库同步中，请不要离开...", Toast.LENGTH_SHORT).show();
        olMainMode.get().jpprogressBar.setCancelable(false);
        olMainMode.get().jpprogressBar.show();
    }
}
