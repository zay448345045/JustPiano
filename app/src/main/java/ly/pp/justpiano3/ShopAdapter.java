package ly.pp.justpiano3;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public final class ShopAdapter extends BaseAdapter {
    private final OLPlayDressRoom olPlayDressRoom;

    ShopAdapter(OLPlayDressRoom oLPlayDressRoom) {
        this.olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final int getCount() {
        return olPlayDressRoom.shopProductList.size();
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
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
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("K", 2);
                    jSONObject.put("I", shopProduct.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                olPlayDressRoom.sendMsg((byte) 26, (byte) 0, (byte) 0, jSONObject.toString());
            });
            jpdialog.setSecondButton("取消", new DialogDismissClick());
            jpdialog.showDialog();
        });
        return view;
    }
}
