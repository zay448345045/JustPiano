package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import ly.pp.justpiano3.protobuf.request.OnlineRequest;

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
                OnlineRequest.ChangeClothes.Builder builder = OnlineRequest.ChangeClothes.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(3);
                builder.setBuyClothesId(i);
                olplaydressroom.sendMsg(33, builder.build());
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
