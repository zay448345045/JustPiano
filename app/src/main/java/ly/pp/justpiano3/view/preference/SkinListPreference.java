package ly.pp.justpiano3.view.preference;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import java.io.File;
import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.SkinListAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.SkinAndSoundFileUtil;
import ly.pp.justpiano3.view.JPProgressBar;

public class SkinListPreference extends DialogPreference {
    public File skinFile;
    public String skinKey = "";
    public Context context;
    public JPProgressBar jpProgressBar;
    private CharSequence[] skinNameList;
    private CharSequence[] skinKeyList;
    private SkinListAdapter skinListAdapter;

    public SkinListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    public SkinListPreference(Context context) {
        super(context, null);
        this.context = context;
    }

    private void loadSkinList() {
        String str = Environment.getExternalStorageDirectory() + "/JustPiano/Skins";
        List<File> localSkinList = SkinAndSoundFileUtil.getLocalSkinList(str);
        int size = localSkinList.size();
        skinNameList = new CharSequence[size + 2];
        skinKeyList = new CharSequence[size + 2];
        for (int i = 0; i < size; i++) {
            str = localSkinList.get(i).getName();
            skinNameList[i] = str.subSequence(0, str.lastIndexOf('.'));
            skinKeyList[i] = Environment.getExternalStorageDirectory() + "/JustPiano/Skins/" + localSkinList.get(i).getName();
        }
        skinNameList[size] = "默认皮肤";
        skinKeyList[size] = "original";
        skinNameList[size + 1] = "更多皮肤...";
        skinKeyList[size + 1] = "more";
    }

    public final void deleteFiles(String str) {
        int i = 0;
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!str.equals("original") && sharedPreferences.getString("skin_list", "original").equals(str)) {
            file = context.getDir("Skin", Context.MODE_PRIVATE);
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    while (i < listFiles.length) {
                        listFiles[i].delete();
                        i++;
                    }
                }
            }
        }
        loadSkinList();
        skinListAdapter.updateSkinList(skinNameList, skinKeyList);
        skinListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        if (!TextUtils.isEmpty(skinKey)) {
            persistString(skinKey);
        }
        if (context instanceof PreferenceActivity) {
            ImageLoadUtil.setBackground(((PreferenceActivity) context));
        }
        GlobalSetting.INSTANCE.loadSettings(context, false);
        setSummary(GlobalSetting.INSTANCE.getSkinName());
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        loadSkinList();
        jpProgressBar = new JPProgressBar(new ContextThemeWrapper(context, R.style.JustPianoTheme));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumWidth(400);
        linearLayout.setPadding(20, 20, 20, 20);
        linearLayout.setBackgroundColor(-1);
        ListView skinListView = new ListView(context);
        skinListView.setDivider(null);
        skinListAdapter = new SkinListAdapter(this, context, skinNameList, skinKeyList);
        skinListView.setAdapter(skinListAdapter);
        linearLayout.addView(skinListView);
        builder.setView(linearLayout);
    }
}
