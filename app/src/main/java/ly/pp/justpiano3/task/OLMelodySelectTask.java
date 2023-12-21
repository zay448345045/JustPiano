package ly.pp.justpiano3.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.OLMelodySelect;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public final class OLMelodySelectTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<OLMelodySelect> olMelodySelect;

    public OLMelodySelectTask(OLMelodySelect oLMelodySelect) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
    }

    @Override
    protected String doInBackground(Void... v) {
        String string = "";
        if (!olMelodySelect.get().type.isEmpty()) {
            // 创建请求参数
            FormBody formBody = new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("page", String.valueOf(olMelodySelect.get().index))
                    .add("type", olMelodySelect.get().type)
                    .build();
            // 创建请求对象
            Request request = new Request.Builder()
                    .url("http://" + OnlineUtil.server + ":8910/JustPianoServer/server/GetListByType")
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
                string = GZIPUtil.ZIPTo(jSONObject.getString("L"));
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
            olMelodySelect.get().fillPageList(olMelodySelect.get().pageNum);
            olMelodySelect.get().popupWindowSelectAdapter.notifyDataSetChanged();
            olMelodySelect.get().songList = olMelodySelect.get().songListHandle(str);
            olMelodySelect.get().bindAdapter(olMelodySelect.get().itemListView, olMelodySelect.get().orderByType, olMelodySelect.get().songCount);
            olMelodySelect.get().itemListView.setCacheColorHint(Color.TRANSPARENT);
            olMelodySelect.get().jpprogressBar.cancel();
            return;
        }
        olMelodySelect.get().jpprogressBar.cancel();
        Toast.makeText(olMelodySelect.get(), "获取数据出错!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
