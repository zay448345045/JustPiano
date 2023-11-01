package ly.pp.justpiano3.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.VibrationUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.KeyboardView;

public class KeyBoard extends Activity implements View.OnTouchListener, MidiDeviceUtil.MidiMessageReceiveListener, View.OnClickListener {
    public KeyboardView firstKeyboardView;
    public KeyboardView secondKeyboardView;
    public LinearLayout keyboard1Layout;
    public LinearLayout keyboard2Layout;
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
        firstKeyboardView = findViewById(R.id.keyboard1_view);
        initKeyboardView(firstKeyboardView);
        secondKeyboardView = findViewById(R.id.keyboard2_view);
        initKeyboardView(secondKeyboardView);
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
        firstKeyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, 0);
        secondKeyboardView.setWhiteKeyNum(keyboard2WhiteKeyNum, 0);
        firstKeyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, 0);
        secondKeyboardView.setWhiteKeyOffset(keyboard2WhiteKeyOffset, 0);
        keyboard1Layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, keyboardWeight));
        keyboard2Layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - keyboardWeight));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            MidiDeviceUtil.setMidiConnectionListener(this);
        }
    }

    private void initKeyboardView(KeyboardView keyboardView) {
        keyboardView.setOctaveTagType(KeyboardView.OctaveTagType.values()[GlobalSetting.INSTANCE.getKeyboardOctaveTagType()]);
        keyboardView.setKeyboardListener(new KeyboardView.KeyboardListener() {
            @Override
            public void onKeyDown(byte pitch, byte volume) {
                SoundEngineUtil.playSound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()), volume);
                if (GlobalSetting.INSTANCE.getSoundVibration()) {
                    VibrationUtil.vibrateOnce(KeyBoard.this, GlobalSetting.INSTANCE.getSoundVibrationTime());
                }
            }

            @Override
            public void onKeyUp(byte pitch) {
                SoundEngineUtil.stopPlaySound((byte) (pitch + GlobalSetting.INSTANCE.getKeyboardSoundTune()));
            }
        });
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
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
        SoundEngineUtil.stopPlayAllSounds();
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
                    view.performClick();
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
                    int keyboard1WhiteKeyNum = firstKeyboardView.getWhiteKeyNum() - 1;
                    firstKeyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_num", firstKeyboardView.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard2_count_down:
                    int keyboard2WhiteKeyNum = secondKeyboardView.getWhiteKeyNum() - 1;
                    secondKeyboardView.setWhiteKeyNum(keyboard2WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_num", secondKeyboardView.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard1_count_up:
                    keyboard1WhiteKeyNum = firstKeyboardView.getWhiteKeyNum() + 1;
                    firstKeyboardView.setWhiteKeyNum(keyboard1WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_num", firstKeyboardView.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard2_count_up:
                    keyboard2WhiteKeyNum = secondKeyboardView.getWhiteKeyNum() + 1;
                    secondKeyboardView.setWhiteKeyNum(keyboard2WhiteKeyNum, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_num", secondKeyboardView.getWhiteKeyNum());
                    edit.apply();
                    break;
                case R.id.keyboard1_move_left:
                    int keyboard1WhiteKeyOffset = firstKeyboardView.getWhiteKeyOffset() - 1;
                    firstKeyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_offset", firstKeyboardView.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard2_move_left:
                    int keyboard2WhiteKeyOffset = secondKeyboardView.getWhiteKeyOffset() - 1;
                    secondKeyboardView.setWhiteKeyOffset(keyboard2WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_offset", secondKeyboardView.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard1_move_right:
                    keyboard1WhiteKeyOffset = firstKeyboardView.getWhiteKeyOffset() + 1;
                    firstKeyboardView.setWhiteKeyOffset(keyboard1WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard1_white_key_offset", firstKeyboardView.getWhiteKeyOffset());
                    edit.apply();
                    break;
                case R.id.keyboard2_move_right:
                    keyboard2WhiteKeyOffset = secondKeyboardView.getWhiteKeyOffset() + 1;
                    secondKeyboardView.setWhiteKeyOffset(keyboard2WhiteKeyOffset, GlobalSetting.INSTANCE.getKeyboardAnim() ? interval : 0);
                    edit.putInt("keyboard2_white_key_offset", secondKeyboardView.getWhiteKeyOffset());
                    edit.apply();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onMidiMessageReceive(byte pitch, byte volume) {
        if (volume > 0) {
            SoundEngineUtil.playSound(pitch, volume);
            secondKeyboardView.fireKeyDown(pitch, volume, null);
        } else {
            SoundEngineUtil.stopPlaySound(pitch);
            secondKeyboardView.fireKeyUp(pitch);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsMode.SETTING_MODE_CODE) {
            ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
            firstKeyboardView.changeSkinKeyboardImage(this);
            secondKeyboardView.changeSkinKeyboardImage(this);
            KeyboardView.OctaveTagType octaveTagType = KeyboardView.OctaveTagType.values()[GlobalSetting.INSTANCE.getKeyboardOctaveTagType()];
            firstKeyboardView.setOctaveTagType(octaveTagType);
            secondKeyboardView.setOctaveTagType(octaveTagType);
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
                break;
            default:
                break;
        }
    }
}
