package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.SoundDownload;
import ly.pp.justpiano3.adapter.SoundListAdapter;
import ly.pp.justpiano3.task.SoundListPreferenceTask;

public final class ChangeSoundClick implements OnClickListener {
    private final SoundListAdapter soundListAdapter;
    private final String f5990b;
    private final int f5991c;

    public ChangeSoundClick(SoundListAdapter c1324mv, String str, int i) {
        soundListAdapter = c1324mv;
        f5990b = str;
        f5991c = i;
    }

    @Override
    public void onClick(View view) {
        switch (f5990b) {
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
                new SoundListPreferenceTask(soundListAdapter.soundListPreference).execute(f5990b, String.valueOf(soundListAdapter.soundKeyList[f5991c]));
                break;
        }
    }
}
