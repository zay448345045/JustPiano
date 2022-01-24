package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

final class HairClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    HairClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.hairUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.hairNow) {
                olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.hairNow = -1;
                return;
            }
            olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.hairArray.get(i));
            olPlayDressRoom.hairNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olPlayDressRoom.sex.equals("f")
                    ? Consts.fHair[i] : Consts.mHair[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", "B");
                    olPlayDressRoom.sendMsg((byte) 33, (byte) 0, (byte) i, jSONObject.toString());
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
