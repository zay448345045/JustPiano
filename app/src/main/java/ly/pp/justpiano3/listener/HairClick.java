package ly.pp.justpiano3.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import ly.pp.justpiano3.activity.online.OLPlayDressRoom;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineChangeClothesDTO;

public final class HairClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    public HairClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.hairUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.hairNow) {
                olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.hairNow = -1;
                return;
            }
            olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.hairArray.get(i));
            olPlayDressRoom.hairNow = i;
        } else {
            int[] priceArr = "f".equals(olPlayDressRoom.sex) ? OLPlayDressRoom.fHair : OLPlayDressRoom.mHair;
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayDressRoom);
            jpDialogBuilder.setTitle("解锁服装");
            // 如果为获取到价格，则只允许试穿
            if (priceArr.length - 1 < i) {
                jpDialogBuilder.setMessage("当前服装无法购买，只能试穿");
            } else {
                jpDialogBuilder.setMessage("确定花费" + (priceArr[i]) + "音符购买此服装吗?");
                jpDialogBuilder.setFirstButton("购买", (dialog, which) -> {
                    OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                    builder.setType(2);
                    builder.setBuyClothesType(0);
                    builder.setBuyClothesId(i);
                    olPlayDressRoom.sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
                    dialog.dismiss();
                });
            }
            if (olPlayDressRoom.hairTry.contains(i)) {
                jpDialogBuilder.setSecondButton("取消试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.none);
                    olPlayDressRoom.hairNow = -1;
                    ((DressAdapter) olPlayDressRoom.hairGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.hairTry.remove((Integer) i);
                });
            } else {
                jpDialogBuilder.setSecondButton("试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.hairArray.get(i));
                    olPlayDressRoom.hairNow = i;
                    ((DressAdapter) olPlayDressRoom.hairGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.hairTry.add(i);
                });
            }
            jpDialogBuilder.buildAndShowDialog();
        }
    }
}
