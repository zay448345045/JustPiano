package ly.pp.justpiano3.activity.online;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.midi.MidiDeviceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kotlin.Pair;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.settings.SettingsActivity;
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
import ly.pp.justpiano3.utils.PmSongUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.VibrationUtil;
import ly.pp.justpiano3.utils.ViewUtil;
import ly.pp.justpiano3.utils.WaterfallUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.KeyboardView;
import ly.pp.justpiano3.view.WaterfallView;
import protobuf.dto.OnlineKeyboardNoteDTO;
import protobuf.dto.OnlineLoadRoomPositionDTO;

public final class OLPlayKeyboardRoom extends OLRoomActivity implements View.OnTouchListener, MidiDeviceUtil.MidiDeviceListener {
    private static final int NOTES_SEND_INTERVAL = 120;
    // 当前用户楼号 - 1
    private int roomPositionSub1 = -1;
    public ExecutorService receiveThreadPool = Executors.newSingleThreadExecutor();
    public Map<Integer, OLKeyboardState> olKeyboardStates = new ConcurrentHashMap<>(Room.CAPACITY);
    private final Queue<OLNote> notesQueue = new ConcurrentLinkedQueue<>();
    private long realTimeLastMessageSendTime;
    private final OnlineKeyboardNoteDTO.Builder onlineKeyboardNoteDtoBuilder = OnlineKeyboardNoteDTO.newBuilder();
    private final Map<Long, Long> olNoteCacheDataMap = new ConcurrentHashMap<>();
    public OLPlayKeyboardRoomHandler olPlayKeyboardRoomHandler = new OLPlayKeyboardRoomHandler(this);
    private LinearLayout playerLayout;
    private LinearLayout keyboardLayout;
    public WaterfallView waterfallView;
    public KeyboardView keyboardView;
    private ScheduledExecutorService keyboardScheduledExecutor;
    private ScheduledExecutorService noteScheduledExecutor;
    // 用于记录上次的移动
    private boolean reSize;
    // 记录目前是否在走动画，不能重复走
    private boolean busyAnim;
    private boolean recordStart;
    private String recordFilePath;
    private String recordFileName;
    private int tabTitleHeight;

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                ImageLoadUtil.setBackground(this, GlobalSetting.getBackgroundPic());
                keyboardView.changeSkinKeyboardImage(this);
                keyboardView.setOctaveTagType(KeyboardView.OctaveTagType.getEntries()
                        .get(GlobalSetting.getKeyboardOctaveTagType()));
                waterfallView.setViewAlpha(GlobalSetting.getWaterfallOnlineAlpha());
                waterfallView.setShowOctaveLine(GlobalSetting.getWaterfallOctaveLine());
                waterfallView.setNoteFallDownSpeed(GlobalSetting.getWaterfallDownSpeed());
                if (GlobalSetting.getKeyboardRealtime()) {
                    stopNotesSchedule();
                } else {
                    openNotesSchedule();
                }
            });

    private void broadNote(byte pitch, byte volume) {
        if (GlobalSetting.getKeyboardRealtime()) {
            olNoteCacheDataMap.put((long) pitch, (long) volume);
            if (System.currentTimeMillis() - realTimeLastMessageSendTime > PmSongUtil.PM_GLOBAL_SPEED) {
                realTimeLastMessageSendTime = System.currentTimeMillis();
                handler.postDelayed(() -> {
                    if (!olNoteCacheDataMap.isEmpty()) {
                        Map<Long, Long> finalMap = new HashMap<>(olNoteCacheDataMap);
                        olNoteCacheDataMap.clear();
                        onlineKeyboardNoteDtoBuilder.clear();
                        onlineKeyboardNoteDtoBuilder.addData(buildNoteHeadData());
                        for (Map.Entry<Long, Long> entry : finalMap.entrySet()) {
                            onlineKeyboardNoteDtoBuilder.addData(0);
                            onlineKeyboardNoteDtoBuilder.addData(entry.getKey());
                            onlineKeyboardNoteDtoBuilder.addData(entry.getValue());
                        }
                        sendMsg(OnlineProtocolType.KEYBOARD, onlineKeyboardNoteDtoBuilder.build());
                    }
                }, PmSongUtil.PM_GLOBAL_SPEED);
            }
        } else if (noteScheduledExecutor != null) {
            notesQueue.offer(new OLNote(System.currentTimeMillis(), pitch, volume));
        }
    }

    private byte buildNoteHeadData() {
        return (byte) (((MidiDeviceUtil.getSustainPedalStatus() ? 1 : 0) << 5)
                + ((MidiDeviceUtil.hasMidiDeviceConnected() ? 1 : 0) << 4)
                + roomPositionSub1);
    }

    public void buildAndShowCpDialog(int dialogType, String message, int coupleType) {
        String coupleTypeText = "情意绵绵的情侣";
        switch (coupleType) {
            case 0 -> {
                return;
            }
            case 1 -> coupleTypeText = "情意绵绵的情侣";
            case 2 -> coupleTypeText = "基情四射的基友";
            case 3 -> coupleTypeText = "百年好合的百合";
        }
        if (dialogType == 4) {
            showCpDialog(coupleTypeText.substring(coupleTypeText.length() - 2) + "证书", message);
        } else if (dialogType == 5) {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示").setMessage(message).setFirstButton("确定", (dialog, which) -> dialog.dismiss())
                    .setCancelableFalse().setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
        }
    }

    public void initPlayer(GridView gridView, Bundle bundle) {
        if (roomPositionSub1 < 0) {
            // 初次加载完成，确认用户已经进入房间内，再开始MIDI监听和记录弹奏，开启瀑布流等
            if (MidiDeviceUtil.isSupportMidiDevice(this)) {
                MidiDeviceUtil.setMidiConnectionListener(this);
            }
            if (!GlobalSetting.getKeyboardRealtime()) {
                openNotesSchedule();
            }
            waterfallView.startPlay(new WaterfallNote[0], GlobalSetting.getWaterfallDownSpeed());
            RelativeLayout.LayoutParams waterfallViewLayoutParams = (RelativeLayout.LayoutParams) waterfallView.getLayoutParams();
            waterfallViewLayoutParams.height = playerLayout.getHeight() - tabTitleHeight;
            waterfallView.setLayoutParams(waterfallViewLayoutParams);
        }
        playerList.clear();
        if (bundle != null) {
            int size = bundle.size() - 2;
            for (int i = 0; i < size; i++) {
                Bundle bundle1 = bundle.getBundle(String.valueOf(i));
                String name = bundle1.getString("N");
                int positionSub1 = bundle1.getByte("PI") - 1;
                OLKeyboardState olKeyboardState = olKeyboardStates.get(positionSub1);
                if (positionSub1 >= 0 && positionSub1 < Room.CAPACITY && olKeyboardState != null) {
                    // 判定位置是否有人，忽略琴娘
                    boolean hasUser = !name.isEmpty() && !name.equals("琴娘");
                    olKeyboardState.setHasUser(hasUser);
                    if (!hasUser) {
                        olKeyboardState.setMidiKeyboardOn(false);
                    }
                }
                if (Objects.equals(name, OLBaseActivity.getKitiName()) && olKeyboardState != null) {
                    // 存储当前用户楼号，用于发弹奏音符
                    roomPositionSub1 = (byte) positionSub1;
                    int colorIndex = bundle1.getInt("IV");
                    keyboardView.setNoteOnColor(colorIndex == 0 ? null : ColorUtil.getUserColorByUserColorIndex(this, colorIndex));
                    olKeyboardState.setMidiKeyboardOn(MidiDeviceUtil.hasMidiDeviceConnected());
                }
                playerList.add(bundle1);
            }
            List<Bundle> list = playerList;
            if (!list.isEmpty()) {
                Collections.sort(list, (o1, o2) -> Integer.compare(o1.getByte("PI"), o2.getByte("PI")));
            }
            gridView.setAdapter(new KeyboardPlayerImageAdapter(list, this));
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        super.handleMessage(message);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (message.what == R.id.keyboard_count_down) {
            keyboardView.setWhiteKeyNum(keyboardView.getWhiteKeyNum() - 1);
            edit.putInt("ol_keyboard_white_key_num", keyboardView.getWhiteKeyNum());
        } else if (message.what == R.id.keyboard_count_up) {
            keyboardView.setWhiteKeyNum(keyboardView.getWhiteKeyNum() + 1);
            edit.putInt("ol_keyboard_white_key_num", keyboardView.getWhiteKeyNum());
        } else if (message.what == R.id.keyboard_move_left) {
            keyboardView.setWhiteKeyOffset(keyboardView.getWhiteKeyOffset() - 1);
            edit.putInt("ol_keyboard_white_key_offset", keyboardView.getWhiteKeyOffset());
        } else if (message.what == R.id.keyboard_move_right) {
            keyboardView.setWhiteKeyOffset(keyboardView.getWhiteKeyOffset() + 1);
            edit.putInt("ol_keyboard_white_key_offset", keyboardView.getWhiteKeyOffset());
        }
        edit.apply();
        onlineWaterfallViewNoteWidthUpdateHandle();
        return false;
    }

    public void onlineWaterfallViewNoteWidthUpdateHandle() {
        if (waterfallView.getVisibility() == View.VISIBLE) {
            waterfallView.setOctaveLineXList(keyboardView.getAllOctaveLineX());
            for (List<WaterfallNote> freeStyleNoteList : waterfallView.getFreeStyleNotes().values()) {
                for (int i = freeStyleNoteList.size() - 1; i >= 0; i--) {
                    WaterfallNote waterfallNote = freeStyleNoteList.get(i);
                    if (waterfallNote != null) {
                        Pair<Float, Float> result = WaterfallUtil.convertToWaterfallWidth(
                                keyboardView, waterfallNote.getPitch());
                        waterfallNote.setLeft(result.getFirst());
                        waterfallNote.setRight(result.getSecond());
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.keyboard_setting) {
            settingsLauncher.launch(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.keyboard_record) {
            try {
                Button recordButton = (Button) view;
                if (!recordStart) {
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                    jpDialogBuilder.setTitle("提示");
                    jpDialogBuilder.setMessage("点击确定按钮开始录音，录音将在点击停止按钮后保存至录音文件，存储位置可在设置中指定");
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
                    recordStart = false;
                    SoundEngineUtil.setRecord(false);
                    File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
                    Uri desUri = FileUtil.getOrCreateFileByUriFolder(this,
                            GlobalSetting.getRecordsSavePath(), "Records", recordFileName);
                    if (FileUtil.moveFileToUri(this, srcFile, desUri)) {
                        Toast.makeText(this, "录音完毕，文件已存储", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "录音文件存储失败", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.ol_keyboard_room);
        initRoomActivity(savedInstanceState);
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
        findViewById(R.id.keyboard_setting).setOnClickListener(this);
        findViewById(R.id.keyboard_record).setOnClickListener(this);
        for (int i = 0; i < Room.CAPACITY; i++) {
            olKeyboardStates.put(i, new OLKeyboardState(false, false, false));
        }
        keyboardView = findViewById(R.id.keyboard_view);
        keyboardView.setOctaveTagType(KeyboardView.OctaveTagType.getEntries().get(GlobalSetting.getKeyboardOctaveTagType()));
        keyboardView.setKeyboardListener(new KeyboardView.KeyboardListener() {
            @Override
            public void onKeyDown(byte pitch, byte volume) {
                OLKeyboardState olKeyboardState = olKeyboardStates.get(roomPositionSub1);
                if (roomPositionSub1 >= 0 && olKeyboardState != null) {
                    if (!olKeyboardState.getMuted()) {
                        SoundEngineUtil.playSound((byte) (pitch + GlobalSetting.getKeyboardSoundTune()), volume);
                    }
                    if (GlobalSetting.getSoundVibration()) {
                        VibrationUtil.vibrateOnce(OLPlayKeyboardRoom.this, GlobalSetting.getSoundVibrationTime());
                    }
                    blinkView(roomPositionSub1);
                }
                if (hasAnotherUser()) {
                    broadNote(pitch, volume);
                }
                onlineWaterfallKeyDownHandle(pitch, volume, keyboardView.getNoteOnColor() == null ?
                        GlobalSetting.getWaterfallFreeStyleColor() : keyboardView.getNoteOnColor());
            }

            @Override
            public void onKeyUp(byte pitch) {
                SoundEngineUtil.stopPlaySound((byte) (pitch + GlobalSetting.getKeyboardSoundTune()));
                if (roomPositionSub1 >= 0) {
                    blinkView(roomPositionSub1);
                }
                if (hasAnotherUser()) {
                    broadNote(pitch, (byte) 0);
                }
                onlineWaterfallKeyUpHandle(pitch);
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int keyboardWhiteKeyNum = sharedPreferences.getInt("ol_keyboard_white_key_num", 15);
        int keyboardWhiteKeyOffset = sharedPreferences.getInt("ol_keyboard_white_key_offset", 21);
        float keyboardWeight = sharedPreferences.getFloat("ol_keyboard_weight", 0.75f);
        keyboardView.setWhiteKeyOffset(keyboardWhiteKeyOffset);
        keyboardView.setWhiteKeyNum(keyboardWhiteKeyNum);
        playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, keyboardWeight));
        keyboardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - keyboardWeight));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        tabTitleHeight = (int) ((displayMetrics.heightPixels * 45f) / 480);
        roomNameView.getLayoutParams().height = tabTitleHeight;
        timeTextView.getLayoutParams().height = tabTitleHeight;
        for (int i = 0; i < 4; i++) {
            roomTabs.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = tabTitleHeight;
            setTabTitleViewLayout(i);
        }
        waterfallView = findViewById(R.id.ol_waterfall_view);
        waterfallView.setTranslationY(tabTitleHeight);
        waterfallView.setViewAlpha(GlobalSetting.getWaterfallOnlineAlpha());
        waterfallView.setShowOctaveLine(GlobalSetting.getWaterfallOctaveLine());
        roomTabs.setCurrentTab(1);
        // 注意这里在向服务端发消息
        sendMsg(OnlineProtocolType.LOAD_ROOM_POSITION, OnlineLoadRoomPositionDTO.getDefaultInstance());
    }

    public void onlineWaterfallKeyDownHandle(byte pitch, byte volume, int color) {
        if (waterfallView != null) {
            Pair<Float, Float> result = WaterfallUtil.convertToWaterfallWidth(
                    keyboardView, pitch);
            waterfallView.addFreeStyleWaterfallNote(
                    result.getFirst(),
                    result.getSecond(),
                    pitch,
                    volume,
                    color);
        }
    }

    public void onlineWaterfallKeyUpHandle(byte pitch) {
        if (waterfallView != null) {
            waterfallView.stopFreeStyleWaterfallNote(pitch);
        }
    }

    @Override
    protected void onDestroy() {
        if (MidiDeviceUtil.isSupportMidiDevice(this)) {
            MidiDeviceUtil.removeMidiConnectionListener();
        }
        stopNotesSchedule();
        if (recordStart) {
            recordStart = false;
            SoundEngineUtil.setRecord(false);
            File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
            Uri desUri = FileUtil.getOrCreateFileByUriFolder(this,
                    GlobalSetting.getRecordsSavePath(), "Records", recordFileName);
            if (FileUtil.moveFileToUri(this, srcFile, desUri)) {
                Toast.makeText(this, "录音完毕，文件已存储", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "录音文件存储失败", Toast.LENGTH_SHORT).show();
            }
        }
        SoundEngineUtil.stopPlayAllSounds();
        waterfallView.stopPlay();
        waterfallView.destroy();
        super.onDestroy();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiConnect(MidiDeviceInfo midiDeviceInfo) {
        OLKeyboardState olKeyboardState = olKeyboardStates.get(roomPositionSub1);
        if (roomPositionSub1 >= 0 && roomPositionSub1 < Room.CAPACITY && olKeyboardState != null) {
            olKeyboardState.setMidiKeyboardOn(true);
        }
        if (playerGrid.getAdapter() != null) {
            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
        } else {
            playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiDisconnect(MidiDeviceInfo midiDeviceInfo) {
        OLKeyboardState olKeyboardState = olKeyboardStates.get(roomPositionSub1);
        if (roomPositionSub1 >= 0 && roomPositionSub1 < Room.CAPACITY && olKeyboardState != null) {
            olKeyboardState.setMidiKeyboardOn(false);
        }
        if (playerGrid.getAdapter() != null) {
            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
        } else {
            playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiMessageReceive(byte pitch, byte volume) {
        OLKeyboardState olKeyboardState = olKeyboardStates.get(roomPositionSub1);
        if (volume > 0) {
            if (roomPositionSub1 >= 0 && roomPositionSub1 < Room.CAPACITY && olKeyboardState != null) {
                if (!olKeyboardState.getMuted()) {
                    SoundEngineUtil.playSound(pitch, volume);
                }
                blinkView(roomPositionSub1);
            }
            keyboardView.fireKeyDown(pitch, volume, keyboardView.getNoteOnColor());
            if (hasAnotherUser()) {
                broadNote(pitch, volume);
            }
            onlineWaterfallKeyDownHandle(pitch, volume, keyboardView.getNoteOnColor() == null ?
                    GlobalSetting.getWaterfallFreeStyleColor() : keyboardView.getNoteOnColor());
        } else {
            if (roomPositionSub1 >= 0 && roomPositionSub1 < Room.CAPACITY && olKeyboardState != null) {
                if (!olKeyboardState.getMuted()) {
                    SoundEngineUtil.stopPlaySound(pitch);
                }
                blinkView(roomPositionSub1);
            }
            keyboardView.fireKeyUp(pitch);
            if (hasAnotherUser()) {
                broadNote(pitch, (byte) 0);
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
                case MotionEvent.ACTION_DOWN -> {
                    reSize = true;
                    view.setPressed(true);
                    waterfallView.setPressed(true);
                }
                case MotionEvent.ACTION_MOVE -> {
                    float weight = event.getRawY() / (playerLayout.getHeight() + keyboardLayout.getHeight());
                    if (reSize && weight > 0.65f && weight < 0.92f) {
                        playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
                        keyboardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - weight));
                        ViewUtil.registerViewLayoutObserver(playerLayout, () -> {
                            RelativeLayout.LayoutParams waterfallViewLayoutParams = (RelativeLayout.LayoutParams) waterfallView.getLayoutParams();
                            waterfallViewLayoutParams.height = playerLayout.getHeight() - tabTitleHeight;
                            waterfallView.setLayoutParams(waterfallViewLayoutParams);
                        });
                    }
                }
                case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    reSize = false;
                    view.setPressed(false);
                    waterfallView.setPressed(false);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerLayout.getLayoutParams();
                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    edit.putFloat("ol_keyboard_weight", layoutParams.weight);
                    edit.apply();
                }
                default -> {
                }
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
            handler.sendMessage(msg);
        }, 0, 80, TimeUnit.MILLISECONDS);
    }

    private void stopAddOrSubtract() {
        if (keyboardScheduledExecutor != null) {
            keyboardScheduledExecutor.shutdownNow();
            keyboardScheduledExecutor = null;
        }
    }

    private void openNotesSchedule() {
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
                    onlineKeyboardNoteDtoBuilder.clear();
                    // 字节数组开头，存入是否开启midi键盘和楼号
                    onlineKeyboardNoteDtoBuilder.addData(buildNoteHeadData());
                    // 存下size然后自减，确保并发环境下size还是根据上面时间戳而计算来的严格的size，否则此时队列中实际size可能增多了
                    while (!notesQueue.isEmpty()) {
                        OLNote olNote = notesQueue.poll();
                        if (olNote == null) {
                            continue;
                        }
                        onlineKeyboardNoteDtoBuilder.addData(olNote.getAbsoluteTime());
                        onlineKeyboardNoteDtoBuilder.addData(olNote.getPitch());
                        onlineKeyboardNoteDtoBuilder.addData(olNote.getVolume());
                    }
                    sendMsg(OnlineProtocolType.KEYBOARD, onlineKeyboardNoteDtoBuilder.build());
                    blinkView(roomPositionSub1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, NOTES_SEND_INTERVAL, NOTES_SEND_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private boolean hasAnotherUser() {
        for (Map.Entry<Integer, OLKeyboardState> entry : olKeyboardStates.entrySet()) {
            if (entry.getKey() != roomPositionSub1 && entry.getValue().getHasUser()) {
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
