package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class KeyBoard extends Activity implements View.OnTouchListener, MidiConnectStart {

    KeyboardModeView keyboardMode1View;
    KeyboardModeView keyboardMode2View;
    LinearLayout keyboard1Layout;
    LinearLayout keyboard2Layout;
    JPApplication jpapplication;
    MidiReceiver midiFramer;
    ScheduledExecutorService scheduledExecutor;
    // 用于记录上次的移动
    private boolean reSize;
    // 记录目前是否在走动画，不能重复走
    private boolean busyAnim;
    // 琴键动画间隔
    private int interval = 320;

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
        keyboardMode1View.addMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int keyIndex, int volume) {
                jpapplication.playSound(keyIndex, volume);
            }

            @Override
            public void onKeyUp(int keyIndex) {

            }
        });
        keyboardMode2View = findViewById(R.id.keyboard2_view);
        keyboardMode2View.addMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int keyIndex, int volume) {
                jpapplication.playSound(keyIndex, volume);
            }

            @Override
            public void onKeyUp(int keyIndex) {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                jpapplication.addMidiConnectionStart(this);
                if (jpapplication.midiOutputPort != null && midiFramer == null) {
                    midiFramer = new MidiFramer(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] data, int offset, int count, long timestamp) {
                            byte command = (byte) (data[0] & MidiConstants.STATUS_COMMAND_MASK);
                            if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
                                keyboardMode1View.fireKeyDown(data[1], data[2]);
                            } else if (command == MidiConstants.STATUS_NOTE_OFF
                                    || (command == MidiConstants.STATUS_NOTE_ON && data[2] == 0)) {
                                keyboardMode1View.fireKeyUp(data[1]);
                            }
                        }
                    });
                    jpapplication.midiOutputPort.connect(midiFramer);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null) {
                    jpapplication.midiOutputPort.disconnect(midiFramer);
                    midiFramer = null;
                    jpapplication.removeMidiConnectionStart(this);
                }
            }
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
                    if (reSize && weight > 0.2f && weight < 0.8f) {
                        keyboard1Layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, weight));
                        keyboard2Layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1 - weight));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
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

    private final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            int viewId = msg.what;
            switch (viewId) {
                case R.id.keyboard1_count_down:
                    keyboardMode1View.setWhiteKeyNum(keyboardMode1View.getWhiteKeyNum() - 1, interval);
                    break;
                case R.id.keyboard2_count_down:
                    keyboardMode2View.setWhiteKeyNum(keyboardMode2View.getWhiteKeyNum() - 1, interval);
                    break;
                case R.id.keyboard1_count_up:
                    keyboardMode1View.setWhiteKeyNum(keyboardMode1View.getWhiteKeyNum() + 1, interval);
                    break;
                case R.id.keyboard2_count_up:
                    keyboardMode2View.setWhiteKeyNum(keyboardMode2View.getWhiteKeyNum() + 1, interval);
                    break;
                case R.id.keyboard1_move_left:
                    keyboardMode1View.setWhiteKeyOffset(keyboardMode1View.getWhiteKeyOffset() - 1, interval);
                    break;
                case R.id.keyboard2_move_left:
                    keyboardMode2View.setWhiteKeyOffset(keyboardMode2View.getWhiteKeyOffset() - 1, interval);
                    break;
                case R.id.keyboard1_move_right:
                    keyboardMode1View.setWhiteKeyOffset(keyboardMode1View.getWhiteKeyOffset() + 1, interval);
                    break;
                case R.id.keyboard2_move_right:
                    keyboardMode2View.setWhiteKeyOffset(keyboardMode2View.getWhiteKeyOffset() + 1, interval);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onMidiConnectionStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null && midiFramer == null) {
                    midiFramer = new MidiFramer(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] data, int offset, int count, long timestamp) {
                            byte command = (byte) (data[0] & MidiConstants.STATUS_COMMAND_MASK);
                            if (command == MidiConstants.STATUS_NOTE_ON && data[2] > 0) {
                                keyboardMode1View.fireKeyDown(data[1], data[2]);
                            } else if (command == MidiConstants.STATUS_NOTE_OFF
                                    || (command == MidiConstants.STATUS_NOTE_ON && data[2] == 0)) {
                                keyboardMode1View.fireKeyUp(data[1]);
                            }
                        }
                    });
                    jpapplication.midiOutputPort.connect(midiFramer);
                }
            }
        }
    }
}
