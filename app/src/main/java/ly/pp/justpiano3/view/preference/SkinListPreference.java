package ly.pp.justpiano3.view.preference;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.TextView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.SkinListAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.view.JPProgressBar;

public final class SkinListPreference extends DialogPreference {
    public Uri skinFile;
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

    public void loadSkinList() {
        File skinsDir = new File(context.getExternalFilesDir(null), "Skins");
        if (!skinsDir.exists()) {
            skinsDir.mkdirs();
        }
        List<File> localSkinList = getLocalSkinList(skinsDir);
        int size = localSkinList.size();
        skinNameList = new CharSequence[size + 3];
        skinKeyList = new CharSequence[size + 3];
        for (int i = 0; i < size; i++) {
            String skinName = localSkinList.get(i).getName();
            skinNameList[i] = skinName.subSequence(0, skinName.lastIndexOf('.'));
            skinKeyList[i] = localSkinList.get(i).toURI().toString();
        }
        skinNameList[size] = "默认皮肤";
        skinKeyList[size] = "original";
        skinNameList[size + 1] = "选择本地皮肤...";
        skinKeyList[size + 1] = "select";
        skinNameList[size + 2] = "下载更多皮肤...";
        skinKeyList[size + 2] = "more";
    }

    private List<File> getLocalSkinList(File file) {
        List<File> linkedList = new LinkedList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile.isFile() && listFile.getName().endsWith(".ps")) {
                    linkedList.add(listFile);
                }
            }
        }
        return linkedList;
    }

    public void deleteFiles(String str) {
        FileUtil.INSTANCE.deleteFileUsingUri(context, Uri.parse(str));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!"original".equals(str) && sharedPreferences.getString("skin_select", "original").equals(str)) {
            File skinDir = new File(context.getFilesDir(), "Skins");
            if (skinDir.isDirectory()) {
                File[] listFiles = skinDir.listFiles();
                if (listFiles != null) {
                    for (File file : listFiles) {
                        file.delete();
                    }
                }
            }
        }
        loadSkinList();
        updateSkinList();
    }

    public void updateSkinList() {
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
        FileUtil.UriInfo uriInfo = FileUtil.INSTANCE.getUriInfo(context, Uri.parse(skinKey));
        setSummary(TextUtils.isEmpty(uriInfo.getDisplayName()) ? "默认皮肤" : uriInfo.getDisplayName());
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
        linearLayout.setBackgroundColor(Color.WHITE);
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setTextColor(Color.BLACK);
        textView.setText("下载皮肤存储位置：SD卡/Android/data/ly.pp.justpiano3/files/Skins，卸载APP时将删除所有文件");
        linearLayout.addView(textView);
        ListView skinListView = new ListView(context);
        skinListView.setDivider(null);
        skinListAdapter = new SkinListAdapter(this, context, skinNameList, skinKeyList);
        skinListView.setAdapter(skinListAdapter);
        linearLayout.addView(skinListView);
        builder.setView(linearLayout);
    }

    public void closeDialog() {
        if (getDialog() != null && getDialog().isShowing()) {
            getDialog().dismiss();
        }
    }
}
