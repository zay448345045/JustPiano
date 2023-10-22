package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public class PlayModeSelect extends Activity implements OnClickListener {

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
        switch (view.getId()) {
            case R.id.game_mode:
                intent = new Intent();
                intent.setClass(this, MelodySelect.class);
                GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
                startActivity(intent);
                finish();
                return;
            case R.id.freestyle_mode:
                intent = new Intent();
                intent.setClass(this, WaterfallActivity.class);
                intent.putExtra("freeStyle", true);
                startActivity(intent);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_play_select);
        ImageLoadUtil.setBackground(this, "ground", findViewById(R.id.layout));
        findViewById(R.id.game_mode).setOnClickListener(this);
        findViewById(R.id.keyboard).setOnClickListener(this);
        findViewById(R.id.freestyle_mode).setOnClickListener(this);
    }
}
