package ly.pp.justpiano3.view;

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

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.SoundListAdapter;
import ly.pp.justpiano3.utils.SkinAndSoundFileUtil;

public class SoundListPreference extends DialogPreference {
    public Context context;
    public String soundKey = "";
    public JPProgressBar jpProgressBar;
    private CharSequence[] soundNameList;
    private CharSequence[] soundKeyList;
    private SoundListAdapter soundListAdapter;

    public SoundListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    public SoundListPreference(Context context) {
        super(context, null);
        this.context = context;
    }

    private void m3922a() {
        String str = Environment.getExternalStorageDirectory() + "/JustPiano/Sounds";
        List<File> f5048c = SkinAndSoundFileUtil.getLocalSoundList(str);
        int size = f5048c.size();
        soundNameList = new CharSequence[(size + 2)];
        soundKeyList = new CharSequence[(size + 2)];
        for (int i = 0; i < size; i++) {
            str = f5048c.get(i).getName();
            soundNameList[i] = str.subSequence(0, str.lastIndexOf('.'));
            soundKeyList[i] = Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + f5048c.get(i).getName();
        }
        soundNameList[size] = "原生音源";
        soundKeyList[size] = "original";
        soundNameList[size + 1] = "更多音源...";
        soundKeyList[size + 1] = "more";
    }

    public final void deleteFiles(String str) {
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
        soundListAdapter.mo3583a(soundNameList, soundKeyList);
        soundListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        persistString(soundKey);
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
        soundListAdapter = new SoundListAdapter(this, context, soundNameList, soundKeyList);
        f5051f.setAdapter(soundListAdapter);
        f5052g.addView(f5051f);
        builder.setView(f5052g);
    }
}
