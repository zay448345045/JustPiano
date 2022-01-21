package ly.pp.justpiano3;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

final class OLPlayDressRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    OLPlayDressRoomHandler(OLPlayDressRoom olPlayDressRoom) {
        weakReference = new WeakReference<>(olPlayDressRoom);
    }

    @Override
    public final void handleMessage(final Message message) {
        final OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) weakReference.get();
        try {
            switch (message.what) {
                case 0:  //保存服装
                    post(() -> {
                        Toast.makeText(olPlayDressRoom, message.getData().getString("I"), Toast.LENGTH_SHORT).show();
                    });
                    break;
                case 1:  //进入换衣间加载音符和解锁情况
                    post(() -> {
                        olPlayDressRoom.goldNum.setText(message.getData().getString("G"));
                        olPlayDressRoom.parseUnlockByteArray(message.getData().getString("U").getBytes());
                        ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                    });
                    break;
                case 2:  //购买服装
                    post(() -> {
                        olPlayDressRoom.goldNum.setText(message.getData().getString("G"));
                        String info = message.getData().getString("I");
                        JPDialog jpDialog = new JPDialog(olPlayDressRoom);
                        jpDialog.setTitle("提示");
                        jpDialog.setMessage(info);
                        jpDialog.setFirstButton("确定", new DialogDismissClick());
                        jpDialog.showDialog();
                        if (info.startsWith("购买成功")) {
                            olPlayDressRoom.parseUnlockByteArray(message.getData().getString("U").getBytes());
                            ((DressAdapter) olPlayDressRoom.hairGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.trousersGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.shoesGridView.getAdapter()).notifyDataSetChanged();
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
