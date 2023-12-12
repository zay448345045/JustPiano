package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import ly.pp.justpiano3.midi.MidiUtil;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.utils.SoundEngineUtil;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public final class SoundListPreferenceTask extends AsyncTask<String, Void, String> {
    private final SoundListPreference soundListPreference;

    public SoundListPreferenceTask(SoundListPreference soundListPreference) {
        this.soundListPreference = soundListPreference;
    }

    @Override
    protected String doInBackground(String... objects) {
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
        if (soundFile.length() > 1024 * 1024 * 1024) {
            return "invalid";
        }
        try {
            if (soundFile.getName().endsWith(".ss")) {
                GZIPUtil.ZIPFileTo(soundFile, dir.toString());
            }
            if (soundFile.getName().endsWith(".ss")) {
                SoundEngineUtil.unloadSf2();
                SoundEngineUtil.teardownAudioStreamNative();
                SoundEngineUtil.unloadWavAssetsNative();
                for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
                    SoundEngineUtil.loadSoundAssetsNative(soundListPreference.context, i);
                }
                SoundEngineUtil.setupAudioStreamNative(2, 44100);
            } else if (soundFile.getName().endsWith(".sf2")) {
                String newSf2Path = FileUtil.INSTANCE.copyFileToAppFilesDir(soundListPreference.context, soundFile);
                SoundEngineUtil.unloadSf2();
                SoundEngineUtil.loadSf2(newSf2Path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (soundListPreference.jpProgressBar.isShowing()) {
            soundListPreference.jpProgressBar.cancel();
        }
        if (Objects.equals("invalid", result)) {
            Toast.makeText(soundListPreference.context, "音源文件大小不得超过1G", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(soundListPreference.context, Objects.equals("error", result)
                    ? "音源设置失败!" : "音源设置成功!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPreExecute() {
        soundListPreference.jpProgressBar.show();
        soundListPreference.jpProgressBar.setCancelable(false);
    }
}
