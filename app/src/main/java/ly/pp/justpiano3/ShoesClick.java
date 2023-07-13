package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import protobuf.dto.OnlineChangeClothesDTO;

final class ShoesClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    ShoesClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.shoesUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.shoesNow) {
                olPlayDressRoom.shoesImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.shoesNow = -1;
                return;
            }
            olPlayDressRoom.shoesImage.setImageBitmap(olPlayDressRoom.shoesArray.get(i));
            olPlayDressRoom.shoesNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olPlayDressRoom.sex.equals("f")
                    ? Consts.fShoes[i] : Consts.mShoes[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(4);
                builder.setBuyClothesId(i);
                olPlayDressRoom.sendMsg(33, builder.build());
                dialog.dismiss();
            });
            if (olPlayDressRoom.shoesTry.contains(i)) {
                jpdialog.setSecondButton("取消试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.shoesImage.setImageBitmap(olPlayDressRoom.none);
                    olPlayDressRoom.shoesNow = -1;
                    ((DressAdapter) olPlayDressRoom.shoesGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.shoesTry.remove((Integer) i);
                });
            } else {
                jpdialog.setSecondButton("试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.shoesImage.setImageBitmap(olPlayDressRoom.shoesArray.get(i));
                    olPlayDressRoom.shoesNow = i;
                    ((DressAdapter) olPlayDressRoom.shoesGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.shoesTry.add(i);
                });
            }
            try {
                jpdialog.showDialog();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }
}
