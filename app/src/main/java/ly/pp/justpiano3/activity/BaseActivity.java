package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.WindowUtil;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoadUtil.setBackground(this, GlobalSetting.getBackgroundPic());
        fullScreenHandle();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            fullScreenHandle();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        Context context = newBase.createConfigurationContext(override);
        super.attachBaseContext(context);
    }

    protected void fullScreenHandle() {
        if (GlobalSetting.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(getWindow());
        }
    }
}
