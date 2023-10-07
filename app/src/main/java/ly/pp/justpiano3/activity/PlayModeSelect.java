package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class PlayModeSelect extends Activity implements OnClickListener {
    private JPApplication jpApplication;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, MainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        JPApplication jPApplication;
        JPApplication jPApplication2;
        Intent intent;
        switch (view.getId()) {
            case R.id.game_mode:
                intent = new Intent();
                intent.setClass(this, MelodySelect.class);
                jPApplication = jpApplication;
                jPApplication.setGameMode(LocalPlayModeEnum.NORMAL);
                startActivity(intent);
                finish();
                return;
            case R.id.practice_mode:
                intent = new Intent();
                intent.setClass(this, MelodySelect.class);
                jPApplication2 = jpApplication;
                jPApplication2.setGameMode(LocalPlayModeEnum.PRACTISE);
                startActivity(intent);
                finish();
                return;
            /*
             case R.id.listen_mode:
                intent = new Intent();
                intent.setClass(this, MelodySelect.class);
                jPApplication2 = jpApplication;
                jPApplication2.setGameMode(LocalPlayModeEnum.HEAR);
                startActivity(intent);
                finish();
                return;
            */
            case R.id.keyboard:
                intent = new Intent();
                intent.setClass(this, KeyBoard.class);
                startActivity(intent);
                finish();
                return;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jpApplication = (JPApplication) getApplication();
        setContentView(R.layout.lo_play_select);
        ImageLoadUtil.setBackGround(this, "ground", findViewById(R.id.layout));
        findViewById(R.id.game_mode).setOnClickListener(this);
        findViewById(R.id.keyboard).setOnClickListener(this);
        findViewById(R.id.practice_mode).setOnClickListener(this);
        //     findViewById(R.id.listen_mode).setOnClickListener(this);
    }
}
