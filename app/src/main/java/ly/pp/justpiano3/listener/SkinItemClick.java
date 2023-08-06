package ly.pp.justpiano3.listener;

import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.adapter.SkinDownloadAdapter;

public final class SkinItemClick implements OnClickListener {
    private final SkinDownloadAdapter skinDownloadAdapter;
    private final String name;
    private final String url;
    private final int size;
    private final String author;

    public SkinItemClick(SkinDownloadAdapter skinDownloadAdapter, String str, String str2, int i, String str3) {
        this.skinDownloadAdapter = skinDownloadAdapter;
        name = str;
        url = str2;
        size = i;
        author = str3;
    }

    @Override
    public void onClick(View view) {
        if (skinDownloadAdapter.skinDownload.list.contains(name)) {
            skinDownloadAdapter.skinDownload.mo2992a(1, name, "", 0, "");
        } else {
            skinDownloadAdapter.skinDownload.mo2992a(0, name, url, size, author);
        }
    }
}
