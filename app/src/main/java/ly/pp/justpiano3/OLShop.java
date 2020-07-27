package ly.pp.justpiano3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OLShop extends BaseActivity implements OnClickListener {
    //public JPApplication jpapplication;


    public TextView info;
    ListView ShopItemListView;
    List<HashMap> shopitemList = new ArrayList<>();
    private LayoutInflater layoutinflater;

    private OLShopItemsAdapter oLShopItemsAdapter;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ol_daily_coin_get:

                break;
            case R.id.ol_coin_give:


                break;


        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ol_shop);
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("C", "1");
            shopitemList.add(hashMap);
        }
        ShopItemListView = findViewById(R.id.item_list);
        layoutinflater = LayoutInflater.from(this);
        oLShopItemsAdapter = new OLShopItemsAdapter(shopitemList, layoutinflater, this);
        ShopItemListView.setAdapter(oLShopItemsAdapter);
    }


    @Override
    protected void onDestroy() {
        JPStack.create();
        JPStack.pop(this);
        super.onDestroy();
    }
}
