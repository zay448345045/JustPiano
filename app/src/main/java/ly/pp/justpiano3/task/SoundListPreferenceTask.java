package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import ly.pp.justpiano3.midi.MidiUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public final class SoundListPreferenceTask extends AsyncTask<String, Void, Void> {
    private final SoundListPreference soundListPreference;

    public SoundListPreferenceTask(SoundListPreference soundListPreference) {
        this.soundListPreference = soundListPreference;
    }

    @Override
    protected Void doInBackground(String... objects) {
        if (objects[0].equals("original")) {
            SoundEngineUtil.reLoadOriginalSounds(soundListPreference.context);
            return null;
        }
        File dir = new File(soundListPreference.context.getFilesDir(), "Sounds");
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }

        File soundFile = new File(objects[1]);
        if (!soundFile.exists()) {
            return null;
        }
        if (soundFile.getName().endsWith(".ss")) {
            GZIPUtil.ZIPFileTo(soundFile, dir.toString());
        }
        SoundEngineUtil.unloadSf2Sound();
        if (soundFile.getName().endsWith(".ss")) {
            SoundEngineUtil.teardownAudioStreamNative();
            SoundEngineUtil.unloadWavAssetsNative();
            for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
                SoundEngineUtil.preloadSounds(soundListPreference.context, i);
            }
            SoundEngineUtil.afterLoadSounds(soundListPreference.context);
        } else if (soundFile.getName().endsWith(".sf2")) {
            SoundEngineUtil.loadSf2Sound(soundListPreference.context, soundFile);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        soundListPreference.jpProgressBar.cancel();
        Toast.makeText(soundListPreference.context, "音源设置成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        soundListPreference.jpProgressBar.show();
    }
}
