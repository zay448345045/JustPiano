package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kotlin.Pair;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.OLKeyboardState;
import ly.pp.justpiano3.entity.OLNote;
import ly.pp.justpiano3.entity.Room;
import ly.pp.justpiano3.entity.WaterfallNote;
import ly.pp.justpiano3.handler.android.OLPlayKeyboardRoomHandler;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.VibrationUtil;
import ly.pp.justpiano3.utils.WaterfallUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import ly.pp.justpiano3.view.KeyboardView;
import ly.pp.justpiano3.view.WaterfallView;
import protobuf.dto.OnlineKeyboardNoteDTO;
import protobuf.dto.OnlineLoadRoomPositionDTO;

public final class OLPlayKeyboardRoom extends OLPlayRoomActivity implements View.OnTouchListener, MidiDeviceUtil.MidiMessageReceiveListener {
    public static final int NOTES_SEND_INTERVAL = 120;
    // 当前用户楼号 - 1
    public byte roomPositionSub1 = -1;
    public ExecutorService receiveThreadPool = Executors.newSingleThreadExecutor();
    public Integer keyboardNoteDownColor;
    public OLKeyboardState[] olKeyboardStates = new OLKeyboardState[Room.CAPACITY];
    private final Queue<OLNote> notesQueue = new ConcurrentLinkedQueue<>();
    public OLPlayKeyboardRoomHandler olPlayKeyboardRoomHandler = new OLPlayKeyboardRoomHandler(this);
    public LinearLayout playerLayout;
    public LinearLayout keyboardLayout;
    public WaterfallView waterfallView;
    public KeyboardView keyboardView;
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
    private boolean recordStart;
    private String recordFilePath;
    private String recordFileName;
    private int tabTitleHeight;

    private void broadNote(int pitch, int volume) {
        if (GlobalSetting.INSTANCE.getKeyboardRealtime()) {
            // 协奏模式
            OnlineKeyboardNoteDTO.Builder builder = OnlineKeyboardNoteDTO.newBuilder();
            builder.addData((((MidiDeviceUtil.hasMidiDeviceConnected() ? 1 : 0) << 4) + roomPositionSub1));
            builder.addData(0);
            builder.addData(pitch);
            builder.addData(volume);
            sendMsg(OnlineProtocolType.KEYBOARD, builder.build());
        } else {
            notesQueue.offer(new OLNote(System.currentTimeMillis(), pitch, volume));
        }
    }

