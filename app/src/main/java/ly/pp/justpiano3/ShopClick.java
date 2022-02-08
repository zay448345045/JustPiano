package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class ShopClick implements OnItemClickListener {
    private final OLPlayDressRoom olplaydressroom;

    ShopClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        JPDialog jpdialog = new JPDialog(olplaydressroom);
        jpdialog.setTitle("解锁服装");
        jpdialog.setMessage("确定花费" + (olplaydressroom.sex.equals("f")
                ? Consts.fShoes[i] : Consts.mShoes[i]) + "音符购买此服装吗?");
        jpdialog.setFirstButton("购买", (dialog, which) -> {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("T", "B");
                olplaydressroom.sendMsg((byte) 33, (byte) 3, (byte) i, jSONObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }).setSecondButton("取消", new DialogDismissClick());
        try {
            jpdialog.showDialog();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }
}
