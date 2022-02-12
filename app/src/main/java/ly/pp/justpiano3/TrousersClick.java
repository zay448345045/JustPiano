package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class TrousersClick implements OnItemClickListener {

    private final OLPlayDressRoom olplaydressroom;

    TrousersClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olplaydressroom.trousersUnlock.contains(i) || i == 0) {
            if (i == olplaydressroom.trousersNow) {
                olplaydressroom.trousersImage.setImageBitmap(olplaydressroom.none);
                olplaydressroom.trousersNow = -1;
                return;
            }
            olplaydressroom.trousersImage.setImageBitmap(olplaydressroom.trousersArray.get(i));
            olplaydressroom.trousersNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olplaydressroom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olplaydressroom.sex.equals("f")
                    ? Consts.fTrousers[i] : Consts.mTrousers[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", "B");
                    olplaydressroom.sendMsg((byte) 33, (byte) 3, (byte) (i + Byte.MIN_VALUE), jSONObject.toString());
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
}
