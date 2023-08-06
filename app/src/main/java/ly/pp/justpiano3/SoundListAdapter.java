package ly.pp.justpiano3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public final class SoundListAdapter extends BaseAdapter {
    final SoundListPreference soundListPreference;
    Context context;
    CharSequence[] soundKeyList;
    private CharSequence[] soundNameList;

    public SoundListAdapter(SoundListPreference soundListPreference, Context context, CharSequence[] soundNameList, CharSequence[] soundKeyList) {
        this.soundListPreference = soundListPreference;
        this.context = context;
        this.soundNameList = soundNameList;
        this.soundKeyList = soundKeyList;
    }

    void mo3583a(CharSequence[] soundNameList, CharSequence[] soundKeyList) {
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
        Button button = view.findViewById(R.id.skin_dele);
        if (soundKey.equals("original") || soundKey.equals("more")) {
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> soundListPreference.deleteFiles(soundKey));
        }
        button = view.findViewById(R.id.set_skin);
        if (soundKey.equals("more")) {
            button.setText("点击获取更多音源");
        } else {
            button.setText("设置音源");
        }
        button.setOnClickListener(new ChangeSoundClick(this, soundKey, i));
        soundListPreference.soundKey = soundKey;
        return view;
    }
}
