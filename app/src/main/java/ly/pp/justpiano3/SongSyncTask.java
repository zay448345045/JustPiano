package ly.pp.justpiano3;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class SongSyncTask extends AsyncTask<String, Void, String> {
    private final WeakReference<Activity> activity;
    private final String maxSongId;
    private int count = 0;

    SongSyncTask(Activity activity, String maxSongId) {
        this.activity = new WeakReference<>(activity);
        this.maxSongId = maxSongId;
    }

    @Override
    protected String doInBackground(String... objects) {
        HttpPost httpPost = new HttpPost("http://" + ((JPApplication) activity.get().getApplication()).getServer() + ":8910/JustPianoServer/server/SongSync");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("version", ((JPApplication) activity.get().getApplication()).getVersion()));
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
                File zipFile = new File(activity.get().getFilesDir().getAbsolutePath() + "/Songs/" + System.currentTimeMillis());
                FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
                fileOutputStream.write(bytes, 0, bytes.length);
                fileOutputStream.close();
                List<File> files = GZIP.ZIPFileTo(zipFile, zipFile.getParentFile().toString());
                zipFile.delete();
                TestSQL testSQL = new TestSQL(activity.get(), "data");
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
                    count++;
                }
                sqliteDataBase.setTransactionSuccessful();
                sqliteDataBase.endTransaction();
                sqliteDataBase.close();
                testSQL.close();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    @Override
    protected final void onPostExecute(String str) {
        if (activity.get() instanceof OLMainMode) {
            ((OLMainMode) activity.get()).loginOnline();
        } else if (activity.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) activity.get();
            melodySelect.jpprogressBar.dismiss();
            JPDialog jpdialog = new JPDialog(melodySelect);
            jpdialog.setTitle("在线曲库同步");
            jpdialog.setCancelableFalse();
            jpdialog.setMessage("在线曲库同步成功，本地新增曲谱" + count + "首，请重新进入本地曲库查看");
            jpdialog.setSecondButton("确定", ((dialogInterface, i) -> {
                Intent intent = new Intent();
                intent.setClass(melodySelect, MainMode.class);
                melodySelect.startActivity(intent);
                melodySelect.finish();
            }));
            jpdialog.showDialog();
        }
    }

    @Override
    protected final void onPreExecute() {
        if (activity.get() instanceof OLMainMode) {
            OLMainMode olMainMode = (OLMainMode) activity.get();
            Toast.makeText(olMainMode, "曲库同步中，请不要离开...", Toast.LENGTH_SHORT).show();
            olMainMode.jpprogressBar.setCancelable(false);
            olMainMode.jpprogressBar.show();
        } else if (activity.get() instanceof MelodySelect) {
            MelodySelect melodySelect = (MelodySelect) activity.get();
            Toast.makeText(melodySelect, "曲库同步中，请不要离开...", Toast.LENGTH_SHORT).show();
            melodySelect.jpprogressBar.setCancelable(false);
            melodySelect.jpprogressBar.show();
        }
    }
}
