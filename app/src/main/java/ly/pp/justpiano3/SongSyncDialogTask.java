package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class SongSyncDialogTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMainMode> olMainMode;
    private String maxSongId = "";

    SongSyncDialogTask(OLMainMode olMainMode, String maxSongId) {
        this.olMainMode = new WeakReference<>(olMainMode);
        this.maxSongId = maxSongId;
    }

    @Override
    protected String doInBackground(String... objects) {
        HttpPost httpPost = new HttpPost("http://" + olMainMode.get().jpapplication.getServer() + ":8910/JustPianoServer/server/SongSyncDialog");
        List<BasicNameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("version", olMainMode.get().jpapplication.getVersion()));
        arrayList.add(new BasicNameValuePair("maxSongId", String.valueOf(maxSongId)));
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
                return EntityUtils.toString(execute.getEntity());
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    @Override
    protected final void onPostExecute(String str) {
        try {
            int i;
            String message = null;
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getInt("C") > 0) {
                message = "您有《" + jsonObject.getString("F") + "》等" + jsonObject.getInt("C") + "首在线曲库最新曲谱需同步至本地曲库，同步后方可进入对战。是否现在同步曲谱?";
                i = 1;
                olMainMode.get().jpprogressBar.dismiss();
            } else {
                olMainMode.get().loginOnline();
                i = 0;
            }
            JPDialog jpdialog = new JPDialog(olMainMode.get());
            jpdialog.setTitle("在线曲库同步");
            jpdialog.setMessage(message);
            jpdialog.setFirstButton("开始同步", (dialog, which) -> {
                dialog.dismiss();
                new SongSyncTask(olMainMode.get(), maxSongId).execute();
            });
            jpdialog.setSecondButton("取消", new DialogDismissClick());
            if (i != 0) {
                jpdialog.showDialog();
            }
        } catch (Exception e) {
            Toast.makeText(olMainMode.get(), "网络连接失败，无法检查曲库同步", Toast.LENGTH_SHORT).show();
            olMainMode.get().jpprogressBar.dismiss();
        }
    }

    @Override
    protected final void onPreExecute() {
        olMainMode.get().jpprogressBar.setCancelable(true);
        olMainMode.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMainMode.get().jpprogressBar.show();
    }
}
