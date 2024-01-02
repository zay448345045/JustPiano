package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.BuildConfig;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.ShowTopInfo;
import ly.pp.justpiano3.adapter.TopUserAdapter;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.OkHttpUtil;
import okhttp3.FormBody;

public final class ShowTopInfoTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<ShowTopInfo> showTopInfo;

    public ShowTopInfoTask(ShowTopInfo showTopInfo) {
        this.showTopInfo = new WeakReference<>(showTopInfo);
    }

    @Override
    protected String doInBackground(Void... v) {
        return OkHttpUtil.sendPostRequest("GetTopListByKeywords", new FormBody.Builder()
                .add("head", String.valueOf(showTopInfo.get().head))
                .add("version", BuildConfig.VERSION_NAME)
                .add("keywords", "C")
                .add("page", String.valueOf(showTopInfo.get().pageNum))
                .add("K", OLBaseActivity.kitiName)
                .add("N", OLBaseActivity.getAccountName())
                .build());
    }

    @Override
    protected void onPostExecute(String str) {
        if (str.length() > 3) {
            try {
                showTopInfo.get().dataList = showTopInfo.get().userListHandle(GZIPUtil.ZIPTo(new JSONObject(str).getString("L")));
                ListAdapter topUserAdapter = new TopUserAdapter(showTopInfo.get(), showTopInfo.get().size, showTopInfo.get().dataList);
                if (showTopInfo.get().listView != null) {
                    showTopInfo.get().listView.setAdapter(topUserAdapter);
                }
                showTopInfo.get().listView.setCacheColorHint(0x0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            showTopInfo.get().jpprogressBar.cancel();
        } else if (str.equals("[]")) {
            showTopInfo.get().jpprogressBar.cancel();
            Toast.makeText(showTopInfo.get(), "数据出错!", Toast.LENGTH_SHORT).show();
        } else {
            showTopInfo.get().jpprogressBar.cancel();
            Toast.makeText(showTopInfo.get(), "连接有错，请尝试重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        showTopInfo.get().jpprogressBar.setCancelable(true);
        showTopInfo.get().jpprogressBar.setOnCancelListener(dialog -> cancel(true));
        showTopInfo.get().jpprogressBar.show();
    }
}
