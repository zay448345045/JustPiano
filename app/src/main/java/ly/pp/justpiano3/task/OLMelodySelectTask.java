package ly.pp.justpiano3.task;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLMelodySelect;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class OLMelodySelectTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<OLMelodySelect> olMelodySelect;

    public OLMelodySelectTask(OLMelodySelect oLMelodySelect) {
        olMelodySelect = new WeakReference<>(oLMelodySelect);
    }

    @Override
    protected String doInBackground(Void... v) {
        if (!olMelodySelect.get().type.isEmpty()) {
            String result = OkHttpUtil.sendPostRequest("GetListByType", new FormBody.Builder()
                    .add("version", BuildConfig.VERSION_NAME)
                    .add("page", String.valueOf(olMelodySelect.get().index))
                    .add("type", olMelodySelect.get().type)
                    .build());
            JSONObject jSONObject;
            try {
                jSONObject = new JSONObject(result);
                String list = GZIPUtil.ZIPTo(jSONObject.getString("L"));
                if (olMelodySelect.get().index == 0) {
                    olMelodySelect.get().pageNum = jSONObject.getInt("P");
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.length() > 4) {
            olMelodySelect.get().fillPageList(olMelodySelect.get().pageNum);
            olMelodySelect.get().popupWindowSelectAdapter.notifyDataSetChanged();
            olMelodySelect.get().songList = olMelodySelect.get().songListHandle(result);
            olMelodySelect.get().bindAdapter(olMelodySelect.get().itemListView, olMelodySelect.get().orderByType, olMelodySelect.get().songCount);
            olMelodySelect.get().itemListView.setCacheColorHint(Color.TRANSPARENT);
        } else {
            Toast.makeText(olMelodySelect.get(), "获取数据出错!", Toast.LENGTH_SHORT).show();
        }
        olMelodySelect.get().jpprogressBar.cancel();
    }

    @Override
    protected void onPreExecute() {
        olMelodySelect.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        olMelodySelect.get().jpprogressBar.show();
    }
}
