package ly.pp.justpiano3;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import java.io.File;
import java.util.List;

public class SoundListPreference extends DialogPreference {
    Context context;
    String str1 = "";
    JPProgressBar jpProgressBar;
    private CharSequence[] f5046a;
    private CharSequence[] f5047b;
    private SoundListAdapter soundListAdapter;

    public SoundListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    private void m3922a() {
        String str = Environment.getExternalStorageDirectory() + "/JustPiano/Sounds";
        List<File> f5048c = SkinAndSoundFileHandle.getLocalSoundList(str);
        int size = f5048c.size();
        f5046a = new CharSequence[(size + 2)];
        f5047b = new CharSequence[(size + 2)];
        for (int i = 0; i < size; i++) {
            str = f5048c.get(i).getName();
            f5046a[i] = str.subSequence(0, str.lastIndexOf('.'));
            f5047b[i] = Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + f5048c.get(i).getName();
        }
        f5046a[size] = "原生音源";
        f5047b[size] = "original";
        f5046a[size + 1] = "更多音源...";
        f5047b[size + 1] = "more";
    }

    final void deleteFiles(String str) {
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getString("sound_list", "original").equals(str)) {
            file = new File(context.getFilesDir().getAbsolutePath() + "/Sounds");
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File delete : listFiles) {
                        delete.delete();
                    }
                }
            }
        }
        m3922a();
        soundListAdapter.mo3583a(f5046a, f5047b);
        soundListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        persistString(str1);
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        m3922a();
        jpProgressBar = new JPProgressBar(new ContextThemeWrapper(context, R.style.JustPianoTheme));
        LinearLayout f5052g = new LinearLayout(context);
        f5052g.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        f5052g.setOrientation(LinearLayout.VERTICAL);
        f5052g.setMinimumWidth(400);
        f5052g.setPadding(20, 20, 20, 20);
        f5052g.setBackgroundColor(-1);
        ListView f5051f = new ListView(context);
        f5051f.setDivider(null);
        soundListAdapter = new SoundListAdapter(this, context, f5046a, f5047b);
        f5051f.setAdapter(soundListAdapter);
        f5052g.addView(f5051f);
        builder.setView(f5052g);
    }
}
