package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;

public final class PlayModeSelect extends BaseActivity implements OnClickListener {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, MainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.game_mode) {
            intent = new Intent();
            intent.setClass(this, MelodySelect.class);
            GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
            startActivity(intent);
            finish();
        } else if (id == R.id.freestyle_mode) {
            intent = new Intent();
            intent.setClass(this, WaterfallActivity.class);
            intent.putExtra("freeStyle", true);
            startActivity(intent);
        } else if (id == R.id.keyboard) {
            intent = new Intent();
            intent.setClass(this, KeyBoard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_play_select);
        findViewById(R.id.game_mode).setOnClickListener(this);
        findViewById(R.id.keyboard).setOnClickListener(this);
        findViewById(R.id.freestyle_mode).setOnClickListener(this);
    }
}
