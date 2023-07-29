package ly.pp.justpiano3;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

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
            // 创建请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("version", olMelodySelect.get().jpapplication.getVersion())
                    .add("page", String.valueOf(olMelodySelect.get().index))
                    .add("type", olMelodySelect.get().f4317e)
                    .build();
            // 创建请求对象
            Request request = new Request.Builder()
                    .url("http://" + olMelodySelect.get().jpapplication.getServer() + ":8910/JustPianoServer/server/GetListByType")
                    .post(formBody) // 设置请求方式为POST
                    .build();
            try {
                // 发送请求并获取响应
                Response response = OkHttpUtil.client().newCall(request).execute();
                if (response.isSuccessful()) { // 判断响应是否成功
                    try {
                        string = response.body().string(); // 获取响应内容
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jSONObject;
            try {
                jSONObject = new JSONObject(string);
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
    protected void onPostExecute(String str) {
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
    protected void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setMessage("正在载入曲库列表,请稍后...");
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
