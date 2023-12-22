package ly.pp.justpiano3.task;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.view.preference.SkinListPreference;

public final class SkinListPreferenceTask extends AsyncTask<String, Void, Void> {
    private final SkinListPreference skinListPreference;

    public SkinListPreferenceTask(SkinListPreference skinListPreference) {
        this.skinListPreference = skinListPreference;
    }

    @Override
    protected Void doInBackground(String... objects) {
        File dir = new File(skinListPreference.context.getFilesDir(), "Skins");
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        if (!Objects.equals(objects[0], "original")) {
            GZIPUtil.unzipFromUri(skinListPreference.context, skinListPreference.skinFile, dir.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (skinListPreference.jpProgressBar.isShowing()) {
            skinListPreference.jpProgressBar.cancel();
        }
        Toast.makeText(skinListPreference.context, "皮肤设置成功!", Toast.LENGTH_SHORT).show();
        skinListPreference.closeDialog();
    }

    @Override
    protected void onPreExecute() {
        skinListPreference.jpProgressBar.show();
        skinListPreference.jpProgressBar.setCancelable(false);
    }
}
