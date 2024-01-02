package ly.pp.justpiano3.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.local.SettingsActivity;
import ly.pp.justpiano3.activity.local.SoundDownload;
import ly.pp.justpiano3.task.SoundListPreferenceTask;
import ly.pp.justpiano3.utils.FilePickerUtil;
import ly.pp.justpiano3.view.preference.SoundListPreference;

public final class SoundListAdapter extends BaseAdapter {
    public final SoundListPreference soundListPreference;
    public Context context;
    public CharSequence[] soundKeyList;
    private CharSequence[] soundNameList;

    public SoundListAdapter(SoundListPreference soundListPreference, Context context, CharSequence[] soundNameList, CharSequence[] soundKeyList) {
        this.soundListPreference = soundListPreference;
        this.context = context;
        this.soundNameList = soundNameList;
        this.soundKeyList = soundKeyList;
    }

    public void updateSoundList(CharSequence[] soundNameList, CharSequence[] soundKeyList) {
        this.soundNameList = soundNameList;
        this.soundKeyList = soundKeyList;
    }

    @Override
    public int getCount() {
        return soundNameList.length;
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
        String soundKey = String.valueOf(soundKeyList[i]);
        ((TextView) view.findViewById(R.id.skin_name_view)).setText(soundNameList[i]);
        Button deleteButton = view.findViewById(R.id.skin_dele);
        if (soundKey.equals("original") || soundKey.equals("more") || soundKey.equals("select")) {
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> soundListPreference.deleteFiles(soundKey));
        }
        Button setButton = view.findViewById(R.id.set);
        switch (soundKey) {
            case "more" -> setButton.setText("点击获取更多音源");
            case "select" -> setButton.setText("点击选择音源");
            default -> setButton.setText("设置音源");
        }
        setButton.setOnClickListener(v -> {
            if (soundKey.equals("more")) {
                ((SettingsActivity) (context)).soundSelectLauncher.launch(new Intent(context, SoundDownload.class));
            } else if (soundKey.equals("select")) {
                FilePickerUtil.openFilePicker((ComponentActivity) context, false,
                        "sound_select", ((SettingsActivity) context).filePickerLauncher);
            } else {
                soundListPreference.soundKey = soundKey;
                new SoundListPreferenceTask(soundListPreference).execute(soundKey);
            }
        });
        return view;
    }
}
