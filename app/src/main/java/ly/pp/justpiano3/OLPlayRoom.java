package ly.pp.justpiano3;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class OLPlayRoom extends BaseActivity implements Callback, OnClickListener {
    public int lv;
    public int cl;
    public SQLiteDatabase sqlitedatabase;
    public Handler handler;
    protected byte hallID0;
    protected String hallName;
    protected String nandu = "";
    List<Bundle> friendPlayerList = new ArrayList<>();
    boolean canNotNextPage;
    OLPlayRoomHandler olPlayRoomHandler;
    ScrollText songNameText;
    TextView sendText;
    List<Bundle> msgList = new ArrayList<>();
    int maxListValue = 100;
    GridView playerGrid;
    List<Bundle> invitePlayerList = new ArrayList<>();
    PopupWindow commonModeGroup = null;
    TabHost roomTabs;
    boolean isOnStart = true;
    String userTo = "";
    String online_1 = "online = 1";
    ListView playerListView;
    ListView friendsListView;
    int roomMode = 0;
    int page;
    User user;
    byte roomID0;
    String roomTitleString;
    JPApplication jpapplication = null;
    TextView roomTitle;
    String playerKind = "";
    Button groupButton;
    Button playButton;
    PlaySongs playSongs;
    ConnectionService connectionService;
    Bundle bundle0;
    Bundle bundle2;
    boolean timeUpdateRunning;
    int currentHand;
    ListView msgListView;
    private int diao;
    private Button playSongsModeButton;
    private TextView searchText;
    private ImageView express;
    private LayoutInflater layoutInflater;
    private final List<Bundle> playerList = new ArrayList<>();
    private PopupWindow expressWindow = null;
    private PopupWindow moreSongs = null;
    private PopupWindow groupModeGroup = null;
    private PopupWindow coupleModeGroup = null;
    private PopupWindow changeclr = null;
    private PopupWindow playSongsMode = null;
    private OLRoomSongsAdapter f4503ab;
    private TestSQL testSQL;
    private Cursor cursor;
    private TextView timeTextView;
    private String sqlWhere = "";
    private int colorNum = 99;
    private TimeUpdateThread timeUpdateThread;
    private ImageView changeColorButton = null;

    public OLPlayRoom() {
        canNotNextPage = false;
        page = 0;
        olPlayRoomHandler = new OLPlayRoomHandler(this);
        timeUpdateThread = null;
    }

    private void m3744a(int i, int i2) {
        String str = "";
        if (!online_1.isEmpty()) {
            str = " AND " + online_1;
        }
        sqlWhere = "diff >= " + i + " AND diff < " + i2 + str;
        Cursor query = sqlitedatabase.query("jp_data", Consts.sqlColumns, sqlWhere, null, null, null, null);
        str = query.moveToPosition((int) (Math.random() * ((double) query.getCount()))) ? query.getString(query.getColumnIndex("path")) : "";
        String str1 = str.substring(6, str.length() - 3);
        jpapplication.setNowSongsName(str1);
        sendMsg((byte) 15, roomID0, hallID0, (diao + 20) + str1);
        query.close();
        if (moreSongs != null) {
            moreSongs.dismiss();
        }
    }

    public void setGroupOrHand(int i, PopupWindow popupWindow) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("G", i);
            sendMsg((byte) 44, roomID0, hallID0, jSONObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void showCpDialog(String str, String str2) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_couple_dialog, findViewById(R.id.dialog));
        try {
            JSONObject jSONObject = new JSONObject(GZIP.ZIPTo(str2));
            JSONObject jSONObject2 = jSONObject.getJSONObject("P");
            User User = new User(jSONObject2.getString("N"), jSONObject2.getJSONObject("D"), jSONObject2.getString("S"), jSONObject2.getInt("L"), jSONObject2.getInt("C"));
            JSONObject jSONObject3 = jSONObject.getJSONObject("C");
            User User2 = new User(jSONObject3.getString("N"), jSONObject3.getJSONObject("D"), jSONObject3.getString("S"), jSONObject3.getInt("L"), jSONObject3.getInt("C"));
            JSONObject jSONObject4 = jSONObject.getJSONObject("I");
            TextView textView = inflate.findViewById(R.id.ol_player_level);
            TextView textView2 = inflate.findViewById(R.id.ol_player_class);
            TextView textView3 = inflate.findViewById(R.id.ol_player_clname);
            TextView textView4 = inflate.findViewById(R.id.ol_couple_name);
            TextView textView5 = inflate.findViewById(R.id.ol_couple_level);
            TextView textView6 = inflate.findViewById(R.id.ol_couple_class);
            TextView textView7 = inflate.findViewById(R.id.ol_couple_clname);
            ImageView imageView = inflate.findViewById(R.id.ol_player_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_player_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_player_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_player_hair);
            ImageView imageView5 = inflate.findViewById(R.id.ol_player_shoes);
            ImageView imageView6 = inflate.findViewById(R.id.ol_couple_mod);
            ImageView imageView7 = inflate.findViewById(R.id.ol_couple_trousers);
            ImageView imageView8 = inflate.findViewById(R.id.ol_couple_jacket);
            ImageView imageView9 = inflate.findViewById(R.id.ol_couple_hair);
            ImageView imageView10 = inflate.findViewById(R.id.ol_couple_shoes);
            TextView textView8 = inflate.findViewById(R.id.couple_bless);
            TextView textView9 = inflate.findViewById(R.id.couple_pionts);
            ImageView imageView11 = inflate.findViewById(R.id.couple_type);
            ((TextView) inflate.findViewById(R.id.ol_player_name)).setText(User.getPlayerName());
            textView.setText("LV." + User.getLevel());
            textView2.setText("CL." + User.getCLevel());
            textView3.setText(Consts.nameCL[User.getCLevel()]);
            textView4.setText(User2.getPlayerName());
            textView5.setText("LV." + User2.getLevel());
            textView6.setText("CL." + User2.getCLevel());
            textView7.setText(Consts.nameCL[User2.getCLevel()]);
            textView8.setText(jSONObject4.getString("B"));
            textView9.setText(String.valueOf(jSONObject4.getInt("P")));
            imageView11.setImageResource(Consts.couples[jSONObject4.getInt("T")]);
            imageView.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_m0.png")));
            if (User.getTrousers() <= 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_t" + (User.getTrousers() - 1) + ".png")));
            }
            if (User.getJacket() <= 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_j" + (User.getJacket() - 1) + ".png")));
            }
            if (User.getHair() <= 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_h" + (User.getHair() - 1) + ".png")));
            }
            if (User.getShoes() <= 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_s" + (User.getShoes() - 1) + ".png")));
            }
            imageView6.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_m0.png")));
            if (User2.getTrousers() <= 0) {
                imageView7.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView7.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_t" + (User2.getTrousers() - 1) + ".png")));
            }
            if (User2.getJacket() <= 0) {
                imageView8.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView8.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_j" + (User2.getJacket() - 1) + ".png")));
            }
            if (User2.getHair() <= 0) {
                imageView9.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView9.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_h" + (User2.getHair() - 1) + ".png")));
            }
            if (User2.getShoes() <= 0) {
                imageView10.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView10.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_s" + (User2.getShoes() - 1) + ".png")));
            }
            new JPDialog(this).setTitle(str).loadInflate(inflate).setFirstButton("祝福", new SendZhufuClick(this, jSONObject4)).setSecondButton("取消", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_info_dialog, findViewById(R.id.dialog));
        try {
            User User = new User(b.getString("U"), new JSONObject(b.getString("DR")), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView5 = inflate.findViewById(R.id.ol_user_shoes);
            TextView textView = inflate.findViewById(R.id.user_info);
            TextView textView2 = inflate.findViewById(R.id.user_psign);
            imageView.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_m0.png")));
            if (User.getTrousers() <= 0) {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView2.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_t" + (User.getTrousers() - 1) + ".png")));
            }
            if (User.getJacket() <= 0) {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView3.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_j" + (User.getJacket() - 1) + ".png")));
            }
            if (User.getHair() <= 0) {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_h" + (User.getHair() - 1) + ".png")));
            }
            if (User.getShoes() <= 0) {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView5.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_s" + (User.getShoes() - 1) + ".png")));
            }
            int lv = b.getInt("LV");
            int targetExp = (int) ((0.5 * lv * lv * lv + 500 * lv) / 10) * 10;
            textView.setText("用户名称:" + b.getString("U")
                    + "\n用户等级:Lv." + lv
                    + "\n经验进度:" + b.getInt("E") + "/" + targetExp
                    + "\n考级进度:Cl." + b.getInt("CL")
                    + "\n所在家族:" + b.getString("F")
                    + "\n在线曲库冠军数:" + b.getInt("W")
                    + "\n在线曲库弹奏总分:" + b.getInt("SC"));
            textView2.setText("个性签名:\n" + (b.getString("P").isEmpty() ? "无" : b.getString("P")));
            new JPDialog(this).setTitle("个人资料").loadInflate(inflate).setFirstButton("加为好友", new AddFriendsClick(this, User.getPlayerName())).setSecondButton("确定", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void m3749b(int i) {
        roomTabs.setCurrentTab(2);
        String str = "";
        if (!online_1.isEmpty()) {
            str = " AND " + online_1;
        }
        sqlWhere = "item = '" + Consts.items[i + 1] + "' OR item = '" + Consts.items[i + 2] + "'" + str;
        if (!sqlWhere.isEmpty()) {
            cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, sqlWhere, null, null, null, null);
            f4503ab.changeCursor(cursor);
            f4503ab.notifyDataSetChanged();
        }
        moreSongs.dismiss();
    }

    private void m3749c(int i) {
        roomTabs.setCurrentTab(2);
        String str = "";
        if (!online_1.isEmpty()) {
            str = " AND " + online_1;
        }
        sqlWhere = "item = '" + Consts.items[i + 1] + "'" + str;
        if (!sqlWhere.isEmpty()) {
            cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, sqlWhere, null, null, null, null);
            f4503ab.changeCursor(cursor);
            f4503ab.notifyDataSetChanged();
        }
        moreSongs.dismiss();
    }

    private void m3749c(int i, int j) {
        roomTabs.setCurrentTab(2);
        String str = "";
        if (!online_1.isEmpty()) {
            str = " AND " + online_1;
        }
        sqlWhere = "item = '" + Consts.items[i + 1] + "' OR item = '" + Consts.items[j + 1] + "'" + str;
        if (!sqlWhere.isEmpty()) {
            cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, sqlWhere, null, null, null, null);
            f4503ab.changeCursor(cursor);
            f4503ab.notifyDataSetChanged();
        }
        moreSongs.dismiss();
    }

    public void m3756h() {
        if (!sqlWhere.isEmpty()) {
            cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, sqlWhere, null, null, null, null);
            f4503ab.changeCursor(cursor);
            f4503ab.notifyDataSetChanged();
        }
    }

    public final void sendMsg(byte b, byte b2, byte b3, String str) {
        if (connectionService != null) {
            connectionService.writeData(b, b2, b3, str, null);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public final void putJPhashMap(byte b, User User) {
        jpapplication.getHashmap().put(b, User);
    }

    protected final void mo2860a(int i, String str, int i2, byte b) {
        String str2 = "";
        String str3 = "";
        String str4 = "";
        String str5 = "情意绵绵的情侣";
        switch (i2) {
            case 0:
                return;
            case 1:
                str5 = "情意绵绵的情侣";
                break;
            case 2:
                str5 = "基情四射的基友";
                break;
            case 3:
                str5 = "百年好合的百合";
                break;
        }
        if (i == 1) {
            str3 = "搭档请求";
            str4 = "邀请";
            str = "您与“" + str + "”在双人模式中最高连击总和超过200,是否邀请对方结为一对" + str5 + "?";
        } else if (i == 2) {
            str3 = "搭档请求";
            str4 = "同意";
            str = str + "请求与您结为一对" + str5 + ",是否同意?";
        } else if (i == 3) {
            str3 = "喜告";
            str4 = "确定";
            str = "祝贺" + str + "成为一对" + str5 + "!";
        } else if (i == 4) {
            showCpDialog(str5.substring(str5.length() - 2) + "证书", str);
            return;
        } else if (i == 5) {
            str3 = "提示";
            str4 = "确定";
        } else {
            str = str4;
            str4 = str3;
            str3 = str2;
        }
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setCancelableFalse();
        jpdialog.setTitle(str3).setMessage(str).setFirstButton(str4, new CpRequestClick(this, i, b, i2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public final void mo2861a(GridView gridView, Bundle bundle) {
        playerList.clear();
        if (bundle != null) {
            int size = bundle.size() - 3;
            for (int i = 0; i < size; i++) {
                playerList.add(bundle.getBundle(String.valueOf(i)));
            }
            List<Bundle> list = playerList;
            if (list != null && !list.isEmpty()) {
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("PI"), o2.getByte("PI")));
            }
            gridView.setAdapter(new PlayerImageAdapter(list, this));
        }
    }

    public final void mo2862a() {
        int posi = msgListView.getFirstVisiblePosition();
        msgListView.setAdapter(new ChattingAdapter(msgList, layoutInflater));
        msgListView.setSelection(posi + 2);
    }

    public final void mo2863a(ListView listView, List<Bundle> list, int i) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        listView.setAdapter(new MainGameAdapter(list, (JPApplication) getApplicationContext(), i, this));
    }

    public final String[] mo2864a(String str) {
        Throwable th;
        Cursor cursor = null;
        String[] strArr = new String[2];
        Cursor query;
        try {
            query = sqlitedatabase.query("jp_data", Consts.sqlColumns, "path = '" + str + "'" + (!online_1.isEmpty() ? " AND " + online_1 : ""), null, null, null, null);
            try {
                if (query.moveToNext()) {
                    strArr[0] = query.getString(query.getColumnIndex("name"));
                    strArr[1] = query.getString(query.getColumnIndex("diff"));
                }
                query.close();
            } catch (Exception e) {
                if (query != null) {
                    query.close();
                }
                return strArr;
            } catch (Throwable th2) {
                cursor = query;
                th = th2;
                throw th;
            }
        } catch (Exception ignored) {
        } catch (Throwable th3) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return strArr;
    }

    final void mo2865b(String str) {
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        TextView textView3 = inflate.findViewById(R.id.title_2);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView2.setText("内容:");
        new JPDialog(this).setTitle("发送私信给:" + str).loadInflate(inflate).setFirstButton("发送", new SendMailClick2(this, textView, str)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public final void mo2867c(String str) {
        userTo = "@" + str + ":";
        if (!str.isEmpty() && !str.equals(JPApplication.kitiName)) {
            sendText.setText(userTo);
        }
        CharSequence text = sendText.getText();
        if (text instanceof Spannable) {
            Selection.setSelection((Spannable) text, text.length());
        }
    }

    public final Handler getHandler() {
        return handler;
    }

    public final String getPlayerKind() {
        return playerKind;
    }

    public final int getMode() {
        return roomMode;
    }

    public void setdiao(int i) {
        diao = i;
    }

    public int getdiao() {
        return diao;
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle data = message.getData();
        switch (message.what) {
            case 1:
                sendMsg((byte) 15, roomID0, hallID0, (diao + 20) + data.getString("S"));
                break;
            case 3:
                CharSequence format = SimpleDateFormat.getTimeInstance(3, Locale.CHINESE).format(new Date());
                if (timeTextView != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                        timeTextView.setText(format + "\n电量:" + batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%");
                    } else {
                        timeTextView.setText(format);
                        timeTextView.setTextSize(20);
                    }
                    break;
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        JPDialog jpdialog = new JPDialog(this);
        jpdialog.setTitle("提示");
        jpdialog.setMessage("退出房间并返回大厅?");
        jpdialog.setFirstButton("确定", new ReturnHallClick(this));
        jpdialog.setSecondButton("取消", new DialogDismissClick());
        try {
            jpdialog.showDialog();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onClick(View view) {
        String str;
        JSONObject jSONObject;
        int i;
        switch (view.getId()) {
            case R.id.favor:
                roomTabs.setCurrentTab(2);
                str = "";
                if (!online_1.isEmpty()) {
                    str = " AND " + online_1;
                }
                sqlWhere = "isfavo = 1" + str;
                if (!sqlWhere.isEmpty()) {
                    cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, sqlWhere, null, null, null, null);
                    f4503ab.changeCursor(cursor);
                    f4503ab.notifyDataSetChanged();
                }
                moreSongs.dismiss();
                return;
            case R.id.couple_1:
                setGroupOrHand(1, coupleModeGroup);
                return;
            case R.id.couple_2:
                setGroupOrHand(2, coupleModeGroup);
                return;
            case R.id.couple_6:
                setGroupOrHand(6, coupleModeGroup);
                return;
            case R.id.couple_4:
                setGroupOrHand(4, coupleModeGroup);
                return;
            case R.id.couple_5:
                setGroupOrHand(5, coupleModeGroup);
                return;
            case R.id.couple_3:
                setGroupOrHand(3, coupleModeGroup);
                return;
            case R.id.group_1:
                setGroupOrHand(1, groupModeGroup);
                return;
            case R.id.group_2:
                setGroupOrHand(2, groupModeGroup);
                return;
            case R.id.group_3:
                setGroupOrHand(3, groupModeGroup);
                return;
            case R.id.left_hand:
                currentHand = 1;
                setGroupOrHand(1, commonModeGroup);
                groupButton.setText("左" + groupButton.getText().toString().substring(1));
                return;
            case R.id.right_hand:
                currentHand = 0;
                setGroupOrHand(0, commonModeGroup);
                groupButton.setText("右" + groupButton.getText().toString().substring(1));
                return;
            case R.id.rand_0:
                m3744a(2, 4);
                return;
            case R.id.rand_all:
                m3744a(0, 2);
                return;
            case R.id.rand_5:
                m3744a(6, 8);
                return;
            case R.id.rand_3:
                m3744a(4, 6);
                return;
            case R.id.rand_7:
                m3744a(8, 12);
                return;
            case R.id.add_favor:
                ContentValues contentValues = new ContentValues();
                contentValues.put("isfavo", 1);
                sqlitedatabase.update("jp_data", contentValues, "path = '" + nandu + "'", null);
                contentValues.clear();
                moreSongs.dismiss();
                m3756h();
                return;
            case R.id.type_l:
                m3749b(1);
                return;
            case R.id.type_j:
                m3749c(0);
                return;
            case R.id.type_e:
                m3749c(3, 6);
                return;
            case R.id.type_d:
                m3749b(4);
                return;
            case R.id.type_h:
                m3749c(7);
                return;
            case R.id.ol_search_b:
                String valueOf = String.valueOf(searchText.getText());
                if (valueOf.contains("'")) {
                    Toast.makeText(this, "关键词中请勿输入单引号!", Toast.LENGTH_SHORT).show();
                    searchText.setText("");
                    return;
                }
                searchText.setText("");
                cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, "name like '%" + valueOf + "%'" + (!online_1.isEmpty() ? " AND " + online_1 : ""), null, null, null, null);
                int count = cursor.getCount();
                if (valueOf.isEmpty()) {
                    f4503ab.changeCursor(cursor);
                    f4503ab.notifyDataSetChanged();
                    return;
                } else if (cursor.getCount() == 0) {
                    Toast.makeText(this, "未搜索到与 " + valueOf + " 有关的曲目!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, "搜索到" + count + "首与 " + valueOf + " 有关的曲目!", Toast.LENGTH_SHORT).show();
                    f4503ab.changeCursor(cursor);
                    f4503ab.notifyDataSetChanged();
                    return;
                }
            case R.id.pre_button:
                page -= 20;
                if (page < 0) {
                    page = 0;
                    return;
                }
                try {
                    jSONObject = new JSONObject();
                    jSONObject.put("T", "L");
                    jSONObject.put("B", page);
                    sendMsg((byte) 34, (byte) 0, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            case R.id.online_button:
                try {
                    jSONObject = new JSONObject();
                    jSONObject.put("T", "L");
                    jSONObject.put("B", -1);
                    sendMsg((byte) 34, (byte) 0, (byte) 0, jSONObject.toString());
                    return;
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    return;
                }
            case R.id.next_button:
                if (!canNotNextPage) {
                    page += 20;
                    if (page >= 0) {
                        try {
                            jSONObject = new JSONObject();
                            jSONObject.put("T", "L");
                            jSONObject.put("B", page);
                            sendMsg((byte) 34, (byte) 0, (byte) 0, jSONObject.toString());
                            return;
                        } catch (JSONException e22) {
                            e22.printStackTrace();
                            return;
                        }
                    }
                    return;
                }
                return;
            case R.id.ol_send_b:
                JSONObject jSONObject2 = new JSONObject();
                try {
                    str = String.valueOf(sendText.getText());
                    if (!str.startsWith(userTo) || str.length() <= userTo.length()) {
                        jSONObject2.put("@", "");
                        jSONObject2.put("M", str);
                    } else {
                        jSONObject2.put("@", userTo);
                        str = str.substring(userTo.length());
                        jSONObject2.put("M", str);
                    }
                    sendText.setText("");
                    jSONObject2.put("V", colorNum);
                    if (!str.isEmpty()) {
                        sendMsg((byte) 13, roomID0, hallID0, jSONObject2.toString());
                    }
                    userTo = "";
                    return;
                } catch (JSONException e222) {
                    e222.printStackTrace();
                    return;
                }
            case R.id.ol_express_b:
                expressWindow.showAtLocation(express, Gravity.CENTER, 0, 0);
                return;
            case R.id.ol_soundstop:
                if (playSongs != null) {
                    playSongs.isPlayingSongs = false;
                    playSongs = null;
                    Toast.makeText(this, "当前曲目已停止播放", Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.ol_changecolor:
                if (changeclr != null) {
                    int[] iArr = new int[2];
                    changeColorButton.getLocationOnScreen(iArr);
                    changeclr.showAtLocation(changeColorButton, 51, iArr[0] / 2 - 30, (int) (iArr[1] * 0.84f));
                }
                return;
            case R.id.white:
                changeChatColor(0, 48, 0xffffffff);
                return;
            case R.id.yellow:
                changeChatColor(10, 1, 0xFFFFFACD);
                return;
            case R.id.blue:
                changeChatColor(14, 2, 0xFF00FFFF);
                return;
            case R.id.red:
                changeChatColor(18, 3, 0xFFFF6666);
                return;
            case R.id.orange:
                changeChatColor(22, 4, 0xFFFFA500);
                return;
            case R.id.purple:
                changeChatColor(25, 5, 0xFFBA55D3);
                return;
            case R.id.pink:
                changeChatColor(30, 6, 0xFFFA60EA);
                return;
            case R.id.gold:
                changeChatColor(35, 7, 0xFFFFD700);
                return;
            case R.id.green:
                changeChatColor(40, 8, 0xFFB7FF72);
                return;
            case R.id.black:
                changeChatColor(50, 9, 0xFF000000);
                return;
            case R.id.onetimeplay:
                if (playSongs != null && playSongs.isPlayingSongs && playSongs.jpapplication.getPlaySongsMode() != 0 && playerKind.equals("H")) {
                    playSongs.jpapplication.setPlaySongsMode(0);
                    Toast.makeText(this, "单次播放已开启", Toast.LENGTH_SHORT).show();
                } else if (playSongs != null && !playerKind.equals("H")) {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.circulateplay:
                if (playSongs != null && playSongs.isPlayingSongs && playSongs.jpapplication.getPlaySongsMode() != 1 && playerKind.equals("H")) {
                    jpapplication.setPlaySongsMode(1);
                    Toast.makeText(this, "单曲循环已开启", Toast.LENGTH_SHORT).show();
                } else if (playSongs != null && !playerKind.equals("H")) {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.randomplay:
                if (playSongs != null && playSongs.isPlayingSongs && playSongs.jpapplication.getPlaySongsMode() != 2 && playerKind.equals("H")) {
                    jpapplication.setPlaySongsMode(2);
                    Toast.makeText(this, "乐曲将随机播放", Toast.LENGTH_SHORT).show();
                } else if (playSongs != null && !playerKind.equals("H")) {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.favorplay:
                if (playSongs != null && playSongs.isPlayingSongs && playSongs.jpapplication.getPlaySongsMode() != 3 && playerKind.equals("H")) {
                    jpapplication.setPlaySongsMode(3);
                    Toast.makeText(this, "乐曲将选择收藏夹内曲目随机播放", Toast.LENGTH_SHORT).show();
                } else if (playSongs != null && !playerKind.equals("H")) {
                    Toast.makeText(this, "您不是房主，不能设置播放模式!", Toast.LENGTH_SHORT).show();
                }
                playSongsMode.dismiss();
                return;
            case R.id.ol_ready_b:
                if (playerKind.equals("G")) {
                    sendMsg((byte) 4, roomID0, hallID0, "R");
                } else {
                    sendMsg((byte) 3, roomID0, hallID0, "");
                }
                return;
            case R.id.room_title:
                if (playerKind.equals("G")) {
                    Toast.makeText(this, "非房主不能修改房名!", Toast.LENGTH_SHORT).show();
                } else {
                    View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                    EditText text1 = inflate.findViewById(R.id.text_1);
                    EditText text2 = inflate.findViewById(R.id.text_2);
                    new JPDialog(this).setTitle("修改房名").loadInflate(inflate).setFirstButton("修改", new ChangeRoomNameClick(this, text1, text2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
                }
                return;
            case R.id.ol_songlist_b:
                moreSongs.showAtLocation(playSongsModeButton, Gravity.CENTER, 0, 0);
                return;
            case R.id.ol_group_b:
                if (roomMode == 0) {
                    PopupWindow popupWindow2 = new PopupWindow(this);
                    View inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_hand_list, null);
                    popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                    popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                    popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    inflate2.findViewById(R.id.left_hand).setOnClickListener(this);
                    inflate2.findViewById(R.id.right_hand).setOnClickListener(this);
                    inflate2.findViewById(R.id.shengdiao).setOnClickListener(this);
                    inflate2.findViewById(R.id.jiangdiao).setOnClickListener(this);
                    inflate2.findViewById(R.id.note_speed_0).setOnClickListener(this);
                    inflate2.findViewById(R.id.note_speed_1).setOnClickListener(this);
                    popupWindow2.setFocusable(true);
                    popupWindow2.setTouchable(true);
                    popupWindow2.setOutsideTouchable(true);
                    popupWindow2.setContentView(inflate2);
                    TextView noteSpeed = popupWindow2.getContentView().findViewById(R.id.note_speed);
                    noteSpeed.setText(Consts.noteSpeed[jpapplication.getDownSpeed()]);
                    commonModeGroup = popupWindow2;
                    popupWindow2.showAtLocation(groupButton, Gravity.CENTER, 0, 0);
                    return;
                } else if (roomMode == 1 && groupModeGroup != null) {
                    groupModeGroup.showAtLocation(groupButton, Gravity.CENTER, 0, 0);
                    return;
                } else if (roomMode == 2 && coupleModeGroup != null) {
                    coupleModeGroup.showAtLocation(groupButton, Gravity.CENTER, 0, 0);
                    return;
                }
                return;
            case R.id.ol_more_b:
                if (playSongsMode != null) {
                    int[] iArr = new int[2];
                    playSongsModeButton.getLocationOnScreen(iArr);
                    playSongsMode.showAtLocation(songNameText, 51, iArr[0], (int) (iArr[1] * 0.73f));
                }
                return;
            case R.id.shengdiao:
                if (playerKind.equals("H") && !jpapplication.getNowSongsName().isEmpty()) {
                    if (diao < 6) {
                        diao++;
                    } else {
                        diao = 6;
                    }
                    sendMsg((byte) 15, roomID0, hallID0, (diao + 20) + jpapplication.getNowSongsName());
                    Message obtainMessage = olPlayRoomHandler.obtainMessage();
                    obtainMessage.what = 12;
                    olPlayRoomHandler.handleMessage(obtainMessage);
                } else {
                    Toast.makeText(this, "您不是房主或您未选择曲目，操作无效!", Toast.LENGTH_LONG).show();
                }
                if (commonModeGroup != null) {
                    commonModeGroup.dismiss();
                }
                return;
            case R.id.jiangdiao:
                if (playerKind.equals("H") && !jpapplication.getNowSongsName().isEmpty()) {
                    if (diao > -6) {
                        diao--;
                    } else {
                        diao = -6;
                    }
                    sendMsg((byte) 15, roomID0, hallID0, (diao + 20) + jpapplication.getNowSongsName());
                    Message obtainMessage = olPlayRoomHandler.obtainMessage();
                    obtainMessage.what = 12;
                    olPlayRoomHandler.handleMessage(obtainMessage);
                } else {
                    Toast.makeText(this, "您不是房主或您未选择曲目，操作无效!", Toast.LENGTH_LONG).show();
                }
                if (commonModeGroup != null) {
                    commonModeGroup.dismiss();
                }
                return;
            case R.id.note_speed_0:
                switch (jpapplication.getDownSpeed()) {
                    case 1:
                        jpapplication.setDownSpeed(2);
                        break;
                    case 2:
                        jpapplication.setDownSpeed(3);
                        break;
                    case 3:
                        jpapplication.setDownSpeed(4);
                        break;
                    case 4:
                        jpapplication.setDownSpeed(6);
                        break;
                }
                if (commonModeGroup != null) {
                    commonModeGroup.dismiss();
                }
                return;
            case R.id.note_speed_1:
                switch (jpapplication.getDownSpeed()) {
                    case 2:
                        jpapplication.setDownSpeed(1);
                        break;
                    case 3:
                        jpapplication.setDownSpeed(2);
                        break;
                    case 4:
                        jpapplication.setDownSpeed(3);
                        break;
                    case 6:
                        jpapplication.setDownSpeed(4);
                        break;
                }
                if (commonModeGroup != null) {
                    commonModeGroup.dismiss();
                }
                return;
            default:
        }
    }

    private void changeChatColor(int lv, int colorNum, int color) {
        if (this.lv >= lv) {
            sendText.setTextColor(color);
            this.colorNum = colorNum;
        } else {
            Toast.makeText(this, "您的等级未达到" + lv + "级，不能使用该颜色!", Toast.LENGTH_SHORT).show();
        }
        if (changeclr != null) {
            changeclr.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityNum = 4;
        JPStack.create();
        JPStack.push(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        layoutInflater = LayoutInflater.from(this);
        jpapplication = (JPApplication) getApplication();
        jpapplication.getHashmap().clear();
        connectionService = jpapplication.getConnectionService();
        setContentView(R.layout.olplayroom);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        roomTitle = findViewById(R.id.room_title);
        bundle0 = getIntent().getExtras();
        bundle2 = bundle0.getBundle("bundle");
        hallID0 = bundle2.getByte("hallID");
        hallName = bundle2.getString("hallName");
        roomID0 = bundle0.getByte("ID");
        roomTitleString = bundle0.getString("R");
        roomMode = bundle0.getInt("mode");
        playerKind = bundle0.getString("isHost");
        roomTitle.setText("[" + roomID0 + "]" + roomTitleString);
        roomTitle.setOnClickListener(this);
        playerGrid = findViewById(R.id.ol_player_grid);
        playerGrid.setCacheColorHint(0);
        playerList.clear();
        Button f4483H = findViewById(R.id.ol_send_b);
        f4483H.setOnClickListener(this);
        timeTextView = findViewById(R.id.time_text);
        Button f4484I = findViewById(R.id.ol_search_b);
        f4484I.setOnClickListener(this);
        playButton = findViewById(R.id.ol_ready_b);
        playButton.setOnClickListener(this);
        playSongsModeButton = findViewById(R.id.ol_more_b);
        playSongsModeButton.setOnClickListener(this);
        groupButton = findViewById(R.id.ol_group_b);
        groupButton.setOnClickListener(this);
        Button f4519ar = findViewById(R.id.pre_button);
        Button f4520as = findViewById(R.id.next_button);
        Button f4521at = findViewById(R.id.online_button);
        f4519ar.setOnClickListener(this);
        f4520as.setOnClickListener(this);
        f4521at.setOnClickListener(this);
        if (playerKind.equals("H")) {
            playButton.setText("开始");
        } else {
            playButton.setText("准备");
        }
        friendsListView = findViewById(R.id.ol_friend_list);
        friendsListView.setCacheColorHint(0);
        sendText = findViewById(R.id.ol_send_text);
        searchText = findViewById(R.id.ol_search_text);
        express = findViewById(R.id.ol_express_b);
        ImageView soundstopbutton = findViewById(R.id.ol_soundstop);
        changeColorButton = findViewById(R.id.ol_changecolor);
        playerListView = findViewById(R.id.ol_player_list);
        playerListView.setCacheColorHint(0);
        express.setOnClickListener(this);
        soundstopbutton.setOnClickListener(this);
        changeColorButton.setOnClickListener(this);
        msgListView = findViewById(R.id.ol_msg_list);
        msgListView.setCacheColorHint(0);
        ListView songsList = findViewById(R.id.ol_song_list);
        songsList.setCacheColorHint(0);
        handler = new Handler(this);
        testSQL = new TestSQL(this, "data");
        sqlitedatabase = testSQL.getWritableDatabase();
        cursor = sqlitedatabase.query("jp_data", Consts.sqlColumns, online_1, null, null, null, null);
        f4503ab = new OLRoomSongsAdapter(this, this, cursor);
        songNameText = findViewById(R.id.ol_songlist_b);
        if (!jpapplication.getNowSongsName().isEmpty()) {
            songNameText.setText(mo2864a("songs/" + jpapplication.getNowSongsName() + ".pm")[0] + "[难度:" + mo2864a("songs/" + jpapplication.getNowSongsName() + ".pm")[1] + "]");
        }
        songNameText.setMovementMethod(ScrollingMovementMethod.getInstance());
        songNameText.setOnClickListener(this);
        songsList.setAdapter(f4503ab);
        sendMsg((byte) 21, roomID0, hallID0, "");
        msgList.clear();
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_express_list, null);
        popupWindow.setContentView(inflate);
        ((GridView) inflate.findViewById(R.id.ol_express_grid)).setAdapter(new ExpressAdapter(jpapplication, connectionService, Consts.expressions, popupWindow, (byte) 13, roomID0, hallID0));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        expressWindow = popupWindow;
        PopupWindow popupWindow2 = new PopupWindow(this);
        PopupWindow popupWindow3;
        View inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_songpop_list, null);
        View inflate3;
        popupWindow2.setContentView(inflate2);
        inflate2.findViewById(R.id.rand_all).setOnClickListener(this);
        inflate2.findViewById(R.id.add_favor).setOnClickListener(this);
        inflate2.findViewById(R.id.favor).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_0).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_3).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_5).setOnClickListener(this);
        inflate2.findViewById(R.id.rand_7).setOnClickListener(this);
        inflate2.findViewById(R.id.type_j).setOnClickListener(this);
        inflate2.findViewById(R.id.type_l).setOnClickListener(this);
        inflate2.findViewById(R.id.type_d).setOnClickListener(this);
        inflate2.findViewById(R.id.type_e).setOnClickListener(this);
        inflate2.findViewById(R.id.type_h).setOnClickListener(this);
        popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        popupWindow2.setTouchable(true);
        popupWindow2.setOutsideTouchable(true);
        moreSongs = popupWindow2;
        switch (roomMode) {
            case 0:
                groupButton.setText("右00");
                break;
            case 1:
                popupWindow2 = new PopupWindow(this);
                inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_group_list, null);
                popupWindow2.setContentView(inflate2);
                popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                inflate2.findViewById(R.id.group_1).setOnClickListener(this);
                inflate2.findViewById(R.id.group_2).setOnClickListener(this);
                inflate2.findViewById(R.id.group_3).setOnClickListener(this);
                popupWindow2.setFocusable(true);
                popupWindow2.setTouchable(true);
                popupWindow2.setOutsideTouchable(true);
                groupModeGroup = popupWindow2;
                break;
            case 2:
                popupWindow2 = new PopupWindow(this);
                inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_couple_list, null);
                popupWindow2.setContentView(inflate2);
                popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                inflate2.findViewById(R.id.couple_1).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_2).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_3).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_4).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_5).setOnClickListener(this);
                inflate2.findViewById(R.id.couple_6).setOnClickListener(this);
                popupWindow2.setFocusable(true);
                popupWindow2.setTouchable(true);
                popupWindow2.setOutsideTouchable(true);
                coupleModeGroup = popupWindow2;
                break;
        }
        popupWindow3 = new PopupWindow(this);
        inflate3 = LayoutInflater.from(this).inflate(R.layout.ol_changecolor, null);
        popupWindow3.setContentView(inflate3);
        popupWindow3.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow3.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow3.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        inflate3.findViewById(R.id.white).setOnClickListener(this);
        inflate3.findViewById(R.id.yellow).setOnClickListener(this);
        inflate3.findViewById(R.id.blue).setOnClickListener(this);
        inflate3.findViewById(R.id.red).setOnClickListener(this);
        inflate3.findViewById(R.id.orange).setOnClickListener(this);
        inflate3.findViewById(R.id.purple).setOnClickListener(this);
        inflate3.findViewById(R.id.pink).setOnClickListener(this);
        inflate3.findViewById(R.id.gold).setOnClickListener(this);
        inflate3.findViewById(R.id.green).setOnClickListener(this);
        inflate3.findViewById(R.id.black).setOnClickListener(this);
        popupWindow3.setFocusable(true);
        popupWindow3.setTouchable(true);
        popupWindow3.setOutsideTouchable(true);
        changeclr = popupWindow3;
        PopupWindow popupWindow4 = new PopupWindow(this);
        View inflate4 = LayoutInflater.from(this).inflate(R.layout.ol_playsongsmode, null);
        popupWindow4.setContentView(inflate4);
        popupWindow4.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
        popupWindow4.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow4.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        inflate4.findViewById(R.id.onetimeplay).setOnClickListener(this);
        inflate4.findViewById(R.id.circulateplay).setOnClickListener(this);
        inflate4.findViewById(R.id.randomplay).setOnClickListener(this);
        inflate4.findViewById(R.id.favorplay).setOnClickListener(this);
        popupWindow4.setFocusable(true);
        popupWindow4.setTouchable(true);
        popupWindow4.setOutsideTouchable(true);
        playSongsMode = popupWindow4;
        roomTabs = findViewById(R.id.tabhost);
        roomTabs.setup();
        TabSpec newTabSpec = roomTabs.newTabSpec("tab1");
        newTabSpec.setContent(R.id.friend_tab);
        newTabSpec.setIndicator("好友");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab2");
        newTabSpec.setContent(R.id.msg_tab);
        newTabSpec.setIndicator("聊天");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab3");
        newTabSpec.setContent(R.id.songs_tab);
        newTabSpec.setIndicator("曲目");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab4");
        newTabSpec.setContent(R.id.players_tab);
        newTabSpec.setIndicator("邀请");
        roomTabs.addTab(newTabSpec);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = 0;
        while (true) {
            int intValue = i;
            if (intValue >= 4) {
                roomTabs.setOnTabChangedListener(new PlayRoomTabChange(this));
                roomTabs.setCurrentTab(1);
                timeUpdateRunning = true;
                timeUpdateThread = new TimeUpdateThread(this);
                timeUpdateThread.start();
                return;
            }
            roomTabs.getTabWidget().getChildTabViewAt(intValue).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            ((TextView) roomTabs.getTabWidget().getChildAt(intValue).findViewById(android.R.id.title)).setTextColor(0xffffffff);
            i = intValue + 1;
        }
    }

    @Override
    protected void onDestroy() {
        timeUpdateRunning = false;
        try {
            timeUpdateThread.interrupt();
        } catch (Exception ignored) {
        }
        msgList.clear();
        playerList.clear();
        invitePlayerList.clear();
        friendPlayerList.clear();
        JPStack.create();
        JPStack.pop(this);
        if (f4503ab != null && f4503ab.getCursor() != null) {
            f4503ab.getCursor().close();
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        if (testSQL != null) {
            testSQL.close();
            testSQL = null;
            sqlitedatabase.close();
            sqlitedatabase = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOnStart) {
            sendMsg((byte) 4, roomID0, hallID0, "N");
        }
        isOnStart = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isOnStart) {
            sendMsg((byte) 4, roomID0, hallID0, "N");
            roomTabs.setCurrentTab(1);
            if (msgListView != null && msgListView.getAdapter() != null) {
                msgListView.setSelection(msgListView.getAdapter().getCount() - 1);
            }
        }
        isOnStart = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isOnStart) {
            sendMsg((byte) 4, roomID0, hallID0, "B");
        }
        isOnStart = false;
    }
}
