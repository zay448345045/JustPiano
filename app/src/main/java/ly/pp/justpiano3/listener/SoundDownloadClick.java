package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.thread.ThreadPoolUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;

public final class SoundDownloadClick implements OnClickListener {
    private final SoundDownload soundDownload;
    private final int type;
    private final String url;
    private final String name;

    public SoundDownloadClick(SoundDownload soundDownload, int i, String str, String str2) {
        this.soundDownload = soundDownload;
        type = i;
        url = str;
        name = str2;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        switch (type) {
            case 0:
                ThreadPoolUtil.execute(() -> SoundDownload.downloadSS(soundDownload, url, name));
                break;
            case 1:
                ThreadPoolUtil.execute(() -> soundDownload.changeSound(name + ".ss"));
                break;
            case 2:
                ThreadPoolUtil.execute(() -> SoundEngineUtil.reLoadOriginalSounds(soundDownload.getApplicationContext()));
                break;
        }
    }
}
