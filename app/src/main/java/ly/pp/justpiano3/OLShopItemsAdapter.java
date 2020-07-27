package ly.pp.justpiano3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public final class OLShopItemsAdapter extends BaseAdapter {
    private List<HashMap> list;
    //    private JPApplication jpApplication;
    private LayoutInflater li;
    private OLShop shop;

    OLShopItemsAdapter(List<HashMap> list, LayoutInflater layoutInflater, OLShop olShop) {
//        this.jpApplication = jpApplication;
        this.list = list;
        li = layoutInflater;
        shop = olShop;
    }

    @Override
    public final int getCount() {
        return list.size();
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
            view = li.inflate(R.layout.ol_shop_item_view, null);
        }

        TextView ol_shop_item_name = view.findViewById(R.id.ol_shop_item_name);
        TextView ol_shop_item_brief = view.findViewById(R.id.ol_shop_item_brief);
        TextView ol_item_price = view.findViewById(R.id.ol_item_price);
        ImageView ol_shop_pic = view.findViewById(R.id.ol_shop_pic);

        String item_name = "改名卡";
        String item_brief = "用于改变用户名称";
        String item_price = "100";

        ol_shop_pic.setImageResource(R.drawable.gold);
        ol_shop_item_name.setText(item_name);
        ol_shop_item_brief.setText(item_brief);
        ol_item_price.setText(item_price);

        return view;
    }
}
