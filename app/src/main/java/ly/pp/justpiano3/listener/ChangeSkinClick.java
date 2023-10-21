package ly.pp.justpiano3.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.File;

import ly.pp.justpiano3.activity.SkinDownload;
import ly.pp.justpiano3.adapter.SkinListAdapter;
import ly.pp.justpiano3.task.SkinListPreferenceTask;

public final class ChangeSkinClick implements OnClickListener {
    private final SkinListAdapter skinListAdapter;
    private final String skinKey;

    public ChangeSkinClick(SkinListAdapter skinListAdapter, String skinKey) {
        this.skinListAdapter = skinListAdapter;
        this.skinKey = skinKey;
    }

    @Override
    public void onClick(View view) {
        skinListAdapter.skinListPreference.skinKey = skinKey;
        switch (skinKey) {
            case "original":
                new SkinListPreferenceTask(skinListAdapter.skinListPreference).execute(skinKey);
                break;
            case "more":
                Intent intent = new Intent();
                intent.setFlags(0);
                intent.setClass(skinListAdapter.context, SkinDownload.class);
                skinListAdapter.context.startActivity(intent);
                break;
            default:
                skinListAdapter.skinListPreference.skinFile = new File(skinKey);
                new SkinListPreferenceTask(skinListAdapter.skinListPreference).execute(skinKey);
                break;
        }
    }
}
