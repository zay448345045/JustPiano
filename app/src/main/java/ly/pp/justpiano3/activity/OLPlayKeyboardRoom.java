package ly.pp.justpiano3.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.midi.MidiReceiver;
import android.os.*;
import android.os.Handler.Callback;
import android.preference.PreferenceManager;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.TabHost.TabSpec;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.*;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.MidiConstants;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.OLKeyboardState;
import ly.pp.justpiano3.entity.OLNote;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.enums.KeyboardSyncModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayKeyboardRoomHandler;
import ly.pp.justpiano3.listener.*;
import ly.pp.justpiano3.listener.tab.PlayRoomTabChange;
import ly.pp.justpiano3.midi.MidiConnectionListener;
import ly.pp.justpiano3.midi.MidiFramer;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.thread.TimeUpdateThread;
import ly.pp.justpiano3.utils.*;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;
import ly.pp.justpiano3.view.KeyboardModeView;
import org.json.JSONObject;
import protobuf.dto.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public final class OLPlayKeyboardRoom extends BaseActivity implements Callback, OnClickListener, View.OnTouchListener, OLPlayRoomInterface, MidiConnectionListener {

    public static final int NOTES_SEND_INTERVAL = 120;
    public int lv;
    public int cl;
    // 当前用户楼号 - 1
    public byte roomPositionSub1 = -1;
    public ExecutorService receiveThreadPool = Executors.newSingleThreadExecutor();
    public int kuang;
    public OLKeyboardState[] olKeyboardStates = new OLKeyboardState[6];
    public Handler handler;
    public byte hallID0;
    public JPProgressBar jpprogressBar;
    public MidiReceiver midiFramer;
    private final Queue<OLNote> notesQueue = new ConcurrentLinkedQueue<>();
    private long lastNoteScheduleTime;
    /**
     * 键盘房间同步模式(默认编排模式)
     */
    private KeyboardSyncModeEnum keyboardSyncMode = KeyboardSyncModeEnum.CONCERTO;
    public String hallName;
    public List<Bundle> friendPlayerList = new ArrayList<>();
    public boolean canNotNextPage;
    public OLPlayKeyboardRoomHandler olPlayKeyboardRoomHandler;
    public TextView sendText;
    public List<Bundle> msgList = new ArrayList<>();
    public int maxListValue = 100;
    public GridView playerGrid;
    public List<Bundle> invitePlayerList = new ArrayList<>();
    public TabHost roomTabs;
    public boolean isOnStart = true;
    public String userTo = "";
    public ListView playerListView;
    public ListView friendsListView;
    public int page;
    public byte roomID0;
    public String roomTitleString;
    public JPApplication jpapplication = null;
    public TextView roomTitle;
    public String playerKind = "";
    public LinearLayout playerLayout;
    public LinearLayout keyboardLayout;
    public ConnectionService connectionService;
    public Bundle bundle0;
    public Bundle bundle2;
    public KeyboardModeView keyboardView;
    public SharedPreferences sharedPreferences;
    public ScheduledExecutorService keyboardScheduledExecutor;
    public ScheduledExecutorService noteScheduledExecutor;
    public ImageView keyboardSetting;
    // 用于记录上次的移动
    private boolean reSize;
    // 记录目前是否在走动画，不能重复走
    private boolean busyAnim;
    // 琴键动画间隔
    private int interval = 320;
    public boolean timeUpdateRunning;
    public ListView msgListView;
    public PopupWindow keyboardSettingPopup = null;
    private ImageView express;
    private LayoutInflater layoutInflater;
    public final List<Bundle> playerList = new ArrayList<>();
    private PopupWindow expressWindow = null;
    private PopupWindow changeclr = null;
    private TextView timeTextView;
    private int colorNum = 99;
    private TimeUpdateThread timeUpdateThread;
    private ImageView changeColorButton = null;
    private boolean recordStart;
    private String recordFilePath;
    private String recordFileName;

    public OLPlayKeyboardRoom() {
        canNotNextPage = false;
        page = 0;
        olPlayKeyboardRoomHandler = new OLPlayKeyboardRoomHandler(this);
        timeUpdateThread = null;
    }

    private void showCpDialog(String str, String str2) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_couple_dialog, findViewById(R.id.dialog));
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONObject jSONObject2 = jSONObject.getJSONObject("P");
            User User = new User(jSONObject2.getString("N"), jSONObject2.getInt("D_H"),
                    jSONObject2.getInt("D_E"), jSONObject2.getInt("D_J"),
                    jSONObject2.getInt("D_T"), jSONObject2.getInt("D_S"),
                    jSONObject2.getString("S"), jSONObject2.getInt("L"), jSONObject2.getInt("C"));
            JSONObject jSONObject3 = jSONObject.getJSONObject("C");
            User User2 = new User(jSONObject3.getString("N"), jSONObject3.getInt("D_H"),
                    jSONObject3.getInt("D_E"), jSONObject3.getInt("D_J"),
                    jSONObject3.getInt("D_T"), jSONObject3.getInt("D_S"),
                    jSONObject3.getString("S"), jSONObject3.getInt("L"), jSONObject3.getInt("C"));
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
            ImageView imageView4e = inflate.findViewById(R.id.ol_player_eye);
            ImageView imageView5 = inflate.findViewById(R.id.ol_player_shoes);
            ImageView imageView6 = inflate.findViewById(R.id.ol_couple_mod);
            ImageView imageView7 = inflate.findViewById(R.id.ol_couple_trousers);
            ImageView imageView8 = inflate.findViewById(R.id.ol_couple_jacket);
            ImageView imageView9 = inflate.findViewById(R.id.ol_couple_hair);
            ImageView imageView9e = inflate.findViewById(R.id.ol_couple_eye);
            ImageView imageView10 = inflate.findViewById(R.id.ol_couple_shoes);
            TextView textView8 = inflate.findViewById(R.id.couple_bless);
            ImageView imageView11 = inflate.findViewById(R.id.couple_type);
            ((TextView) inflate.findViewById(R.id.ol_player_name)).setText(User.getPlayerName());
            textView.setText("LV." + User.getLevel());
            textView2.setText("CL." + User.getClevel());
            textView3.setText(Consts.nameCL[User.getClevel()]);
            textView4.setText(User2.getPlayerName());
            textView5.setText("LV." + User2.getLevel());
            textView6.setText("CL." + User2.getClevel());
            textView7.setText(Consts.nameCL[User2.getClevel()]);
            textView8.setText(jSONObject4.getString("B"));
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
            if (User.getEye() <= 0) {
                imageView4e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_e" + (User.getEye() - 1) + ".png")));
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
            if (User2.getEye() <= 0) {
                imageView9e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView9e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_e" + (User2.getEye() - 1) + ".png")));
            }
            if (User2.getShoes() <= 0) {
                imageView10.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView10.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User2.getSex() + "_s" + (User2.getShoes() - 1) + ".png")));
            }
            new JPDialog(this).setTitle(str).loadInflate(inflate).setFirstButton("祝福:" + jSONObject4.getInt("P"), new SendZhufuClick(this, jSONObject4)).setSecondButton("取消", new DialogDismissClick()).showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInfoDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_info_dialog, findViewById(R.id.dialog));
        try {
            User User = new User(b.getString("U"), b.getInt("DR_H"), b.getInt("DR_E"), b.getInt("DR_J"),
                    b.getInt("DR_T"), b.getInt("DR_S"), b.getString("S"), b.getInt("LV"), b.getInt("CL"));
            ImageView imageView = inflate.findViewById(R.id.ol_user_mod);
            ImageView imageView2 = inflate.findViewById(R.id.ol_user_trousers);
            ImageView imageView3 = inflate.findViewById(R.id.ol_user_jacket);
            ImageView imageView4 = inflate.findViewById(R.id.ol_user_hair);
            ImageView imageView4e = inflate.findViewById(R.id.ol_user_eye);
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
            if (User.getEye() <= 0) {
                imageView4e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/_none.png")));
            } else {
                imageView4e.setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("mod/" + User.getSex() + "_e" + (User.getEye() - 1) + ".png")));
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

    public void sendMsg(int type, MessageLite msg) {
        if (connectionService != null) {
            connectionService.writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    private void broadNote(int pitch, int volume) {
        switch (keyboardSyncMode) {

            case ORCHESTRATE:
                // 编排模式
                notesQueue.offer(new OLNote(System.currentTimeMillis(), pitch, volume));
                break;

            case CONCERTO:
                // 协奏模式
                byte[] notes = new byte[4];
                // 字节数组开头，存入是否开启midi键盘和楼号
                notes[0] = (byte) (((midiFramer == null ? 0 : 1) << 4) + roomPositionSub1);
                notes[1] = (byte) 0;
                notes[2] = (byte) pitch;
                notes[3] = (byte) volume;
                OnlineKeyboardNoteDTO.Builder builder = OnlineKeyboardNoteDTO.newBuilder();
                builder.setData(ByteString.copyFrom(notes));
                sendMsg(OnlineProtocolType.KEYBOARD, builder.build());
                break;
        }

    }

    public void putJPhashMap(byte b, User User) {
        jpapplication.getHashmap().put(b, User);
    }

    public void mo2860a(int i, String str, int i2, byte b) {
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

    public void mo2861a(GridView gridView, Bundle bundle) {
        playerList.clear();
        if (bundle != null) {
            int size = bundle.size() - 2;
            for (int i = 0; i < size; i++) {
                Bundle bundle1 = bundle.getBundle(String.valueOf(i));
                String name = bundle1.getString("N");
                int positionSub1 = bundle1.getByte("PI") - 1;
                if (positionSub1 < olKeyboardStates.length) {
                    // 判定位置是否有人，忽略琴娘
                    boolean hasUser = !name.isEmpty() && !name.equals("琴娘");
                    olKeyboardStates[positionSub1].setHasUser(hasUser);
                    if (!hasUser) {
                        olKeyboardStates[positionSub1].setMidiKeyboardOn(false);
                    }
                }
                if (name.equals(jpapplication.getKitiName())) {
                    // 存储当前用户楼号，用于发弹奏音符
                    roomPositionSub1 = (byte) positionSub1;
                    kuang = bundle1.getInt("IV");
                    olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(midiFramer != null);
                }
                playerList.add(bundle1);
            }
            List<Bundle> list = playerList;
            if (!list.isEmpty()) {
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("PI"), o2.getByte("PI")));
            }
            gridView.setAdapter(new KeyboardPlayerImageAdapter(list, this));
            // 加载完成，确认用户已经进入房间内，开始记录弹奏
            openNotesSchedule();
        }
    }

    public void mo2862a(boolean showChatTime) {
        int posi = msgListView.getFirstVisiblePosition();
        msgListView.setAdapter(new ChattingAdapter(jpapplication, msgList, layoutInflater, showChatTime));
        System.out.println("位置" + posi);
        if (posi > 0) {
            msgListView.setSelection(posi + 2);
        } else {
            msgListView.setSelection(msgListView.getBottom());
        }
    }

    public void mo2863a(ListView listView, List<Bundle> list, int i) {
        if (list != null && !list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> Integer.compare(o2.getInt("O"), o1.getInt("O")));
        }
        listView.setAdapter(new MainGameAdapter(list, (JPApplication) getApplicationContext(), i, this));
    }

    public void sendMail(String str) {
        View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_1);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        TextView textView3 = inflate.findViewById(R.id.title_2);
        inflate.findViewById(R.id.text_2).setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView2.setText("内容:");
        new JPDialog(this).setTitle("发送私信给:" + str).loadInflate(inflate).setFirstButton("发送", new SendMailClick(this, textView, str)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public void mo2867c(String str) {
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
        SharedPreferences.Editor edit = sharedPreferences.edit();
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
            case R.id.keyboard_count_down:
                int keyboard1WhiteKeyNum = keyboardView.getWhiteKeyNum() - 1;
                keyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                edit.putInt("ol_keyboard_white_key_num", keyboardView.getWhiteKeyNum());
                edit.apply();
                break;
            case R.id.keyboard_count_up:
                keyboard1WhiteKeyNum = keyboardView.getWhiteKeyNum() + 1;
                keyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                edit.putInt("ol_keyboard_white_key_num", keyboardView.getWhiteKeyNum());
                edit.apply();
                break;
            case R.id.keyboard_move_left:
                int keyboard1WhiteKeyOffset = keyboardView.getWhiteKeyOffset() - 1;
                keyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                edit.putInt("ol_keyboard_white_key_offset", keyboardView.getWhiteKeyOffset());
                edit.apply();
                break;
            case R.id.keyboard_move_right:
                keyboard1WhiteKeyOffset = keyboardView.getWhiteKeyOffset() + 1;
                keyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                edit.putInt("ol_keyboard_white_key_offset", keyboardView.getWhiteKeyOffset());
                edit.apply();
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
        jpdialog.showDialog();
    }

    @Override
    public void onClick(View view) {
        String str;
        switch (view.getId()) {
            case R.id.pre_button:
                page -= 20;
                if (page < 0) {
                    page = 0;
                    return;
                }
                OnlineLoadUserInfoDTO.Builder builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(page);
                sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                return;
            case R.id.online_button:
                builder = OnlineLoadUserInfoDTO.newBuilder();
                builder.setType(1);
                builder.setPage(-1);
                sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                return;
            case R.id.next_button:
                if (!canNotNextPage) {
                    page += 20;
                    if (page >= 0) {
                        builder = OnlineLoadUserInfoDTO.newBuilder();
                        builder.setType(1);
                        builder.setPage(page);
                        sendMsg(OnlineProtocolType.LOAD_USER_INFO, builder.build());
                        return;
                    }
                    return;
                }
                return;
            case R.id.ol_send_b:
                OnlineRoomChatDTO.Builder builder2 = OnlineRoomChatDTO.newBuilder();
                str = String.valueOf(sendText.getText());
                if (!str.startsWith(userTo) || str.length() <= userTo.length()) {
                    builder2.setUserName("");
                    builder2.setMessage(str);
                } else {
                    builder2.setUserName(userTo);
                    str = str.substring(userTo.length());
                    builder2.setMessage(str);
                }
                sendText.setText("");
                builder2.setColor(colorNum);
                if (!str.isEmpty()) {
                    sendMsg(OnlineProtocolType.ROOM_CHAT, builder2.build());
                }
                userTo = "";
                return;
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
                    Toast.makeText(this, "只有房主才能修改房名!", Toast.LENGTH_SHORT).show();
                } else {
                    View inflate = getLayoutInflater().inflate(R.layout.message_send, findViewById(R.id.dialog));
                    EditText text1 = inflate.findViewById(R.id.text_1);
                    EditText text2 = inflate.findViewById(R.id.text_2);
                    new JPDialog(this).setTitle("修改房名").loadInflate(inflate).setFirstButton("修改", new ChangeRoomNameClick(this, text1, text2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
                }
                return;
            case R.id.keyboard_setting:
                PopupWindow popupWindow2 = new PopupWindow(this);
                View inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_keyboard_setting_list, null);
                popupWindow2.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_box));
                popupWindow2.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow2.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                inflate2.findViewById(R.id.midi_down_tune).setOnClickListener(this);
                inflate2.findViewById(R.id.midi_up_tune).setOnClickListener(this);
                inflate2.findViewById(R.id.sound_down_tune).setOnClickListener(this);
                inflate2.findViewById(R.id.sound_up_tune).setOnClickListener(this);
                inflate2.findViewById(R.id.skin_setting).setOnClickListener(this);
                inflate2.findViewById(R.id.sound_setting).setOnClickListener(this);
                inflate2.findViewById(R.id.keyboard_sync_mode_concerto).setOnClickListener(this);
                inflate2.findViewById(R.id.keyboard_sync_mode_text).setOnClickListener(this);
                inflate2.findViewById(R.id.keyboard_sync_mode_orchestrate).setOnClickListener(this);
                popupWindow2.setFocusable(true);
                popupWindow2.setTouchable(true);
                popupWindow2.setOutsideTouchable(true);
                popupWindow2.setContentView(inflate2);
                // 键盘声调回显
                TextView midiTune = inflate2.findViewById(R.id.midi_tune);
                midiTune.setText(String.valueOf(GlobalSetting.INSTANCE.getMidiKeyboardTune()));
                // 声调回显
                TextView soundTune = inflate2.findViewById(R.id.sound_tune);
                soundTune.setText(String.valueOf(GlobalSetting.INSTANCE.getKeyboardSoundTune()));
                // 同步模式回显
                TextView syncModeText = inflate2.findViewById(R.id.keyboard_sync_mode_text);
                syncModeText.setText(keyboardSyncMode.getDesc());
                keyboardSettingPopup = popupWindow2;
                popupWindow2.showAtLocation(keyboardSetting, Gravity.CENTER, 0, 0);
                return;
            case R.id.midi_down_tune:
                if (GlobalSetting.INSTANCE.getMidiKeyboardTune() > -6) {
                    GlobalSetting.INSTANCE.setMidiKeyboardTune(GlobalSetting.INSTANCE.getMidiKeyboardTune() - 1);
                    GlobalSetting.INSTANCE.saveSettings(jpapplication);
                }
                if (keyboardSettingPopup != null) {
                    keyboardSettingPopup.dismiss();
                }
                return;
            case R.id.midi_up_tune:
                if (GlobalSetting.INSTANCE.getMidiKeyboardTune() < 6) {
                    GlobalSetting.INSTANCE.setMidiKeyboardTune(GlobalSetting.INSTANCE.getMidiKeyboardTune() + 1);
                    GlobalSetting.INSTANCE.saveSettings(jpapplication);
                }
                if (keyboardSettingPopup != null) {
                    keyboardSettingPopup.dismiss();
                }
                return;
            case R.id.sound_down_tune:
                if (GlobalSetting.INSTANCE.getKeyboardSoundTune() > -6) {
                    GlobalSetting.INSTANCE.setKeyboardSoundTune(GlobalSetting.INSTANCE.getKeyboardSoundTune() - 1);
                    GlobalSetting.INSTANCE.saveSettings(jpapplication);
                }
                if (keyboardSettingPopup != null) {
                    keyboardSettingPopup.dismiss();
                }
                return;
            case R.id.sound_up_tune:
                if (GlobalSetting.INSTANCE.getKeyboardSoundTune() < 6) {
                    GlobalSetting.INSTANCE.setKeyboardSoundTune(GlobalSetting.INSTANCE.getKeyboardSoundTune() + 1);
                    GlobalSetting.INSTANCE.saveSettings(jpapplication);
                }
                if (keyboardSettingPopup != null) {
                    keyboardSettingPopup.dismiss();
                }
                return;
            case R.id.skin_setting:
                try {
                    String path = Environment.getExternalStorageDirectory() + "/JustPiano/Skins";
                    List<File> localSkinList = SkinAndSoundFileUtil.getLocalSkinList(path);
                    List<String> skinList = new ArrayList<>();
                    skinList.add("原生主题");
                    for (File file : localSkinList) {
                        skinList.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
                    }
                    View inflate = getLayoutInflater().inflate(R.layout.account_list, findViewById(R.id.dialog));
                    ListView listView = inflate.findViewById(R.id.account_list);
                    JPDialog.JDialog b = new JPDialog(this).setTitle("切换皮肤").loadInflate(inflate).setFirstButton("取消", new DialogDismissClick()).createJDialog();
                    listView.setAdapter(new SimpleSkinListAdapter(skinList, localSkinList, layoutInflater, this, b));
                    b.show();
                    if (keyboardSettingPopup != null) {
                        keyboardSettingPopup.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case R.id.sound_setting:
                try {
                    String path = Environment.getExternalStorageDirectory() + "/JustPiano/Sounds";
                    List<File> localSoundList = SkinAndSoundFileUtil.getLocalSoundList(path);
                    List<String> soundList = new ArrayList<>();
                    soundList.add("原生音源");
                    for (File file : localSoundList) {
                        soundList.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
                    }
                    View inflate = getLayoutInflater().inflate(R.layout.account_list, findViewById(R.id.dialog));
                    ListView listView = inflate.findViewById(R.id.account_list);
                    JPDialog.JDialog b = new JPDialog(this).setTitle("切换皮肤").loadInflate(inflate).setFirstButton("取消", new DialogDismissClick()).createJDialog();
                    listView.setAdapter(new SimpleSoundListAdapter(soundList, localSoundList, layoutInflater, this, b));
                    b.show();
                    if (keyboardSettingPopup != null) {
                        keyboardSettingPopup.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case R.id.keyboard_record:
                try {
                    Button recordButton = (Button) view;
                    if (!recordStart) {
                        JPDialog jpdialog = new JPDialog(this);
                        jpdialog.setTitle("提示");
                        jpdialog.setMessage("点击确定按钮开始录音，录音将在点击停止按钮后保存至录音文件");
                        jpdialog.setFirstButton("确定", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            String date = DateUtil.format(DateUtil.now());
                            recordFilePath = getFilesDir().getAbsolutePath() + "/Records/" + date + ".raw";
                            recordFileName = date + "录音.wav";
                            SoundEngineUtil.setRecordFilePath(recordFilePath);
                            SoundEngineUtil.setRecord(true);
                            recordStart = true;
                            Toast.makeText(this, "开始录音...", Toast.LENGTH_SHORT).show();
                            recordButton.setText("■");
                            recordButton.setTextColor(getResources().getColor(R.color.dark));
                            recordButton.setBackground(getResources().getDrawable(R.drawable.selector_ol_orange));
                        });
                        jpdialog.setSecondButton("取消", new DialogDismissClick());
                        jpdialog.showDialog();
                    } else {
                        recordButton.setText("●");
                        recordButton.setTextColor(getResources().getColor(R.color.v3));
                        recordButton.setBackground(getResources().getDrawable(R.drawable.selector_ol_button));
                        SoundEngineUtil.setRecord(false);
                        recordStart = false;
                        File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
                        File desFile = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/" + recordFileName);
                        FileUtil.moveFile(srcFile, desFile);
                        Toast.makeText(this, "录音完毕，文件已存储至SD卡\\JustPiano\\Records中", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                }
                return;
            default:
        }

        /* 使用gradle 8以上的推荐写法 有空把上面也优化了 TODO */
        int viewId = view.getId();
        if (viewId == R.id.keyboard_sync_mode_text) {
            try {
                new JPDialog(this).setTitle(getString(R.string.msg_this_is_what))
                        .setMessage(getString(R.string.ol_keyboard_sync_mode_help))
                        .setFirstButton("确定", new DialogDismissClick()).createJDialog().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (viewId == R.id.keyboard_sync_mode_orchestrate) {
            keyboardSyncMode = KeyboardSyncModeEnum.ORCHESTRATE;
            if (keyboardSettingPopup != null) {
                keyboardSettingPopup.dismiss();
            }
        } else if (viewId == R.id.keyboard_sync_mode_concerto) {
            keyboardSyncMode = KeyboardSyncModeEnum.CONCERTO;
            if (keyboardSettingPopup != null) {
                keyboardSettingPopup.dismiss();
            }
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
        JPStack.push(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        layoutInflater = LayoutInflater.from(this);
        jpapplication = (JPApplication) getApplication();
        jpapplication.getHashmap().clear();
        connectionService = jpapplication.getConnectionService();
        setContentView(R.layout.olplaykeyboardroom);
        SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
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
        playerList.clear();
        Button sendBotton = findViewById(R.id.ol_send_b);
        sendBotton.setOnClickListener(this);
        timeTextView = findViewById(R.id.time_text);
        Button preButton = findViewById(R.id.pre_button);
        Button nextButton = findViewById(R.id.next_button);
        Button onlineButton = findViewById(R.id.online_button);
        preButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        onlineButton.setOnClickListener(this);
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
        sendMsg(OnlineProtocolType.LOAD_ROOM_POSITION, OnlineLoadRoomPositionDTO.getDefaultInstance());
        msgList.clear();
        jpprogressBar = new JPProgressBar(this);
        PopupWindow popupWindow = new PopupWindow(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.ol_express_list, null);
        popupWindow.setContentView(inflate);
        ((GridView) inflate.findViewById(R.id.ol_express_grid)).setAdapter(new ExpressAdapter(jpapplication, connectionService, Consts.expressions, popupWindow, 13));
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable._none));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        expressWindow = popupWindow;
        PopupWindow popupWindow3 = new PopupWindow(this);
        View inflate3 = LayoutInflater.from(this).inflate(R.layout.ol_changecolor, null);
        popupWindow3.setContentView(inflate3);
        popupWindow3.setBackgroundDrawable(getResources().getDrawable(R.drawable._none));
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
        keyboardSetting = findViewById(R.id.keyboard_setting);
        keyboardSetting.setOnClickListener(this);
        Button keyboardRecord = findViewById(R.id.keyboard_record);
        keyboardRecord.setOnClickListener(this);
        for (int i = 0; i < olKeyboardStates.length; i++) {
            olKeyboardStates[i] = new OLKeyboardState(false, false, false, false);
        }
        keyboardView = findViewById(R.id.keyboard_view);
        keyboardView.setMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(byte pitch, byte volume) {
                if (hasAnotherUser()) {
                    broadNote(pitch, volume);
                }
                if (roomPositionSub1 >= 0) {
                    if (!olKeyboardStates[roomPositionSub1].isMuted()) {
                        SoundEngineUtil.playSound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()), volume);
                    }
                    if (!olKeyboardStates[roomPositionSub1].isPlaying()) {
                        olKeyboardStates[roomPositionSub1].setPlaying(true);
                        if (playerGrid.getAdapter() != null) {
                            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onKeyUp(byte pitch) {
                if (hasAnotherUser()) {
                    broadNote(pitch, 0);
                }
                if (roomPositionSub1 >= 0) {
                    if (!olKeyboardStates[roomPositionSub1].isPlaying()) {
                        olKeyboardStates[roomPositionSub1].setPlaying(true);
                        if (playerGrid.getAdapter() != null) {
                            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int keyboardWhiteKeyNum = sharedPreferences.getInt("ol_keyboard_white_key_num", 15);
        int keyboardWhiteKeyOffset = sharedPreferences.getInt("ol_keyboard_white_key_offset", 14);
        float keyboardWeight = sharedPreferences.getFloat("ol_keyboard_weight", 0.75f);
        keyboardView.setWhiteKeyOffset(keyboardWhiteKeyOffset, 0);
        keyboardView.setWhiteKeyNum(keyboardWhiteKeyNum, 0);
        playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, keyboardWeight));
        keyboardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - keyboardWeight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (MidiUtil.getMidiOutputPort() != null && midiFramer == null) {
                    midiFramer = new MidiFramer(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] data, int offset, int count, long timestamp) {
                            midiConnectHandle(data);
                        }
                    });
                    MidiUtil.getMidiOutputPort().connect(midiFramer);
                    MidiUtil.addMidiConnectionListener(this);
                }
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
            TextView tv = roomTabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(0xffffffff);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
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
        JPStack.pop(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            if (MidiUtil.getMidiOutputPort() != null) {
                if (midiFramer != null) {
                    MidiUtil.getMidiOutputPort().disconnect(midiFramer);
                    midiFramer = null;
                }
            }
            MidiUtil.removeMidiConnectionStart(this);
        }
        stopNotesSchedule();
        if (recordStart) {
            SoundEngineUtil.setRecord(false);
            recordStart = false;
            File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
            File desFile = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/" + recordFileName);
            FileUtil.moveFile(srcFile, desFile);
            Toast.makeText(this, "录音完毕，文件已存储至SD卡\\JustPiano\\Records中", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOnStart) {
            openNotesSchedule();
            OnlineChangeRoomUserStatusDTO.Builder builder1 = OnlineChangeRoomUserStatusDTO.newBuilder();
            builder1.setStatus("N");
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder1.build());
        }
        isOnStart = true;

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isOnStart) {
            openNotesSchedule();
            OnlineChangeRoomUserStatusDTO.Builder builder1 = OnlineChangeRoomUserStatusDTO.newBuilder();
            builder1.setStatus("N");
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder1.build());
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
            OnlineChangeRoomUserStatusDTO.Builder builder1 = OnlineChangeRoomUserStatusDTO.newBuilder();
            builder1.setStatus("B");
            sendMsg(OnlineProtocolType.CHANGE_ROOM_USER_STATUS, builder1.build());
        }
        isOnStart = false;
    }

    @Override
    public void onMidiConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            if (MidiUtil.getMidiOutputPort() != null && midiFramer == null) {
                midiFramer = new MidiFramer(new MidiReceiver() {
                    @Override
                    public void onSend(byte[] data, int offset, int count, long timestamp) {
                        midiConnectHandle(data);
                    }
                });
                MidiUtil.getMidiOutputPort().connect(midiFramer);
                olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(true);
                runOnUiThread(() -> {
                    if (playerGrid.getAdapter() != null) {
                        ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                    } else {
                        playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
                    }
                });
            }
        }
    }

    @Override
    public void onMidiDisconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            if (midiFramer != null) {
                MidiUtil.getMidiOutputPort().disconnect(midiFramer);
                midiFramer = null;
                olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(false);
                runOnUiThread(() -> {
                    if (playerGrid.getAdapter() != null) {
                        ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                    } else {
                        playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
                    }
                });
            }
        }
    }

    public void midiConnectHandle(byte[] data) {
        byte command = (byte) (data[0] & MidiConstants.STATUS_COMMAND_MASK);
        byte pitch = (byte) (data[1] + GlobalSetting.INSTANCE.getMidiKeyboardTune());
        if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
            keyboardView.fireKeyDown(pitch, data[2], kuang == 0 ? null : ColorUtil.getKuangColorByKuangIndex(this, kuang));
            if (hasAnotherUser()) {
                broadNote(pitch, data[2]);
            }
            if (roomPositionSub1 >= 0) {
                if (!olKeyboardStates[roomPositionSub1].isMuted()) {
                    SoundEngineUtil.playSound(pitch, data[2]);
                }
                if (!olKeyboardStates[roomPositionSub1].isPlaying()) {
                    olKeyboardStates[roomPositionSub1].setPlaying(true);
                    runOnUiThread(() -> {
                        if (playerGrid.getAdapter() != null) {
                            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                        }
                    });
                }
            }
        } else if (command == MidiConstants.STATUS_NOTE_OFF
                || (command == MidiConstants.STATUS_NOTE_ON && data[2] == 0)) {
            keyboardView.fireKeyUp(pitch);
            if (hasAnotherUser()) {
                broadNote(pitch, 0);
            }
            if (roomPositionSub1 >= 0) {
                if (!olKeyboardStates[roomPositionSub1].isPlaying()) {
                    olKeyboardStates[roomPositionSub1].setPlaying(true);
                    runOnUiThread(() -> {
                        if (playerGrid.getAdapter() != null) {
                            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                        }
                    });
                }
            }
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
                    if (reSize && weight > 0.65f && weight < 0.92f) {
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
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerLayout.getLayoutParams();
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putFloat("ol_keyboard_weight", layoutParams.weight);
                    edit.apply();
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
        keyboardScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        keyboardScheduledExecutor.scheduleWithFixedDelay(() -> {
            Message msg = Message.obtain(handler);
            msg.what = vid;
            interval -= 40;
            interval = Math.max(80, interval);
            handler.sendMessage(msg);
        }, 0, 80, TimeUnit.MILLISECONDS);
    }

    private void stopAddOrSubtract() {
        if (keyboardScheduledExecutor != null) {
            keyboardScheduledExecutor.shutdownNow();
            keyboardScheduledExecutor = null;
            interval = 320;
        }
    }

    private void openNotesSchedule() {
        if (roomPositionSub1 == -1) {
            // 未初始化楼号，房间未完全加载完成，不开定时器
            return;
        }
        if (kuang > 0) {
            keyboardView.setNoteOnColor(ColorUtil.getKuangColorByKuangIndex(this, kuang));
        }
        if (noteScheduledExecutor == null) {
            lastNoteScheduleTime = System.currentTimeMillis();
            noteScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            noteScheduledExecutor.scheduleWithFixedDelay(() -> {
                // 时间戳和size尽量严格放在一起
                long scheduleTimeNow = System.currentTimeMillis();
                int size = notesQueue.size();
                // 刷新玩家弹奏闪烁（删除闪烁）
                if (olKeyboardStates[roomPositionSub1].isPlaying()) {
                    olKeyboardStates[roomPositionSub1].setPlaying(false);
                    runOnUiThread(() -> {
                        if (playerGrid.getAdapter() != null) {
                            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                        }
                    });
                }
                // 房间里没有其他人，停止发任何消息，清空弹奏队列（因为可能刚刚变为房间没人的状态，队列可能有遗留
                if (!hasAnotherUser()) {
                    notesQueue.clear();
                    lastNoteScheduleTime = scheduleTimeNow;
                    return;
                }
                // 未检测到这段间隔有弹奏音符，或者房间里没有其他人，就不发消息给服务器，直接返回并记录此次定时任务执行时间点
                if (size == 0) {
                    lastNoteScheduleTime = scheduleTimeNow;
                    return;
                }
                try {
                    long timeLast = lastNoteScheduleTime;
                    byte[] notes = new byte[size * 3 + 1];
                    // 字节数组开头，存入是否开启midi键盘和楼号
                    notes[0] = (byte) (((midiFramer == null ? 0 : 1) << 4) + roomPositionSub1);
                    int i = 1;
                    int pollIndex = size;
                    // 存下size然后自减，确保并发环境下size还是根据上面时间戳而计算来的严格的size，否则此时队列中实际size可能增多了
                    while (pollIndex > 0) {
                        OLNote olNote = notesQueue.poll();
                        pollIndex--;
                        if (olNote == null) {
                            break;
                        }
                        // 记录并发问题：到下面i++时触发越界，可见队列size已经在并发环境下变化，必须在里面再判断一次size
                        notes[i++] = (byte) (olNote.getAbsoluteTime() - timeLast);
                        notes[i++] = (byte) olNote.getPitch();
                        notes[i++] = (byte) olNote.getVolume();
                        // 切换时间点
                        timeLast = olNote.getAbsoluteTime();
                    }
                    OnlineKeyboardNoteDTO.Builder builder = OnlineKeyboardNoteDTO.newBuilder();
                    builder.setData(ByteString.copyFrom(notes));
                    sendMsg(OnlineProtocolType.KEYBOARD, builder.build());
                    if (olKeyboardStates[roomPositionSub1].isPlaying()) {
                        olKeyboardStates[roomPositionSub1].setPlaying(false);
                        runOnUiThread(() -> {
                            if (playerGrid.getAdapter() != null) {
                                ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lastNoteScheduleTime = scheduleTimeNow;
                }
            }, NOTES_SEND_INTERVAL, NOTES_SEND_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private boolean hasAnotherUser() {
        for (int i = 0; i < olKeyboardStates.length; i++) {
            if (i != (roomPositionSub1) && olKeyboardStates[i].isHasUser()) {
                return true;
            }
        }
        return false;
    }

    private void stopNotesSchedule() {
        if (noteScheduledExecutor != null) {
            noteScheduledExecutor.shutdownNow();
            noteScheduledExecutor = null;
        }
    }
}
