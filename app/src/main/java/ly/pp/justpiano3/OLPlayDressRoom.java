package ly.pp.justpiano3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.MessageLite;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import protobuf.dto.OnlineChangeClothesDTO;
import protobuf.dto.OnlineShopDTO;

public class OLPlayDressRoom extends BaseActivity implements OnClickListener {
    public String sex = "f";
    public Bitmap none;
    public Context context;
    public TabHost tabhost;
    OLPlayDressRoomHandler olPlayDressRoomHandler;
    ImageView trousersImage;
    ImageView jacketImage;
    ImageView hairImage;
    ImageView eyeImage;
    ImageView shoesImage;
    TextView goldNum;
    GridView hairGridView;
    GridView eyeGridView;
    GridView jacketGridView;
    GridView trousersGridView;
    GridView shoesGridView;
    ListView shopListView;
    List<ShopProduct> shopProductList = new ArrayList<>();
    List<Bitmap> hairArray = new ArrayList<>();
    List<Bitmap> eyeArray = new ArrayList<>();
    List<Bitmap> jacketArray = new ArrayList<>();
    List<Bitmap> trousersArray = new ArrayList<>();
    List<Bitmap> shoesArray = new ArrayList<>();
    List<Integer> hairUnlock = new ArrayList<>();
    List<Integer> eyeUnlock = new ArrayList<>();
    List<Integer> jacketUnlock = new ArrayList<>();
    List<Integer> trousersUnlock = new ArrayList<>();
    List<Integer> shoesUnlock = new ArrayList<>();
    List<Integer> hairTry = new ArrayList<>();
    List<Integer> eyeTry = new ArrayList<>();
    List<Integer> jacketTry = new ArrayList<>();
    List<Integer> trousersTry = new ArrayList<>();
    List<Integer> shoesTry = new ArrayList<>();
    JPProgressBar jpprogressBar;
    int hairNow = -1;
    int eyeNow = -1;
    int level = 0;
    int jacketNow = -1;
    int trousersNow = -1;
    int shoesNow = -1;
    int hairNum = 0;
    int eyeNum = 0;
    int jacketNum = 0;
    private Bitmap body;
    private int trousersNum = 0;
    private int shoesNum = 0;
    private ConnectionService connectionservice;

    private void setDressAdapter(GridView gridView, List<Bitmap> arrayList, int type) {
        gridView.setAdapter(new DressAdapter(arrayList, this, type));
    }

