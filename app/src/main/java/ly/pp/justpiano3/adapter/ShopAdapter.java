package ly.pp.justpiano3.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLPlayDressRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.ShopProduct;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineShopDTO;

public final class ShopAdapter extends BaseAdapter {
    private final OLPlayDressRoom olPlayDressRoom;

    public ShopAdapter(OLPlayDressRoom oLPlayDressRoom) {
        this.olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public int getCount() {
        return olPlayDressRoom.shopProductList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = olPlayDressRoom.getLayoutInflater().inflate(R.layout.ol_shop_item_view, null);
        }
        ShopProduct shopProduct = olPlayDressRoom.shopProductList.get(i);
        ((TextView) view.findViewById(R.id.ol_shop_item_name)).setText(shopProduct.getName());
        ((TextView) view.findViewById(R.id.ol_shop_item_desc)).setText(shopProduct.getDescription());
        ((TextView) view.findViewById(R.id.ol_shop_item_price)).setText(String.valueOf(shopProduct.getPrice()));
        int drawableById = olPlayDressRoom.getDrawableById(shopProduct.getPicture(), R.drawable.class);
        ((ImageView) view.findViewById(R.id.ol_shop_item_pic)).setImageResource(
                drawableById == 0 ? R.drawable.family : drawableById);
        view.findViewById(R.id.ol_shop_item_buy).setOnClickListener(v -> {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(olPlayDressRoom);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("确定花费" + shopProduct.getPrice() + "音符购买此商品吗?");
            jpDialogBuilder.setFirstButton("购买", (dialog, which) -> {
                dialog.dismiss();
                olPlayDressRoom.jpprogressBar.show();
                OnlineShopDTO.Builder builder = OnlineShopDTO.newBuilder();
                builder.setType(2);
                builder.setProductId(shopProduct.getId());
                olPlayDressRoom.sendMsg(OnlineProtocolType.SHOP, builder.build());
            });
            jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
            jpDialogBuilder.buildAndShowDialog();
        });
        return view;
    }
}
