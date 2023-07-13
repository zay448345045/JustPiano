package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import protobuf.dto.OnlineChangeClothesDTO;

final class EyeClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    EyeClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.eyeUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.eyeNow) {
                olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.eyeNow = -1;
                return;
            }
            olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.eyeArray.get(i));
            olPlayDressRoom.eyeNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olPlayDressRoom.sex.equals("f")
                    ? Consts.fEye[i] : Consts.mEye[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(1);
                builder.setBuyClothesId(i);
                olPlayDressRoom.sendMsg(33, builder.build());
                dialog.dismiss();
            });
            if (olPlayDressRoom.eyeTry.contains(i)) {
                jpdialog.setSecondButton("取消试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.none);
                    olPlayDressRoom.eyeNow = -1;
                    ((DressAdapter) olPlayDressRoom.eyeGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.eyeTry.remove((Integer) i);
                });
            } else {
                jpdialog.setSecondButton("试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.eyeImage.setImageBitmap(olPlayDressRoom.eyeArray.get(i));
                    olPlayDressRoom.eyeNow = i;
                    ((DressAdapter) olPlayDressRoom.eyeGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.eyeTry.add(i);
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
