package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.local.SkinDownload;
import ly.pp.justpiano3.activity.settings.SettingsActivity;
import ly.pp.justpiano3.task.SkinListPreferenceTask;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.view.preference.SkinListPreference;

public final class SkinListAdapter extends BaseAdapter {
    public final SkinListPreference skinListPreference;
    private final Context context;
    private CharSequence[] skinNameList;
    private CharSequence[] skinKeyList;

    public SkinListAdapter(SkinListPreference skinListPreference, Context context, CharSequence[] skinNameList, CharSequence[] skinKeyList) {
        this.skinListPreference = skinListPreference;
        this.context = context;
        this.skinNameList = skinNameList;
        this.skinKeyList = skinKeyList;
    }

    public void updateSkinList(CharSequence[] skinNameList, CharSequence[] skinKeyList) {
        this.skinNameList = skinNameList;
        this.skinKeyList = skinKeyList;
    }

    @Override
    public int getCount() {
        return skinNameList.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.ol_skin_view, null);
        }
        String skinKey = String.valueOf(skinKeyList[i]);
        ((TextView) view.findViewById(R.id.skin_name_view)).setText(skinNameList[i]);
        Button deleteButton = view.findViewById(R.id.skin_dele);
        if (skinKey.equals("original") || skinKey.equals("more") || skinKey.equals("select")) {
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> skinListPreference.deleteFiles(skinKey));
        }
        Button setButton = view.findViewById(R.id.set);
        switch (skinKey) {
            case "more" -> setButton.setText("点击获取更多皮肤");
            case "select" -> setButton.setText("点击选择皮肤");
            default -> setButton.setText("设置皮肤");
        }
        setButton.setOnClickListener(v -> {
            if (skinKey.equals("more")) {
                ((SettingsActivity) (context)).skinSelectLauncher.launch(new Intent(context, SkinDownload.class));
            } else if (skinKey.equals("select")) {
                FilePickerUtil.openFilePicker(context, false,
                        "skin_select", ((SettingsActivity) context).filePickerLauncher);
            } else {
                skinListPreference.skinKey = skinKey;
                skinListPreference.skinFile = Objects.equals("original", skinKey) ? null : Uri.parse(skinKey);
                new SkinListPreferenceTask(skinListPreference).execute(skinKey);
            }
        });
        return view;
    }
}
