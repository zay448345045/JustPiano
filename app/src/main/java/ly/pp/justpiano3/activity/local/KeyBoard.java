package ly.pp.justpiano3.activity.local;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.MidiDeviceUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.VibrationUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.KeyboardView;

public final class KeyBoard extends BaseActivity implements View.OnTouchListener, MidiDeviceUtil.MidiDeviceListener, View.OnClickListener {
    private KeyboardView firstKeyboardView;
    private KeyboardView secondKeyboardView;
    private LinearLayout keyboard1Layout;
    private LinearLayout keyboard2Layout;
    private ScheduledExecutorService scheduledExecutor;
    private SharedPreferences sharedPreferences;
    // 用于记录上次的移动
    private boolean reSize;
    // 记录目前是否在走动画，不能重复走
    private boolean busyAnim;
    private boolean recordStart;
    private String recordFilePath;
    private String recordFileName;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, PlayModeSelect.class));
        finish();
    }

    @Override
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
        float keyboardWeight = sharedPreferences.getFloat("keyboard_weight", 0.5f);
        firstKeyboardView.setWhiteKeyNum(sharedPreferences.getInt("keyboard1_white_key_num", 8));
        secondKeyboardView.setWhiteKeyNum(sharedPreferences.getInt("keyboard2_white_key_num", 8));
        firstKeyboardView.setWhiteKeyOffset(sharedPreferences.getInt("keyboard1_white_key_offset", 28));
        secondKeyboardView.setWhiteKeyOffset(sharedPreferences.getInt("keyboard2_white_key_offset", 21));
        keyboard1Layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, keyboardWeight));
        keyboard2Layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - keyboardWeight));
        if (MidiDeviceUtil.isSupportMidiDevice(this)) {
            MidiDeviceUtil.setMidiConnectionListener(this);
        }
    }

    private void initKeyboardView(KeyboardView keyboardView) {
        keyboardView.setOctaveTagType(KeyboardView.OctaveTagType.getEntries().get(GlobalSetting.INSTANCE.getKeyboardOctaveTagType()));
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
        if (MidiDeviceUtil.isSupportMidiDevice(this)) {
            MidiDeviceUtil.removeMidiConnectionListener();
        }
        if (recordStart) {
            recordStart = false;
            SoundEngineUtil.setRecord(false);
            File srcFile = new File(recordFilePath.replace(".raw", ".wav"));
            Uri desUri = FileUtil.getOrCreateFileByUriFolder(this,
                    GlobalSetting.INSTANCE.getRecordsSavePath(), "Records", recordFileName);
            if (FileUtil.moveFileToUri(this, srcFile, desUri)) {
                Toast.makeText(this, "录音完毕，文件已存储", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "录音文件存储失败", Toast.LENGTH_SHORT).show();
            }
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
                case MotionEvent.ACTION_DOWN -> {
                    if (id == R.id.keyboard1_layout) {
                        if (view.getHeight() - event.getY() < 30) {
                            reSize = true;
                        }
                    } else {
                        if (event.getY() < 30) {
                            reSize = true;
                        }
                    }
                }
                case MotionEvent.ACTION_MOVE -> {
                    float weight = event.getRawY() / (keyboard1Layout.getHeight() + keyboard2Layout.getHeight());
                    if (reSize && weight > 0.15f && weight < 0.85f) {
                        keyboard1Layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
                        keyboard2Layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - weight));
                    }
                }
                case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) keyboard1Layout.getLayoutParams();
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putFloat("keyboard_weight", layoutParams.weight);
                    edit.apply();
                    reSize = false;
                    view.performClick();
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
            handler.sendMessage(msg);
        }, 0, 80, TimeUnit.MILLISECONDS);
    }

    private void stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    private final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (message.what == R.id.keyboard1_count_down) {
                firstKeyboardView.setWhiteKeyNum(firstKeyboardView.getWhiteKeyNum() - 1);
                edit.putInt("keyboard1_white_key_num", firstKeyboardView.getWhiteKeyNum());
            } else if (message.what == R.id.keyboard2_count_down) {
                secondKeyboardView.setWhiteKeyNum(secondKeyboardView.getWhiteKeyNum() - 1);
                edit.putInt("keyboard2_white_key_num", secondKeyboardView.getWhiteKeyNum());
            } else if (message.what == R.id.keyboard1_count_up) {
                firstKeyboardView.setWhiteKeyNum(firstKeyboardView.getWhiteKeyNum() + 1);
                edit.putInt("keyboard1_white_key_num", firstKeyboardView.getWhiteKeyNum());
            } else if (message.what == R.id.keyboard2_count_up) {
                secondKeyboardView.setWhiteKeyNum(secondKeyboardView.getWhiteKeyNum() + 1);
                edit.putInt("keyboard2_white_key_num", secondKeyboardView.getWhiteKeyNum());
            } else if (message.what == R.id.keyboard1_move_left) {
                firstKeyboardView.setWhiteKeyOffset(firstKeyboardView.getWhiteKeyOffset() - 1);
                edit.putInt("keyboard1_white_key_offset", firstKeyboardView.getWhiteKeyOffset());
            } else if (message.what == R.id.keyboard2_move_left) {
                secondKeyboardView.setWhiteKeyOffset(secondKeyboardView.getWhiteKeyOffset() - 1);
                edit.putInt("keyboard2_white_key_offset", secondKeyboardView.getWhiteKeyOffset());
            } else if (message.what == R.id.keyboard1_move_right) {
                firstKeyboardView.setWhiteKeyOffset(firstKeyboardView.getWhiteKeyOffset() + 1);
                edit.putInt("keyboard1_white_key_offset", firstKeyboardView.getWhiteKeyOffset());
            } else if (message.what == R.id.keyboard2_move_right) {
                secondKeyboardView.setWhiteKeyOffset(secondKeyboardView.getWhiteKeyOffset() + 1);
                edit.putInt("keyboard2_white_key_offset", secondKeyboardView.getWhiteKeyOffset());
            }
            edit.apply();
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
            ImageLoadUtil.setBackground(this);
            firstKeyboardView.changeSkinKeyboardImage(this);
            secondKeyboardView.changeSkinKeyboardImage(this);
            KeyboardView.OctaveTagType octaveTagType = KeyboardView.OctaveTagType.getEntries().get(GlobalSetting.INSTANCE.getKeyboardOctaveTagType());
            firstKeyboardView.setOctaveTagType(octaveTagType);
            secondKeyboardView.setOctaveTagType(octaveTagType);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.keyboard_setting) {
            startActivityForResult(new Intent(this, SettingsMode.class), SettingsMode.SETTING_MODE_CODE);
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
                            GlobalSetting.INSTANCE.getRecordsSavePath(), "Records", recordFileName);
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
}
