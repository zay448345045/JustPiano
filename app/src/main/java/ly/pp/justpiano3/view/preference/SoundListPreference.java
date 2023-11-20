package ly.pp.justpiano3.view.preference;

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

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.SoundListAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.SkinAndSoundFileUtil;
import ly.pp.justpiano3.view.JPProgressBar;

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

    private void loadSoundList() {
        List<File> localSoundList = SkinAndSoundFileUtil.getLocalSoundList(
                Environment.getExternalStorageDirectory() + "/JustPiano/Sounds");
        int size = localSoundList.size();
        soundNameList = new CharSequence[size + 2];
        soundKeyList = new CharSequence[size + 2];
        for (int i = 0; i < size; i++) {
            String soundName = localSoundList.get(i).getName();
            soundNameList[i] = soundName.subSequence(0, soundName.lastIndexOf('.'));
            soundKeyList[i] = Environment.getExternalStorageDirectory() + "/JustPiano/Sounds/" + localSoundList.get(i).getName();
        }
        soundNameList[size] = "默认音源";
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
        // 如果正在使用这个音源，则删除这个音源解压后的文件
        if (sharedPreferences.getString("sound_list", "original").equals(str)) {
            file = new File(context.getFilesDir().getAbsolutePath() + "/Sounds");
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null) {
                    for (File delete : listFiles) {
                        delete.delete();
                    }
                }
            }
        }
        loadSoundList();
        soundListAdapter.updateSoundList(soundNameList, soundKeyList);
        soundListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        if (!StringUtil.isNullOrEmpty(soundKey)) {
            persistString(soundKey);
        }
        GlobalSetting.INSTANCE.loadSettings(context, false);
        setSummary("当前音源：" + GlobalSetting.INSTANCE.getSoundName());
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        loadSoundList();
        jpProgressBar = new JPProgressBar(new ContextThemeWrapper(context, R.style.JustPianoTheme));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumWidth(400);
        linearLayout.setPadding(20, 20, 20, 20);
        linearLayout.setBackgroundColor(-1);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        soundListAdapter = new SoundListAdapter(this, context, soundNameList, soundKeyList);
        listView.setAdapter(soundListAdapter);
        linearLayout.addView(listView);
        builder.setView(linearLayout);
    }
}
