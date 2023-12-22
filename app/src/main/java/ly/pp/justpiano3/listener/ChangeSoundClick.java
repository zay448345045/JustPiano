package ly.pp.justpiano3.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.adapter.SoundListAdapter;
import ly.pp.justpiano3.task.SoundListPreferenceTask;
import ly.pp.justpiano3.utils.FilePickerUtil;

public final class ChangeSoundClick implements OnClickListener {
    private final SoundListAdapter soundListAdapter;
    private final String soundKey;

    public ChangeSoundClick(SoundListAdapter soundListAdapter, String soundKey) {
        this.soundListAdapter = soundListAdapter;
        this.soundKey = soundKey;
    }

    @Override
    public void onClick(View view) {
        if (soundKey.equals("more")) {
            Intent intent = new Intent();
            intent.setClass(soundListAdapter.context, SoundDownload.class);
            soundListAdapter.context.startActivity(intent);
        } else if (soundKey.equals("select")) {
            FilePickerUtil.openFilePicker((Activity) soundListAdapter.context, false, "sound_select");
        } else {
            soundListAdapter.soundListPreference.soundKey = soundKey;
            new SoundListPreferenceTask(soundListAdapter.soundListPreference).execute(soundKey);
        }
    }
}
