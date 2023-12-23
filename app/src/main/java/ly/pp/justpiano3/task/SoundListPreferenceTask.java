package ly.pp.justpiano3.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

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
        DocumentFile soundFile = FileUtil.INSTANCE.uriToDocumentFile(soundListPreference.context, Uri.parse(objects[0]));
        if (soundFile == null || soundFile.getName() == null || soundFile.length() > 1024 * 1024 * 1024) {
            return "invalid";
        }
        try {
            if (soundFile.getName().endsWith(".ss")) {
                GZIPUtil.unzipFromUri(soundListPreference.context, soundFile.getUri(), dir.toString());
                SoundEngineUtil.teardownAudioStreamNative();
                Thread.sleep(20);
                SoundEngineUtil.unloadSf2();
                Thread.sleep(20);
                SoundEngineUtil.unloadWavAssetsNative();
                Thread.sleep(20);
                SoundEngineUtil.setupAudioStreamNative();
                Thread.sleep(20);
                for (int i = MidiUtil.MAX_PIANO_MIDI_PITCH; i >= MidiUtil.MIN_PIANO_MIDI_PITCH; i--) {
                    SoundEngineUtil.loadSoundAssetsNative(soundListPreference.context, i);
                }
                Thread.sleep(20);
                SoundEngineUtil.startAudioStreamNative();
            } else if (soundFile.getName().endsWith(".sf2")) {
                String newSf2Path = FileUtil.INSTANCE.copyDocumentFileToAppFilesDir(soundListPreference.context, soundFile);
                if (newSf2Path == null) {
                    return "invalid";
                }
                SoundEngineUtil.teardownAudioStreamNative();
                Thread.sleep(20);
                SoundEngineUtil.unloadSf2();
                Thread.sleep(20);
                SoundEngineUtil.setupAudioStreamNative();
                Thread.sleep(20);
                SoundEngineUtil.loadSf2(newSf2Path);
                Thread.sleep(20);
                SoundEngineUtil.startAudioStreamNative();
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
            Toast.makeText(soundListPreference.context, "音源文件无效，或大小超过1G", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(soundListPreference.context, Objects.equals("error", result)
                    ? "音源设置失败!" : "音源设置成功!", Toast.LENGTH_SHORT).show();
        }
        soundListPreference.closeDialog();
    }

    @Override
    protected void onPreExecute() {
        soundListPreference.jpProgressBar.show();
        soundListPreference.jpProgressBar.setCancelable(false);
    }
}
