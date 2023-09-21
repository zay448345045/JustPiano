package ly.pp.justpiano3.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import android.widget.TabHost.TabSpec;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.google.protobuf.ByteString;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.KeyboardPlayerImageAdapter;
import ly.pp.justpiano3.adapter.SimpleSkinListAdapter;
import ly.pp.justpiano3.adapter.SimpleSoundListAdapter;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.entity.OLKeyboardState;
import ly.pp.justpiano3.entity.OLNote;
import ly.pp.justpiano3.enums.KeyboardSyncModeEnum;
import ly.pp.justpiano3.handler.android.OLPlayKeyboardRoomHandler;
import ly.pp.justpiano3.midi.JPMidiReceiver;
import ly.pp.justpiano3.midi.MidiConnectionListener;
import ly.pp.justpiano3.utils.*;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import ly.pp.justpiano3.view.KeyboardModeView;
import protobuf.dto.OnlineKeyboardNoteDTO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public final class OLPlayKeyboardRoom extends OLPlayRoomActivity implements View.OnTouchListener, MidiConnectionListener {
    public static final int NOTES_SEND_INTERVAL = 120;
    // 当前用户楼号 - 1
    public byte roomPositionSub1 = -1;
    public ExecutorService receiveThreadPool = Executors.newSingleThreadExecutor();
    public Integer keyboardNoteDownColor;
    public OLKeyboardState[] olKeyboardStates = new OLKeyboardState[6];
    public MidiReceiver midiReceiver;
    public boolean midiKeyboardOn;
    private final Queue<OLNote> notesQueue = new ConcurrentLinkedQueue<>();
    private long lastNoteScheduleTime;
    /**
     * 键盘房间同步模式(默认编排模式)
     */
    private KeyboardSyncModeEnum keyboardSyncMode = KeyboardSyncModeEnum.CONCERTO;
    public OLPlayKeyboardRoomHandler olPlayKeyboardRoomHandler = new OLPlayKeyboardRoomHandler(this);
    public LinearLayout playerLayout;
    public LinearLayout keyboardLayout;
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
    public PopupWindow keyboardSettingPopup;
    private boolean recordStart;
    private String recordFilePath;
    private String recordFileName;

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
                notes[0] = (byte) (((midiKeyboardOn ? 1 : 0) << 4) + roomPositionSub1);
                notes[1] = (byte) 0;
                notes[2] = (byte) pitch;
                notes[3] = (byte) volume;
                OnlineKeyboardNoteDTO.Builder builder = OnlineKeyboardNoteDTO.newBuilder();
                builder.setData(ByteString.copyFrom(notes));
                sendMsg(OnlineProtocolType.KEYBOARD, builder.build());
                break;
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
            jpDialogBuilder.setTitle("提示").setMessage(str).setFirstButton("确定", ((dialog, which) -> dialog.dismiss()))
                    .setSecondButton("取消", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
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
                if (name.equals(jpapplication.getKitiName())) {
                    // 存储当前用户楼号，用于发弹奏音符
                    roomPositionSub1 = (byte) positionSub1;
                    int kuangIndex = bundle1.getInt("IV");
                    keyboardNoteDownColor = kuangIndex == 0 ? null : ColorUtil.getKuangColorByKuangIndex(this, kuangIndex);
                    olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(midiKeyboardOn);
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
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.keyboard_setting:
                PopupWindow popupWindow2 = new PopupWindow(this);
                View inflate2 = LayoutInflater.from(this).inflate(R.layout.ol_keyboard_setting_list, null);
                popupWindow2.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.filled_box, getTheme()));
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
                    JPDialogBuilder.JPDialog b = new JPDialogBuilder(this).setTitle("切换皮肤").loadInflate(inflate)
                            .setFirstButton("取消", ((dialog, which) -> dialog.dismiss())).createJPDialog();
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
                    JPDialogBuilder.JPDialog b = new JPDialogBuilder(this).setTitle("切换皮肤").loadInflate(inflate)
                            .setFirstButton("取消", ((dialog, which) -> dialog.dismiss())).createJPDialog();
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
                        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                        jpDialogBuilder.setTitle("提示");
                        jpDialogBuilder.setMessage("点击确定按钮开始录音，录音将在点击停止按钮后保存至录音文件");
                        jpDialogBuilder.setFirstButton("确定", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            String date = DateUtil.format(DateUtil.now());
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
                        jpDialogBuilder.setSecondButton("取消", ((dialog, which) -> dialog.dismiss()));
                        jpDialogBuilder.buildAndShowDialog();
                    } else {
                        recordButton.setText("●");
                        recordButton.setTextColor(ContextCompat.getColor(this, R.color.v3));
                        recordButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_ol_button, getTheme()));
                        SoundEngineUtil.setRecord(false);
                        recordStart = false;
                        File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
                        File desFile = new File(Environment.getExternalStorageDirectory() + "/JustPiano/Records/" + recordFileName);
                        FileUtil.INSTANCE.moveFile(srcFile, desFile);
                        Toast.makeText(this, "录音完毕，文件已存储至SD卡\\JustPiano\\Records中", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            default:
        }
        /* 使用gradle 8以上的推荐写法 有空把上面也优化了 TODO */
        int viewId = view.getId();
        if (viewId == R.id.keyboard_sync_mode_text) {
            try {
                new JPDialogBuilder(this).setTitle(getString(R.string.msg_this_is_what))
                        .setMessage(getString(R.string.ol_keyboard_sync_mode_help))
                        .setFirstButton("确定", ((dialog, which) -> dialog.dismiss())).buildAndShowDialog();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPStack.push(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.ol_keyboard_room);
        initRoomActivity(savedInstanceState);
        jpprogressBar = new JPProgressBar(this);
        TabSpec newTabSpec = roomTabs.newTabSpec("tab3");
        newTabSpec.setContent(R.id.players_tab);
        newTabSpec.setIndicator("邀请");
        roomTabs.addTab(newTabSpec);
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
            olKeyboardStates[i] = new OLKeyboardState(false, false, false);
        }
        keyboardView = findViewById(R.id.keyboard_view);
        keyboardView.setOctaveTagType(KeyboardModeView.OctaveTagType.values()[GlobalSetting.INSTANCE.getKeyboardOctaveTagType()]);
        keyboardView.setMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(byte pitch, byte volume) {
                if (roomPositionSub1 >= 0) {
                    if (!olKeyboardStates[roomPositionSub1].isMuted()) {
                        SoundEngineUtil.playSound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()), volume);
                    }
                    blinkView(roomPositionSub1);
                }
                if (hasAnotherUser()) {
                    broadNote(pitch, volume);
                }
            }

            @Override
            public void onKeyUp(byte pitch) {
                if (roomPositionSub1 >= 0) {
                    blinkView(roomPositionSub1);
                }
                if (hasAnotherUser()) {
                    broadNote(pitch, 0);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            if (MidiDeviceUtil.getMidiOutputPort() != null) {
                buildAndConnectMidiReceiver();
            }
            MidiDeviceUtil.addMidiConnectionListener(this);
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        for (int i = 0; i < 3; i++) {
            roomTabs.getTabWidget().getChildTabViewAt(i).getLayoutParams().height = (displayMetrics.heightPixels * 45) / 480;
            setTabTitleViewLayout(i);
        }
        roomTabs.setCurrentTab(1);
    }

    @Override
    protected void onDestroy() {
        JPStack.pop(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            if (MidiDeviceUtil.getMidiOutputPort() != null && midiReceiver != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                MidiDeviceUtil.getMidiOutputPort().disconnect(midiReceiver);
            }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void buildAndConnectMidiReceiver() {
        midiKeyboardOn = true;
        if (midiReceiver == null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            midiReceiver = new JPMidiReceiver(this);
            MidiDeviceUtil.getMidiOutputPort().connect(midiReceiver);
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiConnect() {
        if (MidiDeviceUtil.getMidiOutputPort() != null) {
            buildAndConnectMidiReceiver();
            olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(true);
            if (playerGrid.getAdapter() != null) {
                ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
            } else {
                playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
            }
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiDisconnect() {
        if (MidiDeviceUtil.getMidiOutputPort() != null) {
            midiKeyboardOn = false;
            if (midiReceiver != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                MidiDeviceUtil.getMidiOutputPort().disconnect(midiReceiver);
                midiReceiver = null;
            }
        }
        olKeyboardStates[roomPositionSub1].setMidiKeyboardOn(false);
        if (playerGrid.getAdapter() != null) {
            ((KeyboardPlayerImageAdapter) (playerGrid.getAdapter())).notifyDataSetChanged();
        } else {
            playerGrid.setAdapter(new KeyboardPlayerImageAdapter(playerList, this));
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiReceiveMessage(byte pitch, byte volume) {
        pitch += GlobalSetting.INSTANCE.getMidiKeyboardTune();
        if (volume > 0) {
            onMidiReceiveKeyDownHandle(pitch, volume);
        } else {
            onMidiReceiveKeyUpHandle(pitch);
        }
    }

    private void onMidiReceiveKeyDownHandle(byte pitch, byte volume) {
        if (roomPositionSub1 >= 0) {
            if (!olKeyboardStates[roomPositionSub1].isMuted()) {
                SoundEngineUtil.playSound(pitch, volume);
            }
            blinkView(roomPositionSub1);
        }
        keyboardView.fireKeyDown(pitch, volume, keyboardNoteDownColor);
        if (hasAnotherUser()) {
            broadNote(pitch, volume);
        }
    }

    private void onMidiReceiveKeyUpHandle(byte pitch) {
        if (roomPositionSub1 >= 0) {
            blinkView(roomPositionSub1);
        }
        keyboardView.fireKeyUp(pitch);
        if (hasAnotherUser()) {
            broadNote(pitch, 0);
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
        keyboardView.setNoteOnColor(keyboardNoteDownColor);
        if (noteScheduledExecutor == null) {
            lastNoteScheduleTime = System.currentTimeMillis();
            noteScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            noteScheduledExecutor.scheduleWithFixedDelay(() -> {
                // 时间戳和size尽量严格放在一起
                long scheduleTimeNow = System.currentTimeMillis();
                int size = notesQueue.size();
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
                    notes[0] = (byte) (((midiKeyboardOn ? 1 : 0) << 4) + roomPositionSub1);
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
                    blinkView(roomPositionSub1);
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
            if (i != roomPositionSub1 && olKeyboardStates[i].isHasUser()) {
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
        runOnUiThread(() -> {
            View itemView = playerGrid.getChildAt(index);
            if (itemView == null) {
                return;
            }
            View playingView = itemView.findViewById(R.id.ol_player_playing);
            if (playingView.getVisibility() == View.VISIBLE) {
                return;
            }
            playingView.setVisibility(View.VISIBLE);
            playingView.postDelayed(() -> playingView.setVisibility(View.INVISIBLE), 200);
        });
    }
}
