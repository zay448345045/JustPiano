package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class OLMelodySelectTask extends AsyncTask<String, Void, String> {
    private final WeakReference<OLMelodySelect> olMelodySelect;
    private String str = "";

    OLMelodySelectTask(OLMelodySelect oLMelodySelect) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
    }

    @Override
    protected String doInBackground(String... objects) {
        String string = "";
        if (!olMelodySelect.get().f4317e.isEmpty()) {
            HttpPost httpPost = new HttpPost("http://" + olMelodySelect.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetListByType");
            List<BasicNameValuePair> arrayList = new ArrayList<>();
            arrayList.add(new BasicNameValuePair("version", olMelodySelect.get().jpapplication.getVersion()));
            arrayList.add(new BasicNameValuePair("page", String.valueOf(olMelodySelect.get().index)));
            arrayList.add(new BasicNameValuePair("type", olMelodySelect.get().f4317e));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(arrayList, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                defaultHttpClient.getParams().setParameter("http.connection.timeout", 10000);
                defaultHttpClient.getParams().setParameter("http.socket.timeout", 10000);
                HttpResponse execute = defaultHttpClient.execute(httpPost);
                if (execute.getStatusLine().getStatusCode() == 200) {
                    try {
                        str = EntityUtils.toString(execute.getEntity());
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (Exception e5) {
                e5.printStackTrace();
            }
            JSONObject jSONObject;
            try {
                jSONObject = new JSONObject(str);
                string = GZIP.ZIPTo(jSONObject.getString("L"));
                if (olMelodySelect.get().index == 0) {
                    olMelodySelect.get().pageNum = jSONObject.getInt("P");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return string;
    }

    @Override
    protected final void onPostExecute(String str) {
        if (str.length() > 4) {
            olMelodySelect.get().m3643a(olMelodySelect.get().pageNum);
            olMelodySelect.get().popupWindowSelectAdapter.notifyDataSetChanged();
            olMelodySelect.get().mo2809a(str);
            olMelodySelect.get().mo2808a(olMelodySelect.get().f4327p, olMelodySelect.get().f4316c, olMelodySelect.get().f4322k);
            olMelodySelect.get().f4327p.setCacheColorHint(0);
            olMelodySelect.get().jpprogressBar.cancel();
            return;
        }
        olMelodySelect.get().jpprogressBar.cancel();
        Toast.makeText(olMelodySelect.get(), "获取数据出错!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected final void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setMessage("正在载入曲库列表,请稍后...");
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
