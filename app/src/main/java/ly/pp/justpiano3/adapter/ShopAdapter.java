package ly.pp.justpiano3.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayDressRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.ShopProduct;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.view.JPDialog;
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
        TextView shopItemNameView = view.findViewById(R.id.ol_shop_item_name);
        shopItemNameView.setText(shopProduct.getName());
        TextView shopItemDescriptionView = view.findViewById(R.id.ol_shop_item_desc);
        shopItemDescriptionView.setText(shopProduct.getDescription());
        TextView shopItemPrice = view.findViewById(R.id.ol_shop_item_price);
        shopItemPrice.setText(String.valueOf(shopProduct.getPrice()));
        ImageView shopItemImageView = view.findViewById(R.id.ol_shop_item_pic);
        int drawableById = olPlayDressRoom.getDrawableById(shopProduct.getPicture(), R.drawable.class);
        if (drawableById == 0) {
            shopItemImageView.setImageResource(R.drawable.family);
        } else {
            shopItemImageView.setImageResource(drawableById);
        }
        Button shopItemBuyButton = view.findViewById(R.id.ol_shop_item_buy);
        shopItemBuyButton.setOnClickListener(v -> {
            JPDialog jpdialog = new JPDialog(olPlayDressRoom);
            jpdialog.setTitle("提示");
            jpdialog.setMessage("确定花费" + shopProduct.getPrice() + "音符购买此商品吗?");
            jpdialog.setFirstButton("购买", (dialog, which) -> {
                dialog.dismiss();
                olPlayDressRoom.jpprogressBar.show();
                OnlineShopDTO.Builder builder = OnlineShopDTO.newBuilder();
                builder.setType(2);
                builder.setProductId(shopProduct.getId());
                olPlayDressRoom.sendMsg(OnlineProtocolType.SHOP, builder.build());
            });
            jpdialog.setSecondButton("取消", new DialogDismissClick());
            jpdialog.showDialog();
        });
        return view;
    }
}
