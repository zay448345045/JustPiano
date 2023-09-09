package ly.pp.justpiano3.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import ly.pp.justpiano3.utils.GZIPUtil;
import ly.pp.justpiano3.view.SkinListPreference;

import java.io.File;

public final class SkinListPreferenceTask extends AsyncTask<String, Void, String> {
    private final SkinListPreference skinListPreference;

    public SkinListPreferenceTask(SkinListPreference skinListPreference) {
        this.skinListPreference = skinListPreference;
    }

    @Override
    protected String doInBackground(String... objects) {
        File dir = skinListPreference.context.getDir("Skin", Context.MODE_PRIVATE);
        if (dir.isDirectory()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
        GZIPUtil.ZIPFileTo(skinListPreference.f5024d, dir.toString());
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        skinListPreference.jpProgressBar.cancel();
        Toast.makeText(skinListPreference.context, "皮肤设置成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        skinListPreference.jpProgressBar.show();
    }
}
