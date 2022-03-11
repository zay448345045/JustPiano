package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import ly.pp.justpiano3.protobuf.request.OnlineRequest;

final class HairClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    HairClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (olPlayDressRoom.hairUnlock.contains(i) || i == 0) {
            if (i == olPlayDressRoom.hairNow) {
                olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.none);
                olPlayDressRoom.hairNow = -1;
                return;
            }
            olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.hairArray.get(i));
            olPlayDressRoom.hairNow = i;
        } else {
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("解锁服装");
            jpdialog.setMessage("确定花费" + (olPlayDressRoom.sex.equals("f")
                    ? Consts.fHair[i] : Consts.mHair[i]) + "音符购买此服装吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                OnlineRequest.ChangeClothes.Builder builder = OnlineRequest.ChangeClothes.newBuilder();
                builder.setType(2);
                builder.setBuyClothesType(0);
                builder.setBuyClothesId(i);
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
