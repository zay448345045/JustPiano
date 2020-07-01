package ly.pp.justpiano3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

final class RecordCheckChange implements OnCheckedChangeListener {
    private final MelodySelect melodySelect;

    RecordCheckChange(MelodySelect melodySelect) {
        this.melodySelect = melodySelect;
    }

    @Override
    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (z && melodySelect.sharedPreferences.getBoolean("record_dialog", true)) {
            melodySelect.mo2785a("该功能需要root，一部分未root过的机型开启录音后可能会引起强制关闭。录制成功后，将保存到SD卡下面的Justpiano/Record文件夹下。为减少环境杂音，可以尝试按住MIC孔。", 0);
            if (ContextCompat.checkSelfPermission(melodySelect, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(melodySelect, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        } else if (z) {
            if (ContextCompat.checkSelfPermission(melodySelect, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(melodySelect, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        }
    }
}
