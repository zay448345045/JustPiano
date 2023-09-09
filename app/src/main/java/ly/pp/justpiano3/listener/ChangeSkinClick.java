package ly.pp.justpiano3.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.SkinDownload;
import ly.pp.justpiano3.adapter.SkinListAdapter;
import ly.pp.justpiano3.task.SkinListPreferenceTask;

import java.io.File;

public final class ChangeSkinClick implements OnClickListener {
    private final SkinListAdapter skinListAdapter;
    private final String f5945b;

    public ChangeSkinClick(SkinListAdapter c1305mc, String str) {
        skinListAdapter = c1305mc;
        f5945b = str;
    }

    @Override
    public void onClick(View view) {
        int i = 0;
        switch (f5945b) {
            case "original":
                File dir = skinListAdapter.context.getDir("Skin", Context.MODE_PRIVATE);
                if (dir.isDirectory()) {
                    File[] listFiles = dir.listFiles();
                    if (listFiles != null && listFiles.length > 0) {
                        while (i < listFiles.length) {
                            listFiles[i].delete();
                            i++;
                        }
                    }
                }
                break;
            case "more":
                Intent intent = new Intent();
                intent.setFlags(0);
                intent.setClass(skinListAdapter.context, SkinDownload.class);
                skinListAdapter.context.startActivity(intent);
                break;
            default:
                skinListAdapter.skinListPreference.f5024d = new File(f5945b);
                new SkinListPreferenceTask(skinListAdapter.skinListPreference).execute();
                break;
        }
    }
}
