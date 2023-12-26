package ly.pp.justpiano3.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import ly.pp.justpiano3.activity.local.SoundDownload;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.WindowUtil;

public class BaseActivity extends ComponentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoadUtil.setBackground(this);
        if (GlobalSetting.INSTANCE.getAllFullScreenShow()) {
            WindowUtil.fullScreenHandle(getWindow());
        } else {
            WindowUtil.exitFullScreenHandle(getWindow());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!(this instanceof JustPiano) && !(this instanceof SoundDownload)
                && !SoundEngineUtil.isAudioStreamStart()) {
            SoundEngineUtil.restartStream();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (GlobalSetting.INSTANCE.getAllFullScreenShow()) {
                WindowUtil.fullScreenHandle(getWindow());
            } else {
                WindowUtil.exitFullScreenHandle(getWindow());
            }
        }
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration config = resources.getConfiguration();
        if (config.fontScale != 1.0f) {
            config.fontScale = 1.0f;
            Context context = getApplicationContext();
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
        return resources;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (newConfig.fontScale != 1.0f) {
            // 屏蔽字体大小调整的系统设置
            newConfig = new Configuration(newConfig);
            newConfig.fontScale = 1.0f;
            applyOverrideConfiguration(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }
}
