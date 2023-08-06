package ly.pp.justpiano3.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.JPDialog;
import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.constant.Consts;
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
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olPlayDressRoom.sex.equals("f")
                    ? Consts.fJacket[i] : Consts.mJacket[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(2);
                builder.setBuyClothesId(i);
                olPlayDressRoom.sendMsg(33, builder.build());
                dialog.dismiss();
            });
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
            try {
                jpdialog.showDialog();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }
}
