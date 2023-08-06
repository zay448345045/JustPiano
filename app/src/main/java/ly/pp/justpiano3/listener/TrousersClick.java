package ly.pp.justpiano3.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.JPDialog;
import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.constant.Consts;
import protobuf.dto.OnlineChangeClothesDTO;

public final class TrousersClick implements OnItemClickListener {

    private final OLPlayDressRoom olPlayDressRoom;

    public TrousersClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.trousersUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.trousersNow) {
                olPlayDressRoom.trousersImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.trousersNow = -1;
                return;
            }
            olPlayDressRoom.trousersImage.setImageBitmap(olPlayDressRoom.trousersArray.get(i));
            olPlayDressRoom.trousersNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olPlayDressRoom.sex.equals("f")
                    ? Consts.fTrousers[i] : Consts.mTrousers[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(3);
                builder.setBuyClothesId(i);
                olPlayDressRoom.sendMsg(33, builder.build());
                dialog.dismiss();
            });
            if (olPlayDressRoom.trousersTry.contains(i)) {
                jpdialog.setSecondButton("取消试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.trousersImage.setImageBitmap(olPlayDressRoom.none);
                    olPlayDressRoom.trousersNow = -1;
                    ((DressAdapter) olPlayDressRoom.trousersGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.trousersTry.remove((Integer) i);
                });
            } else {
                jpdialog.setSecondButton("试穿", (dialog, which) -> {
                    dialog.dismiss();
                    olPlayDressRoom.trousersImage.setImageBitmap(olPlayDressRoom.trousersArray.get(i));
                    olPlayDressRoom.trousersNow = i;
                    ((DressAdapter) olPlayDressRoom.trousersGridView.getAdapter()).notifyDataSetChanged();
                    olPlayDressRoom.trousersTry.add(i);
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
