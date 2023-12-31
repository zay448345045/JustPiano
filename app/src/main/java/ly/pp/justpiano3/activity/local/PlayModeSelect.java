package ly.pp.justpiano3.activity.local;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.enums.LocalPlayModeEnum;

public final class PlayModeSelect extends BaseActivity implements OnClickListener {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainMode.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.game_mode) {
            GlobalSetting.INSTANCE.setLocalPlayMode(LocalPlayModeEnum.NORMAL);
            startActivity(new Intent(this, MelodySelect.class));
            finish();
        } else if (id == R.id.freestyle_mode) {
            Intent intent = new Intent(this, WaterfallActivity.class);
            intent.putExtra("freeStyle", true);
            startActivity(intent);
        } else if (id == R.id.keyboard) {
            startActivity(new Intent(this, KeyBoard.class));
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
