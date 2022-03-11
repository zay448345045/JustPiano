package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import ly.pp.justpiano3.protobuf.request.OnlineRequest;

final class ShoesClick implements OnItemClickListener {
    private final OLPlayDressRoom olplaydressroom;

    ShoesClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olplaydressroom.shoesUnlock.contains(i) || i == 0) {
            if (i == olplaydressroom.shoesNow) {
                olplaydressroom.shoesImage.setImageBitmap(olplaydressroom.none);
                olplaydressroom.shoesNow = -1;
                return;
            }
            olplaydressroom.shoesImage.setImageBitmap(olplaydressroom.shoesArray.get(i));
            olplaydressroom.shoesNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olplaydressroom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olplaydressroom.sex.equals("f")
                    ? Consts.fShoes[i] : Consts.mShoes[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineRequest.ChangeClothes.Builder builder = OnlineRequest.ChangeClothes.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(4);
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
