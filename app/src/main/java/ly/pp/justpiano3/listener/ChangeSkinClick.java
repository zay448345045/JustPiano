package ly.pp.justpiano3.listener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.Objects;

import ly.pp.justpiano3.activity.SkinDownload;
import ly.pp.justpiano3.adapter.SkinListAdapter;
import ly.pp.justpiano3.task.SkinListPreferenceTask;
import ly.pp.justpiano3.utils.FilePickerUtil;

public final class ChangeSkinClick implements OnClickListener {
    private final SkinListAdapter skinListAdapter;
    private final String skinKey;

    public ChangeSkinClick(SkinListAdapter skinListAdapter, String skinKey) {
        this.skinListAdapter = skinListAdapter;
        this.skinKey = skinKey;
    }

    @Override
    public void onClick(View view) {
        if (skinKey.equals("more")) {
            Intent intent = new Intent();
            intent.setClass(skinListAdapter.context, SkinDownload.class);
            ((Activity) (skinListAdapter.context)).startActivityForResult(intent, SkinDownload.SKIN_DOWNLOAD_REQUEST_CODE);
        } else if (skinKey.equals("select")) {
            FilePickerUtil.openFilePicker((Activity) skinListAdapter.context, false, "skin_select");
        } else {
            skinListAdapter.skinListPreference.skinKey = skinKey;
            skinListAdapter.skinListPreference.skinFile = Objects.equals("original", skinKey) ? null : Uri.parse(skinKey);
            new SkinListPreferenceTask(skinListAdapter.skinListPreference).execute(skinKey);
        }
    }
}
