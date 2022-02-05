package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.midi.MidiReceiver;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KeyBoard extends Activity implements View.OnClickListener {

    KeyboardModeView keyboardMode1View;
    KeyboardModeView keyboardMode2View;
    MidiFramer midiFramer;
    JPApplication jpapplication;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, PlayModeSelect.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyboard1_count_down:
                keyboardMode1View.setWhiteKeyNum(keyboardMode1View.getWhiteKeyNum() - 1);
                break;
            case R.id.keyboard2_count_down:
                keyboardMode2View.setWhiteKeyNum(keyboardMode2View.getWhiteKeyNum() - 1);
                break;
            case R.id.keyboard1_count_up:
                keyboardMode1View.setWhiteKeyNum(keyboardMode1View.getWhiteKeyNum() + 1);
                break;
            case R.id.keyboard2_count_up:
                keyboardMode2View.setWhiteKeyNum(keyboardMode2View.getWhiteKeyNum() + 1);
                break;
            case R.id.keyboard1_move_left:
                keyboardMode1View.setWhiteKeyOffset(keyboardMode1View.getWhiteKeyOffset() - 1);
                break;
            case R.id.keyboard2_move_left:
                keyboardMode2View.setWhiteKeyOffset(keyboardMode2View.getWhiteKeyOffset() - 1);
                break;
            case R.id.keyboard1_move_right:
                keyboardMode1View.setWhiteKeyOffset(keyboardMode1View.getWhiteKeyOffset() + 1);
                break;
            case R.id.keyboard2_move_right:
                keyboardMode2View.setWhiteKeyOffset(keyboardMode2View.getWhiteKeyOffset() + 1);
                break;
            default:
                break;
        }
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
        Button keyboard1CountDown = findViewById(R.id.keyboard1_count_down);
        keyboard1CountDown.setOnClickListener(this);
        Button keyboard2CountDown = findViewById(R.id.keyboard2_count_down);
        keyboard2CountDown.setOnClickListener(this);
        Button keyboard1Countup = findViewById(R.id.keyboard1_count_up);
        keyboard1Countup.setOnClickListener(this);
        Button keyboard2Countup = findViewById(R.id.keyboard2_count_up);
        keyboard2Countup.setOnClickListener(this);
        Button keyboard1MoveLeft = findViewById(R.id.keyboard1_move_left);
        keyboard1MoveLeft.setOnClickListener(this);
        Button keyboard2MoveLeft = findViewById(R.id.keyboard2_move_left);
        keyboard2MoveLeft.setOnClickListener(this);
        Button keyboard1MoveRight = findViewById(R.id.keyboard1_move_right);
        keyboard1MoveRight.setOnClickListener(this);
        Button keyboard2MoveRight = findViewById(R.id.keyboard2_move_right);
        keyboard2MoveRight.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                if (jpapplication.midiOutputPort != null) {
                    jpapplication.midiOutputPort.connect(new MidiFramer(new MidiReceiver() {
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
                    }));
                }
            }
        }
    }
}
