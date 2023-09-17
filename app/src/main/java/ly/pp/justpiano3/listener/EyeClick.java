package ly.pp.justpiano3.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineChangeClothesDTO;

public final class EyeClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    public EyeClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.eyeUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.eyeNow) {
                olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.eyeNow = -1;
                return;
            }
            olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.eyeArray.get(i));
            olPlayDressRoom.eyeNow = i;
        } else {
            int[] priceArr = "f".equals(olPlayDressRoom.sex) ? OLPlayDressRoom.fEye : OLPlayDressRoom.mEye;
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
                    builder.setBuyClothesType(1);
                    builder.setBuyClothesId(i);
                    olPlayDressRoom.sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
                    dialog.dismiss();
                });
            }
            if (olPlayDressRoom.eyeTry.contains(i)) {
                jpDialogBuilder.setSecondButton("取消试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.none);
                    olPlayDressRoom.eyeNow = -1;
                    ((DressAdapter) olPlayDressRoom.eyeGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.eyeTry.remove((Integer) i);
                });
            } else {
                jpDialogBuilder.setSecondButton("试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.eyeArray.get(i));
                    olPlayDressRoom.eyeNow = i;
                    ((DressAdapter) olPlayDressRoom.eyeGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.eyeTry.add(i);
                });
            }
            jpDialogBuilder.buildAndShowDialog();
        }
    }
}
