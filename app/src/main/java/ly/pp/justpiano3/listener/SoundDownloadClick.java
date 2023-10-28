package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;

public final class SoundDownloadClick implements OnClickListener {
    private final SoundDownload soundDownload;
    private final int type;
    private final String soundId;
    private final String name;
    private final String soundType;

    public SoundDownloadClick(SoundDownload soundDownload, int eventType, String soundId, String soundFileName, String soundType) {
        this.soundDownload = soundDownload;
        type = eventType;
        this.soundId = soundId;
        name = soundFileName;
        this.soundType = soundType;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        switch (type) {
            case 0:
                ThreadPoolUtil.execute(() -> SoundDownload.downloadSound(soundDownload, soundId, name, soundType));
                break;
            case 1:
                ThreadPoolUtil.execute(() -> soundDownload.changeSound(name + soundType));
                break;
            case 2:
                ThreadPoolUtil.execute(() -> SoundEngineUtil.reLoadOriginalSounds(soundDownload.getApplicationContext()));
                break;
        }
    }
}
