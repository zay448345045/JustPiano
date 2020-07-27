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
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OLPlayDressRoom extends BaseActivity implements OnClickListener {
    public String sex = "f";
    public Bitmap none;
    public Context context;
    public TabHost tabhost;
    OLPlayDressRoomHandler olPlayDressRoomHandler;
    ImageView trousersImage;
    ImageView jacketImage;
    ImageView hairImage;
    ImageView shoesImage;
    TextView goldNum;
    GridView hairGridView;
    GridView jacketGridView;
    GridView trousersGridView;
    GridView shoesGridView;
    List<Bitmap> hairArray = new ArrayList<>();
    List<Bitmap> jacketArray = new ArrayList<>();
    List<Bitmap> trousersArray = new ArrayList<>();
    List<Bitmap> shoesArray = new ArrayList<>();
    List<Integer> hairUnlock = new ArrayList<>();
    List<Integer> jacketUnlock = new ArrayList<>();
    List<Integer> trousersUnlock = new ArrayList<>();
    List<Integer> shoesUnlock = new ArrayList<>();
    int hairNow = -1;
    int level = 0;
    int jacketNow = -1;
    int trousersNow = -1;
    int shoesNow = -1;
    int hairNum = 0;
    int jacketNum = 0;
    private Bitmap body;
    private int trousersNum = 0;
    private int shoesNum = 0;
    private ConnectionService connectionservice;

    private void setDressAdapter(GridView gridView, List<Bitmap> arrayList, int type) {
        gridView.setAdapter(new DressAdapter(arrayList, this, type));
    }

    public final void sendMsg(byte b, byte b2, byte b3, String str) {
        if (connectionservice != null) {
            connectionservice.writeData(b, b2, b3, str, null);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public final void parseUnlockByteArray(byte[] bytes) {
        for (int i = 0; i < bytes.length; i += 2) {
            List<Integer> list = null;
            switch (bytes[i]) {
                case 0:
                    list = hairUnlock;
                    break;
                case 1:
                    list = jacketUnlock;
                    break;
                case 2:
                    list = trousersUnlock;
                    break;
                case 3:
                    list = shoesUnlock;
                    break;
            }
            list.add((int) bytes[i + 1]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ol_dress_ok:
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("T", "D");
                    jSONObject.put("TR", trousersNow + 1);
                    String str = "JA";
                    int i = jacketNow + 1;
                    jSONObject.put(str, i);
                    jSONObject.put("HA", hairNow + 1);
                    jSONObject.put("SH", shoesNow + 1);
                    sendMsg((byte) 33, (byte) 0, (byte) 0, jSONObject.toString());
                    Intent intent = new Intent(this, OLPlayHallRoom.class);
                    intent.putExtra("T", trousersNow + 1);
                    str = "J";
                    i = jacketNow + 1;
                    intent.putExtra(str, i);
                    intent.putExtra("H", hairNow + 1);
                    intent.putExtra("O", shoesNow + 1);
                    intent.putExtra("S", sex);
                    setResult(-1, intent);
                } catch (JSONException e) {
                    e.printStackTrace();
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
        JPStack.create();
        JPStack.push(this);
        olPlayDressRoomHandler = new OLPlayDressRoomHandler(this);
        JPApplication jpApplication = (JPApplication) getApplication();
        connectionservice = jpApplication.getConnectionService();
        Bundle extras = getIntent().getExtras();
        hairNow = extras.getInt("H");
        jacketNow = extras.getInt("J");
        trousersNow = extras.getInt("T");
        level = extras.getInt("Lv");
        shoesNow = extras.getInt("O");
        sex = extras.getString("S");
        if (sex.equals("f")) {
            hairNum = Consts.fHair.length;
            trousersNum = Consts.fTrousers.length;
            jacketNum = Consts.fJacket.length;
            shoesNum = Consts.fShoes.length;
        } else {
            hairNum = Consts.mHair.length;
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
        newTabSpec.setContent(R.id.jacket_tab);
        newTabSpec.setIndicator("上衣");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab3");
        newTabSpec.setContent(R.id.trousers_tab);
        newTabSpec.setIndicator("下衣");
        tabhost.addTab(newTabSpec);
        newTabSpec = tabhost.newTabSpec("tab4");
        newTabSpec.setContent(R.id.shoes_tab);
        newTabSpec.setIndicator("鞋子");
        tabhost.addTab(newTabSpec);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        for (int i = 0; i < 4; i++) {
            tabhost.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 512;
            ((TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title)).setTextColor(0xffffffff);
        }
        tabhost.setOnTabChangedListener(new PlayDressRoomTabChange(this));
        tabhost.setCurrentTab(1);
        ImageView dressMod = findViewById(R.id.ol_dress_mod);
        trousersImage = findViewById(R.id.ol_dress_trousers);
        jacketImage = findViewById(R.id.ol_dress_jacket);
        hairImage = findViewById(R.id.ol_dress_hair);
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
        jacketGridView = findViewById(R.id.ol_jacket_grid);
        trousersGridView = findViewById(R.id.ol_trousers_grid);
        shoesGridView = findViewById(R.id.ol_shoes_grid);
        setDressAdapter(hairGridView, hairArray, 0);
        setDressAdapter(jacketGridView, jacketArray, 1);
        setDressAdapter(trousersGridView, trousersArray, 2);
        setDressAdapter(shoesGridView, shoesArray, 3);
        if (hairNow < 0) {
            hairImage.setImageBitmap(none);
        } else {
            hairImage.setImageBitmap(hairArray.get(hairNow));
        }
        hairGridView.setOnItemClickListener(new HairClick(this));
        i2 = jacketNow;
        if (i2 < 0) {
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
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("T", "L");
            sendMsg((byte) 33, (byte) 0, (byte) 0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        int i;
        int i2 = 0;
        for (i = 0; i < hairNum; i++) {
            hairArray.get(i).recycle();
        }
        hairArray.clear();
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
        JPStack.create();
        JPStack.pop(this);
        super.onDestroy();
    }
}
