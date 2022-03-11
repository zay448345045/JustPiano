package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import ly.pp.justpiano3.protobuf.request.OnlineRequest;

final class JacketClick implements OnItemClickListener {

    private final OLPlayDressRoom olplaydressroom;

    JacketClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olplaydressroom.jacketUnlock.contains(i) || i == 0) {
            if (i == olplaydressroom.jacketNow) {
                olplaydressroom.jacketImage.setImageBitmap(olplaydressroom.none);
                olplaydressroom.jacketNow = -1;
                return;
            }
            olplaydressroom.jacketImage.setImageBitmap(olplaydressroom.jacketArray.get(i));
            olplaydressroom.jacketNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olplaydressroom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olplaydressroom.sex.equals("f")
                    ? Consts.fJacket[i] : Consts.mJacket[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineRequest.ChangeClothes.Builder builder = OnlineRequest.ChangeClothes.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(2);
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
