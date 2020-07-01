package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class ExpressClick implements OnClickListener {
    private final ExpressAdapter expressAdapter;
    private final int expressID;

    ExpressClick(ExpressAdapter expressAdapter, int i) {
        this.expressAdapter = expressAdapter;
        expressID = i;
    }

    @Override
    public final void onClick(View view) {
        if (expressAdapter.popupWindow != null && expressAdapter.popupWindow.isShowing()) {
            expressAdapter.popupWindow.dismiss();
            if (expressAdapter.cs != null) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("@", "");
                    jSONObject.put("M", "//" + expressID);
                    jSONObject.put("V", 99);
                    expressAdapter.cs.writeData(expressAdapter.f6038g, expressAdapter.f6036e, expressAdapter.f6037f, jSONObject.toString(), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
