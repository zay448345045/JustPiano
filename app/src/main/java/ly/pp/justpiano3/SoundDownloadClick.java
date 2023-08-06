package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final class SoundDownloadClick implements OnClickListener {
    private final SoundDownload soundDownload;
    private final int type;
    private final String url;
    private final String name;

    SoundDownloadClick(SoundDownload soundDownload, int i, String str, String str2) {
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
                ThreadPoolUtils.execute(() -> SoundDownload.downloadSS(soundDownload, url, name));
                break;
            case 1:
                ThreadPoolUtils.execute(() -> soundDownload.mo3006a(name + ".ss"));
                break;
            case 2:
                ThreadPoolUtils.execute(() -> JPApplication.reLoadOriginalSounds(soundDownload.getApplicationContext()));
                break;
        }
    }
}
