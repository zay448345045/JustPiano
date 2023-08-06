package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.KeyBoard;
import ly.pp.justpiano3.activity.MainMode;
import ly.pp.justpiano3.activity.MelodySelect;

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
                jPApplication.setGameMode(0);
                startActivity(intent);
                finish();
                return;
            case R.id.practice_mode:
                intent = new Intent();
                intent.setClass(this, MelodySelect.class);
                jPApplication2 = jpApplication;
                jPApplication2.setGameMode(2);
                startActivity(intent);
                finish();
                return;
            case R.id.listen_mode:
                intent = new Intent();
                intent.setClass(this, MelodySelect.class);
                jPApplication2 = jpApplication;
                jPApplication2.setGameMode(3);
                startActivity(intent);
                finish();
                return;
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
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpApplication = (JPApplication) getApplication();
        setContentView(R.layout.play_select);
        jpApplication.setBackGround(this, "ground", findViewById(R.id.layout));
        Button f4685c = findViewById(R.id.game_mode);
        f4685c.setOnClickListener(this);
        Button f4687e = findViewById(R.id.keyboard);
        f4687e.setOnClickListener(this);
        Button f4688f = findViewById(R.id.practice_mode);
        f4688f.setOnClickListener(this);
        Button f4689g = findViewById(R.id.listen_mode);
        f4689g.setOnClickListener(this);
    }
}
