package ly.pp.justpiano3.view.preference;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.adapter.SoundListAdapter;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.FileUtil;
import ly.pp.justpiano3.view.JPProgressBar;

public final class SoundListPreference extends DialogPreference {
    private DialogFragmentCompat dialogFragmentCompat;
    public String soundKey = "";
    public JPProgressBar jpProgressBar;
    private CharSequence[] soundNameList;
    private CharSequence[] soundKeyList;
    private SoundListAdapter soundListAdapter;

    public SoundListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SoundListPreference(Context context) {
        super(context, null);
    }

    private void loadSoundList() {
        File soundsDir = new File(getContext().getExternalFilesDir(null), "Sounds");
        if (!soundsDir.exists()) {
            soundsDir.mkdirs();
        }
        List<File> localSoundList = getLocalSoundList(soundsDir);
        int size = localSoundList.size();
        soundNameList = new CharSequence[size + 3];
        soundKeyList = new CharSequence[size + 3];
        for (int i = 0; i < size; i++) {
            String soundName = localSoundList.get(i).getName();
            soundNameList[i] = soundName.subSequence(0, soundName.lastIndexOf('.'));
            soundKeyList[i] = localSoundList.get(i).toURI().toString();
        }
        soundNameList[size] = "默认音源";
        soundKeyList[size] = "original";
        soundNameList[size + 1] = "选择本地音源...";
        soundKeyList[size + 1] = "select";
        soundNameList[size + 2] = "下载更多音源...";
        soundKeyList[size + 2] = "more";
    }

    private List<File> getLocalSoundList(File file) {
        List<File> linkedList = new LinkedList<>();
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File listFile : listFiles) {
                if (listFile.isFile() && (listFile.getName().endsWith(".ss") || listFile.getName().endsWith(".sf2"))) {
                    linkedList.add(listFile);
                }
            }
        }
        return linkedList;
    }

    public void deleteFiles(String str) {
        FileUtil.deleteFileUsingUri(getContext(), Uri.parse(str));
        loadSoundList();
        soundListAdapter.updateSoundList(soundNameList, soundKeyList);
        soundListAdapter.notifyDataSetChanged();
    }

    public String getPersistedString() {
        return getPersistedString("original");
    }

    public DialogFragmentCompat newDialog() {
        dialogFragmentCompat = new DialogFragmentCompat(this);
        return dialogFragmentCompat;
    }

    public void closeDialog() {
        if (dialogFragmentCompat != null) {
            dialogFragmentCompat.closeDialog();
        }
    }

    public static class DialogFragmentCompat extends PreferenceDialogFragmentCompat {

        private final SoundListPreference soundListPreference;

        public DialogFragmentCompat(SoundListPreference soundListPreference) {
            this.soundListPreference = soundListPreference;
            Bundle bundle = new Bundle(1);
            bundle.putString(ARG_KEY, soundListPreference.getKey());
            setArguments(bundle);
        }

        @Override
        protected void onPrepareDialogBuilder(@NonNull AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            soundListPreference.loadSoundList();
            soundListPreference.jpProgressBar = new JPProgressBar(new ContextThemeWrapper(soundListPreference.getContext(), R.style.JustPianoTheme));
            LinearLayout linearLayout = new LinearLayout(soundListPreference.getContext());
            linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setMinimumWidth(400);
            linearLayout.setPadding(20, 20, 20, 20);
            linearLayout.setBackgroundColor(Color.WHITE);
            TextView textView = new TextView(soundListPreference.getContext());
            textView.setTextSize(14);
            textView.setTextColor(Color.BLACK);
            textView.setText("下载音源存储位置：SD卡/Android/data/ly.pp.justpiano3/files/Sounds，卸载APP时将删除所有文件");
            linearLayout.addView(textView);
            ListView listView = new ListView(soundListPreference.getContext());
            listView.setDivider(null);
            soundListPreference.soundListAdapter = new SoundListAdapter(soundListPreference,
                    soundListPreference.getContext(), soundListPreference.soundNameList, soundListPreference.soundKeyList);
            listView.setAdapter(soundListPreference.soundListAdapter);
            linearLayout.addView(listView);
            builder.setView(linearLayout);
        }

        @Override
        public void onDialogClosed(boolean positiveResult) {
            soundListPreference.persistString(soundListPreference.soundKey);
            GlobalSetting.loadSettings(soundListPreference.getContext(), false);
            FileUtil.UriInfo uriInfo = FileUtil.getUriInfo(soundListPreference.getContext(), Uri.parse(soundListPreference.soundKey));
            soundListPreference.setSummary(TextUtils.isEmpty(uriInfo.getDisplayName()) ? "默认音源" : uriInfo.getDisplayName());
        }

        public void closeDialog() {
            if (getDialog() != null && getDialog().isShowing()) {
                getDialog().dismiss();
            }
        }
    }
}
