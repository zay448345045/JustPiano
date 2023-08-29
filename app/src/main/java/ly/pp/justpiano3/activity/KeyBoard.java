package ly.pp.justpiano3.activity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import ly.pp.justpiano3.*;
import ly.pp.justpiano3.constant.MidiConstants;
import ly.pp.justpiano3.entity.Setting;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.midi.MidiConnectionListener;
import ly.pp.justpiano3.midi.MidiFramer;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.MidiUtil;
import ly.pp.justpiano3.utils.SkinImageLoadUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.KeyboardModeView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.M)
public class KeyBoard extends Activity implements View.OnTouchListener, MidiConnectionListener, View.OnClickListener {

    public KeyboardModeView keyboardMode1View;
    public KeyboardModeView keyboardMode2View;
    public LinearLayout keyboard1Layout;
    public LinearLayout keyboard2Layout;
    public JPApplication jpapplication;
    public MidiReceiver midiFramer;
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.keyboard_mode);
        jpapplication = (JPApplication) getApplication();
        keyboardMode1View = findViewById(R.id.keyboard1_view);
        keyboardMode1View.setMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int pitch, int volume) {
                SoundEngineUtil.playSound(pitch + jpapplication.getSetting().getKeyboardSoundTune(), volume);
            }

            @Override
            public void onKeyUp(int pitch) {

            }
        });
        keyboardMode2View = findViewById(R.id.keyboard2_view);
        keyboardMode2View.setMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int pitch, int volume) {
                SoundEngineUtil.playSound(pitch + jpapplication.getSetting().getKeyboardSoundTune(), volume);
            }

            @Override
            public void onKeyUp(int pitch) {

            }
        });
        keyboard1Layout = findViewById(R.id.keyboard1_layout);
        keyboard1Layout.setOnTouchListener(this);
        keyboard2Layout = findViewById(R.id.keyboard2_layout);
        keyboard2Layout.setOnTouchListener(this);
        Button keyboard1CountDown = findViewById(R.id.keyboard1_count_down);
        keyboard1CountDown.setOnTouchListener(this);
        Button keyboard2CountDown = findViewById(R.id.keyboard2_count_down);
        keyboard2CountDown.setOnTouchListener(this);
        Button keyboard1Countup = findViewById(R.id.keyboard1_count_up);
        keyboard1Countup.setOnTouchListener(this);
        Button keyboard2Countup = findViewById(R.id.keyboard2_count_up);
        keyboard2Countup.setOnTouchListener(this);
        Button keyboard1MoveLeft = findViewById(R.id.keyboard1_move_left);
        keyboard1MoveLeft.setOnTouchListener(this);
        Button keyboard2MoveLeft = findViewById(R.id.keyboard2_move_left);
        keyboard2MoveLeft.setOnTouchListener(this);
        Button keyboard1MoveRight = findViewById(R.id.keyboard1_move_right);
        keyboard1MoveRight.setOnTouchListener(this);
        Button keyboard2MoveRight = findViewById(R.id.keyboard2_move_right);
        keyboard2MoveRight.setOnTouchListener(this);
        ImageView keyboardSetting = findViewById(R.id.keyboard_setting);
        keyboardSetting.setOnClickListener(this);
        Button keyboardRecord = findViewById(R.id.keyboard_record);
        keyboardRecord.setOnClickListener(this);
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
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (MidiUtil.getMidiOutputPort() != null) {
                    if (midiFramer != null) {
                        MidiUtil.getMidiOutputPort().disconnect(midiFramer);
                        midiFramer = null;
                    }
                    MidiUtil.removeMidiConnectionStart(this);
                }
            }
        }
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
                    keyboardMode1View.setWhiteKeyNum(keyboard1WhiteKeyNum, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_num", keyboardMode1View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard2_count_down:
                    int keyboard2WhiteKeyNum = keyboardMode2View.getWhiteKeyNum() - 1;
                    keyboardMode2View.setWhiteKeyNum(keyboard2WhiteKeyNum, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_num", keyboardMode2View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard1_count_up:
                    keyboard1WhiteKeyNum = keyboardMode1View.getWhiteKeyNum() + 1;
                    keyboardMode1View.setWhiteKeyNum(keyboard1WhiteKeyNum, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_num", keyboardMode1View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard2_count_up:
                    keyboard2WhiteKeyNum = keyboardMode2View.getWhiteKeyNum() + 1;
                    keyboardMode2View.setWhiteKeyNum(keyboard2WhiteKeyNum, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_num", keyboardMode2View.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard1_move_left:
                    int keyboard1WhiteKeyOffset = keyboardMode1View.getWhiteKeyOffset() - 1;
                    keyboardMode1View.setWhiteKeyOffset(keyboard1WhiteKeyOffset, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_offset", keyboardMode1View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard2_move_left:
                    int keyboard2WhiteKeyOffset = keyboardMode2View.getWhiteKeyOffset() - 1;
                    keyboardMode2View.setWhiteKeyOffset(keyboard2WhiteKeyOffset, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_offset", keyboardMode2View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard1_move_right:
                    keyboard1WhiteKeyOffset = keyboardMode1View.getWhiteKeyOffset() + 1;
                    keyboardMode1View.setWhiteKeyOffset(keyboard1WhiteKeyOffset, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_offset", keyboardMode1View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard2_move_right:
                    keyboard2WhiteKeyOffset = keyboardMode2View.getWhiteKeyOffset() + 1;
                    keyboardMode2View.setWhiteKeyOffset(keyboard2WhiteKeyOffset, jpapplication.getSetting().getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_offset", keyboardMode2View.getWhiteKeyOffset());
                    edit.apply();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onMidiConnect() {
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
                }
            }
        }
    }

    @Override
    public void onMidiDisconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (midiFramer != null) {
                    MidiUtil.getMidiOutputPort().disconnect(midiFramer);
                    midiFramer = null;
                }
            }
        }
    }

    public void midiConnectHandle(byte[] data) {
        byte command = (byte) (data[0] & MidiConstants.STATUS_COMMAND_MASK);
        int pitch = data[1] + jpapplication.getSetting().getMidiKeyboardTune();
        if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
            keyboardMode1View.fireKeyDown(pitch, data[2], 0xffffffff);
            SoundEngineUtil.playSound(pitch, data[2]);
        } else if (command == MidiConstants.STATUS_NOTE_OFF
                || (command == MidiConstants.STATUS_NOTE_ON && data[2] == 0)) {
            keyboardMode1View.fireKeyUp(pitch);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Setting.SETTING_MODE_CODE) {
            SkinImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
            keyboardMode1View.changeSkinKeyboardImage(this);
            keyboardMode2View.changeSkinKeyboardImage(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyboard_setting:
                Intent intent = new Intent();
                intent.setClass(this, SettingsMode.class);
                startActivityForResult(intent, Setting.SETTING_MODE_CODE);
                break;
            case R.id.keyboard_record:
                try {
                    Button recordButton = (Button) view;
                    if (!recordStart) {
                        JPDialog jpdialog = new JPDialog(this);
                        jpdialog.setTitle("提示");
                        jpdialog.setMessage("点击确定按钮开始录音，录音将在点击停止按钮后保存至录音文件");
                        jpdialog.setFirstButton("确定", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            String date = DateUtil.format(DateUtil.now(), DateUtil.TEMPLATE_DEFAULT_CHINESE);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
