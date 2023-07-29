package ly.pp.justpiano3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

final class SoundListAdapter extends BaseAdapter {
    final SoundListPreference soundListPreference;
    Context context;
    CharSequence[] f5984c;
    private CharSequence[] f5983b;

    SoundListAdapter(SoundListPreference soundListPreference, Context context, CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        this.soundListPreference = soundListPreference;
        this.context = context;
        f5983b = charSequenceArr;
        f5984c = charSequenceArr2;
    }

    final void mo3583a(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        f5983b = charSequenceArr;
        f5984c = charSequenceArr2;
    }

    @Override
    public int getCount() {
        return f5983b.length;
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
        String valueOf = String.valueOf(f5984c[i]);
        ((TextView) view.findViewById(R.id.skin_name)).setText(f5983b[i]);
        Button button = view.findViewById(R.id.skin_dele);
        if (valueOf.equals("original") || valueOf.equals("more")) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> soundListPreference.deleteFiles(valueOf));
        }
        button = view.findViewById(R.id.set_skin);
        if (valueOf.equals("more")) {
            button.setText("点击获取更多音源");
        } else {
            button.setText("设置音源");
        }
        button.setOnClickListener(new ChangeSoundClick(this, valueOf, i));
        soundListPreference.str1 = valueOf;
        return view;
    }
}
