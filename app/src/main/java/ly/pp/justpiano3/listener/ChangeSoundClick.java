package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.adapter.SoundListAdapter;
import ly.pp.justpiano3.task.SoundListPreferenceTask;

public final class ChangeSoundClick implements OnClickListener {
    private final SoundListAdapter soundListAdapter;
    private final String soundKey;
    private final int position;

    public ChangeSoundClick(SoundListAdapter soundListAdapter, String soundKey, int position) {
        this.soundListAdapter = soundListAdapter;
        this.soundKey = soundKey;
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        switch (soundKey) {
            case "original":
                new SoundListPreferenceTask(soundListAdapter.soundListPreference).execute("original");
                break;
            case "more":
                Intent intent = new Intent();
                intent.setFlags(0);
                intent.setClass(soundListAdapter.context, SoundDownload.class);
                soundListAdapter.context.startActivity(intent);
                break;
            default:
                new SoundListPreferenceTask(soundListAdapter.soundListPreference).execute(soundKey, String.valueOf(soundListAdapter.soundKeyList[position]));
                break;
        }
    }
}