    public final void sendMsg(int type, MessageLite msg) {
        if (connectionservice != null) {
            connectionservice.writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public final void handleUnlockClothes(byte[] bytes) {
        if (bytes != null) {
            for (int i = 0; i < bytes.length; i += 2) {
                List<Integer> list = null;
                switch (bytes[i]) {
                    case 0:
                        list = hairUnlock;
                        break;
                    case 1:
                        list = eyeUnlock;
                        break;
                    case 2:
                        list = jacketUnlock;
                        break;
                    case 3:
                        list = trousersUnlock;
                        break;
                    case 4:
                        list = shoesUnlock;
                        break;
                }
                list.add((int) bytes[i + 1] - Byte.MIN_VALUE);
            }
        }
    }

    public final void handleBuyClothes(int type, int id) {
        List<Integer> list = null;
        List<Integer> tryList = null;
        switch (type) {
            case 0:
                list = hairUnlock;
                tryList = hairTry;
                break;
            case 1:
                list = eyeUnlock;
                tryList = eyeTry;
                break;
            case 2:
                list = jacketUnlock;
                tryList = jacketTry;
                break;
            case 3:
                list = trousersUnlock;
                tryList = trousersTry;
                break;
            case 4:
                list = shoesUnlock;
                tryList = shoesTry;
                break;
        }
        tryList.remove((Integer) id);
        list.add(id);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ol_dress_ok:
                if (hairTry.isEmpty() && eyeTry.isEmpty() && jacketTry.isEmpty() && trousersTry.isEmpty() && shoesTry.isEmpty()) {
                    OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                    builder.setType(0);
                    builder.setHair(hairNow + 1);
                    builder.setEye(eyeNow + 1);
                    builder.setJacket(jacketNow + 1);
                    builder.setTrousers(trousersNow + 1);
                    builder.setShoes(shoesNow + 1);
                    sendMsg(33, builder.build());
                    Intent intent = new Intent(this, OLPlayHallRoom.class);
                    intent.putExtra("T", trousersNow + 1);
                    intent.putExtra("J", jacketNow + 1);
                    intent.putExtra("H", hairNow + 1);
                    intent.putExtra("E", eyeNow + 1);
                    intent.putExtra("O", shoesNow + 1);
                    intent.putExtra("S", sex);
                    setResult(-1, intent);
                } else {
                    JPDialog jpDialog = new JPDialog(this);
                    jpDialog.setTitle("提示");
                    jpDialog.setMessage("您有正在试穿的服装，请取消试穿所有服装后保存");
                    jpDialog.setFirstButton("确定", new DialogDismissClick());
                    jpDialog.showDialog();
                }
                break;
            case R.id.ol_dress_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        context = this;
        super.onCreate(bundle);
        activityNum = 2;
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        olPlayDressRoomHandler = new OLPlayDressRoomHandler(this);
        JPApplication jpApplication = (JPApplication) getApplication();
        connectionservice = jpApplication.getConnectionService();
        Bundle extras = getIntent().getExtras();
        hairNow = extras.getInt("H");
        eyeNow = extras.getInt("E");
        jacketNow = extras.getInt("J");
        trousersNow = extras.getInt("T");
        level = extras.getInt("Lv");
        shoesNow = extras.getInt("O");
        sex = extras.getString("S");
        if (sex.equals("f")) {
            hairNum = Consts.fHair.length;
            eyeNum = Consts.fEye.length;
            trousersNum = Consts.fTrousers.length;
            jacketNum = Consts.fJacket.length;
            shoesNum = Consts.fShoes.length;
        } else {
            hairNum = Consts.mHair.length;
            eyeNum = Consts.mEye.length;
            trousersNum = Consts.mTrousers.length;
            jacketNum = Consts.mJacket.length;
            shoesNum = Consts.mShoes.length;
        }
        setContentView(R.layout.olplaydressroom);
        jpApplication.setBackGround(this, "ground", findViewById(R.id.layout));
        Button dressOK = findViewById(R.id.ol_dress_ok);
        Button dressCancel = findViewById(R.id.ol_dress_cancel);
        dressOK.setOnClickListener(this);
        dressCancel.setOnClickListener(this);
        tabhost = findViewById(R.id.tabhost);
        tabhost.setup();
        TabSpec newTabSpec = tabhost.newTabSpec("tab1");
        newTabSpec.setContent(R.id.hair_tab);
        newTabSpec.setIndicator("头发");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab2");
        newTabSpec.setContent(R.id.eye_tab);
        newTabSpec.setIndicator("眼睛");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab3");
        newTabSpec.setContent(R.id.jacket_tab);
        newTabSpec.setIndicator("上衣");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab4");
        newTabSpec.setContent(R.id.trousers_tab);
        newTabSpec.setIndicator("下衣");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab5");
        newTabSpec.setContent(R.id.shoes_tab);
        newTabSpec.setIndicator("鞋子");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab6");
        newTabSpec.setContent(R.id.shop_tab);
        newTabSpec.setIndicator("商品");
        tabhost.addTab(newTabSpec);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        for (int i = 0; i < 6; i++) {
            tabhost.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 512;
            TextView tv = tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(0xffffffff);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        tabhost.setOnTabChangedListener(str -> {
            int intValue = Integer.parseInt(str.substring(str.length() - 1)) - 1;
            int childCount = tabhost.getTabWidget().getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (intValue == i) {
                    tabhost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_orange);
                } else {
                    tabhost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(R.drawable.selector_ol_blue);
                }
            }
            // 商品标签
            if (intValue == childCount - 1) {
                jpprogressBar.show();
                OnlineShopDTO.Builder builder = OnlineShopDTO.newBuilder();
                builder.setType(1);
                sendMsg(26, builder.build());
            }
        });
        tabhost.setCurrentTab(2);
        ImageView dressMod = findViewById(R.id.ol_dress_mod);
        trousersImage = findViewById(R.id.ol_dress_trousers);
        jacketImage = findViewById(R.id.ol_dress_jacket);
        hairImage = findViewById(R.id.ol_dress_hair);
        eyeImage = findViewById(R.id.ol_dress_eye);
        shoesImage = findViewById(R.id.ol_dress_shoes);
        goldNum = findViewById(R.id.gold_num);
        try {
            body = BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_m0.png"));
            none = BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dressMod.setImageBitmap(body);
        dressMod.setColorFilter(-1, Mode.MULTIPLY);
        int i2 = 0;
        while (i2 < hairNum) {
            try {
                hairArray.add(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_h" + i2 + ".png")));
                i2++;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        for (i2 = 0; i2 < eyeNum; i2++) {
            try {
                eyeArray.add(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_e" + i2 + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (i2 = 0; i2 < jacketNum; i2++) {
            try {
                jacketArray.add(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_j" + i2 + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (i2 = 0; i2 < trousersNum; i2++) {
            try {
                trousersArray.add(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_t" + i2 + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (i2 = 0; i2 < shoesNum; i2++) {
            try {
                shoesArray.add(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_s" + i2 + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        hairGridView = findViewById(R.id.ol_hair_grid);
        eyeGridView = findViewById(R.id.ol_eye_grid);
        jacketGridView = findViewById(R.id.ol_jacket_grid);
        trousersGridView = findViewById(R.id.ol_trousers_grid);
        shoesGridView = findViewById(R.id.ol_shoes_grid);
        shopListView = findViewById(R.id.ol_shop_list);
        setDressAdapter(hairGridView, hairArray, 0);
        setDressAdapter(eyeGridView, eyeArray, 1);
        setDressAdapter(jacketGridView, jacketArray, 2);
        setDressAdapter(trousersGridView, trousersArray, 3);
        setDressAdapter(shoesGridView, shoesArray, 4);
        shopListView.setAdapter(new ShopAdapter(this));
        if (hairNow < 0) {
            hairImage.setImageBitmap(none);
        } else {
            hairImage.setImageBitmap(hairArray.get(hairNow));
        }
        hairGridView.setOnItemClickListener(new HairClick(this));
        if (eyeNow < 0) {
            eyeImage.setImageBitmap(none);
        } else {
            eyeImage.setImageBitmap(eyeArray.get(eyeNow));
        }
        eyeGridView.setOnItemClickListener(new EyeClick(this));
        if (jacketNow < 0) {
            jacketImage.setImageBitmap(none);
        } else {
            jacketImage.setImageBitmap(jacketArray.get(jacketNow));
        }
        jacketGridView.setOnItemClickListener(new JacketClick(this));
        if (trousersNow < 0) {
            trousersImage.setImageBitmap(none);
        } else {
            trousersImage.setImageBitmap(trousersArray.get(trousersNow));
        }
        trousersGridView.setOnItemClickListener(new TrousersClick(this));
        if (shoesNow < 0) {
            shoesImage.setImageBitmap(none);
        } else {
            shoesImage.setImageBitmap(shoesArray.get(shoesNow));
        }
        shoesGridView.setOnItemClickListener(new ShoesClick(this));
        OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
        builder.setType(1);
        sendMsg(33, builder.build());
    }

    @Override
    protected void onDestroy() {
        int i;
        int i2 = 0;
        for (i = 0; i < hairNum; i++) {
            hairArray.get(i).recycle();
        }
        hairArray.clear();
        for (i = 0; i < eyeNum; i++) {
            eyeArray.get(i).recycle();
        }
        eyeArray.clear();
        for (i = 0; i < jacketNum; i++) {
            jacketArray.get(i).recycle();
        }
        jacketArray.clear();
        for (i = 0; i < trousersNum; i++) {
            trousersArray.get(i).recycle();
        }
        trousersArray.clear();
        while (i2 < shoesNum) {
            shoesArray.get(i2).recycle();
            i2++;
        }
        shoesArray.clear();
        body.recycle();
        JPStack.pop(this);
        super.onDestroy();
    }

    public int getDrawableById(String drawable, Class<?> c) {
        if (drawable == null || drawable.isEmpty()) {
            return 0;
        }
        try {
            Field idField = c.getDeclaredField(drawable);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
