package ly.pp.justpiano3.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineChangeClothesDTO;

public final class ShoesClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    public ShoesClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.shoesUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.shoesNow) {
                olPlayDressRoom.shoesImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.shoesNow = -1;
                return;
            }
            olPlayDressRoom.shoesImage.setImageBitmap(olPlayDressRoom.shoesArray.get(i));
            olPlayDressRoom.shoesNow = i;
        } else {
            int[] priceArr = "f".equals(olPlayDressRoom.sex) ? OLPlayDressRoom.fShoes : OLPlayDressRoom.mShoes;
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            // 如果为获取到价格，则只允许试穿
            if (priceArr.length - 1 < i) {
                jpdialog.setMessage("当前服装无法购买，只能试穿");
            } else {
                jpdialog.setMessage("确定花费" + (priceArr[i]) + "音符购买此服装吗?");
                jpdialog.setFirstButton("购买", (dialog, which) -> {
                    OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                    builder.setType(2);
                    builder.setBuyClothesType(4);
                    builder.setBuyClothesId(i);
                    olPlayDressRoom.sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
                    dialog.dismiss();
                });
            }
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
