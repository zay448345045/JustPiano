package ly.pp.justpiano3.activity;

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

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.DressAdapter;
import ly.pp.justpiano3.adapter.ShopAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.ShopProduct;
import ly.pp.justpiano3.handler.android.OLPlayDressRoomHandler;
import ly.pp.justpiano3.listener.EyeClick;
import ly.pp.justpiano3.listener.HairClick;
import ly.pp.justpiano3.listener.JacketClick;
import ly.pp.justpiano3.listener.ShoesClick;
import ly.pp.justpiano3.listener.TrousersClick;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import protobuf.dto.OnlineChangeClothesDTO;
import protobuf.dto.OnlineShopDTO;

public class OLPlayDressRoom extends OLBaseActivity implements OnClickListener {
    public String sex = "f";
    public Bitmap none;
    public TabHost tabhost;
    public OLPlayDressRoomHandler olPlayDressRoomHandler;
    public ImageView trousersImage;
    public ImageView jacketImage;
    public ImageView hairImage;
    public ImageView eyeImage;
    public ImageView shoesImage;
    public TextView goldNum;
    public GridView hairGridView;
    public GridView eyeGridView;
    public GridView jacketGridView;
    public GridView trousersGridView;
    public GridView shoesGridView;
    public ListView shopListView;
    public List<ShopProduct> shopProductList = new ArrayList<>();
    public List<Bitmap> hairArray = new ArrayList<>();
    public List<Bitmap> eyeArray = new ArrayList<>();
    public List<Bitmap> jacketArray = new ArrayList<>();
    public List<Bitmap> trousersArray = new ArrayList<>();
    public List<Bitmap> shoesArray = new ArrayList<>();
    public List<Integer> hairUnlock = new ArrayList<>();
    public List<Integer> eyeUnlock = new ArrayList<>();
    public List<Integer> jacketUnlock = new ArrayList<>();
    public List<Integer> trousersUnlock = new ArrayList<>();
    public List<Integer> shoesUnlock = new ArrayList<>();
    public List<Integer> hairTry = new ArrayList<>();
    public List<Integer> eyeTry = new ArrayList<>();
    public List<Integer> jacketTry = new ArrayList<>();
    public List<Integer> trousersTry = new ArrayList<>();
    public List<Integer> shoesTry = new ArrayList<>();
    public JPProgressBar jpprogressBar;
    public int hairNow = -1;
    public int eyeNow = -1;
    public int level = 0;
    public int jacketNow = -1;
    public int trousersNow = -1;
    public int shoesNow = -1;
    private ConnectionService connectionservice;

