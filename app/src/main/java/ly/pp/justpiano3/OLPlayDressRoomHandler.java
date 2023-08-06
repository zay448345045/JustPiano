package ly.pp.justpiano3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.entity.ShopProduct;

import java.lang.ref.WeakReference;

public final class OLPlayDressRoomHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public OLPlayDressRoomHandler(OLPlayDressRoom olPlayDressRoom) {
        weakReference = new WeakReference<>(olPlayDressRoom);
    }

    @Override
    public void handleMessage(final Message message) {
        final OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) weakReference.get();
        try {
            switch (message.what) {
                case 0:  // 保存服装
                    post(() -> Toast.makeText(olPlayDressRoom, message.getData().getString("I"), Toast.LENGTH_SHORT).show());
                    break;
                case 1:  // 进入换衣间加载音符和解锁情况
                    post(() -> {
                        olPlayDressRoom.goldNum.setText(message.getData().getString("G"));
                        olPlayDressRoom.handleUnlockClothes(message.getData().getByteArray("U"));
                        ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                    });
                    break;
                case 2:  // 购买服装
                    post(() -> {
                        olPlayDressRoom.goldNum.setText(message.getData().getString("G"));
                        String info = message.getData().getString("I");
                        JPDialog jpDialog = new JPDialog(olPlayDressRoom);
                        jpDialog.setTitle("提示");
                        jpDialog.setMessage(info);
                        jpDialog.setFirstButton("确定", new DialogDismissClick());
                        jpDialog.showDialog();
                        if (info.startsWith("购买成功")) {
                            int buyClothesType = message.getData().getInt("U_T");
                            int buyClothesId = message.getData().getInt("U_I");
                            olPlayDressRoom.handleBuyClothes(buyClothesType, buyClothesId);
                            ((DressAdapter) olPlayDressRoom.hairGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.eyeGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.trousersGridView.getAdapter()).notifyDataSetChanged();
                            ((DressAdapter) olPlayDressRoom.shoesGridView.getAdapter()).notifyDataSetChanged();
                        }
                    });
                    break;
                case 3:  // 加载商品
                    post(() -> {
                        olPlayDressRoom.jpprogressBar.dismiss();
                        Bundle data = message.getData();
                        olPlayDressRoom.goldNum.setText(data.getString("G"));
                        olPlayDressRoom.shopProductList.clear();
                        int size = data.size() - 1;
                        for (int i = 0; i < size; i++) {
                            Bundle bundle = data.getBundle(String.valueOf(i));
                            olPlayDressRoom.shopProductList.add(new ShopProduct(bundle.getInt("I"),
                                    bundle.getString("N"), bundle.getString("P"),
                                    bundle.getInt("G"), bundle.getString("D")));
                        }
                        ShopAdapter adapter = (ShopAdapter) olPlayDressRoom.shopListView.getAdapter();
                        if (adapter == null) {
                            olPlayDressRoom.shopListView.setAdapter(new ShopAdapter(olPlayDressRoom));
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                case 4:  // 购买商品
                    post(() -> {
                        olPlayDressRoom.jpprogressBar.dismiss();
                        Bundle data = message.getData();
                        olPlayDressRoom.goldNum.setText(data.getString("G"));
                        JPDialog jpdialog = new JPDialog(olPlayDressRoom);
                        jpdialog.setTitle("提示");
                        jpdialog.setMessage(data.getString("I"));
                        jpdialog.setFirstButton("确定", new DialogDismissClick());
                        try {
                            jpdialog.showDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
