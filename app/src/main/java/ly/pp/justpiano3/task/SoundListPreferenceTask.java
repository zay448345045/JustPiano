package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.SoundListPreference;

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
            if (listFiles != null) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        SoundEngineUtil.teardownAudioStreamNative();
        SoundEngineUtil.unloadWavAssetsNative();

        File soundFile = new File(objects[1]);
        if (!soundFile.exists()) {
            return null;
        }
        if (!objects[0].equals("original") && soundFile.getName().endsWith(".ss")) {
            GZIPUtil.ZIPFileTo(soundFile, dir.toString());
        }

        if (soundFile.getName().endsWith(".ss")) {
            SoundEngineUtil.unloadSf2Sound();
            for (int i = 108; i >= 24; i--) {
                SoundEngineUtil.preloadSounds(soundListPreference.context, i);
            }
            SoundEngineUtil.afterLoadSounds(soundListPreference.context);
        } else if (soundFile.getName().endsWith(".sf2")) {
            SoundEngineUtil.loadSf2Sound(soundListPreference.context, soundFile);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        soundListPreference.jpProgressBar.show();
    }
}
