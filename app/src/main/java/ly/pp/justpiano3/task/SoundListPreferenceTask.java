package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.SoundListPreference;
import ly.pp.justpiano3.utils.GZIPUtil;

import java.io.File;

public final class SoundListPreferenceTask extends AsyncTask<String, Void, String> {
    private final SoundListPreference soundListPreference;

    public SoundListPreferenceTask(SoundListPreference soundListPreference) {
        this.soundListPreference = soundListPreference;
    }

    @Override
    protected void onPostExecute(String str) {
        soundListPreference.jpProgressBar.cancel();
        Toast.makeText(soundListPreference.context, "音源设置成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... objects) {
        File dir = new File(soundListPreference.context.getFilesDir(), "Sounds");
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        if (!objects[0].equals("original")) {
            GZIPUtil.ZIPFileTo(new File(objects[1]), dir.toString());
        }
        JPApplication.teardownAudioStreamNative();
        JPApplication.unloadWavAssetsNative();
        for (int i = 108; i >= 24; i--) {
            JPApplication.preloadSounds(soundListPreference.context, i);
        }
        JPApplication.confirmLoadSounds(soundListPreference.context);
        return null;
    }

    @Override
    protected void onPreExecute() {
        soundListPreference.jpProgressBar.show();
    }
}