    public void mo2860a(int i, String str, int i2) {
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
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setCancelableFalse();
            jpDialogBuilder.setTitle("提示").setMessage(str).setFirstButton("确定", (dialog, which) -> dialog.dismiss())
                    .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        }
    }

    public void initPlayer(GridView gridView, Bundle bundle) {
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
                if (Objects.equals(name, jpapplication.getKitiName())) {
                    // 存储当前用户楼号，用于发弹奏音符
                    roomPositionSub1 = (byte) positionSub1;
                    int colorIndex = bundle1.getInt("IV");
                    keyboardNoteDownColor = colorIndex == 0 ? null : ColorUtil.getUserColorByUserColorIndex(this, colorIndex);
                    olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(MidiDeviceUtil.hasMidiDeviceConnected());
                }
                playerList.add(bundle1);
            }
            List<Bundle> list = playerList;
            if (!list.isEmpty()) {
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("PI"), o2.getByte("PI")));
            }
            gridView.setAdapter(new KeyboardPlayerImageAdapter(list, this));
            // 加载完成，确认用户已经进入房间内，再开始MIDI监听和记录弹奏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                MidiDeviceUtil.setMidiConnectionListener(this);
            }
            openNotesSchedule();
            waterfallView.startPlay(new WaterfallNote[0], GlobalSetting.INSTANCE.getWaterfallDownSpeed());
        }
        RelativeLayout.LayoutParams waterfallViewLayoutParams = (RelativeLayout.LayoutParams) waterfallView.getLayoutParams();
        waterfallViewLayoutParams.height = playerLayout.getHeight() - tabTitleHeight;
        waterfallView.setLayoutParams(waterfallViewLayoutParams);
    }

    @Override
    public boolean handleMessage(Message message) {
        super.handleMessage(message);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        switch (message.what) {
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
        onlineWaterfallViewNoteWidthUpdateHandle();
        return false;
    }

    public void onlineWaterfallViewNoteWidthUpdateHandle() {
        if (waterfallView.getVisibility() == View.VISIBLE) {
            waterfallView.setOctaveLineXList(keyboardView.getAllOctaveLineX());
            for (WaterfallNote waterfallNote : waterfallView.getFreeStyleNotes()) {
                Pair<Float, Float> result = WaterfallUtil.Companion.convertWidthToWaterfallWidth(
                        waterfallNote.getPitch(), keyboardView.convertPitchToReact(waterfallNote.getPitch()));
                waterfallNote.setLeft(result.getFirst());
                waterfallNote.setRight(result.getSecond());
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.keyboard_setting:
                Intent intent = new Intent();
                intent.setClass(this, SettingsMode.class);
                startActivityForResult(intent, SettingsMode.SETTING_MODE_CODE);
                return;
            case R.id.keyboard_record:
                try {
                    Button recordButton = (Button) view;
                    if (!recordStart) {
                        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                        jpDialogBuilder.setTitle("提示");
                        jpDialogBuilder.setMessage("点击确定按钮开始录音，录音将在点击停止按钮后保存至录音文件，请确保您授予了app的文件存储权限");
                        jpDialogBuilder.setFirstButton("确定", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            String date = DateUtil.format(DateUtil.now(), DateUtil.TEMPLATE_DEFAULT_CHINESE);
                            recordFilePath = getFilesDir().getAbsolutePath() + "/Records/" + date + ".raw";
                            recordFileName = date + "录音.wav";
                            SoundEngineUtil.setRecordFilePath(recordFilePath);
                            SoundEngineUtil.setRecord(true);
                            recordStart = true;
                            Toast.makeText(this, "开始录音...", Toast.LENGTH_SHORT).show();
                            recordButton.setText("■");
                            recordButton.setTextColor(ContextCompat.getColor(this, R.color.dark));
                            recordButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_ol_orange, getTheme()));
                        });
                        jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                        jpDialogBuilder.buildAndShowDialog();
                    } else {
                        recordButton.setText("●");
                        recordButton.setTextColor(ContextCompat.getColor(this, R.color.v3));
                        recordButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_ol_button, getTheme()));
                        SoundEngineUtil.setRecord(false);
                        recordStart = false;
                        File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
                        File desFile = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/" + recordFileName);
                        if (FileUtil.INSTANCE.moveFile(srcFile, desFile)) {
                            Toast.makeText(this, "录音完毕，文件已存储至SD卡\\JustPiano\\Records中", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "录音文件存储失败，请检查是否授予APP文件存储权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsMode.SETTING_MODE_CODE) {
            ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
            waterfallView.setViewAlpha(GlobalSetting.INSTANCE.getWaterfallOnlineAlpha());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.ol_keyboard_room);
        initRoomActivity(savedInstanceState);
        jpprogressBar = new JPProgressBar(this);
        TabSpec newTabSpec = roomTabs.newTabSpec("tab3");
        newTabSpec.setContent(R.id.msg_tab);
        newTabSpec.setIndicator("瀑布");
        roomTabs.addTab(newTabSpec);
        newTabSpec = roomTabs.newTabSpec("tab4");
        newTabSpec.setContent(R.id.players_tab);
        newTabSpec.setIndicator("邀请");
        roomTabs.addTab(newTabSpec);
        playerLayout = findViewById(R.id.player_layout);
        keyboardLayout = findViewById(R.id.keyboard_layout);
        findViewById(R.id.keyboard_count_down).setOnTouchListener(this);
        findViewById(R.id.keyboard_count_up).setOnTouchListener(this);
        findViewById(R.id.keyboard_move_left).setOnTouchListener(this);
        findViewById(R.id.keyboard_move_right).setOnTouchListener(this);
        findViewById(R.id.keyboard_resize).setOnTouchListener(this);
        keyboardSetting = findViewById(R.id.keyboard_setting);
        keyboardSetting.setOnClickListener(this);
        findViewById(R.id.keyboard_record).setOnClickListener(this);
        for (int i = 0; i < olKeyboardStates.length; i++) {
            olKeyboardStates[i] = new OLKeyboardState(false, false, false);
        }
        keyboardView = findViewById(R.id.keyboard_view);
        keyboardView.setOctaveTagType(KeyboardView.OctaveTagType.values()[GlobalSetting.INSTANCE.getKeyboardOctaveTagType()]);
        keyboardView.setKeyboardListener(new KeyboardView.KeyboardListener() {
            @Override
            public void onKeyDown(byte pitch, byte volume) {
                if (roomPositionSub1 >= 0) {
                    if (!olKeyboardStates[roomPositionSub1].getMuted()) {
                        SoundEngineUtil.playSound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()), volume);
                    }
                    if (GlobalSetting.INSTANCE.getSoundVibration()) {
                        VibrationUtil.vibrateOnce(OLPlayKeyboardRoom.this, GlobalSetting.INSTANCE.getSoundVibrationTime());
                    }
                    blinkView(roomPositionSub1);
                }
                if (hasAnotherUser()) {
                    broadNote(pitch, volume);
                }
                onlineWaterfallKeyDownHandle(pitch, volume, keyboardNoteDownColor == null ?
                        GlobalSetting.INSTANCE.getWaterfallFreeStyleColor() : keyboardNoteDownColor);
            }

            @Override
            public void onKeyUp(byte pitch) {
                SoundEngineUtil.stopPlaySound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()));
                if (roomPositionSub1 >= 0) {
                    blinkView(roomPositionSub1);
                }
                if (hasAnotherUser()) {
                    broadNote(pitch, 0);
                }
                onlineWaterfallKeyUpHandle(pitch);
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        tabTitleHeight = (int) ((displayMetrics.heightPixels * 45f) / 480);
        for (int i = 0; i < 4; i++) {
            roomTabs.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = tabTitleHeight;
            setTabTitleViewLayout(i);
        }
        waterfallView = findViewById(R.id.ol_waterfall_view);
        waterfallView.setTranslationY(tabTitleHeight);
        waterfallView.setViewAlpha(GlobalSetting.INSTANCE.getWaterfallOnlineAlpha());
        roomTabs.setCurrentTab(1);
        // 注意这里在向服务端发消息
        sendMsg(OnlineProtocolType.LOAD_ROOM_POSITION, OnlineLoadRoomPositionDTO.getDefaultInstance());
    }

    public void onlineWaterfallKeyDownHandle(byte pitch, byte volume, int color) {
        if (waterfallView != null) {
            Pair<Float, Float> result = WaterfallUtil.Companion.convertWidthToWaterfallWidth(
                    pitch, keyboardView.convertPitchToReact(pitch));
            waterfallView.addFreeStyleWaterfallNote(
                    new WaterfallNote(
                            result.getFirst(),
                            result.getSecond(),
                            waterfallView.getHeight() + waterfallView.getPlayProgress(),
                            Float.MAX_VALUE,
                            color,
                            pitch,
                            volume
                    )
            );
        }
    }

    public void onlineWaterfallKeyUpHandle(byte pitch) {
        if (waterfallView != null) {
            waterfallView.stopFreeStyleWaterfallNote(pitch);
        }
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            MidiDeviceUtil.removeMidiConnectionListener(this);
        }
        stopNotesSchedule();
        if (recordStart) {
            SoundEngineUtil.setRecord(false);
            recordStart = false;
            File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
            File desFile = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/" + recordFileName);
            FileUtil.INSTANCE.moveFile(srcFile, desFile);
            Toast.makeText(this, "录音完毕，文件已存储至SD卡\\JustPiano\\Records中", Toast.LENGTH_SHORT).show();
        }
        SoundEngineUtil.stopPlayAllSounds();
        waterfallView.stopPlay();
        waterfallView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        openNotesSchedule();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopNotesSchedule();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiConnect(String deviceName) {
        olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(true);
        if (playerGrid.getAdapter() != null) {
            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
        } else {
            playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiDisconnect(String deviceName) {
        olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(false);
        if (playerGrid.getAdapter() != null) {
            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
        } else {
            playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiMessageReceive(byte pitch, byte volume) {
        if (volume > 0) {
            if (roomPositionSub1 >= 0) {
                if (!olKeyboardStates[roomPositionSub1].getMuted()) {
                    SoundEngineUtil.playSound(pitch, volume);
                }
                blinkView(roomPositionSub1);
            }
            keyboardView.fireKeyDown(pitch, volume, keyboardNoteDownColor);
            if (hasAnotherUser()) {
                broadNote(pitch, volume);
            }
            onlineWaterfallKeyDownHandle(pitch, volume, keyboardNoteDownColor == null ?
                    GlobalSetting.INSTANCE.getWaterfallFreeStyleColor() : keyboardNoteDownColor);
        } else {
            if (roomPositionSub1 >= 0) {
                if (!olKeyboardStates[roomPositionSub1].getMuted()) {
                    SoundEngineUtil.stopPlaySound(pitch);
                }
                blinkView(roomPositionSub1);
            }
            keyboardView.fireKeyUp(pitch);
            if (hasAnotherUser()) {
                broadNote(pitch, 0);
            }
            onlineWaterfallKeyUpHandle(pitch);
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
                        RelativeLayout.LayoutParams waterfallViewLayoutParams = (RelativeLayout.LayoutParams) waterfallView.getLayoutParams();
                        waterfallViewLayoutParams.height = playerLayout.getHeight() - tabTitleHeight;
                        waterfallView.setLayoutParams(waterfallViewLayoutParams);
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
                    RelativeLayout.LayoutParams waterfallViewLayoutParams = (RelativeLayout.LayoutParams) waterfallView.getLayoutParams();
                    waterfallViewLayoutParams.height = playerLayout.getHeight() - tabTitleHeight;
                    waterfallView.setLayoutParams(waterfallViewLayoutParams);
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
                view.performClick();
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
        keyboardView.setNoteOnColor(keyboardNoteDownColor);
        if (noteScheduledExecutor == null) {
            noteScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            noteScheduledExecutor.scheduleWithFixedDelay(() -> {
                // 房间里没有其他人，停止发任何消息，清空弹奏队列（因为可能刚刚变为房间没人的状态，队列可能有遗留）
                if (!hasAnotherUser()) {
                    notesQueue.clear();
                    return;
                }
                // 未检测到这段间隔有弹奏音符，就不发消息
                if (notesQueue.isEmpty()) {
                    return;
                }
                try {
                    OnlineKeyboardNoteDTO.Builder builder = OnlineKeyboardNoteDTO.newBuilder();
                    // 字节数组开头，存入是否开启midi键盘和楼号
                    builder.addData(((MidiDeviceUtil.hasMidiDeviceConnected() ? 1 : 0) << 4) + roomPositionSub1);
                    // 存下size然后自减，确保并发环境下size还是根据上面时间戳而计算来的严格的size，否则此时队列中实际size可能增多了
                    while (!notesQueue.isEmpty()) {
                        OLNote olNote = notesQueue.poll();
                        if (olNote == null) {
                            continue;
                        }
                        builder.addData(olNote.getAbsoluteTime());
                        builder.addData(olNote.getPitch());
                        builder.addData(olNote.getVolume());
                    }
                    sendMsg(OnlineProtocolType.KEYBOARD, builder.build());
                    blinkView(roomPositionSub1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, NOTES_SEND_INTERVAL, NOTES_SEND_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private boolean hasAnotherUser() {
        for (int i = 0; i < olKeyboardStates.length; i++) {
            if (i != roomPositionSub1 && olKeyboardStates[i].getHasUser()) {
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

    /**
     * 指定楼的view的闪烁，用于键盘模式弹奏时
     *
     * @param index 索引，楼号 - 1
     */
    public void blinkView(int index) {
        View itemView = playerGrid.getChildAt(index);
        if (itemView == null) {
            return;
        }
        View playingView = itemView.findViewById(R.id.ol_player_playing);
        if (playingView.getVisibility() == View.VISIBLE) {
            return;
        }
        playingView.post(() -> playingView.setVisibility(View.VISIBLE));
        playingView.postDelayed(() -> playingView.setVisibility(View.INVISIBLE), 200);
    }
}
