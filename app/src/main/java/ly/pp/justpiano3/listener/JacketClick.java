package ly.pp.justpiano3.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineChangeClothesDTO;

public final class JacketClick implements OnItemClickListener {

    private final OLPlayDressRoom olPlayDressRoom;

    public JacketClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.jacketUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.jacketNow) {
                olPlayDressRoom.jacketImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.jacketNow = -1;
                return;
            }
            olPlayDressRoom.jacketImage.setImageBitmap(olPlayDressRoom.jacketArray.get(i));
            olPlayDressRoom.jacketNow = i;
        } else {
            int[] priceArr = "f".equals(olPlayDressRoom.sex) ? OLPlayDressRoom.fJacket : OLPlayDressRoom.mJacket;
            JPDialogBuilder jpdialog = new JPDialogBuilder(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            // 如果为获取到价格，则只允许试穿
            if (priceArr.length - 1 < i) {
                jpdialog.setMessage("当前服装无法购买，只能试穿");
            } else {
                jpdialog.setMessage("确定花费" + (priceArr[i]) + "音符购买此服装吗?");
                jpdialog.setFirstButton("购买", (dialog, which) -> {
                    OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                    builder.setType(2);
                    builder.setBuyClothesType(2);
                    builder.setBuyClothesId(i);
                    olPlayDressRoom.sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
                    dialog.dismiss();
                });
            }


            if (olPlayDressRoom.jacketTry.contains(i)) {
                jpdialog.setSecondButton("取消试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.jacketImage.setImageBitmap(olPlayDressRoom.none);
                    olPlayDressRoom.jacketNow = -1;
                    ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.jacketTry.remove((Integer) i);
                });
            } else {
                jpdialog.setSecondButton("试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.jacketImage.setImageBitmap(olPlayDressRoom.jacketArray.get(i));
                    olPlayDressRoom.jacketNow = i;
                    ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.jacketTry.add(i);
                });
            }
            jpdialog.buildAndShowDialog();
        }
    }
}
