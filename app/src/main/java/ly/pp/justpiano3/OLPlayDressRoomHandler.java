package ly.pp.justpiano3;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

final class OLPlayDressRoomHandler extends Handler {
    private WeakReference weakReference;

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
                case 1:  //进入换衣间加载极品币和解锁情况
                    break;
                case 2:  //购买服装
                    break;
            }
        } catch (Exception ignored) {
        }
    }
}
