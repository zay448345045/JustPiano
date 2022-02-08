package ly.pp.justpiano3;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.midi.MidiReceiver;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class OLPlayKeyboardRoom extends BaseActivity implements Callback, OnClickListener, View.OnTouchListener, OLPlayRoomInterface, MidiConnectionListener {

    public static final int NOTES_SEND_INTERVAL = 400;
    public int lv;
    public int cl;
    public byte roomPosition;
    public int kuang;
    public OLKeyboardState[] olKeyboardStates = new OLKeyboardState[3];
    public Handler handler;
    protected byte hallID0;
    MidiReceiver midiFramer;
    private Queue<OLNote> notesQueue = new ConcurrentLinkedQueue<>();
    private long lastNoteScheduleTime;
    protected String hallName;
    List<Bundle> friendPlayerList = new ArrayList<>();
    boolean canNotNextPage;
    OLPlayKeyboardRoomHandler olPlayKeyboardRoomHandler;
    TextView sendText;
    List<Bundle> msgList = new ArrayList<>();
    int maxListValue = 100;
    GridView playerGrid;
    GridView keyboardStatusGrid;
    List<Bundle> invitePlayerList = new ArrayList<>();
    TabHost roomTabs;
    boolean isOnStart = true;
    String userTo = "";
    ListView playerListView;
    ListView friendsListView;
    int page;
    byte roomID0;
    String roomTitleString;
    JPApplication jpapplication = null;
    TextView roomTitle;
    String playerKind = "";
    LinearLayout playerLayout;
    LinearLayout keyboardLayout;
    ConnectionService connectionService;
    Bundle bundle0;
    Bundle bundle2;
    KeyboardModeView keyboardView;
    ScheduledExecutorService scheduledExecutor;
    ScheduledExecutorService noteScheduledExecutor;
    // 用于记录上次的移动
    private boolean reSize;
    // 记录目前是否在走动画，不能重复走
    private boolean busyAnim;
    // 琴键动画间隔
    private int interval = 320;
    boolean timeUpdateRunning;
    ListView msgListView;
    private ImageView express;
    private LayoutInflater layoutInflater;
    private final List<Bundle> playerList = new ArrayList<>();
    private PopupWindow expressWindow = null;
    private PopupWindow changeclr = null;
    private TextView timeTextView;
    private int colorNum = 99;
    private TimeUpdateThread timeUpdateThread;
    private ImageView changeColorButton = null;

    public OLPlayKeyboardRoom() {
        canNotNextPage = false;
        page = 0;
        olPlayKeyboardRoomHandler = new OLPlayKeyboardRoomHandler(this);
        timeUpdateThread = null;
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

    @Override
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
        if (i == 4) {
            showCpDialog(str5.substring(str5.length() - 2) + "证书", str);
        } else if (i == 5) {
            JPDialog jpdialog = new JPDialog(this);
            jpdialog.setCancelableFalse();
            jpdialog.setTitle("提示").setMessage(str).setFirstButton("确定", new DialogDismissClick()).setSecondButton("取消", new DialogDismissClick()).showDialog();
        }
    }

    public final void mo2861a(GridView gridView, Bundle bundle) {
        playerList.clear();
        if (bundle != null) {
            int size = bundle.size() - 3;
            for (int i = 0; i < size; i++) {
                Bundle bundle1 = bundle.getBundle(String.valueOf(i));
                if (bundle1.getString("N").equals(jpapplication.getKitiName())) {
                    // 存储当前用户楼号，用于发弹奏音符
                    roomPosition = bundle1.getByte("PI");
                    kuang = bundle1.getInt("IV");
                }
                playerList.add(bundle1);
            }
            List<Bundle> list = playerList;
            if (!list.isEmpty()) {
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("PI"), o2.getByte("PI")));
            }
            gridView.setAdapter(new KeyboardPlayerImageAdapter(list, this));
            keyboardStatusGrid.setAdapter(new KeyboardPlayerStatusAdapter(this));
            // 加载完成，确认用户已经进入房间内，开始记录弹奏
            openNotesSchedule();
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

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
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
            case 4:
                Bundle data = message.getData();
                int notesSpeed = data.getInt("SPEED");
                boolean midiKeyboardOn = data.getBoolean("MIDI_ON");
                olKeyboardStates[roomPosition - 1].setMidiKeyboardOn(midiKeyboardOn);
                olKeyboardStates[roomPosition - 1].setSpeed(notesSpeed);
                ((KeyboardPlayerStatusAdapter) (keyboardStatusGrid.getAdapter())).notifyDataSetChanged();
                break;
            case R.id.keyboard_count_down:
                int keyboard1WhiteKeyNum = keyboardView.getWhiteKeyNum() - 1;
                keyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, interval);
                break;
            case R.id.keyboard_count_up:
                keyboard1WhiteKeyNum = keyboardView.getWhiteKeyNum() + 1;
                keyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, interval);
                break;
            case R.id.keyboard_move_left:
                int keyboard1WhiteKeyOffset = keyboardView.getWhiteKeyOffset() - 1;
                keyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, interval);
                break;
            case R.id.keyboard_move_right:
                keyboard1WhiteKeyOffset = keyboardView.getWhiteKeyOffset() + 1;
                keyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, interval);
                break;
            default:
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
        switch (view.getId()) {
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
        setContentView(R.layout.olplaykeyboardroom);
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        roomTitle = findViewById(R.id.room_title);
        bundle0 = getIntent().getExtras();
        bundle2 = bundle0.getBundle("bundle");
        hallID0 = bundle2.getByte("hallID");
        hallName = bundle2.getString("hallName");
        roomID0 = bundle0.getByte("ID");
        roomTitleString = bundle0.getString("R");
        playerKind = bundle0.getString("isHost");
        roomTitle.setText("[" + roomID0 + "]" + roomTitleString);
        roomTitle.setOnClickListener(this);
        playerGrid = findViewById(R.id.ol_player_grid);
        playerGrid.setCacheColorHint(0);
        keyboardStatusGrid = findViewById(R.id.ol_keyboard_state);
        keyboardStatusGrid.setCacheColorHint(0);
        playerList.clear();
        Button f4483H = findViewById(R.id.ol_send_b);
        f4483H.setOnClickListener(this);
        timeTextView = findViewById(R.id.time_text);
        Button f4519ar = findViewById(R.id.pre_button);
        Button f4520as = findViewById(R.id.next_button);
        Button f4521at = findViewById(R.id.online_button);
        f4519ar.setOnClickListener(this);
        f4520as.setOnClickListener(this);
        f4521at.setOnClickListener(this);
        friendsListView = findViewById(R.id.ol_friend_list);
        friendsListView.setCacheColorHint(0);
        sendText = findViewById(R.id.ol_send_text);
        express = findViewById(R.id.ol_express_b);
        changeColorButton = findViewById(R.id.ol_changecolor);
        playerListView = findViewById(R.id.ol_player_list);
        playerListView.setCacheColorHint(0);
        express.setOnClickListener(this);
        changeColorButton.setOnClickListener(this);
        msgListView = findViewById(R.id.ol_msg_list);
        msgListView.setCacheColorHint(0);
        handler = new Handler(this);
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
        PopupWindow popupWindow3 = new PopupWindow(this);
        View inflate3 = LayoutInflater.from(this).inflate(R.layout.ol_changecolor, null);
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
        newTabSpec.setContent(R.id.players_tab);
        newTabSpec.setIndicator("邀请");
        roomTabs.addTab(newTabSpec);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        playerLayout = findViewById(R.id.player_layout);
        keyboardLayout = findViewById(R.id.keyboard_layout);
        Button keyboardCountDown = findViewById(R.id.keyboard_count_down);
        keyboardCountDown.setOnTouchListener(this);
        Button keyboardCountup = findViewById(R.id.keyboard_count_up);
        keyboardCountup.setOnTouchListener(this);
        Button keyboardMoveLeft = findViewById(R.id.keyboard_move_left);
        keyboardMoveLeft.setOnTouchListener(this);
        Button keyboardMoveRight = findViewById(R.id.keyboard_move_right);
        keyboardMoveRight.setOnTouchListener(this);
        Button keyboardResize = findViewById(R.id.keyboard_resize);
        keyboardResize.setOnTouchListener(this);
        for (int i = 0; i < olKeyboardStates.length; i++) {
            olKeyboardStates[i] = new OLKeyboardState();
        }
        keyboardView = findViewById(R.id.keyboard_view);
        keyboardView.addMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int pitch, int volume) {
                notesQueue.offer(new OLNote(System.currentTimeMillis(), pitch, volume));
                if (!olKeyboardStates[roomPosition - 1].isMuted()) {
                    jpapplication.playSound(pitch, volume);
                }
            }

            @Override
            public void onKeyUp(int pitch) {
                notesQueue.offer(new OLNote(System.currentTimeMillis(), pitch, 0));
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null && midiFramer == null) {
                    midiFramer = new MidiFramer(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] data, int offset, int count, long timestamp) {
                            midiConnectHandle(data);
                        }
                    });
                    jpapplication.midiOutputPort.connect(midiFramer);
                }
                jpapplication.addMidiConnectionListener(this);
            }
        }
        int i = 0;
        while (true) {
            int intValue = i;
            if (intValue >= 3) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null) {
                    if (midiFramer != null) {
                        jpapplication.midiOutputPort.disconnect(midiFramer);
                        midiFramer = null;
                    }
                    jpapplication.removeMidiConnectionStart(this);
                }
            }
        }
        stopNotesSchedule();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOnStart) {
            openNotesSchedule();
            sendMsg((byte) 4, roomID0, hallID0, "N");
        }
        isOnStart = true;

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isOnStart) {
            openNotesSchedule();
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
            stopNotesSchedule();
            sendMsg((byte) 4, roomID0, hallID0, "B");
        }
        isOnStart = false;
    }

    @Override
    public void onMidiConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null && midiFramer == null) {
                    midiFramer = new MidiFramer(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] data, int offset, int count, long timestamp) {
                            midiConnectHandle(data);
                        }
                    });
                    jpapplication.midiOutputPort.connect(midiFramer);
                }
            }
        }
    }

    @Override
    public void onMidiDisconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (midiFramer != null) {
                    jpapplication.midiOutputPort.disconnect(midiFramer);
                    midiFramer = null;
                }
            }
        }
    }

    public void midiConnectHandle(byte[] data) {
        byte command = (byte) (data[0] & MidiConstants.STATUS_COMMAND_MASK);
        if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
            keyboardView.fireKeyDown(data[1], data[2], kuang, true);
        } else if (command == MidiConstants.STATUS_NOTE_OFF
                || (command == MidiConstants.STATUS_NOTE_ON && data[2] == 0)) {
            keyboardView.fireKeyUp(data[1], true);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        int id = view.getId();
        if (id == R.id.keyboard_resize) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    reSize = true;
                    view.setPressed(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float weight = event.getRawY() / (playerLayout.getHeight() + keyboardLayout.getHeight());
                    if (reSize && weight > 0.5f && weight < 0.8f) {
                        playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
                        keyboardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - weight));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    reSize = false;
                    view.setPressed(false);
                    break;
                default:
                    break;
            }
        } else {
            if (action == MotionEvent.ACTION_DOWN) {
                if (!busyAnim) {
                    view.setPressed(true);
                    updateAddOrSubtract(id);
                    busyAnim = true;
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                view.setPressed(false);
                stopAddOrSubtract();
                busyAnim = false;
            }
        }
        return true;
    }

    private void updateAddOrSubtract(int viewId) {
        final int vid = viewId;
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = new Message();
            msg.what = vid;
            interval -= 40;
            interval = Math.max(80, interval);
            handler.sendMessage(msg);
        }, 0, 80, TimeUnit.MILLISECONDS);
    }

    private void stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
            interval = 320;
        }
    }

    private void openNotesSchedule() {
        if (roomPosition == 0) {
            // 未初始化楼号，房间未完全加载完成，不开定时器
            return;
        }
        keyboardView.setNoteOnColor(kuang);
        if (noteScheduledExecutor == null) {
            lastNoteScheduleTime = System.currentTimeMillis();
            noteScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            noteScheduledExecutor.scheduleWithFixedDelay(() -> {
                int size = notesQueue.size();
                Message msg = new Message();
                Bundle bundle = new Bundle();
                msg.what = 4;
                bundle.putBoolean("MIDI_ON", midiFramer != null);
                bundle.putInt("SPEED", (size << 10) / NOTES_SEND_INTERVAL);
                msg.setData(bundle);
                handler.sendMessage(msg);
                long scheduleTimeNow = System.currentTimeMillis();
                // 未检测到这段间隔有弹奏音符，直接返回并记录此次定时任务执行时间点
                if (size == 0) {
                    lastNoteScheduleTime = scheduleTimeNow;
                    return;
                }
                try {
                    long timeLast = lastNoteScheduleTime;
                    byte[] notes = new byte[size * 3 + 1];
                    notes[0] = (byte) (((midiFramer == null ? 0 : 1) << 4) + roomPosition);
                    int i = 1;
                    int pollIndex = size;
                    while (pollIndex > 0) {
                        OLNote olNote = notesQueue.poll();
                        pollIndex--;
                        if (olNote == null) {
                            break;
                        }
                        // 记录并发问题：到下面i++时触发越界，可见队列size已经在并发环境下变化，必须在里面再判断一次size
                        notes[i++] = (byte) ((olNote.getAbsoluteTime() - timeLast) >> 2);
                        notes[i++] = (byte) olNote.getPitch();
                        notes[i++] = (byte) olNote.getVolume();
                        timeLast = olNote.getAbsoluteTime();
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("N", GZIP.arrayToZIP(notes));
                    sendMsg((byte) 39, roomID0, hallID0, jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lastNoteScheduleTime = scheduleTimeNow;
                }
            }, NOTES_SEND_INTERVAL, NOTES_SEND_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopNotesSchedule() {
        if (noteScheduledExecutor != null) {
            noteScheduledExecutor.shutdownNow();
            noteScheduledExecutor = null;
        }
    }
}