    // 装价格常量
    public static int[] fHair = new int[0];
    public static int[] mHair = new int[0];
    public static int[] fEye = new int[0];
    public static int[] mEye = new int[0];
    public static int[] fJacket = new int[0];
    public static int[] mJacket = new int[0];
    public static int[] fTrousers = new int[0];
    public static int[] mTrousers = new int[0];
    public static int[] fShoes = new int[0];
    public static int[] mShoes = new int[0];

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
                    sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
                    Intent intent = new Intent(this, OLPlayHallRoom.class);
                    intent.putExtra("T", trousersNow + 1);
                    intent.putExtra("J", jacketNow + 1);
                    intent.putExtra("H", hairNow + 1);
                    intent.putExtra("E", eyeNow + 1);
                    intent.putExtra("O", shoesNow + 1);
                    intent.putExtra("S", sex);
                    setResult(-1, intent);
                } else {
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                    jpDialogBuilder.setTitle("提示");
                    jpDialogBuilder.setMessage("您有正在试穿的服装，请取消试穿所有服装后保存");
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
                    jpDialogBuilder.buildAndShowDialog();
                }
                break;
            case R.id.ol_dress_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        olPlayDressRoomHandler = new OLPlayDressRoomHandler(this);
        connectionservice = ((JPApplication) getApplication()).getConnectionService();
        Bundle extras = getIntent().getExtras();
        hairNow = extras.getInt("H");
        eyeNow = extras.getInt("E");
        jacketNow = extras.getInt("J");
        trousersNow = extras.getInt("T");
        level = extras.getInt("Lv");
        shoesNow = extras.getInt("O");
        sex = extras.getString("S");
        setContentView(R.layout.ol_dressroom);
        ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
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
                tabhost.getTabWidget().getChildTabViewAt(i).setBackgroundResource(
                        intValue == i ? R.drawable.selector_ol_orange : R.drawable.selector_ol_blue);
            }

            // 商品标签
            jpprogressBar.show();
            if (intValue == childCount - 1) {
                OnlineShopDTO.Builder builder = OnlineShopDTO.newBuilder();
                builder.setType(1);
                sendMsg(OnlineProtocolType.SHOP, builder.build());
            } else {
                OnlineChangeClothesDTO.Builder builder = OnlineChangeClothesDTO.newBuilder();
                builder.setType(3);
                builder.setBuyClothesType(intValue);
                sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
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
        none = ImageLoadUtil.dressBitmapCacheMap.get("mod/_none.png");
        dressMod.setImageBitmap(ImageLoadUtil.dressBitmapCacheMap.get(sex.equals("f") ? "mod/f_m0.png" : "mod/m_m0.png"));
        dressMod.setColorFilter(-1, Mode.MULTIPLY);
        Bitmap bitmap;
        int count = 0;
        do {
            try {
                bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_h" + count + ".png"));
                if (bitmap.getByteCount() > 0) {
                    hairArray.add(bitmap);
                    count++;
                }
            } catch (IOException e) {
                bitmap = null;
                e.printStackTrace();
            }
        } while (bitmap != null && bitmap.getByteCount() > 0);

        count = 0;
        do {
            try {
                bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_e" + count + ".png"));
                if (bitmap.getByteCount() > 0) {
                    eyeArray.add(bitmap);
                    count++;
                }
            } catch (IOException e) {
                bitmap = null;
                e.printStackTrace();
            }
        } while (bitmap != null && bitmap.getByteCount() > 0);
        count = 0;
        do {
            try {
                bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_j" + count + ".png"));
                if (bitmap.getByteCount() > 0) {
                    jacketArray.add(bitmap);
                    count++;
                }
            } catch (IOException e) {
                bitmap = null;
                e.printStackTrace();
            }
        } while (bitmap != null && bitmap.getByteCount() > 0);
        count = 0;
        do {
            try {
                bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_t" + count + ".png"));
                if (bitmap.getByteCount() > 0) {
                    trousersArray.add(bitmap);
                    count++;
                }
            } catch (IOException e) {
                bitmap = null;
                e.printStackTrace();
            }
        } while (bitmap != null && bitmap.getByteCount() > 0);
        count = 0;
        do {
            try {
                bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + sex + "_s" + count + ".png"));
                if (bitmap.getByteCount() > 0) {
                    shoesArray.add(bitmap);
                    count++;
                }
            } catch (IOException e) {
                bitmap = null;
                e.printStackTrace();
            }
        } while (bitmap != null && bitmap.getByteCount() > 0);

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
        sendMsg(OnlineProtocolType.CHANGE_CLOTHES, builder.build());
    }

    @Override
    protected void onDestroy() {
        for (Bitmap bitmap : hairArray) {
            bitmap.recycle();
        }
        hairArray.clear();

        for (Bitmap bitmap : eyeArray) {
            bitmap.recycle();
        }
        eyeArray.clear();

        for (Bitmap bitmap : jacketArray) {
            bitmap.recycle();
        }
        jacketArray.clear();

        for (Bitmap bitmap : trousersArray) {
            bitmap.recycle();
        }
        trousersArray.clear();

        for (Bitmap bitmap : shoesArray) {
            bitmap.recycle();
        }
        shoesArray.clear();

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
