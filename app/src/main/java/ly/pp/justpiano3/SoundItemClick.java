package ly.pp.justpiano3;

import android.view.View;
import android.view.View.OnClickListener;

public final class SoundItemClick implements OnClickListener {
    private final SoundDownloadAdapter soundDownloadAdapter;
    private final String name;
    private final String url;
    private final int size;
    private final String author;

    public SoundItemClick(SoundDownloadAdapter soundDownloadAdapter, String str, String str2, int i, String str3) {
        this.soundDownloadAdapter = soundDownloadAdapter;
        name = str;
        url = str2;
        size = i;
        author = str3;
    }

    @Override
    public void onClick(View view) {
        if (soundDownloadAdapter.soundDownload.list.contains(name)) {  //已经下载过了，传参1
            soundDownloadAdapter.soundDownload.mo3005a(1, name, "", 0, "");
        } else {
            soundDownloadAdapter.soundDownload.mo3005a(0, name, url, size, author);
        }
    }
}
