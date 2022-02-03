package ly.pp.justpiano3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KeyBoard extends Activity implements View.OnClickListener {

    KeyboardModeView keyboardMode1View;
    KeyboardModeView keyboardMode2View;
    JPApplication jpapplication;
    Bitmap keyboardImage;

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
                // TODO not right
                keyboardMode1View.setKeyNum(keyboardMode1View.getKeyNum() - 1);
                break;
            case R.id.keyboard2_count_down:
                keyboardMode2View.setKeyNum(keyboardMode2View.getKeyNum() - 1);
                break;
            case R.id.keyboard1_count_up:
                keyboardMode1View.setKeyNum(keyboardMode1View.getKeyNum() + 1);
                break;
            case R.id.keyboard2_count_up:
                keyboardMode2View.setKeyNum(keyboardMode2View.getKeyNum() + 1);
                break;
            case R.id.keyboard1_move_left:
                keyboardMode1View.setKeyOffset(keyboardMode1View.getKeyOffset() - 1);
                break;
            case R.id.keyboard2_move_left:
                keyboardMode2View.setKeyOffset(keyboardMode2View.getKeyOffset() - 1);
                break;
            case R.id.keyboard1_move_right:
                keyboardMode1View.setKeyOffset(keyboardMode1View.getKeyOffset() + 1);
                break;
            case R.id.keyboard2_move_right:
                keyboardMode2View.setKeyOffset(keyboardMode2View.getKeyOffset() + 1);
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
        keyboardImage = jpapplication.loadImage("key_board_hd");
        keyboardMode1View = findViewById(R.id.keyboard1_view);
        keyboardMode1View.addMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int keyIndex) {
                jpapplication.playSound(keyIndex, 64);
            }

            @Override
            public void onKeyUp(int keyIndex) {

            }
        });
        keyboardMode2View = findViewById(R.id.keyboard2_view);
        keyboardMode2View.addMusicKeyListener(new KeyboardModeView.MusicKeyListener() {
            @Override
            public void onKeyDown(int keyIndex) {
                jpapplication.playSound(keyIndex, 64);
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
    }
}
