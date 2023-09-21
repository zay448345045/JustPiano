package ly.pp.justpiano3.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.midi.MidiReceiver;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.midi.JPMidiReceiver;
import ly.pp.justpiano3.midi.MidiConnectionListener;
import ly.pp.justpiano3.utils.*;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.KeyboardModeView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KeyBoard extends Activity implements View.OnTouchListener, MidiConnectionListener, View.OnClickListener {
    public KeyboardModeView keyboardMode1View;
    public KeyboardModeView keyboardMode2View;
    public LinearLayout keyboard1Layout;
    public LinearLayout keyboard2Layout;
    public JPApplication jpapplication;
    private MidiReceiver midiReceiver;
    public ScheduledExecutorService scheduledExecutor;
    public SharedPreferences sharedPreferences;
    // 用于记录上次的移动
    private boolean reSize;
    // 记录目前是否在走动画，不能重复走
    private boolean busyAnim;
    // 琴键动画间隔
    private int interval = 320;
    private boolean recordStart;
    private String recordFilePath;
    private String recordFileName;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, PlayModeSelect.class);
        startActivity(intent);
        finish();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_keyboard_mode);
        jpapplication = (JPApplication) getApplication();
        keyboardMode1View = findViewById(R.id.keyboard1_view);
        initKeyboardModeView(keyboardMode1View);
        keyboardMode2View = findViewById(R.id.keyboard2_view);
        initKeyboardModeView(keyboardMode2View);
        keyboard1Layout = findViewById(R.id.keyboard1_layout);
        keyboard1Layout.setOnTouchListener(this);
        keyboard2Layout = findViewById(R.id.keyboard2_layout);
        keyboard2Layout.setOnTouchListener(this);
        findViewById(R.id.keyboard1_count_down).setOnTouchListener(this);
        findViewById(R.id.keyboard2_count_down).setOnTouchListener(this);
        findViewById(R.id.keyboard1_count_up).setOnTouchListener(this);
        findViewById(R.id.keyboard2_count_up).setOnTouchListener(this);
        findViewById(R.id.keyboard1_move_left).setOnTouchListener(this);
        findViewById(R.id.keyboard2_move_left).setOnTouchListener(this);
        findViewById(R.id.keyboard1_move_right).setOnTouchListener(this);
        findViewById(R.id.keyboard2_move_right).setOnTouchListener(this);
        findViewById(R.id.keyboard_setting).setOnClickListener(this);
        findViewById(R.id.keyboard_record).setOnClickListener(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int keyboard1WhiteKeyNum = sharedPreferences.getInt("keyboard1_white_key_num", 8);
        int keyboard2WhiteKeyNum = sharedPreferences.getInt("keyboard2_white_key_num", 8);
        int keyboard1WhiteKeyOffset = sharedPreferences.getInt("keyboard1_white_key_offset", 21);
        int keyboard2WhiteKeyOffset = sharedPreferences.getInt("keyboard2_white_key_offset", 14);
        float keyboardWeight = sharedPreferences.getFloat("keyboard_weight", 0.5f);
        keyboardMode1View.setWhiteKeyNum(keyboard1WhiteKeyNum, 0);
        keyboardMode2View.setWhiteKeyNum(keyboard2WhiteKeyNum, 0);
        keyboardMode1View.setWhiteKeyOffset(keyboard1WhiteKeyOffset, 0);
        keyboardMode2View.setWhiteKeyOffset(keyboard2WhiteKeyOffset, 0);
        keyboard1Layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, keyboardWeight));
        keyboard2Layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - keyboardWeight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            buildAndConnectMidiReceiver();
            MidiDeviceUtil.addMidiConnectionListener(this);
        }
    }

    private void initKeyboardModeView(KeyboardModeView keyboardModeView) {
        keyboardModeView.setOctaveTagType(KeyboardModeView.OctaveTagType.values()[GlobalSetting.INSTANCE.getKeyboardOctaveTagType()]);
        keyboardModeView.setMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(byte pitch, byte volume) {
                SoundEngineUtil.playSound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()), volume);
            }

            @Override
            public void onKeyUp(byte pitch) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            if (MidiDeviceUtil.getMidiOutputPort() != null && midiReceiver != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                MidiDeviceUtil.getMidiOutputPort().disconnect(midiReceiver);
            }
            MidiDeviceUtil.removeMidiConnectionListener(this);
        }
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
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        int id = view.getId();
        if (id == R.id.keyboard1_layout || id == R.id.keyboard2_layout) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (id == R.id.keyboard1_layout) {
                        if (view.getHeight() - event.getY() < 30) {
                            reSize = true;
                        }
                    } else {
                        if (event.getY() < 30) {
                            reSize = true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float weight = event.getRawY() / (keyboard1Layout.getHeight() + keyboard2Layout.getHeight());
                    if (reSize && weight > 0.15f && weight < 0.85f) {
                        keyboard1Layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
                        keyboard2Layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - weight));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) keyboard1Layout.getLayoutParams();
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putFloat("keyboard_weight", layoutParams.weight);
                    edit.apply();
                    reSize = false;
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
            Message msg = Message.obtain(handler);
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

    private final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            switch (msg.what) {
                case R.id.keyboard1_count_down:
                    int keyboard1WhiteKeyNum = keyboardMode1View.getWhiteKeyNum() - 1;
                    keyboardMode1View.setWhiteKeyNum(keyboard1WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_num", keyboardMode1View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard2_count_down:
                    int keyboard2WhiteKeyNum = keyboardMode2View.getWhiteKeyNum() - 1;
                    keyboardMode2View.setWhiteKeyNum(keyboard2WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_num", keyboardMode2View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard1_count_up:
                    keyboard1WhiteKeyNum = keyboardMode1View.getWhiteKeyNum() + 1;
                    keyboardMode1View.setWhiteKeyNum(keyboard1WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_num", keyboardMode1View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard2_count_up:
                    keyboard2WhiteKeyNum = keyboardMode2View.getWhiteKeyNum() + 1;
                    keyboardMode2View.setWhiteKeyNum(keyboard2WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_num", keyboardMode2View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard1_move_left:
                    int keyboard1WhiteKeyOffset = keyboardMode1View.getWhiteKeyOffset() - 1;
                    keyboardMode1View.setWhiteKeyOffset(keyboard1WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_offset", keyboardMode1View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard2_move_left:
                    int keyboard2WhiteKeyOffset = keyboardMode2View.getWhiteKeyOffset() - 1;
                    keyboardMode2View.setWhiteKeyOffset(keyboard2WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_offset", keyboardMode2View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard1_move_right:
                    keyboard1WhiteKeyOffset = keyboardMode1View.getWhiteKeyOffset() + 1;
                    keyboardMode1View.setWhiteKeyOffset(keyboard1WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_offset", keyboardMode1View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard2_move_right:
                    keyboard2WhiteKeyOffset = keyboardMode2View.getWhiteKeyOffset() + 1;
                    keyboardMode2View.setWhiteKeyOffset(keyboard2WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_offset", keyboardMode2View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void buildAndConnectMidiReceiver() {
        if (MidiDeviceUtil.getMidiOutputPort() != null && midiReceiver == null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            midiReceiver = new JPMidiReceiver(this);
            MidiDeviceUtil.getMidiOutputPort().connect(midiReceiver);
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiConnect() {
        buildAndConnectMidiReceiver();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiDisconnect() {
        if (MidiDeviceUtil.getMidiOutputPort() != null && midiReceiver != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            MidiDeviceUtil.getMidiOutputPort().disconnect(midiReceiver);
            midiReceiver = null;
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiReceiveMessage(byte pitch, byte volume) {
        pitch += GlobalSetting.INSTANCE.getMidiKeyboardTune();
        if (volume > 0) {
            keyboardMode2View.fireKeyDown(pitch, volume, null);
            SoundEngineUtil.playSound(pitch, volume);
        } else {
            keyboardMode2View.fireKeyUp(pitch);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsMode.SETTING_MODE_CODE) {
            ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
            keyboardMode1View.changeSkinKeyboardImage(this);
            keyboardMode2View.changeSkinKeyboardImage(this);
            KeyboardModeView.OctaveTagType octaveTagType = KeyboardModeView.OctaveTagType.values()[GlobalSetting.INSTANCE.getKeyboardOctaveTagType()];
            keyboardMode1View.setOctaveTagType(octaveTagType);
            keyboardMode2View.setOctaveTagType(octaveTagType);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyboard_setting:
                Intent intent = new Intent();
                intent.setClass(this, SettingsMode.class);
                startActivityForResult(intent, SettingsMode.SETTING_MODE_CODE);
                break;
            case R.id.keyboard_record:
                try {
                    Button recordButton = (Button) view;
                    if (!recordStart) {
                        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
                        jpDialogBuilder.setTitle("提示");
                        jpDialogBuilder.setMessage("点击确定按钮开始录音，录音将在点击停止按钮后保存至录音文件");
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
                break;
            default:
                break;
        }
    }
}
