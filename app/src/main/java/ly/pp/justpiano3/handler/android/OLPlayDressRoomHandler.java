package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.online.OLPlayDressRoom;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.adapter.ShopAdapter;
import ly.pp.justpiano3.entity.ShopProduct;
import ly.pp.justpiano3.view.JPDialogBuilder;

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
                case 0 ->  // 保存服装
                        post(() -> Toast.makeText(olPlayDressRoom, message.getData().getString("I"), Toast.LENGTH_SHORT).show());
                case 1 ->  // 进入换衣间加载音符和解锁情况
                        post(() -> {
                            olPlayDressRoom.goldNum.setText(message.getData().getString("G"));
                            olPlayDressRoom.handleUnlockClothes(message.getData().getByteArray("U"));
                            ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                        });
                case 2 ->  // 购买服装
                        post(() -> {
                            olPlayDressRoom.goldNum.setText(message.getData().getString("G"));
                            String info = message.getData().getString("I");
                            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayDressRoom);
                            jpDialogBuilder.setTitle("提示");
                            jpDialogBuilder.setMessage(info);
                            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                            jpDialogBuilder.buildAndShowDialog();
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
                case 3 ->  // 加载商品
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
                case 4 ->  // 购买商品
                        post(() -> {
                            olPlayDressRoom.jpprogressBar.dismiss();
                            Bundle data = message.getData();
                            olPlayDressRoom.goldNum.setText(data.getString("G"));
                            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayDressRoom);
                            jpDialogBuilder.setTitle("提示");
                            jpDialogBuilder.setMessage(data.getString("I"));
                            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                            jpDialogBuilder.buildAndShowDialog();
                        });
                case 5 ->  // 接收服务器下发的服装价格
                        post(() -> {
                            olPlayDressRoom.jpprogressBar.dismiss();
                            Bundle data = message.getData();
                            int[] prices = data.getIntArray("P");
                            int clothesType = data.getInt("C_T");
                            boolean isFemale = "f".equals(olPlayDressRoom.sex);
                            switch (clothesType) {
                                case 0 -> {
                                    OLPlayDressRoom.fHair = isFemale ? prices : OLPlayDressRoom.fHair;
                                    OLPlayDressRoom.mHair = !isFemale ? prices : OLPlayDressRoom.mHair;
                                    ((DressAdapter) olPlayDressRoom.hairGridView.getAdapter()).notifyDataSetChanged();
                                }
                                case 1 -> {
                                    OLPlayDressRoom.fEye = isFemale ? prices : OLPlayDressRoom.fEye;
                                    OLPlayDressRoom.mEye = !isFemale ? prices : OLPlayDressRoom.mEye;
                                    ((DressAdapter) olPlayDressRoom.eyeGridView.getAdapter()).notifyDataSetChanged();
                                }
                                case 2 -> {
                                    OLPlayDressRoom.fJacket = isFemale ? prices : OLPlayDressRoom.fJacket;
                                    OLPlayDressRoom.mJacket = !isFemale ? prices : OLPlayDressRoom.mJacket;
                                    ((DressAdapter) olPlayDressRoom.jacketGridView.getAdapter()).notifyDataSetChanged();
                                }
                                case 3 -> {
                                    OLPlayDressRoom.fTrousers = isFemale ? prices : OLPlayDressRoom.fTrousers;
                                    OLPlayDressRoom.mTrousers = !isFemale ? prices : OLPlayDressRoom.mTrousers;
                                    ((DressAdapter) olPlayDressRoom.trousersGridView.getAdapter()).notifyDataSetChanged();
                                }
                                case 4 -> {
                                    OLPlayDressRoom.fShoes = isFemale ? prices : OLPlayDressRoom.fShoes;
                                    OLPlayDressRoom.mShoes = !isFemale ? prices : OLPlayDressRoom.mShoes;
                                    ((DressAdapter) olPlayDressRoom.shoesGridView.getAdapter()).notifyDataSetChanged();
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
